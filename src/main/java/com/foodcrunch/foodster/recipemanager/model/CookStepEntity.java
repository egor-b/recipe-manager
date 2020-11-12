package com.foodcrunch.foodster.recipemanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "COOKSTEP")
public class CookStepEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COOKSTEP_SEQ")
    @SequenceGenerator(name = "COOKSTEP_SEQ", sequenceName = "COOKSTEP_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "STEP_NUMBER", nullable = false)
    private int stepId;
    @Column(name = "STEP", nullable = false)
    private String step;

    @Column(name = "IMAGE_LINK")
    private String _link;

    @JsonIgnore
    public long getId() { return id; }
    public void setImage(String _link) { this._link = _link; } //will need to think about it
}
