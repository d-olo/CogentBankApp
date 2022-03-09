package com.learning.exception;

/**
 * Exception thrown when an enum is not present in the database.
 * @author Dionel Olo
 * @since Mar 8, 2022
 */
public class EnumNotFoundException extends RuntimeException {

	public EnumNotFoundException(String msg) {
		super(msg);
	}
	
	@Override
	public String toString() {
		return super.getMessage();
	}
	
}
