package com.example.p1150512.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.p1150512.entity.Atm;

public interface AtmDao extends JpaRepository<Atm, String> {
    // 繼承 JpaRepository 就有 save、findById、existsById 等基本方法，暫不需要額外自訂
}