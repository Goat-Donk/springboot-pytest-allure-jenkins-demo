package com.example.demo.user.model;

public class User {

    private Long id;
    private String username;
    private String email;
    private Integer age;
    private String city;

    public User() {
    }

    public User(Long id, String username, String email, Integer age, String city) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
