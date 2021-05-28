package com.ursa.tools.amazon.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ursa.tools.amazon.constant.enums.UserRole;
import com.ursa.tools.amazon.model.audit.DateAudit;

import lombok.Data;

@Data
@Entity
@Table(name = "user",
      indexes = {
    	  @Index(name="idx_user_email", columnList = "email", unique = true)
      })
public class User extends DateAudit {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    
    @JsonIgnore
    private String password;

    @Column(name = "full_name")
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;

}
