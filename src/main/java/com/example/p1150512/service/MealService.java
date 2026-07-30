package com.example.p1150512.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.p1150512.dao.MealDao;
import com.example.p1150512.entity.Meal;

import org.springframework.transaction.annotation.Transactional;

/**
* @Service: 是用來標記業務邏輯層（Business Logic Layer / Service 層）組件的註解<br>
* 加在類別上方時，Spring 主要會幫你處理以下三件核心事務：<br>
* 1. 自動註冊為 Spring Bean（納入 IoC 容器託管）<br>
* 2. 標記商業邏輯的「集中地」<br>
* 3. 事務管理（Transaction）切入點<br>
* Transaction 的使用時機:<br>
* 1. 一個方法中，有使用到多個不同的 Dao 時 --> 跨表 新增/修改/刪除 資料
* 2. 一個方法中，有多次使用同一個 Dao 時(迴圈) --> 一次新增/修改/刪除 多筆資料<br>
* 結論就是 新增/修改/刪除 的資料，要嘛全部成功，要嘛全部不成功
* */
@Service
public abstract class MealService {
	/* @Autowired: 把被 spring 託管的物件(MealDao)注入/引入到容器(變數 mealDao)中，<br>
	 * 如果要求注入的物件是沒有被託管的，系統啟動或使用到該物件時就會報錯<br>
	 * 使用的物件其宣告方式就等同於一個類別中屬性的宣告，格式如下: <br>
	 * 被存取權限 物件名稱(類別/介面) 變數名稱 */




	@Autowired
   private MealDao mealDao;
   /**
    * 新增單筆餐點的商業邏輯（含防呆驗證）
    * * @param name  餐點名稱
    * @param price 餐點價格
    * @return String 提示訊息（成功或失敗原因）
    */
   @Transactional // 將事務管理加在 Service 層
   public String addMeal(String name, int price) {
      
       // ===== 參數防呆驗證 (Input Validation) =====
       // 使用排除法 --> 先將參數一一檢查，只要不符合規定，直接跳出方法
       // 1. 檢查名稱是否為空
   	// 一定要先檢查類別是否是 Null，不然該物件是 Null時，null 使用點(.)去呼叫方法時會
   	// 報錯(NullPointerException，空指針例外)
//        if (name == null || name.trim().isEmpty()) {
//            return "新增失敗：餐點名稱不能為空字串或 NULL！";
//        }
   	// 上面這段可用下面的程式碼來替代
       // StringUtils.hasText(name): 同時判斷　name 不是 null 以及非全空白字串
       // !StringUtils.hasText(name): 前面有驚嘆號，等同於 StringUtils.hasText(name) == false
       if(!StringUtils.hasText(name)) {
       	return "新增失敗：餐點名稱不能為空字串或 NULL！";
       }
      
       // 2. 檢查名稱長度（對應資料庫的 length = 100）
       if (name.length() > 100) {
           return "新增失敗：餐點名稱長度不能超過 100 個字！";
       }
      
       // 3. 檢查價格是否合理（對應資料庫的 nullable = false 且符合商業邏輯）
       if (price < 0) {
           return "新增失敗：餐點價格不能為負數！";
       }
       // ===== 執行資料庫操作 =====
       try {
           // 呼叫 Dao 層的 nativeQuery 語法
           int rowsAffected = mealDao.insertMeal(name.trim(), price);
           // 只有新增一筆，結果要嘛等於1，要嘛報錯(因為新增的語法是寫 insert into，有相同PK就會報錯)
           // 有用 try-catch 將程式碼包住，報錯的話會被下面的 catch 抓取
           if (rowsAffected == 1) {
               return "「" + name + "」餐點新增成功！";
           } else {
               return "餐點新增失敗！";
           }


       } catch (Exception e) {
           // 捕捉可能發生的資料庫異常（例如：違反唯一主鍵，餐點名稱(欄位 name 的值)重複了）
           // 注意：如果拋出 RuntimeException，@Transactional 會自動觸發回滾
           return "新增失敗：資料庫發生錯誤（可能餐點名稱已重複）。" + e.getMessage();
       }
   }






/**
* @SpringBootTest: 在執行測試方法時，會先啟動一個完整的 Spring 容器，掃描所有需要被託管的物件並
* 載入到 Spring 容器中託管，所以可以使用 @Autowired 將被託管的物件引入並使用
* */




/**
* @Service: 是用來標記業務邏輯層（Business Logic Layer / Service 層）組件的註解<br>
* 加在類別上方時，Spring 主要會幫你處理以下三件核心事務：<br>
* 1. 自動註冊為 Spring Bean（納入 IoC 容器託管）<br>
* 2. 標記商業邏輯的「集中地」<br>
* 3. 事務管理（Transaction）切入點<br>
* Transaction 的使用時機: 新增/修改/刪除 資料，
*  事務的重點就是 新增/修改/刪除 的資料，要嘛全部(不管多少筆資料)成功，要嘛全部不成功
* */

/* rollbackFor = Exception.class: 擴大事務的回溯範圍，確保不論程式發生『任何種類』的錯誤或異常，資料庫都會安全地回復原狀（Rollback）<br>
    * @Transactional 預設資料回溯的例外是 RuntimeException，為了確保不管發生哪種例外，資料都會全部回溯到尚未變更之前
    * */
   @Transactional(rollbackFor = Exception.class)

/* ?1: 位置參數佔位符，其作用就是「挖一個空位，等著把 Java 方法傳進來的變數填進去」。
	 * Optional: 一樣是個容器，主要是強迫使用者要去判斷得到的結果是否為 null*/


