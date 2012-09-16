package com.hotinno.feedmonitor.dao.feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FeedDao {
	// Injected database connection:
	@PersistenceContext(name = "feedmonitorEmf")
	private EntityManager em;

	@Transactional
	public void persist(Feed feed) {
		em.persist(feed);
	}

	@Transactional
	public void merge(Feed feed) {
		em.merge(feed);
	}

	public List<Feed> getAll() {
		TypedQuery<Feed> query = em.createQuery(
				"SELECT f FROM Feed f ORDER BY ID", Feed.class);
		return query.getResultList();
	}

	public List<String> getAllUrls() {
		TypedQuery<String> query = em.createQuery(
				"SELECT distinct f.url FROM Feed f", String.class);
		return query.getResultList();
	}

	/**
	 * TODO Get all which URL equals the one's with specific ID
	 *
	 * @param id
	 * @return
	 */
	public List<Feed> getAllById(long id) {
		TypedQuery<Feed> query = em.createQuery(
				"SELECT f FROM Feed f WHERE f.id = ?", Feed.class);
		query.setParameter(1, id);

		ArrayList<Feed> resultList = new ArrayList<Feed>();
		resultList.add(query.getSingleResult());
		return resultList;
	}

	public Date getLastFetchTime() {
		TypedQuery<Date> query = em.createQuery(
				"SELECT f.lastUpdated FROM Feed f ORDER BY f.lastUpdated DESC",
				Date.class);
		query.setMaxResults(1);
		return query.getSingleResult();
	}

	@Transactional
	public int deleteById(long id) {
		Query query = em.createQuery("DELETE FROM Feed f WHERE f.id = ?");
		query.setParameter(1, id);

		return query.executeUpdate();
	}

	@Transactional
	public int clearById(long id) {
		Query query = em
				.createQuery("UPDATE Feed f SET f.lastUpdated = null WHERE f.id = ?");
		query.setParameter(1, id);

		return query.executeUpdate();
	}

	public Feed getById(long id) {
		TypedQuery<Feed> query = em.createQuery(
				"SELECT f FROM Feed f WHERE f.id = ?", Feed.class);
		query.setParameter(1, id);

		return query.getSingleResult();
	}

	public List<Feed> getAllByUrl(String url) {
		TypedQuery<Feed> query = em.createQuery(
				"SELECT f FROM Feed f WHERE f.url = ?", Feed.class);

		query.setParameter(1, url);

		return query.getResultList();
	}
}
