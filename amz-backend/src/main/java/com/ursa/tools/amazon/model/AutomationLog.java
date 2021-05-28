package com.ursa.tools.amazon.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ursa.tools.amazon.constant.enums.AutomationLogType;
import com.ursa.tools.amazon.model.audit.DateAudit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "automation_log")
public class AutomationLog extends DateAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "automation_id")
	@JsonIgnore
	private Automation automation;
	
	private String content;
	
	@Enumerated(EnumType.STRING)
	private AutomationLogType type;
	
	public AutomationLog(AutomationLogType type, String content, Automation automation) {
		this.type = type;
		this.content = content;
		this.automation = automation;
	}
		
}
