package com.foodcrunch.foodster.recipemanager.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "FOOD")
public class FoodEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COOKFOOD_SEQ")
    @SequenceGenerator(name = "COOKFOOD_SEQ", sequenceName = "COOKFOOD_SEQ", allocationSize = 1)
    private long id;

    @ManyToOne
    private RecipeEntity recipeEntity;

    @JsonProperty("product")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "FOOD_ID", referencedColumnName = "ID")
    private ProductEntity productEntity;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @Column(name = "UNIT", nullable = false)
    private String unit;

    public void setUnit(String unit) {
        this.unit = unit.trim();
    }
}
