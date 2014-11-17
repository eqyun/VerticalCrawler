package com.pada.mydao.db;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
/**
 * 所有修改类都要配有相应 的事务
 * 主查询方案为DetachedCriteria
 * @author eqyun
 *
 */
public interface BaseDAO {
	
	void updateSQL(String sqlQuery);
	
	List<?> createQuery(final String sqlQuery,Class<?> clazz);
	
	
	List<?> findByDetachedCriteria(DetachedCriteria detachedCriteria);
	/**
	 * DetachedCriteria detachedCriteria = DetachedCriteria.forClass(VariableFor401k.class);
		detachedCriteria.add(Restrictions.eq("StrategyID", id));
	 * @param model
	 */
	Object save(final Object model);
	void save(Session session,Object model);
	
	void saveOrUpdate(final Object model);
	
	void saveOrUpdate(Session sessin,Object model);
	
	void update(final Object model);
	void update(Session session,Object model);
	
	void delete(final Object model);
	void delete(Session session ,Object model);
	
	/**
	 * 抛个异常提醒你要调用closeSession(Session session) 方法关事务.此方法要手动关闭session，切记
	 * @return
	 */
	Session getSession() throws Exception;
	void closeSession(Session session);
	
	Object get(Long ID,Class<?> clazz);
	
}
