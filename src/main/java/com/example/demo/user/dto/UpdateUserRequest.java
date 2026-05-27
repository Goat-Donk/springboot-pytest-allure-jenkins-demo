package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class UpdateUserRequest {

    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "email format is invalid")
    private String email;

    @Min(value = 1, message = "age must be greater than 0")
    @Max(value = 120, message = "age must be less than or equal to 120")
    private Integer age;

    @NotBlank(message = "city cannot be blank")
    private String city;

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
