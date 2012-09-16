package com.hotinno.feedmonitor.dao.btseed;

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
@Table(name = "BT_SEED")
@Data
@NoArgsConstructor
public class BtSeed implements Serializable {
	private static final long serialVersionUID = -3405638492761444561L;

	// Persistent Fields:
	@Id
	@GeneratedValue
	@Setter(AccessLevel.NONE)
	private Long id;
	private String name;
	private Timestamp date;

	@Column(name = "MAGNET_URL", length = 32768)
	private String magnetUrl;

	private boolean processed;

	@Column(name = "PROCESSED_TIME")
	private Timestamp processedTime;

	private String comment;

	public BtSeed(String name, String magnetUrl) {
		this.name = name;
		this.magnetUrl = magnetUrl;
		this.date = new Timestamp(System.currentTimeMillis());
	}

}
