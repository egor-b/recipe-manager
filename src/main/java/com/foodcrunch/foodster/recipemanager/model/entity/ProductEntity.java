package com.foodcrunch.foodster.recipemanager.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "PRODUCT")
public class ProductEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_SEQ")
    @SequenceGenerator(name = "PRODUCT_SEQ", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @JsonProperty("pic")
    @Column(name = "IMAGE_REFERENCE")
    private String image;

    public void setName(String name) {
        this.name = name.trim();
    }
}
