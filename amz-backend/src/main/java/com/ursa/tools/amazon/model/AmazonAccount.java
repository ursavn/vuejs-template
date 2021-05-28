package com.ursa.tools.amazon.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;
import com.ursa.tools.amazon.model.audit.DateAudit;
import com.ursa.tools.amazon.model.audit.UserDateAudit;

import lombok.Data;

@Data
@Entity
@Table(name = "amazon_account")
public class AmazonAccount extends UserDateAudit {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@Column(name = "email", length = 320, nullable = false)
	private String email;
	
	@JsonIgnore
	@Column(name = "password", nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	private AmazonAccountStatus status;
	
	@Column(name = "status_note", length = 500)
	private String statusNote;

	@JsonIgnore
	@Column(name = "cookies", length = 3000)
	private String cookies;
}
