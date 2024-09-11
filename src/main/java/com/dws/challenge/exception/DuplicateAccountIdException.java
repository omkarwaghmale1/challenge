package com.dws.challenge.exception;

import javax.security.auth.login.AccountNotFoundException;

public class DuplicateAccountIdException extends RuntimeException {

  public DuplicateAccountIdException(String message) {
    super(message);
  }

}
