package com.learning.enums;

/**
 * Enum for an account's type.
 * @author Dionel Olo
 * @since Mar 7, 2022
 */
public enum AccountType {
	ACCOUNT_SAVINGS {
		public String toString() {
			return "Savings Account";
		}
	},
	ACCOUNT_CURRENT {
		public String toString() {
			return "Current Account";
		}
	}
}
