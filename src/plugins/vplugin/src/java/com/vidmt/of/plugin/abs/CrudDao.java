package com.vidmt.of.plugin.abs;

import java.util.List;

/**
 * 对于需要实现实体类的，可以实现此接口
 * @param <T>
 */
public interface CrudDao<T extends CrudEntity> extends BaseDao<T>{
	public T find(T entity);
	public List<T> findList(T entity);
	@Deprecated
	public List<T> findAll();
	public int save(T entity);
	public int update(T entity);
	public int saveOrUpdate(T endity);
	@Deprecated
	public int delete(String id);
	public int delete(T entity);
}