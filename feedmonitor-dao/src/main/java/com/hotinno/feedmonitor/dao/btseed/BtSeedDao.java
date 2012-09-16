package com.hotinno.feedmonitor.dao.btseed;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BtSeedDao {
	// Injected database connection:
	@PersistenceContext
	private EntityManager em;

	// Stores a new guest:
	@Transactional
	public void persist(BtSeed seed) {
		em.persist(seed);
	}

	@Transactional
	public void persist(List<BtSeed> seeds) {
		for (BtSeed btSeed : seeds) {
			persist(btSeed);
		}
	}

	@Transactional
	public void merge(BtSeed seed) {
		em.merge(seed);
	}

	@Transactional
	public int deleteById(long id) {
		Query query = em.createQuery("DELETE FROM BtSeed b WHERE b.id = ?");
		query.setParameter(1, id);

		return query.executeUpdate();
	}

	@Transactional
	public int clearById(long id) {
		Query query = em
				.createQuery("UPDATE BtSeed b SET b.processed = ?, b.processedTime = ? WHERE b.id = ?");
		query.setParameter(1, false);
		query.setParameter(2, null);
		query.setParameter(3, id);

		return query.executeUpdate();
	}

	// Retrieves all the guests:
	public List<BtSeed> getAll() {
		TypedQuery<BtSeed> query = em.createQuery("SELECT f FROM BtSeed f",
				BtSeed.class);
		return query.getResultList();
	}

	public List<BtSeed> getAllUnProcessed() {
		TypedQuery<BtSeed> query = em.createQuery(
				"SELECT f FROM BtSeed f WHERE f.processed = ?", BtSeed.class);
		query.setParameter(1, false);
		return query.getResultList();
	}

	public BtSeed getById(long id) {
		TypedQuery<BtSeed> query = em.createQuery(
				"SELECT b FROM BtSeed b WHERE b.id = ?", BtSeed.class);
		query.setParameter(1, id);

		return query.getSingleResult();
	}

	public boolean isMagnetUrlExisted(String magnetUrl) {
		TypedQuery<Long> query = em.createQuery(
				"SELECT count(*) FROM BtSeed f WHERE f.magnetUrl = ?",
				Long.class);
		query.setParameter(1, magnetUrl);
		if (query.getSingleResult() > 0) {
			return true;
		}
		return false;
	}

	// Retrieves all the guests:
	public List<BtSeed> getTop(int number) {
		TypedQuery<BtSeed> query = em.createQuery(
				"SELECT f FROM BtSeed f ORDER BY f.date DESC", BtSeed.class)
				.setMaxResults(number);
		return query.getResultList();
	}

}
