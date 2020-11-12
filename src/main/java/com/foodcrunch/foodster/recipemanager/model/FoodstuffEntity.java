package com.foodcrunch.foodster.recipemanager.model;

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

    public FoodstuffEntity() {}

    public FoodstuffEntity(long id, String name) {
        this.setId(id);
        this.name = name;
    }

//    @JsonIgnore
    public long getId() { return id; }
    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }
    public void setId(long id) { this.id = id; }
}
