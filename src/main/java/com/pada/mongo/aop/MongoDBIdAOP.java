package com.pada.mongo.aop;

import java.lang.reflect.Field;
import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.pada.mongo.annotation.CascadeSave;
import com.pada.mongo.domain.BaseDomain;
import com.pada.mongo.event.IdIncrementEvent;

@Aspect
@Component
public class MongoDBIdAOP {
    @Autowired
    private IdIncrementEvent incrementEvent;

    @Autowired
    private MongoOperations mongoOperations;

    @Pointcut("execution(* org.springframework.data.mongodb.repository.MongoRepository.save(..))")
    public void dBIdCutPoint() {
    }

    @Around(value = "dBIdCutPoint()")
    public Object pointCut(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.getArgs()[0];
        if (object instanceof Collection) {
            Iterable<?> iterable = (Iterable<?>) object;
            for (Object _o : iterable) {
                if (_o instanceof BaseDomain) {
                    setId((BaseDomain) _o);
                    onBeforeSave(_o);  //在 Save 之前先检查该 Object 中是否需要级联存储
                }
            }
        } else {
            if (object instanceof BaseDomain) {
                setId((BaseDomain) object);
                onBeforeSave(object); //在 Save 之前先检查该 Object 中是否需要级联存储
            }

        }
        return joinPoint.proceed();
    }

    private synchronized void setId(BaseDomain baseDomain) {
        incrementEvent.incrementIdSetting(baseDomain);
    }


    private void onBeforeSave(final Object source) {

        // 遍历 source 中所有的 field, 如果 field 存在同时有 @DBRef 和 @CascadeSave
        // 则需要将该 field 设置 Id 并进行存储
        // 暂无考虑 field 为 Collection 的情况
        ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {

            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);

                if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {
                    final Object fieldValue = field.get(source);

                    DbRefFieldCallback callback = new DbRefFieldCallback();

                    ReflectionUtils.doWithFields(fieldValue.getClass(), callback);

                    if (!callback.isIdFound()) {
                        throw new MappingException("Cannot perform cascade save on child object without id set");
                    }
                    setId((BaseDomain) fieldValue);
                    mongoOperations.save(fieldValue);
                }
            }
        });
    }

    private static class DbRefFieldCallback implements ReflectionUtils.FieldCallback {
        private boolean idFound;

        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            ReflectionUtils.makeAccessible(field);

            if (field.isAnnotationPresent(Id.class)) {
                idFound = true;
            }
        }

        public boolean isIdFound() {
            return idFound;
        }
    }

}
