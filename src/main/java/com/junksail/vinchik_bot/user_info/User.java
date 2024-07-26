package com.junksail.vinchik_bot.user_info;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "user_info")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private long chatId;

    @Column(name = "gender")
    private String gender;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private String age;

    @Column(name = "description")
    private String description;

    @Column(name = "city")
    private String city;

    @Column(name = "photo_url")
    private String photoURL;

    public User(String gender, String name, String age, String description, String city, String photoURL) {
        this.gender = gender;
        this.name = name;
        this.age = age;
        this.description = description;
        this.city = city;
        this.photoURL = photoURL;
    }
}