	@Query(value = "select * from meal where name = ?1", nativeQuery = true)
   public Optional<Meal> findByName(String name) {
	    // 這裡會變成空的，你需要自己寫內容
	    return null; 
	}
@Transactional(rollbackFor = Exception.class)
   public String updateByName(String name, int newPrice) {
   	// 檢查參數
       // 防呆 1：檢查名稱是否為空
       if (!StringUtils.hasText(name)) {
           return "修改失敗：餐點名稱不能為空！";
       }
       // 防呆 2：檢查新價格是否合理
       if (newPrice <= 0) {
           return "修改失敗：價格不能小於等於 0 元！";
       }
       // 執行修改: 不需要先檢查資料是否存在，可根據 updatePriceByName 的回傳結果(0 或 大於 0) 來確定資料是否更新成功
       int rowsAffected = mealDao.updatePriceByName(name.trim(), newPrice);
       if (rowsAffected > 0) {
           return "「" + name + "」的價格已成功修改為 " + newPrice + " 元！";
       } else {
           return "修改失敗：資料庫未受影響。";
       }
   }

/**
    * 🎯 2. 查詢特定餐點價格
    * @return 價格，若找不到餐點則回傳 -1（用作錯誤訊號）
    */
   @Transactional(readOnly = true) // 唯讀事務，可以優化查詢效能
   public int getPrice(String name) {
       // 防呆：名稱不能為空
       if (!StringUtils.hasText(name)) {
           return -1;
       }
       Optional<Meal> mealOpt = mealDao.findByName(name.trim());
       // 如果有找到，回傳價格；找不到則回傳 -1
       // Meal::getPrice 等同於 meal -> meal.getPrice()
       return mealOpt.map(Meal::getPrice).orElse(-1);
   }
   /**
    * 🎯 3. 查詢所有餐點
    */
   @Transactional(readOnly = true)
   public List<Meal> getAllMeals() {
       // 直接呼叫 JpaRepository 內建的 findAll() 即可，不需手動寫 SQL
       return mealDao.findAll();
   }


/**
    * 一次新增多筆餐點（全有或全無原則）
    * * @param mealList 要新增的餐點列表
    * @return 執行結果訊息
    */
   @Transactional(rollbackFor = Exception.class) // 🎯 關鍵：只要任何一筆失敗，整批都會回滾！
   public String addMeals(List<Meal> mealList) {
      
       // 1. 基本防呆：檢查傳入的 List 是否為空
//        if (mealList == null || mealList.isEmpty()) {
//            return "新增失敗：請提供至少一筆餐點資料！";
//        }
   	// CollectionUtils.isEmpty: 可以同時判斷 一個集合是否為 null 或者是 空集合
       if (CollectionUtils.isEmpty(mealList)) {
           return "新增失敗：請提供至少一筆餐點資料！";
       }
       // 2. 逐筆進行參數防呆驗證
       // 採用「先防呆，後寫入」的策略，避免寫入到一半才發現後面有壞資料
       for (int i = 0; i < mealList.size(); i++) {
           Meal meal = mealList.get(i);
           String name = meal.getName();
           int price = meal.getPrice();
           int index = i + 1; // 用於提示是第幾筆資料出錯
           // 檢查名稱是否為空
           if (!StringUtils.hasText(name)) {
               throw new IllegalArgumentException("第 " + index + " 筆資料新增失敗：餐點名稱不能為空字串或 NULL！");
           }
          
           // 檢查名稱長度
           if (name.length() > 100) {
               throw new IllegalArgumentException("第 " + index + " 筆資料新增失敗：餐點名稱「" + name + "」長度不能超過 100 個字！");
           }
          
           // 檢查價格是否合理
           if (price <= 0) {
               throw new IllegalArgumentException("第 " + index + " 筆資料「" + name + "」新增失敗：餐點價格不能小於或等於 0 元！");
           }
       }
       // 3. 執行資料庫批次寫入
       try {
       	// Spring data jpa 的 save():
       	// PK 已存在 --> update
       	// PK 不存在 --> insert
//        	mealDao.saveAll(mealList);
           int totalInserted = 0;
           // 使用 for 迴圈 一筆一筆新增
           for (Meal meal : mealList) {
               // 呼叫原本 Dao 的單筆新增方法
               int rowsAffected = mealDao.insertMeal(meal.getName().trim(), meal.getPrice());
              
               if (rowsAffected == 1) {
                   totalInserted++;
               } else {
                   // 如果沒有新增成功，手動拋出異常以觸發回滾
                   throw new RuntimeException("新增餐點「" + meal.getName() + "」時資料庫未受影響！");
               }
           }
           return "成功新增 " + totalInserted + " 筆餐點！";
          
       } catch (Exception e) {
           // 💡 關鍵：
           // 由於加了 @Transactional，這裡一旦發生任何異常（不論是 SQL 重複主鍵還是我們手動 throw 的錯誤），
           // 資料庫都會把前面已經新增成功的資料「全部撤銷（Rollback）」，確保一筆都不會殘留在資料庫。
          
           // 重新往外拋出異常，讓 Spring 偵測到並執行 Rollback
           throw new RuntimeException("批次新增失敗，已全部回滾！錯誤原因：" + e.getMessage(), e);
       }
   }



@Query(value = "select * from meal where name in (?1)", nativeQuery = true)
	public abstract List<Meal> findAllByNameList(List<String> nameList);




/**
    * 一次新增多筆餐點（全有或全無，且預先檢查 PK 唯一性）
    */
   @Transactional(rollbackFor = Exception.class)
   public String addMealList(List<Meal> mealList) {
      
       // ===== 🛑 階段一：基本格式驗證 =====
       if (CollectionUtils.isEmpty(mealList)) {
           return "新增失敗：請提供至少一筆餐點資料！";
       }
       // 用來收集所有預備新增的餐點名稱(PK)
       List<String> newNames = new ArrayList<>();
       for (int i = 0; i < mealList.size(); i++) {
           Meal meal = mealList.get(i);
           String name = meal.getName();
           int price = meal.getPrice();
           int index = i + 1;
           if (!StringUtils.hasText(name)) {
               return "第 " + index + " 筆資料驗證失敗：餐點名稱不能為空！";
           }
           if (name.length() > 100) {
               return "第 " + index + " 筆資料驗證失敗：名稱「" + name + "」長度不能超過 100 個字！";
           }
           if (price <= 0) {
               return "第 " + index + " 筆資料驗證失敗：「" + name + "」價格不能小於或等於 0 元！";
           }
           // 將經過去除空白處理的名稱存入 List 中
           newNames.add(name.trim());
       }
       // ===== 🛑 階段二：預防性檢查 (PK 衝突預檢) =====
      
       // 1. 檢查傳入的清單本身「內部」有沒有重複的名稱（例如傳入兩個「滷肉飯」）
       long distinctCount = newNames.stream().distinct().count();
       if (distinctCount < newNames.size()) {
           return "新增失敗：您上傳的餐點清單中，含有重複的餐點名稱！";
       }
       // 2. 與資料庫比對所有的 PK 是否都不存在，使用 in
       List<Meal> existingMeals = mealDao.findByNameIn(newNames);
      
       // 如果查出來的結果不為空，代表有餐點已經存在了！
       if (!existingMeals.isEmpty()) {
           // 收集所有已存在的餐點名稱，用逗號隔開呈現在錯誤訊息中
           String duplicateNames = existingMeals.stream()
                   .map(Meal::getName)
                   .collect(Collectors.joining("、"));
          
           return "新增失敗：餐點「" + duplicateNames + "」已存在於系統中，請勿重複新增！";
       }
       // ===== 💾 階段三：安全寫入資料庫 =====
       try {
           // 此時我們百分之百確信：
           // 1. 格式皆正確
           // 2. 清單內無重複
           // 3. 資料庫中無重複
           // 呼叫 saveAll() 將會非常安全且順利地全部走 INSERT 寫入！
           List<Meal> savedMeals = mealDao.saveAll(mealList);
           return "成功新增 " + savedMeals.size() + " 筆餐點！";
          
       } catch (Exception e) {
           // 雖然理論上前面都檢查過了，但為了防止極端高併發情況（例如兩個人同一毫秒送出相同請求），
           // 這裡依然加上 try-catch 作為最後防線，並藉由拋出例外觸發 @Transactional 的 Rollback
           throw new RuntimeException("寫入資料庫時發生未知錯誤，已整批回滾。詳細原因：" + e.getMessage(), e);
       }
   }
   }




