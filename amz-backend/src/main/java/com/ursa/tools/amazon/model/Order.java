package com.ursa.tools.amazon.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ursa.tools.amazon.model.audit.DateAudit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "`order`")
public class Order extends DateAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "automation_id")
	private Integer automationId;
	
	@Column(name = "user_id")
	private Integer userId;
	
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	@EqualsAndHashCode.Exclude
    @ToString.Exclude
	private Product product;
	
	@Column(name = "seller_id")
	private Integer sellerId;
	
	@Column(name = "seller_name")
	private String sellerName;
	
	@Column(name = "amazon_account_id")
	private Integer amazonAccountId;
	
	@Column(name = "amazon_account_email")
	private String amazonAccountEmail;
	
	private Integer quantity;
	private Long price;
	private Long point;
	private Long coupon;

}
