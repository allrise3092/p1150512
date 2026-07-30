package com.example.p1150512.entity;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.*;

@Entity
@Table(name = "person_info")
public class PersonInfo {

    @Id
    @Column(name = "id", length = 45)
    private String id;

    @Column(name = "user_name", length = 20)
    private String userName;

    @Column(name = "user_age")
    private int userAge;
    /*@JsonAlias: 是 Jackson 專門用來處理「一對多別名匹配」的註解<br>
     * 當你傳入的 JSON 欄位是 "userCity" 時，JPA 實體的 city 會被賦值<br>
     * 當你傳入的 JSON 欄位是預設的 "city" 時，它依然能被正常匹配賦值
     * */
    @JsonAlias({"userCity"})
    @Column(name = "city", length = 45)
    private String city;

    public PersonInfo() {}

    public PersonInfo(String id, String userName, int userAge, String city) {
        this.id = id;
        this.userName = userName;
        this.userAge = userAge;
        this.city = city;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getUserAge() { return userAge; }
    public void setUserAge(int userAge) { this.userAge = userAge; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}