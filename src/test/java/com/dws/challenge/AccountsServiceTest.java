package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.BalanceException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  void transferAmount() {
    Account account1 = new Account("129",BigDecimal.valueOf(1000));
    this.accountsService.createAccount(account1);
    Account account2 = new Account("124",BigDecimal.valueOf(1000));
    this.accountsService.createAccount(account2);
    this.accountsService.transferAmount("129","124",BigDecimal.valueOf(500));

    assertThat(this.accountsService.getAccount("124").getBalance()).isEqualTo(BigDecimal.valueOf(1500));
    assertThat(this.accountsService.getAccount("129").getBalance()).isEqualTo(BigDecimal.valueOf(500));
  }

  @Test()
  void transferAmount_InsufficientBalance() {
    Account account1 = new Account("Id-127",BigDecimal.valueOf(1000));
    this.accountsService.createAccount(account1);
    Account account2 = new Account("Id-124",BigDecimal.valueOf(1000));
    this.accountsService.createAccount(account2);

    try {
      this.accountsService.transferAmount("Id-127","Id-124",BigDecimal.valueOf(2000));
      fail("Insufficient balance");
    } catch (BalanceException ex) {
      assertThat(ex.getMessage()).isEqualTo("Insufficient balance");
    }
  }

  @Test()
  void transferAmount_NegetiveAmount() {

    try {
      this.accountsService.transferAmount("Id-21","id-22",BigDecimal.valueOf(-20));
      fail("Transfer amount must be positive");
    } catch (BalanceException ex) {
      assertThat(ex.getMessage()).isEqualTo("Transfer amount must be positive");
    }
  }

  @Test()
  void transferAmount_AccountNotFound() {

    try {
      this.accountsService.transferAmount("778","779",BigDecimal.valueOf(500));
      fail("Account not found");
    } catch (AccountNotFoundException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account not found");
    }
  }

  @Test()
  void transferAmount_AccountNotBlank() {

    try {
      this.accountsService.transferAmount(""," ",BigDecimal.valueOf(500));
      fail("Account id is blank");
    } catch (AccountNotFoundException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id is blank");
    }
  }
}
