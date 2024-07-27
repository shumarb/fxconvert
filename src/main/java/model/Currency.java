/**
 * --------------------------------------------------------------------------------
 * Currency.java
 * --------------------------------------------------------------------------------
 * A Currency entity comprises of the name of the currency, and the 8 attributes below.
 * Every currency object corresponds to the format of the currency information
 * stored in the fx_rates.json file
 * --------------------------------------------------------------------------------
 * @author Sheikh Umar
 * --------------------------------------------------------------------------------
 */

package model;

public class Currency {

	/**
	 * A currency's acronym.
	 */
	private String acronym;
	
	/**
	 * A currency's code.
	 */
	private String code;
	
	/**
	 * A currency's alphacode.
	 */
	private String alphaCode;
	
	/**
	 * A currency's numeric code.
	 */
	private String numericCode;
	
	/**
	 * A currency's name.
	 */
	private String name;
	
	/**
	 * A currency's rate.
	 */
	private double rate;
	
	/**
	 * A currency's date.
	 */
	private String date;
	
	/**
	 * A currency's inverse rate.
	 */
	private double inverseRate;
	
	/**
	 * Non-empty constructor for testing.
	 */
	public Currency(double rate, double inverseRate, String... strValues) {
		this.rate = rate;
		this.inverseRate = inverseRate;
		for (int i = 0; i < strValues.length; i++) {
			switch(i) {
				case 0: 
					this.acronym = strValues[0]; 
					break;
				case 1: 
					this.code = strValues[1]; 
					break;
				case 2: 
					this.alphaCode = strValues[2]; 
					break;
				case 3: 
					this.numericCode = strValues[3];
					break;
				case 4: 
					this.name = strValues[4];
					break;
				case 5: 
					this.date = strValues[5];
					break;
			}
		}
	}
	
	/**
	 * Default no-args constructor for ObjectMapper to function properly.
	 */
	public Currency() {
		
	}
	
	/**
	 * Retrieves a currency's name.
	 * 
	 * @return currency's name.
	 */
	public String getAcronym() {
		return acronym;
	}
	
	/**
	 * Retrieves a currency's code.
	 * 
	 * @return currency's code.
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Retrieves a currency's code.
	 * 
	 * @return currency's code.
	 */
	public String getAlphaCode() {
		return alphaCode;
	}
	
	/**
	 * Retrieves a currency's numeric code.
	 * 
	 * @return currency's numeric code.
	 */
	public String getNumericCode() {
		return numericCode;
	}
	
	/**
	 * Retrieves a currency's name.
	 * 
	 * @return currency's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieves a currency's rate of exchange
	 * against the US Dollar (usd).
	 * 
	 * @return currency's currency's rate of exchange
	 * against the US Dollar (in this program, it is denoted as usd).
	 */
	public double getRate() {
		return rate;
	}
	
	/**
	 * Retrieves the date that the currency's rate of exchange
	 * against the US Dollar (usd) was recorded.
	 * 
	 * @return the date that currency's rate of exchange
	 * against the US Dollar (usd) was recorded.
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * Retrieves a currency's inverse rate of exchange
	 * against the US Dollar (usd).
	 * 
	 * @return currency's currency's inverse rate of exchange
	 * against the US Dollar (usd).
	 */
	public double getInverseRate() {
		return inverseRate;
	}

	/**
	 * Sets the currency's acronym.
	 * 
	 * @param acronym The currency's acronym.
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * Sets the currency's code.
	 * 
	 * @param code The currency's code.
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Sets the currency's alpha code.
	 * 
	 * @param alphaCode The currency's alpha code.
	 */
	public void setAlphaCode(String alphaCode) {
		this.alphaCode = alphaCode;
	}
	
	/**
	 * Sets the currency's numeric code.
	 * 
	 * @param numericCode The currency's numeric code.
	 */
	public void setNumericCode(String numericCode) {
		this.numericCode = numericCode;
	}

	/**
	 * Sets the currency's name.
	 * 
	 * @param name The currency's name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the currency's rate of exchange 
	 * against the US dollar (in this program, it is denoted as usd).
	 *
	 * 
	 * @param rate The currency's rate.
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}

	/**
	 * Sets the currency's date.
	 * 
	 * @param date The currency's date.
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Sets the currency's inverse rate of exchange.
	 * 
	 * @param inverseRate The currency's inverse rate of exchange
	 * against the US Dollar (in this program, it is denoted as usd)
	 */
	public void setInverseRate(double inverseRate) {
		this.inverseRate = inverseRate;
	}
	
}
