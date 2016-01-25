package com.vidmt.of.plugin.abs;

/**
 * DAO支持类实现,对于不需要创建实体类的数据库操作，可以实现此接口
 * @param <T>
 */
public interface BaseDao<T>{
	public T load(Long id);
}