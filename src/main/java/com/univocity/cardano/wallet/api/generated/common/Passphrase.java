package com.univocity.cardano.wallet.api.generated.common;


import com.fasterxml.jackson.annotation.*;


/**
 * Information about the wallet's passphrase
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Passphrase {


	@JsonProperty("last_updated_at")
	private String lastUpdatedAt;

	/**
	 * Returns the last updated at.
	 * - Format: {@code iso-8601-date-and-time}.
	 * 
	 * - Example: 
	 *   <pre>{@code Thu Feb 28 01:16:45 ACDT 2019}</pre>
	 * 
	 * @return the last updated at
	 */
	public String getLastUpdatedAt(){
		return lastUpdatedAt;
	}

	/**
	 * Defines the last updated at.
	 * - Format: {@code iso-8601-date-and-time}.
	 * 
	 * - Example: 
	 *   <pre>{@code Thu Feb 28 01:16:45 ACDT 2019}</pre>
	 * 
	 * @param lastUpdatedAt the last updated at
	 */
	public void setLastUpdatedAt(String lastUpdatedAt){
		if (lastUpdatedAt == null) {
			throw new IllegalArgumentException("Value of lastUpdatedAt cannot be null");
		}

		this.lastUpdatedAt = lastUpdatedAt;
	}
}
