package com.example.p1150512.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "new_meal")
@IdClass(NewMealId.class)// 宣告複合主鍵類別: 讓 Entity 知道多個有加 @Id 的屬性
// 被集中到哪個類別管理

public class NewMeal {

    @Id
    @Column(name = "name",length = 25)
    private String name;

    @Id
    @Column(name = "cooking_style",length = 50)
    private String cookingStyle;

    @Column(name = "price", nullable = false)
    private int price;

    public NewMeal() {}

    public NewMeal(String name, String cookingStyle, int price) {
        this.name = name;
        this.cookingStyle = cookingStyle;
        this.price = price;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCookingStyle() { return cookingStyle; }
    public void setCookingStyle(String cookingStyle) { this.cookingStyle = cookingStyle; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}