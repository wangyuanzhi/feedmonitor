package com.hotinno.feedmonitor.dao.guest;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
public class Guest implements Serializable {
	private static final long serialVersionUID = 5613442545285726909L;

	// Persistent Fields:
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@Column(name = "SIGNING_DATE")
	private Timestamp signingDate;

	public Guest(String name) {
		this.name = name;
		this.signingDate = new Timestamp(System.currentTimeMillis());
	}

	@Override
	public String toString() {
		return name + " (signed on " + signingDate + ")";
	}
}