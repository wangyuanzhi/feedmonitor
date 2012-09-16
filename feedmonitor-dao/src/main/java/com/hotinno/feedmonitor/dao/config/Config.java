package com.hotinno.feedmonitor.dao.config;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "section",
//"name" }) })
@Entity
@Data
@NoArgsConstructor
public class Config implements Serializable {
	private static final long serialVersionUID = -5868655430401568126L;

	// Persistent Fields:
	@Id
	@GeneratedValue
	@Setter(AccessLevel.NONE)
	private Long id;
	private String section;
	private String name;
	private String value;
	private String comment;

	@Setter(AccessLevel.NONE)
	@Column(name = "ADDED_TIME")
	private Timestamp addedTime;

	@Setter(AccessLevel.NONE)
	@Column(name = "LAST_UPDATED")
	private Timestamp lastUpdated;

	@PrePersist
	@PreUpdate
	public void updateDate() {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		lastUpdated = time;
		if (addedTime == null) {
			addedTime = time;
		}
	}

}
