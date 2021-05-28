package com.ursa.tools.amazon.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
	private static final int EXPIRATION = 60 * 24;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Integer id;

	private String token;

	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	@JsonIgnore
	private User user;

	@Column(name = "expired_date")
	private Date expiredDate;
	
	public PasswordResetToken() {}

	public PasswordResetToken(String token, User user) {
		this.token = token;
		this.user = user;
	}
	
	@PrePersist
	public void prePersist() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, EXPIRATION);
		this.expiredDate = cal.getTime();
	}
}
