package com.example.p1150512.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.p1150512.dao.PersonInfoDao;
import com.example.p1150512.entity.PersonInfo;

@Service
public class PersonInfoService {

	private String pattern = "^[a-zA-Z].*";
	@Autowired
   private PersonInfoDao personInfoDao;
	
	/**
    * 1. 創建資訊 (支援 1~多筆，印出創建結果，失敗則印出原因並回滾)
    */
   @Transactional(rollbackFor = Exception.class)
   public void createPersons(List<PersonInfo> personList) {
      
       // ===== 🛑 格式與基本防呆 =====
       if (CollectionUtils.isEmpty(personList)) {
           System.out.println("【❌ 創建失敗】請提供至少一筆個人資訊！");
           return; // void 方法直接用 return 中斷
       }
       // 用來蒐集 personList 中的所有 id，因為這個方法規定只能新增，所以要檢查新增所有 id 是否已存在
       List<String> inputIds = new ArrayList<>();
       // 1.參數檢查 2. 蒐集 id
       for (int i = 0; i < personList.size(); i++) {
           PersonInfo person = personList.get(i);
           int index = i + 1;
           if (!StringUtils.hasText(person.getId())) {
               System.out.println("【❌ 驗證失敗】第 " + index + " 筆資料的 ID 不能為空！");
               return;
           }
          
           String trimmedId = person.getId().trim();
           person.setId(trimmedId); // 回寫去除空白的 ID
           // ID 必須以英文字母開頭
           if (!trimmedId.matches(pattern)) {
               System.out.println("【❌ 驗證失敗】第 " + index + " 筆資料的 ID「" + trimmedId + "」必須以英文字母開頭！");
               return;
           }
           if (!StringUtils.hasText(person.getUserName())) {
               System.out.println("【❌ 驗證失敗】第 " + index + " 筆資料的使用者名稱不能為空！");
               return;
           }
           if (person.getUserAge() < 0) {
               System.out.println("【❌ 驗證失敗】第 " + index + " 筆資料的年齡不能為負數！");
               return;
           }
           // 蒐集 ID，先檢查 id 是否有重複
           if(inputIds.contains(trimmedId)) {
           	System.out.println("【❌ 創建失敗】您提交的清單中，含有重複的 ID: " + trimmedId);
               return;
           }
           inputIds.add(trimmedId);
       }
       // ===== 🛑 資料庫「一次性批次 PK 查詢」 =====
       List<PersonInfo> existingPersons = personInfoDao.findAllByIdsNative(inputIds);
       // 若得到的結果不是空 --> 表示要新增的 id 已存在
       if (!existingPersons.isEmpty()) {
           System.out.print("【❌ 創建失敗】以下 ID 已存在於系統中，不可重複新增：");
           existingPersons.forEach(p -> System.out.print("[" + p.getId() + "] "));
           System.out.println();
           return;
       }
       // ===== 💾 執行資料庫原生 SQL 新增 =====
       try {
           for (PersonInfo person : personList) {
               personInfoDao.insertPersonNative(
                   person.getId(),
                   person.getUserName().trim(),
                   person.getUserAge(),
                   person.getCity() != null ? person.getCity().trim() : null
               );
           }
           // 上面 for 迴圈中的 新增資料，可以使用 Jpa 的 saveAll
       	//personInfoDao.saveAll(personList);
          
           // 成功後直接印出結果
           System.out.println("【🟢 創建成功】已成功新增 " + personList.size() + " 筆個人資訊：");
           for (PersonInfo person : personList) {
               System.out.println("  ➡️ ID: " + person.getId() + ", 姓名: " + person.getUserName() + ", 年齡: " + person.getUserAge() + ", 城市: " + person.getCity());
           }
          
       } catch (Exception e) {
           System.out.println("【🔥 系統錯誤】寫入資料庫時發生異常，已整批撤銷回滾！錯誤原因：" + e.getMessage());
           // ⚠️ 雖然是 void 方法，但為了讓 @Transactional 觸發回滾，必須 throw 異常
           throw new RuntimeException(e);
       }
   }
  
   /**
    * 2. 更新資訊 (依據 ID 修改，直接印出更新結果)
    * @throws Exception
    */
   @Transactional(rollbackFor = Exception.class)
   public void updatePerson(PersonInfo person) throws Exception {
      
       // ===== 🛑 參數防呆 =====
   	// 以下2種方法擇1使用
   	// 方法1
   	String checkResult = checkParam(person);
   	if(checkResult != null) {
   		return;
   	}
   	// 方法2
   	checkParam2(person);
       // ===== 💾 執行更新 =====
       try {
           int rowsAffected = personInfoDao.updatePersonNative(
               person.getId().trim(),
               person.getUserName().trim(),
               person.getUserAge(),
               person.getCity() != null ? person.getCity().trim() : null
           );
           if (rowsAffected == 1) {
               System.out.println("【🟢 更新成功】ID 為「" + person.getId() + "」的資訊已順利更新！");
           } else {
               System.out.println("【❌ 更新失敗】系統中找不到 ID 為「" + person.getId() + "」的資料！");
           }
       } catch (Exception e) {
           System.out.println("【🔥 系統錯誤】更新資料庫時發生異常，操作已撤銷。原因：" + e.getMessage());
           throw new Exception(e);
       }
   }
  
