package com.foodcrunch.foodster.recipemanager.auth.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "USER", schema = "USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "UID", nullable = false, unique = true)
    private String uid;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "LNAME", nullable = false)
    private String lName;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "PIC")
    private String pic;

    @Column(name = "ACCOUNT_TYPE")
    private String accountType;

    @Column(name = "IS_DISABLE", nullable = false)
    private boolean isDisable = false;

    @Column(name = "VIOLATION", nullable = false)
    private Integer violation = 0;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "USER_ROLE", schema = "USERS",
//            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
//            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
//    private Set<RoleEntity> role;

}
