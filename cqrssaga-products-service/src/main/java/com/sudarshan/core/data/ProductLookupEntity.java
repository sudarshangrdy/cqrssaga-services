package com.sudarshan.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="productlookup")
@AllArgsConstructor
@NoArgsConstructor
public class ProductLookupEntity implements Serializable {
	
	private static final long serialVersionUID = 2912212189087426008L;

	@Id
	@Column(unique = true)
	private String productId;
	
	@Column(unique = true)
	private String title;

}
