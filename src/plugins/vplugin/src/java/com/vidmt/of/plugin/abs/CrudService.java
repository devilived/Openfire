package com.vidmt.of.plugin.abs;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * 对于需要实现实体类的，可以实现此接口
 */
@Transactional(readOnly = false)
public abstract class CrudService<D extends CrudDao<T>, T extends CrudEntity> extends BaseService<D, T> {
	/**
	 * 获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public T load(Long id) {
		return dao.load(id);
	}

	/**
	 * 获取单条数据
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = true)
	public T find(T entity) {
		return dao.find(entity);
	}

	/**
	 * 查询列表数据
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<T> findList(T entity) {
		return dao.findList(entity);
	}

	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public List<T> findAll() {
		return dao.findAll();
	}

	/**
	 * 保存数据（插入或更新）
	 * 
	 * @param entity
	 */
	public int saveOrUpdate(T entity) {
		return dao.saveOrUpdate(entity);
	}

	public int save(T entity) {
		return dao.save(entity);
	}

	public int update(T entity) {
		return dao.update(entity);
	}

	/**
	 * 删除数据
	 * 
	 * @param entity
	 */
	public void delete(T entity) {
		dao.delete(entity);
	}

}
