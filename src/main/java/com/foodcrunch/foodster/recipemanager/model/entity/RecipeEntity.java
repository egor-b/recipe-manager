package com.foodcrunch.foodster.recipemanager.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "RECIPE")
public class RecipeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECIPE_SEQ")
    @SequenceGenerator(name = "RECIPE_SEQ", sequenceName = "RECIPE_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "TIME", nullable = false)
    private int time;

    @Column(name = "SERVE", nullable = false)
    private int serve;

    @Column(name = "LEVEL")
    private double level;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "ABOUT", nullable = false)
    private String about;

    @JsonProperty("timestamp")
    @Column(name = "DATE", nullable = false)
    private String date;// = new Date();

    @Column(name = "USER_ID", nullable = true)
    private String userId;

    @Column(name = "VISIBLE", nullable = false)
    private String visible;

    @Column(name = "LANGUAGE", nullable = false)
    private String lang;

    @JsonProperty("food")
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RECIPE_ID", referencedColumnName = "ID")
    private Set<FoodEntity> foodEntity;

    @JsonProperty("step")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RECIPE_ID", referencedColumnName = "ID")
    private Set<StepEntity> stepEntity;

    @JsonProperty("image")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RECIPE_ID", referencedColumnName = "ID")
    private Set<ImageEntity> imageEntity;

}
