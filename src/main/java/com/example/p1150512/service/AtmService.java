package com.example.p1150512.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.p1150512.dao.AtmDao;
import com.example.p1150512.entity.Atm;

@Service
public class AtmService {

    private static final String ACCOUNT_PATTERN = "^[a-zA-Z0-9_]{3,8}$";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9_]{8,16}$";

    @Autowired
    private AtmDao atmDao;

    /**
     * 新增資訊：帳號密碼防呆檢查，只能新增（不能覆蓋已存在的帳號）
     */
    @Transactional(rollbackFor = Exception.class)
    public String addInfo(String account, String password) {

        // 檢查帳號
        if (account == null || account.trim().isEmpty()) {
            return "【❌ 新增失敗】帳號不能為空或空白字串！";
        }
        if (!account.matches(ACCOUNT_PATTERN)) {
            return "【❌ 新增失敗】帳號格式錯誤：需為大小寫英文、數字或底線，長度 3~8 碼！";
        }

        // 檢查密碼
        if (password == null || password.trim().isEmpty()) {
            return "【❌ 新增失敗】密碼不能為空或空白字串！";
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            return "【❌ 新增失敗】密碼格式錯誤：需為大小寫英文、數字或底線，長度 8~16 碼！";
        }

        // 只能新增：檢查帳號是否已存在
        if (atmDao.existsById(account)) {
            return "【❌ 新增失敗】帳號「" + account + "」已存在，不可重複新增！";
        }

        Atm atm = new Atm(account, password, 0);
        atmDao.save(atm);
        return "【🟢 新增成功】帳號「" + account + "」已成功建立，初始餘額為 0！";
    }

    /**
     * 透過帳號取得餘額（需檢查帳號和密碼，回傳不能含 password）
     */
    @Transactional(readOnly = true)
    public Object getBalanceByAccount(String account, String password) {

        String checkResult = checkAccountAndPassword(account, password);
        if (checkResult != null) {
            return checkResult;
        }

        Atm atm = atmDao.findById(account).get();
        return new AtmBalanceResponse(atm.getAccount(), atm.getBalance());
    }

    /**
     * 修改密碼（需檢查帳號和密碼）
     */
    @Transactional(rollbackFor = Exception.class)
    public String updatePasswordByAccount(String account, String oldPassword, String newPassword) {

        String checkResult = checkAccountAndPassword(account, oldPassword);
        if (checkResult != null) {
            return checkResult;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return "【❌ 修改失敗】新密碼不能為空或空白字串！";
        }
        if (!newPassword.matches(PASSWORD_PATTERN)) {
            return "【❌ 修改失敗】新密碼格式錯誤：需為大小寫英文、數字或底線，長度 8~16 碼！";
        }

        Atm atm = atmDao.findById(account).get();
        atm.setPassword(newPassword);
        atmDao.save(atm);

        return "【🟢 修改成功】帳號「" + account + "」的密碼已成功更新！";
    }

    /**
     * 存款
     */
    @Transactional(rollbackFor = Exception.class)
    public String deposit(String account, String password, int amount) {

        String checkResult = checkAccountAndPassword(account, password);
        if (checkResult != null) {
            return checkResult;
        }

        if (amount <= 0) {
            return "【❌ 存款失敗】存款金額必須大於 0！";
        }

        Atm atm = atmDao.findById(account).get();
        int newBalance = atm.getBalance() + amount;
        atm.setBalance(newBalance);
        atmDao.save(atm);

        return "【🟢 存款成功】帳號「" + account + "」存入 " + amount + " 元，目前餘額為 " + newBalance + " 元！";
    }

    /**
     * 提款
     */
    @Transactional(rollbackFor = Exception.class)
    public String withdraw(String account, String password, int amount) {

        String checkResult = checkAccountAndPassword(account, password);
        if (checkResult != null) {
            return checkResult;
        }

        if (amount <= 0) {
            return "【❌ 提款失敗】提款金額必須大於 0！";
        }

        Atm atm = atmDao.findById(account).get();

        if (atm.getBalance() < amount) {
            return "【❌ 提款失敗】餘額不足！目前餘額為 " + atm.getBalance() + " 元，無法提領 " + amount + " 元。";
        }

        int newBalance = atm.getBalance() - amount;
        atm.setBalance(newBalance);
        atmDao.save(atm);

        return "【🟢 提款成功】帳號「" + account + "」提領 " + amount + " 元，目前餘額為 " + newBalance + " 元！";
    }

    /**
     * 共用：檢查帳號是否存在、密碼是否正確
     * 回傳 null 表示檢查通過；回傳字串表示錯誤訊息
     */
    private String checkAccountAndPassword(String account, String password) {
        if (account == null || account.trim().isEmpty()) {
            return "【❌ 操作失敗】帳號不能為空！";
        }
        Optional<Atm> atmOpt = atmDao.findById(account);
        if (!atmOpt.isPresent()) {
            return "【❌ 操作失敗】查無此帳號「" + account + "」！";
        }
        Atm atm = atmOpt.get();
        if (password == null || !password.equals(atm.getPassword())) {
            return "【❌ 操作失敗】密碼錯誤！";
        }
        return null;
    }

    /**
     * 回傳給前端的餘額查詢結果（不含 password 欄位）
     */
    public static class AtmBalanceResponse {
        private String account;
        private int balance;

        public AtmBalanceResponse(String account, int balance) {
            this.account = account;
            this.balance = balance;
        }

        public String getAccount() { return account; }
        public int getBalance() { return balance; }
    }
}