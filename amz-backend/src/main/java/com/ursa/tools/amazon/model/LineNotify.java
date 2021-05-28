package com.ursa.tools.amazon.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ursa.tools.amazon.model.audit.UserDateAudit;

import lombok.Data;

@Data
@Entity
@Table(name = "line_notify")
public class LineNotify extends UserDateAudit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@JsonIgnore
	private String token;
	
	@Column(name = "target_type")
	private String targetType;
	
	private String target;
}
