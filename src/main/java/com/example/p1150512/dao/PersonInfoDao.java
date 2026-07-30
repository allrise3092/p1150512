package com.example.p1150512.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.p1150512.entity.PersonInfo;

public interface PersonInfoDao extends JpaRepository<PersonInfo, String> {

    // 1. 依 id 清單查詢是否已存在（用來檢查 PK 重複）
    @Query(value = "select * from person_info where id in (:ids)", nativeQuery = true)
    List<PersonInfo> findAllByIdsNative(@Param("ids") List<String> ids);

    // 2. 新增單筆
    @Modifying
    @Query(value = "insert into person_info (id, user_name, user_age, city) values (:id, :userName, :userAge, :city)", nativeQuery = true)
    int insertPersonNative(@Param("id") String id, @Param("userName") String userName,
                            @Param("userAge") int userAge, @Param("city") String city);

    // 3. 依 id 更新
    @Modifying
    @Query(value = "update person_info set user_name = :userName, user_age = :userAge, city = :city where id = :id", nativeQuery = true)
    int updatePersonNative(@Param("id") String id, @Param("userName") String userName,
                           @Param("userAge") int userAge, @Param("city") String city);

    // 4. 取得全部
    @Query(value = "select * from person_info", nativeQuery = true)
    List<PersonInfo> findAllPersonsNative();

    // 5. 依 id 查詢單筆
    @Query(value = "select * from person_info where id = :id", nativeQuery = true)
    Optional<PersonInfo> findByIdNative(@Param("id") String id);

    // 6. 依年齡大於篩選
    @Query(value = "select * from person_info where user_age > :age", nativeQuery = true)
    List<PersonInfo> findByAgeGreaterThanNative(@Param("age") int age);
}