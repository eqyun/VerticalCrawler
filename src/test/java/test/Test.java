/*package test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.pada.common.ReflectUtil;
import com.pada.spider.annotation.SpiderKeyInject;

public class Test {
	@org.junit.Test
	public void dateTest(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String value ="2014-10-31 14:15:11";
	}
	Map<Class<?>,Object> map = Maps.newHashMap();
	
	public Object testInject(Class<?> ObjectType,Map<String,Object[]> maps,Object strategy) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		
		Field[] fields=TestA.class.getDeclaredFields();
		
		Object object = createObjectByClass(ObjectType);
		
		
		for(Field field : fields){
			Annotation annotation = field.getAnnotation(SpiderKeyInject.class);
			if(annotation!=null){
				Class<?> clazz = ((SpiderKeyInject)annotation).clazz();
				if(clazz.equals(ObjectType)){
					String attr = ((SpiderKeyInject)annotation).attr();
					
					ReflectUtil.setFieldValue(object, attr,maps.get(ReflectUtil.getFieldValue(strategy, field.getName())));
				}
			}
		}
		
		return object;
		
		
	}
	
	@org.junit.Test
	public void testInject() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		
		TestA testA = new TestA();
		Map<String,Object> objects = Maps.newHashMap();
		Date date = new Date();
		date.setYear(1999);
		objects.put("post_date",date);
		testA.setDate("post_date");
		
		Field[] fields=TestA.class.getDeclaredFields();
		
		for(Field field : fields){
			Annotation annotation = field.getAnnotation(SpiderKeyInject.class);
			if(annotation!=null){
				Class<?> clazz = ((SpiderKeyInject)annotation).clazz();
				String attr = ((SpiderKeyInject)annotation).attr();
				Object object = createObjectByClass(clazz);
				ReflectUtil.setFieldValue(object, attr,objects.get(ReflectUtil.getFieldValue(testA, field.getName())));
			}
		}
		
		
		
		
		Set<Class<?>> keys = map.keySet();
		for(Class<?> key : keys){
			Object value = map.get(key);
			
		}
		
		
		
	}
	
	private Object createObjectByClass(Class<?> clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		if(!map.containsKey(clazz))
		{
			Object object = Class.forName(clazz.getName()).newInstance();
			map.put(clazz, object);
			
		}
		return map.get(clazz);
	}
	

}
*/