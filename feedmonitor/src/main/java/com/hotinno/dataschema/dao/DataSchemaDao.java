package com.hotinno.dataschema.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSchemaDao {
	// Injected database connection:
	@PersistenceContext
	private EntityManager em;

	// Stores a new guest:
	@Transactional
	public List run(String sql) {
		Query query = em.createNativeQuery(sql);
		try {
			return query.getResultList();
		} catch (javax.persistence.PersistenceException e) {
			Throwable e1 = e.getCause().getCause();
			if ("Can not issue data manipulation statements with executeQuery()."
					.equals(e1.getMessage())) {
				List result = new ArrayList();
				try {
					final int executeUpdateResult = query.executeUpdate();
					result.add(executeUpdateResult);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
				return result;
			}

			throw new RuntimeException(e);
		}
	}
}