   // 把參數檢查放在一個方法中，一般都是私有方法
   // 回傳的資料型態:
   // 1. 是 void --> 方法本身以及呼叫的方法要有 try-catch
   // 2. 非 void --> 呼叫的方法可以根據得到的結果來判斷程式是否繼續往下執行
   private String checkParam(PersonInfo person) {
   	 if (person == null || !StringUtils.hasText(person.getId())) {
            System.out.println("【❌ 更新失敗】無效的請求資料或遺失 ID！");
            return "【❌ 更新失敗】無效的請求資料或遺失 ID！";
        }
        if (!StringUtils.hasText(person.getUserName())) {
            System.out.println("【❌ 更新失敗】使用者名稱不能為空！");
            return "【❌ 更新失敗】使用者名稱不能為空！";
        }
        if (person.getUserAge() < 0) {
            System.out.println("【❌ 更新失敗】年齡不能為負數！");
            return "【❌ 更新失敗】年齡不能為負數！";
        }
        // 參數檢查都沒問題時
        return null;
   }
  
   // 回傳資料型態是 void，要把不符合條件的參數檢查當成例外拋出
   private void checkParam2(PersonInfo person) throws Exception {
  	 if (person == null || !StringUtils.hasText(person.getId())) {
           System.out.println("【❌ 更新失敗】無效的請求資料或遺失 ID！");
           throw new Exception("【❌ 更新失敗】無效的請求資料或遺失 ID！");
       }
       if (!StringUtils.hasText(person.getUserName())) {
           System.out.println("【❌ 更新失敗】使用者名稱不能為空！");
           throw new Exception("【❌ 更新失敗】使用者名稱不能為空！");
       }
       if (person.getUserAge() < 0) {
           System.out.println("【❌ 更新失敗】年齡不能為負數！");
           throw new Exception("【❌ 更新失敗】年齡不能為負數！");
       }
  }
   /**
    * 3. 取得所有資訊 (印出全部資料)
    */
   @Transactional(readOnly = true)
   public void getAllPersons() {
       List<PersonInfo> list = personInfoDao.findAllPersonsNative();
      
       System.out.println("==================================================");
       System.out.println("【🔍 查詢結果】全系統個人資訊列表如下：");
       if (list.isEmpty()) {
           System.out.println("（目前資料庫中沒有任何個人資訊）");
       } else {
           list.forEach(p -> System.out.println("  ▪️ ID: " + p.getId() + " | 姓名: " + p.getUserName() + " | 年齡: " + p.getUserAge() + " | 城市: " + p.getCity()));
       }
       System.out.println("==================================================");
   }
   /**
    * 4. 透過 id 取得對應的個人資訊 (直接印出單筆資料)
    */
   @Transactional(readOnly = true)
   public void getPersonById(String id) {
       if (!StringUtils.hasText(id)) {
           System.out.println("【❌ 查詢失敗】請輸入有效的 ID！");
           return;
       }
       Optional<PersonInfo> personOpt = personInfoDao.findByIdNative(id.trim());
      
       if (personOpt.isPresent()) {
           PersonInfo p = personOpt.get();
           System.out.println("【🟢 查詢成功】已找到 ID 為「" + id + "」的個人資訊：");
           System.out.println("  ➡️ 姓名: " + p.getUserName() + " | 年齡: " + p.getUserAge() + " | 城市: " + p.getCity());
       } else {
           System.out.println("【❌ 查詢失敗】找不到 ID 為「" + id + "」的個人資訊。");
       }
   }
   /**
    * 5. 找出年紀大於輸入條件的所有資訊 (印出符合條件的列表)
    */
   @Transactional(readOnly = true)
   public void getPersonsOlderThan(int age) {
       if (age < 0) {
           System.out.println("【❌ 查詢失敗】年齡門檻不能為負數！");
           return;
       }
       List<PersonInfo> list = personInfoDao.findByAgeGreaterThanNative(age);
       System.out.println("==================================================");
       System.out.println("【🔍 篩選結果】年齡大於 " + age + " 歲的名單如下：");
       if (list.isEmpty()) {
           System.out.println("（沒有符合年齡大於 " + age + " 歲的資料）");
       } else {
           list.forEach(p -> System.out.println("  ▪️ ID: " + p.getId() + " | 姓名: " + p.getUserName() + " | 年齡: " + p.getUserAge() + "歲 | 城市: " + p.getCity()));
       }
       System.out.println("==================================================");
   }

}