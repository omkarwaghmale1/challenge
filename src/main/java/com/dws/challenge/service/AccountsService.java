package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.BalanceException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
      this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public void transferAmount(String accountFromId,String accountToId, BigDecimal amount) {

    if (amount.intValue() < 0) {
      throw new BalanceException("Transfer amount must be positive");
    }
    Account accountTo = checkAccountId(accountToId);
    Account accountFrom = checkAccountId(accountFromId);
    BigDecimal balance = getAccount(accountFromId).getBalance();
    if(balance.compareTo(amount) < 0) {
      throw new BalanceException("Insufficient balance");
    }
    accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
    sendNotificationToSender(accountTo,amount,accountToId);
    accountTo.setBalance(accountTo.getBalance().add(amount));
    sendNotificationToReceiver(accountTo,amount,accountFromId);
  }

  private void sendNotificationToSender(Account account, BigDecimal amount, String accountToId) {
    // Send notification to receiver
    this.notificationService.notifyAboutTransfer(account,
            "Transferred " + amount + " to account " + accountToId);
  }

  private void sendNotificationToReceiver(Account account, BigDecimal amount, String accountFromId) {
    // Send notification to sender
    notificationService.notifyAboutTransfer(account,
            "Received " + amount + " from account " + accountFromId);
  }

  private Account checkAccountId(String accountId) {
    // check account present in database
    if(accountId.isBlank()){
      throw new AccountNotFoundException("Account id is blank");
    }
    Account account = getAccount(accountId);
    if(account == null) {
      throw new AccountNotFoundException("Account not found");
    }
    return  account;
  }
}
