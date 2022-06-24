package com.sudarshan.core.data;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="products")
public class ProductEntity implements Serializable {

	private static final long serialVersionUID = 3442543148561876087L;
	
	@Id
	@Column(unique = true)
	private String productId;
	
	@Column(unique = true)
	private String title;
	
	private BigDecimal price;
	
	private Integer quantity;

}
