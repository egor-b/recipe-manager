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

@Data
@Entity
@Table(name = "PURCHASE")
public class PurchaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECIPE_SEQ")
    @SequenceGenerator(name = "RECIPE_SEQ", sequenceName = "RECIPE_SEQ", allocationSize = 1)
    private long id;

    @JsonProperty("recipeName")
    @Column(name = "RECIPE_NAME", nullable = false)
    private String recipeName;

    @JsonProperty("serve")
    @Column(name = "SERVE", nullable = false)
    private int serve;

    @JsonProperty("size")
    @Column(name = "SIZE", nullable = true)
    private Double size;

    @JsonProperty("isAvailable")
    @Column(name = "IS_AVAILABLE", nullable = false)
    private boolean isAvailable;

    @JsonProperty("recipeId")
    @Column(name = "RECIPE_ID", nullable = false)
    private long recipeId;

    @JsonProperty("foodId")
    @Column(name = "FOOD_ID", nullable = false)
    private long food;

    @JsonProperty("userId")
    @Column(name = "USER_ID", nullable = false)
    private String userId;

}
