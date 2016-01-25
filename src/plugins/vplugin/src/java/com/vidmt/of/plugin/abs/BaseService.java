package com.vidmt.of.plugin.abs;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 对于不需要创建实体类的数据库操作，可以实现此接口
 */
public abstract class BaseService<D extends BaseDao<T>, T> {
	@Autowired
	protected D dao;
}
