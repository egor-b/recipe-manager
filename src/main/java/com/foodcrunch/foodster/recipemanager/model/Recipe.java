package com.foodcrunch.foodster.recipemanager.model;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "RECIPE")
public class Recipe implements Serializable {

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

    @Column(name = "LEVEL", nullable = false)
    private double level;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "ABOUT", nullable = false)
    private String about;

    @JsonProperty("timestamp")
    @Column(name = "DATE", nullable = false)
    private Date date = new Date();

    @Column(name = "USER_ID", nullable = false)
    private long userId;

    @Column(name = "VISIBLE", nullable = false)
    private boolean visible;

    @Column(name = "LANGUAGE", nullable = false)
    private String lang;

    @JsonProperty("food")
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RECIPE_ID", referencedColumnName = "ID")
    private Set<CookFoodEntity> cookFoodEntity;

    @JsonProperty("step")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RECIPE_ID", referencedColumnName = "ID")
    private Set<CookStepEntity> cookStepEntity;

    @JsonProperty("image")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RECIPE_ID", referencedColumnName = "ID")
    private Set<ImageEntity> imageEntity;

}
