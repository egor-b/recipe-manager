package com.foodcrunch.foodster.recipemanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

@Entity
@Table(name = "COOKFOOD")
public class CookFoodEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COOKFOOD_SEQ")
    @SequenceGenerator(name = "COOKFOOD_SEQ", sequenceName = "COOKFOOD_SEQ", allocationSize = 1)
    private long id;

    @ManyToOne
    private Recipe recipe;

    @JsonProperty("foodstuff")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "FOOD_ID", referencedColumnName = "ID")
    private FoodstuffEntity foodstuffEntity;

    @Column(name = "SIZE", nullable = false)
    private int size;

    @Column(name = "MEASURE", nullable = false)
    private String measure;

    @JsonProperty("_link")
    @Column(name = "IMAGE_LINK")
    private String image;

    public CookFoodEntity() {}

    @JsonIgnore
    public long getId() { return id; }
    public FoodstuffEntity getFoodstuffEntity() { return foodstuffEntity; }
    public int getSize() { return  size; }
    public String getMeasure() { return measure; }
    public String getImage() { return image; }

    public void setId(long id) { this.id = id; }
    public void setFoodstuffEntity(FoodstuffEntity foodstuffEntity) { this.foodstuffEntity = foodstuffEntity; }
    public void setSize(int size) { this.size = size; }
    public void setMeasure(String measure) { this.measure = measure; }
    public void setImage(String _link) { this.image = _link; }
}
