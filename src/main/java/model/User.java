/**
 * ----------------------------------------------------------------------------------------
 * User.java
 * ----------------------------------------------------------------------------------------
 * A User entity comprises of a name and a wallet.
 * ----------------------------------------------------------------------------------------
 * The wallet contains mappings of currency to the value of the currency.
 * Example: A user has SGD100. Hence, the user's wallet SGD100 in this format: SGD-100,
 * where the key is the currency, and the value of the value of 100.
 * ----------------------------------------------------------------------------------------
 * @author Sheikh Umar
 * ----------------------------------------------------------------------------------------
 */

package model;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {

	/**
	 * A user has these attributes:
	 * a name in the form of a string,
	 * and a wallet to store at least 1 currency and it's value.
	 */
	private String name;
	private Map <String, Double> wallet;
	
	/**
	 * Creation of a user based on user's name.
	 * 
	 * @param name The user's name.
	 */
	public User(String name) {
		this.name = name;
		this.wallet = new HashMap <String, Double> ();
	}
	
	/**
	 * Default no-args constructor for Jackson Deserialisation.
	 */
	public User() {
		
	}
	
	/**
	 * Retrieves the user's name.
	 * 
	 * @return the user's name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the user's wallet.
	 * 
	 * @return the user's wallet.
	 */
	public Map<String, Double> getWallet() {
		return this.wallet;
	}
	
	/**
	 * Setters for Jackson Deserialisation.
	 *
	 * @param name The user's name.
	 */
	public void setName(String name) {
		
		this.name = name;
	}
	
	/**
	 * Setters for Jackson Deserialisation.
	 * 
	 * @param wallet a {@code HashMap} where the key is a {@code String} representing the currency code,
	 * and the value is a {@code Double} representing the amount of that currency.
	 */
	
	public void setWallet(HashMap <String, Double> wallet) {
		this.wallet = wallet;
	}

	/**
	 * Stores a currency and value of that currency into the user's wallet
	 * when a valid transaction can be made.
	 * 
	 * @param currency 	A currency to be added to the user's waller.
	 * @param amount  	The amount of this currency to be reflected in the user's wallet
	 */
	public void addCurrencyToWallet(String currency, double amount) {
		this.wallet.put(currency, amount);
	}
	
	/**
	 * Increases a currency's value in the user's wallet.
	 * This happens when a transaction is successful.
	 *
	 * @param currency A currency in the user's wallet.
	 * @param amountOfIncrease The amount to increase the value of the currency in the user's wallet.
	 */
	public void increaseCurrencyValueInWallet(String currency, double amountOfIncrease) {
		DecimalFormat df = new DecimalFormat("#.##");
		String newAmount = df.format(this.wallet.getOrDefault(currency, 0.0) + amountOfIncrease);
		this.wallet.put(currency, Double.valueOf(newAmount));
	}

	/**
	 * Increases a currency's value in the user's wallet.
	 * This happens when a transaction is successful.
	 *
	 * @param currency A currency in the user's wallet.
	 * @param amountOfDecrease The amount to decrease the value of the currency in the user's wallet.
	 */
	public void decreaseCurrencyValueInWallet(String currency, double amountOfDecrease) {
		DecimalFormat df = new DecimalFormat("#.##");
		if (this.wallet.containsKey(currency)) {
			double currentAmount = this.wallet.get(currency);
			String newAmount = df.format(currentAmount - amountOfDecrease);
			this.wallet.put(currency, Double.valueOf(newAmount));
		}
	}
	
	/**
	 * Checks if a currency's value in the user's wallet is 0.
	 * If yes, remove this currency from the user's wallet because it is of no value.
	 * 
	 * @param currency The fromCurrency after a successful transaction.
	 * @return True if the value of the currency in the user's wallet is 0, false otherwise.
	 */
	public boolean isValueOfCurrencyInWalletEqualToZero(String currency) {
		return this.wallet.get(currency) == 0;
	}
	
	/**
	 * Removes a currency from the wallet if it's value is equal to 0.
	 * 
	 * @param currency The currency to remove from the user's wallet.
	 */
	public void removesCurrencyWithValueOfZero(String currency) {
		if (this.wallet.get(currency) == 0) {
			this.wallet.remove(currency);
		}
	}
	
	/**
	 * Retrieves the current value of a currency in the User's wallet during a transaction.
	 * 
	 * @param currency A currency involved in a transaction.
	 * @return value of currency in the user's wallet.
	 * @thrown NullPointerException if currency is not in the user's wallet.
	 */
	public double getCurrencyValueInWallet(String currency) {
		if (this.wallet.containsKey(currency)) {
			return this.wallet.get(currency);
		} throw new NullPointerException(currency + " is not in " + this.name + "'s wallet");
	}

	/**
	 * Checks if a specified currency exists in the user's wallet.
	 *
	 * @param currency the currency code to check for in the wallet (e.g., "USD").
	 * @return {@code true} if the currency exists in the user's wallet, {@code false} otherwise.
	 */
	public boolean isCurrencyInWallet(String currency) {
		
		return wallet.containsKey(currency);
	}
	
	/**
	 * Update user's wallet after a valid transaction
	 * 
	 * @param fromCurrency 					The currency to be converted from.
	 * @param toCurrency 					The currency to be converted toto be converted to.
	 * @param amountToConvert   			The amount of currency to be converted from
	 * @param amountToIncreaseToCurrencyBy  The and amount to increase currency to be converted to.
	 */
	public void updatesWallet(String fromCurrency,
							  String toCurrency,
							  double amountToConvert,
							  double amountToIncreaseToCurrencyBy) {
		// 1. Increase value of toCurrency
		this.increaseCurrencyValueInWallet(toCurrency, amountToIncreaseToCurrencyBy);
		
		// 2. Decrease value of fromCurrency
		this.decreaseCurrencyValueInWallet(fromCurrency, amountToConvert);
		
		// 3. If the value of the fromCurrency in the user's wallet is zero, remove it from the user's wallet 
		if (this.isValueOfCurrencyInWalletEqualToZero(fromCurrency)) {
			removesCurrencyWithValueOfZero(fromCurrency);
		}
	}
	
	/**
	 * Retrieves number of currencies in a user's wallet.
	 * 
	 * @return -1 if user's wallet is null, else a non-negative number.
	 */
	@JsonIgnore
	public int getsNumberOfCurrenciesInWallet() {
		return this.wallet == null ? -1 : this.wallet.size();
	}
	
}
