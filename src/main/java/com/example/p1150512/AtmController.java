package com.example.p1150512;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.p1150512.service.AtmService;

@RestController
@RequestMapping("/api/atm")
public class AtmController {

    @Autowired
    private AtmService atmService;

    /**
     * 1. 新增資訊
     * POST http://localhost:8080/api/atm
     */
    @PostMapping
    public ResponseEntity<String> addInfo(@RequestParam("account") String account, @RequestParam("password") String password) {
        String result = atmService.addInfo(account, password);
        if (result.contains("❌")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 2. 透過帳號取得餘額
     * GET http://localhost:8080/api/atm/balance?account=abc123&password=abc12345
     */
    @GetMapping("/balance")
    public ResponseEntity<Object> getBalanceByAccount(@RequestParam("account") String account, @RequestParam("password") String password) {
        Object result = atmService.getBalanceByAccount(account, password);
        if (result instanceof String) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 3. 修改密碼
     * PUT http://localhost:8080/api/atm/password
     */
    @PutMapping("/password")
    public ResponseEntity<String> updatePasswordByAccount(@RequestParam("account") String account,
                                                           @RequestParam("oldPassword") String oldPassword,
                                                           @RequestParam("newPassword") String newPassword) {
        String result = atmService.updatePasswordByAccount(account, oldPassword, newPassword);
        if (result.contains("❌")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 4. 存款
     * PUT http://localhost:8080/api/atm/deposit
     */
    @PutMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestParam("account") String account,
                                           @RequestParam("password") String password,
                                           @RequestParam("amount") int amount) {
        String result = atmService.deposit(account, password, amount);
        if (result.contains("❌")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 5. 提款
     * PUT http://localhost:8080/api/atm/withdraw
     */
    @PutMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam("account") String account,
                                            @RequestParam("password") String password,
                                            @RequestParam("amount") int amount) {
        String result = atmService.withdraw(account, password, amount);
        if (result.contains("❌")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}