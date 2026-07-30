package com.example.p1150512.entity;

import java.io.Serializable;
import java.util.Objects;

public class NewMealId implements Serializable {
    private String name;
    private String cookingStyle;

    public NewMealId() {}

    public NewMealId(String name, String cookingStyle) {
        this.name = name;
        this.cookingStyle = cookingStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewMealId)) return false;
        NewMealId that = (NewMealId) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(cookingStyle, that.cookingStyle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cookingStyle);
    }
}