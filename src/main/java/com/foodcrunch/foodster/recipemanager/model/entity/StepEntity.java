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
@Table(name = "STEP")
public class StepEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COOKSTEP_SEQ")
    @SequenceGenerator(name = "COOKSTEP_SEQ", sequenceName = "COOKSTEP_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "STEP_NUMBER", nullable = false)
    private int stepNumber;

    @Column(name = "STEP", nullable = false)
    private String step;

    @JsonProperty("pic")
    @Column(name = "IMAGE_REFERENCE")
    private String image;

}
