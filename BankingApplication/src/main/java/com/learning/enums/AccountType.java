package com.learning.enums;

/**
 * Enum for an account's type.
 * @author Dionel Olo
 * @since Mar 7, 2022
 */
public enum AccountType {
	SAVINGS {
		public String toString() {
			return "Savings Account";
		}
	},
	CURRENT {
		public String toString() {
			return "Current Account";
		}
	}
}
