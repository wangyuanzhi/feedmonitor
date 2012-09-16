package com.hotinno.feedmonitor.dao.config;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ConfigDao {
	// Injected database connection:
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void persist(Config config) {
		em.persist(config);
	}

	@Transactional
	public void persist(List<Config> configList) {
		for (Config config : configList) {
			em.persist(config);
		}
	}

	@Transactional
	public void merge(Config config) {
		em.merge(config);
	}

	@Transactional
	public void merge(List<Config> configList) {
		for (Config config : configList) {
			em.merge(config);
		}
	}

	public List<Config> getConfigBySection(String section) {
		TypedQuery<Config> query = em.createQuery(
				"SELECT c FROM Config c WHERE c.section = ?", Config.class);
		query.setParameter(1, section);

		return query.getResultList();
	}

	public Config getConfig(String section, String name) {
		TypedQuery<Config> query = em.createQuery(
				"SELECT c FROM Config c WHERE c.section = ? AND c.name = ?",
				Config.class);

		query.setParameter(1, section);
		query.setParameter(2, name);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
