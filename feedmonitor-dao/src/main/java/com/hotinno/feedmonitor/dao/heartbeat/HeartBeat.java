package com.hotinno.feedmonitor.dao.heartbeat;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "HEART_BEAT")
@Data
@NoArgsConstructor
public class HeartBeat implements Serializable {
	private static final long serialVersionUID = 5613442545925726909L;

	// Persistent Fields:
	@Id
	@GeneratedValue
	@Setter(AccessLevel.NONE)
	private Long id;
	private Timestamp time;
	private String ip;

	@Column(name="LAST_BEAT")
	private Timestamp lastBeat;

	public HeartBeat(String ip) {
		this.ip = ip;
	}

	@PrePersist
	@PreUpdate
	public void updateDate() {
		Timestamp tmpTime = new Timestamp(System.currentTimeMillis());
		lastBeat = tmpTime;
		if (time == null) {
			time = tmpTime;
		}
	}

}
