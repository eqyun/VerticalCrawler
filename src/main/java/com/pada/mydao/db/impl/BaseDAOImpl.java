package com.pada.mydao.db.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pada.mydao.db.BaseDAO;


@Repository
@Transactional
public class BaseDAOImpl implements BaseDAO {

	@Autowired
	private SessionFactory _sessionFactory;

	private Session getCurrentSession() {
		return _sessionFactory.getCurrentSession();
	}

	@Override
	public void updateSQL(String sqlQuery) {
		Session session  = _sessionFactory.openSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(sqlQuery);
		query.executeUpdate();
		session.getTransaction().commit();
		closeSession(session);
	}

	@Override
	public List<?> findByDetachedCriteria(DetachedCriteria detachedCriteria) {
		Session session = getCurrentSession();
		Criteria criteria = detachedCriteria.getExecutableCriteria(session);
		List<?> list = criteria.list();
		return list;
	}

	@Override
	public Object save(Object model) {
		return getCurrentSession().save(model);
	}

	@Override
	public void save(Session session, Object model) {
		session.save(model);
	}

	@Override
	public void saveOrUpdate(Object model) {
		getCurrentSession().save(model);

	}

	@Override
	public void saveOrUpdate(Session session, Object model) {
		session.saveOrUpdate(model);
	}

	@Override
	public void update(Object model) {
		getCurrentSession().update(model);

	}

	@Override
	public void update(Session session, Object model) {
		session.update(model);

	}

	@Override
	public void delete(Object model) {
		getCurrentSession().delete(model);

	}

	@Override
	public void delete(Session session, Object model) {
		session.delete(model);
	}

	@Override
	public Session getSession() throws Exception {
		Session session  = _sessionFactory.openSession();
		session.beginTransaction();
		return session;
	}

	@Override
	public void closeSession(Session session) {
		if(session!=null && session.isOpen()){
			session.close();
		}
	}

	@Override
	public List<?> createQuery(String sqlQuery, Class<?> clazz) {
		Session session = getCurrentSession();
		SQLQuery query = session.createSQLQuery(sqlQuery).addEntity(clazz);
		List<?> pusList = query.list();
		return pusList;
	}

	@Override
	public Object get(Long ID, Class<?> clazz) {
		return getCurrentSession().get(clazz, ID);
	}


}
