package com.hotinno.feedmonitor.dao.heartbeat;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HeartBeatDao {
	// Injected database connection:
	@PersistenceContext
	private EntityManager em;

	// Stores a new heartbeat:
	@Transactional
	public void persist(HeartBeat ip) {
		em.persist(ip);
	}

	public List<HeartBeat> getLastHeartBearts(int count) {
		TypedQuery<HeartBeat> query = em.createQuery(
				"SELECT hb FROM HeartBeat hb ORDER BY hb.time DESC",
				HeartBeat.class).setMaxResults(count);
		return query.getResultList();
	}

	// Retrieves all the guests:
	public long getTotalNumber() {
		TypedQuery<Long> query = em.createQuery(
				"SELECT count(*) FROM HeartBeat", Long.class);
		return query.getSingleResult();
	}

	@Transactional
	public int deleteBefore(Timestamp timestamp) {
		Query query = em
				.createQuery("DELETE FROM HeartBeat hb WHERE hb.time < ?");

		query.setParameter(1, timestamp);

		int result = query.executeUpdate();
		return result;
	}

	@Transactional
	public void merge(HeartBeat heartBeat) {
		em.merge(heartBeat);
	}

}
