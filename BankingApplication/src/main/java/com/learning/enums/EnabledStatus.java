package com.learning.enums;

/**
 * Enum signifying whether an account or user is enabled or disabled.
 * @author Dionel Olo
 * @since Mar 7, 2022
 */
public enum EnabledStatus {
	STATUS_ENABLED {
		public String toString() {
			return "Enabled";
		}
	},
	STATUS_DISABLED {
		public String toString() {
			return "Disabled";
		}
	};
}
