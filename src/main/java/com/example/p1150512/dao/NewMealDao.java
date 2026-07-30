package com.example.p1150512.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.p1150512.entity.NewMeal;
import com.example.p1150512.entity.NewMealId;

public interface NewMealDao extends JpaRepository<NewMeal, NewMealId> {
    // 題目要求：不須定義任何方法
}