package com.foodcrunch.foodster.recipemanager.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "FOODSTUFF")
public class FoodstuffEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FOODSTUFF_SEQ")
    @SequenceGenerator(name = "FOODSTUFF_SEQ", sequenceName = "FOODSTUFF_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @JsonProperty("pic")
    @Column(name = "FOOD_PIC_BYTE")
    private byte[] image;

    public FoodstuffEntity() {}

    public long getId() { return id; }
    public String getName() {
        return name;
    }
    public byte[] getImage() {
        return image;
    }

    public void setName(String name) { this.name = name; }
    public void setId(long id) { this.id = id; }
    public void setImage(byte[] image) {
        this.image = image;
    }
}
