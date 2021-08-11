package com.zivlazarov.test.client.db;

import javax.persistence.*;

@Entity
public class TestEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private String name;

    @Column
    private String email;

    public TestEntity() {}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
