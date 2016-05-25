package com.xnx3.j2ee.dao;

import static org.hibernate.criterion.Example.create;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.xnx3.j2ee.entity.User;
import com.xnx3.j2ee.util.Page;
import com.xnx3.j2ee.util.Sql;

/**
 * 通用的
 * @author 管雷鸣
 */
@Transactional
public class GlobalDAO {
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	protected void initDao() {
		// do nothing
	}
	
	/**
	 * 获取查询的信息条数
	 * @param tableName 表名,多个表中间用,分割，如: "user,message,log"。同样如果是多个表，where参数需要增加关联条件
	 * @param where 查询条件，直接使用 {@link Sql#getWhere(javax.servlet.http.HttpServletRequest, String[], String)} 来组合
	 * @return
	 */
	public int count(String tableName,String where){
		String queryString = "SELECT count(*) FROM "+tableName+where;
		BigInteger count = (BigInteger)getCurrentSession().createSQLQuery(queryString).uniqueResult();
		return count.intValue();
	}

//	/**
//	 * 查询列表，配合 {@link Page} {@link Sql} 一块使用
//	 * @param selectFrom 如 SELECT * FROM user
//	 * @param where {@link Sql#getWhere(javax.servlet.http.HttpServletRequest, String[], String)}
//	 * @param limitStart limit开始的记录数
//	 * @param limitNumber limit返回多少条记录
//	 * @param entityClass 转化为什么实体类
//	 * @return
//	 */
//	public List findBySqlQuery(String selectFrom,String where,Page page,Class entityClass) {
//		try {
//			String orderBy = "";
//			if(page.getOrderBy() != null){
//				orderBy = " ORDER BY "+page.getOrderBy();
//			}
//			String queryString = selectFrom+where+orderBy+" LIMIT "+page.getLimitStart()+","+page.getEveryNumber();
//			Query queryObject = getCurrentSession().createSQLQuery(queryString).addEntity(entityClass);
//			return queryObject.list();
//		} catch (RuntimeException re) {
//			throw re;
//		}
//	}
//	
//	/**
//	 * 同 {@link #findBySqlQuery(String, String, int, int, Class)}
//	 * 返回的是<List<Map<String,Object>>>
//	 * @param sql 执行的sql，不包含limit，limit会自动拼接
//	 * @return 
//	 */
//	public List<Map<String,String>> findBySqlQuery(String sql,Page page) {
//		try {
//			String orderBy = "";
//			if(page.getOrderBy() != null){
//				orderBy = " ORDER BY "+page.getOrderBy();
//			}
//			String queryString = sql+orderBy+" LIMIT "+page.getLimitStart()+","+page.getEveryNumber();
//			Query queryObject = getCurrentSession().createSQLQuery(queryString).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//			return queryObject.list();
//		} catch (RuntimeException re) {
//			throw re;
//		}
//	}
	
	/**
	 * 传入 {@link Sql} 查询List列表
	 * @param sql 组合好的{@link Sql}
	 * @return List<Map<String,String>>
	 */
	public List<Map<String,String>> findMapBySql(Sql sql){
		try {
			Query queryObject = getCurrentSession().createSQLQuery(sql.getSql()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			return queryObject.list();
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	/**
	 * 查询列表,返回实体类 List<Entity>，配合 {@link Sql} 一块使用
	 * @param sql 组合好的查询{@link Sql}
	 * @param entityClass 转化为什么实体类
	 * @return List<Entity>
	 */
	public List findEntityBySqlQuery(Sql sql,Class entityClass) {
		try {
			Query queryObject = getCurrentSession().createSQLQuery(sql.getSql()).addEntity(entityClass);
			return queryObject.list();
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	
	/**
	 * 添加/修改
	 * @param entity 实体类
	 */
	public void save(Object entity) {
		try {
			getCurrentSession().saveOrUpdate(entity);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	/**
	 * 删除
	 * @param entity 实体类
	 */
	public void delete(Object entity) {
		try {
			getCurrentSession().delete(entity);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	/**
	 * 根据主键查记录
	 * @param entity 实体类 如 {@link User}.class
	 * @param id 主键id
	 * @return Object 可直接转换为实体类
	 */
	public Object findById(Class c , int id) {
		try {
			Object instance = getCurrentSession().get(c.getCanonicalName(), id);
			return instance;
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	/**
	 * 根据实体类对象的赋值查纪录列表
	 * @param obj 实体类
	 * @return {@link List}
	 */
	public List findByExample(Object entity) {
		try {
			List results = getCurrentSession()
					.createCriteria(entity.getClass().getCanonicalName())
					.add(create(entity)).list();
			return results;
		} catch (RuntimeException re) {
			throw re;
		}
	}

	/**
	 * 根据字段名查值
	 * @param c {@link Class} 实体类，如 {@link User}.class
	 * @param propertyName 数据表字段名
	 * @param value  值
	 * @return {@link List}
	 */
	public List findByProperty(Class c,String propertyName, Object value) {
		try {
			String queryString = "from "+c.getSimpleName()+" as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	/**
	 * 执行SQL语句
	 * @param sql 要执行的SQL语句
	 * @return
	 */
	public int executeSql(String sql){    
        int result ;    
        SQLQuery query = getCurrentSession().createSQLQuery(sql);    
        result = query.executeUpdate();    
        return result;
    }
	
	public static GlobalDAO getFromApplicationContext(ApplicationContext ctx) {
		return (GlobalDAO) ctx.getBean("GlobalDAO");
	}
}