package com.learning.exception;

public class AccountDisabledException extends RuntimeException {
	public AccountDisabledException(String e) {
		super(e);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
