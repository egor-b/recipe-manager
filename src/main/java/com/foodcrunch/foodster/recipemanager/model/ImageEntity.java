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
@Table(name = "IMAGE")
public class ImageEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMAGE_SEQ")
    @SequenceGenerator(name = "IMAGE_SEQ", sequenceName = "IMAGE_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "IMAGE_LINK", nullable = false)
    private String _link;

    @JsonIgnore
    public long getId() { return id; }
}
