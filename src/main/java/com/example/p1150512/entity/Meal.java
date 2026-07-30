package com.example.p1150512.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
/**
* @: 註解（Annotations）
* @Entity: 告訴 JPA 框架這個 Java 類別不是普通的類別，它對應到資料庫的一張資料表<br>
* 			只要加上這個註解，JPA 就會開始管理這個類別的生命週期，並允許你使用 Dao(Repository) 進行資料庫的 CRUD（增刪查改）操作<br>
* @Table: 用來指定這個 Entity 要映射(mapping)到資料庫的哪一張特定資料表<br>
* name ="meal":字串 meal 就是資料表的名稱
* */
@Entity
@Table(name = "meal")
public class Meal {
	/*
	 * @Id: 宣告該屬性（欄位）是這張資料表的主鍵 (PK)；
	 *      在 JPA 中，任何一個 @Entity 類別都強制一定要有一個屬性加上 @Id，否則專案啟動時會直接報錯
	 * @Column: 用來定義 Java 屬性與資料庫資料表欄位之間的映射細節與約束條件
	 * name = "name"，等號後面的字串 name 是欄位名稱，指的是要將該屬性對應到哪個欄位
	 * length = 100 限制字串的最大長度
	 * */
   @Id
   @Column(name = "name", length = 100)
   private String name;
   /*
    * nullable = false，防止 Java 型態轉換時引發 NullPointerException，資料庫的預設值是 NULL，
    * 但這邊的資料型態是 int(基本資寮型態沒null)
    * */
   @Column(name = "price", nullable = false)
   private int price;
   // --- 建構子 (Constructor) ---
 /*  public Meal() {
       // JPA 規範要求必須有一個無參數的建構子
   }
   public Meal(String name, int price) {
       this.name = name;
       this.price = price;
   */
   // --- Getters and Setters ---
   public String getName() {
       return name;
   }
   public void setName(String name) {
       this.name = name;
   }
   public int getPrice() {
       return price;
   }
   public void setPrice(int price) {
       this.price = price;
   }
}
