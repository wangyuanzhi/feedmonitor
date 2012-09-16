package com.hotinno.feedmonitor.dao.feed;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Data
@NoArgsConstructor
public class Feed implements Serializable {
	private static final long serialVersionUID = -3405638438621444561L;

	// Persistent Fields:
	@Id
	@GeneratedValue
	@Setter(AccessLevel.NONE)
	private Long id;

	private String name;
	private String url;

	@Column(name = "KEY_WORDS")
	private String keywords;

	@Column(name = "LAST_UPDATED")
	private Timestamp lastUpdated;

	private String comment;

	public Feed(String name, String url, String keywords) {
		this.name = name;
		this.url = url;
		this.keywords = keywords;
	}
}
