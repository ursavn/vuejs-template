package com.ursa.tools.amazon.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ursa.tools.amazon.constant.enums.PriceOption;
import com.ursa.tools.amazon.constant.enums.QuantityOption;
import com.ursa.tools.amazon.constant.enums.SellerOption;
import com.ursa.tools.amazon.model.audit.UserDateAudit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "automation")
public class Automation extends UserDateAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	@EqualsAndHashCode.Exclude
    @ToString.Exclude
	private Product product;
	
	private Boolean status;
		
	@Column(name = "from_time")
	private Integer fromTime; // Minute of day
	
	@Column(name = "to_time")
	private Integer toTime;   //Minute of day	
	
	@Column(name = "prime_only")
	private Boolean primeOnly;	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "seller_option")
	private SellerOption sellerOption;
	
	@Column(name = "seller_option_value", length = 1000)
	private String sellerOptionValue;
	
	private Long price;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "price_option")
	private PriceOption priceOption;
	
	private Integer quantity;
	
	@Column(name = "purchase_quantity")
	private Integer purchaseQuantity;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "quantity_option")
	private QuantityOption quantityOption;
	
	@Column(name = "quantity_option_value")
	private Integer quantityOptionValue;
	
	@Column(name = "accounts")
	private String accounts;
	
	@OneToMany(mappedBy = "automation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @OrderBy("id desc")
	private List<AutomationLog> automationLogs;

}
