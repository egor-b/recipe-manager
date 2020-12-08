package com.foodcrunch.foodster.recipemanager.auth.model;

import com.foodcrunch.foodster.recipemanager.auth.model.enumaration.Role;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Data
@Entity
@Table(name = "ROLE", schema = "USERS")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "NAME")
    @Enumerated(EnumType.STRING)
    private Role name;

    @ManyToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<UserEntity> user;

}
