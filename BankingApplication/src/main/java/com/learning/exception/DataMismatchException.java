package com.learning.exception;

public class DataMismatchException extends RuntimeException {
	public DataMismatchException(String e) {
		super(e);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
