/**
 * -----------------------------------------------------------------------------------------------------
 * Runner.java
 * -----------------------------------------------------------------------------------------------------
 * This program serves as an automated currency conversion system that 
 * processes one or more transactions involving one or more users via the following:
 * -----------------------------------------------------------------------------------------------------
 * 1. The program validates at least one transaction.
 * Every transaction has four components:
 * 1.1. The user's name.
 * 1.2. The currency to be converted from (fromCurrency).
 * 1.3. The currency to be converted to (toCurrency).
 * 1.4. The amount of the fromCurrency to be converted to the toCurrency.
 * -----------------------------------------------------------------------------------------------------
 * 2. A transaction is either valid or invalid.
 * Every invalid transaction is skipped, whilst a currency conversion occurs for a valid transaction.
 * after which th the currencies and amounts in the user's wallet are updated.
 * -----------------------------------------------------------------------------------------------------
 * 3. After every transaction, a message will be displayed in the console and stored in a logger.
 * The message displayed indicates either one of the following:
 * 3.1. A valid transaction, and the amount and currencies involved in the conversion.
 * 3.2. An invalid transaction, and the reason that the transaction was skipped.
 * -----------------------------------------------------------------------------------------------------
 * @author Sheikh Umar
 * -----------------------------------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Currency;
import model.User;
import exceptions.InsufficientAmountForConversionException;
import exceptions.InvalidAmountException;
import exceptions.InvalidCurrencyException;
import exceptions.InvalidNumberOfComponentsException;
import exceptions.SameCurrencyException;
import exceptions.UserHasNoCurrencyException;
import exceptions.UserNotFoundException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Runner {
	
	/**
	 * Constant variables named according to the files it is accessing
	 */
	private static final String FX_RATES_FILE = "src/main/resources/fx_rates.json";
	private static final String TRANSACTIONS_FILE = "src/main/resources/transactions.txt";
	private static final String USERS_FILE = "src/main/resources/users.json";
	
	/**
	 * Logger to log message on validity of every transaction
	 */
	private static final Logger logger = LogManager.getLogger(Runner.class);
	
	/**
	 * List of users from Users.json
	 */
	public static List <User> users = new ArrayList <User> ();
	
	/**
	 * List of currency and it's respective currency object from fx_rate.json
	 */
	public static Map <String, Currency> currencies = new HashMap <String, Currency> ();
	
	/**
	 * Currency conversion
	 * 
	 * @param currencyType 	The type of currency involving USD.
	 * @param currency 		The currency to apply conversion on.
	 * @param amount 		The amount involved in the conversion.
	 * @return 				The amount of currency converted to USD
	 */
	private static double conversionInvolvingUsd(String currencyType,
												 String currency,
												 double amount) {

        if (currencyType.equals("convert to USD")) {
			return amount * currencies.get(currency).getInverseRate();
		}
		
		return amount * currencies.get(currency).getRate();
	}
	
	
	/**
	 * Convert the user's fromCurrency to the toCurrency.
	 * And update the values of the currencies in the user's wallet.
	 * 
	 * @param user The user involved in the conversion, currency to be converted from, and currency to be converted to (toCurrency),
	 * and the amount for conversion.
	 * @throws StreamReadException if there is an error reading the JSON stream.
	 * @throws DatabindException if there is an error binding the JSON data to the object model.
	 * @throws IOException if there is an error reading or writing to the file system.
	 */
	public static void currencyConversion(User user,
										  String fromCurrency,
										  String toCurrency,
										  double amountToConvert) throws StreamReadException, DatabindException, IOException {
		
		DecimalFormat df = new DecimalFormat("#.##");
		double amountToIncreaseToCurrencyBy = 0;
		
		// 1. Conversion of currency
		
		// 1.1. toCurrency is in usd
		if (toCurrency.equals("usd")) {
			
			// 1.1.1. Get usd equivalent of amountToConvert
			amountToIncreaseToCurrencyBy = conversionInvolvingUsd("convert to USD", fromCurrency, amountToConvert);
			
		} else if (fromCurrency.equals("usd")) {
			// 1.2. fromCurrency is in usd
			
			// 1.2.1. Compute the equivalent of the fromCurrency in usd
			amountToIncreaseToCurrencyBy = conversionInvolvingUsd("convert from USD", toCurrency, amountToConvert);
			
		} else {
			// 1.3. Both fromCurrency and toCurrency are not usd
			
			// 1.3.1. Get usd equivalent of amountToConvert
			double amountToConvertInUsd = conversionInvolvingUsd("convert to USD", fromCurrency, amountToConvert);
			
			// 1.3.2. Get toCurrency equivalent of amountToConvertInUsd
			amountToIncreaseToCurrencyBy = conversionInvolvingUsd("convert from USD", toCurrency, amountToConvertInUsd);		
		}
		
		// 2. Update the toCurrency and fromCurrency values in the user's wallet
		user.updatesWallet(fromCurrency, toCurrency, amountToConvert, amountToIncreaseToCurrencyBy);
		
		// 3. Display message to indicate conversion of currencies has been successfully completed
		logger.info("Valid Transaction: Success! Converted " + fromCurrency + df.format(amountToConvert) + " to " + toCurrency + df.format(amountToIncreaseToCurrencyBy) + " for " + user.getName() + ".");
		
		// 4. Update user's profile in users.json with updated values and currencies in wallet
		serialization();
	}
	
	/**
	 * Checks if a user has enough value in the FROM currency for conversion.
	 * 
	 * @param user 				The user involved in the currency conversion.
	 * @param fromCurrency 		The currency to be converted from.
	 * @param amountToConvert 	The amount of currency to be converted from.
	 * @throws InsufficientAmountForConversionException if the amount for conversion is more than the amount of the FROM currency in the user's wallet.
	 */
	public static void isSufficientAmountForConversion(User user,
													   String fromCurrency,
													   double amountToConvert) throws InsufficientAmountForConversionException {
		if (user.getCurrencyValueInWallet(fromCurrency) < amountToConvert) {
			throw new InsufficientAmountForConversionException();
		}
		
	}

	/**
	 * Checks if a user has a currency to be converted from (fromCurrency) in his/her wallet.
	 * 
	 * @param user 				The user involved in the transaction.
	 * @param fromCurrency 		The convert to convert from.
	 * @throws UserHasNoCurrencyException if the user does not have the FROM currency in his/her wallet
	 */
	public static void doesUserHaveCurrency(User user, String fromCurrency) throws UserHasNoCurrencyException {
		
		if (!user.isCurrencyInWallet(fromCurrency)) {
			throw new UserHasNoCurrencyException();
		}
		
	}

	/**
	 * Checks if an amount to be converted is valid.
	 * 
	 * @params amount to be converted from the for currency to the to currency.
	 * @throws InvalidAmountException if the amount to convert is less than or equal to 0.
	 */
	public static void isValidAmount(double amountToConvert) throws InvalidAmountException {
		
		if (amountToConvert <= 0) {
			throw new InvalidAmountException();
		}
		
	}

	/**
	 * Checks if a currency exists in the forex exchange (fx_rates.json).
	 * 
	 * @param currency provided in the transaction.
	 * @throws InvalidCurrencyException if the currency provided does not exist.
	 */
	public static void isValidCurrency(String currency) throws InvalidCurrencyException {
        if (!currency.equals("usd") && !currencies.containsKey(currency)) {
			throw new InvalidCurrencyException();
		}
	}

	/**
	 * Checks if two currencies are the same.
	 * 
	 * @param toCurrency 	The currency to be converted to.
	 * @param fromCurrency 	The currency to be converted from.
	 * @throws SameCurrencyException if the 2 currencies provided for conversion are the same.
	 */
	public static void isSameCurrency(String toCurrency, String fromCurrency) throws SameCurrencyException {
		if (toCurrency.equals(fromCurrency)) {
			throw new SameCurrencyException();
		}
	}

	/**
	 * Check if the user exists
	 * 
	 * @param name of user
	 * @throws UserNotFoundException if the user cannot be found
	 */
	public static User getsUser(String name) throws UserNotFoundException {
        for (User currentUser : users) {
            if (currentUser.getName().equals(name)) {
                return currentUser;
            }
        }
		throw new UserNotFoundException();
	}
	
	/**
	 * Checks that a transaction has 4 components.
	 * 
	 * @param transaction An array containing the components of a transaction.
	 * @throws InvalidNumberOfComponentsException if the transaction does not have 4 components.
	 */
	private static void isValidTransaction(String[] transaction) throws InvalidNumberOfComponentsException {
		if (transaction.length != 4) {
			throw new InvalidNumberOfComponentsException();
		}
	}
	
	/**
	 * Execution of the serialization for users.json after a valid transaction.
	 * 
	 * @throws StreamReadException if there is an error reading the JSON stream
	 * @throws DatabindException if there is an error binding the JSON data to the object model
	 * @throws IOException if there is an error reading or writing to the file system
	 */
	private static void serialization() throws StreamWriteException, DatabindException, IOException {
		File destination = new File(USERS_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(destination, users);
	}
	
	/**
	 * Execution of deserialization of fx_rates.json and users.json before processing transactions.
	 * 
	 * @throws StreamReadException if there is an error reading the JSON stream
	 * @throws DatabindException if there is an error binding the JSON data to the object model
	 * @throws IOException if there is an error reading or writing to the file system
	 */
	private static void deserialization() throws StreamReadException, DatabindException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		/*
		 * Extract every entry in users.json,
		 * parse it into a User object,
		 * and add it to the users list
		 */
		users = objectMapper.readValue(
					new File(USERS_FILE),
					objectMapper.getTypeFactory().constructCollectionType(List.class, User.class)
				);
		
		/*
		 * Extract every entry in fx_rates.json,
		 * form a Currency object, 
		 * and add it to the currencies hashmap,
		 * where the key-value mappings are currencyCode-Currency object
		 */
		currencies = objectMapper.readValue(
				new File(FX_RATES_FILE),
	            objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Currency.class)
	    );
	}
	
	/**
	 * Processes every transaction.
	 * 
	 * @throws DatabindException if there is an error binding the JSON data to the object model
	 * @throws IOException if there is an error reading or writing to the file system
	 * @throws NumberFormatException if the string cannot be parsed to a double
	 * @throws SameCurrencyException if the 2 currencies provided for conversion are the same
	 * @throws InvalidCurrencyException if the currency provided does not exist
	 * @throws InvalidAmountException if the amount to convert is less than or equal to 0
	 * @throws InvalidNumberOfComponentsException if the transaction line does not have 4 components
	 * @throws UserHasNoCurrencyException if the user does not have the FROM currency in his/her wallet
	 * @throws InsufficientAmountForConversionException if the amount for conversion is more than the amount of the FROM currency in the user's wallet
	 */
	public static void main(String[] args) throws
            DatabindException,
													IOException,
													NumberFormatException,
            SameCurrencyException,
													InvalidCurrencyException,
													InvalidAmountException,
													InvalidNumberOfComponentsException,
													UserHasNoCurrencyException,
													InsufficientAmountForConversionException {
		double amount = 0;
		String username = null;
		String transaction = null;
		String fromCurrency = null;
		String toCurrency = null;
		InputStream inputStream = new FileInputStream(TRANSACTIONS_FILE);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);	
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        
        logger.info("Starting application...");
        while ((transaction = bufferedReader.readLine()) != null) {
			
			/*
			 * Using try-with-resources ensure the resources are automatically closed
			 * after usage. This prevents resource leaks.
			 */
			try {    
				
					/*
					 * 1. Deserialize the users.json and fx_rates.json files
					 * to populate the users list and currencies map respectively.
					 */
					deserialization();
					
					/*
					 * 2. Read every line in the transaction
					 * and determine if it is a valid or invalid transaction.
					 * Finally, logging is carried out for every transaction
					 * both on the console and in a log file.
					 */
					String[] transactionComponents = transaction.split(" ");
					isValidTransaction(transactionComponents);
					username = transactionComponents[0];
					User user = getsUser(username);
					fromCurrency = transactionComponents[1];
					toCurrency = transactionComponents[2];
					isSameCurrency(toCurrency, fromCurrency);
					isValidCurrency(fromCurrency);
					isValidCurrency(toCurrency);
					amount = Double.parseDouble(transactionComponents[3]);
					isValidAmount(amount);
					doesUserHaveCurrency(user, fromCurrency);
					isSufficientAmountForConversion(user, fromCurrency, amount);
					currencyConversion(user, fromCurrency, toCurrency, amount);
					
	        } catch (InsufficientAmountForConversionException e) {

                logger.error("Skipped Transaction: {} has insufficient amount of {} (FROM currency).", username, fromCurrency);
	        	
	        } catch (UserHasNoCurrencyException e) {

                logger.error("Skipped Transaction: {} does not have {} (FROM currency).", username, fromCurrency);
	        	
	        } catch (InvalidNumberOfComponentsException e) {
	        	
	        	logger.error("Skipped Transaction: Transaction does not have exactly 4 components as required.");
	        	
	        } catch (InvalidCurrencyException e) {
	        	
	        	logger.error("Skipped Transaction: One or both of the currencies is invalid.");
	        	
	        } catch (InvalidAmountException e) {
	        	
	        	logger.error("Skipped Transaction: Amount to convert is less than or equal to 0.");
	        	
	        } catch (UserNotFoundException e) {

                logger.error("Skipped Transaction: User called {} not found.", username);
	        	
	        } catch (SameCurrencyException e) {
	        	
	        	logger.error("Skipped Transaction: Both the FROM and TO currencies are the same.");	
	            
	        } catch (NumberFormatException e) {
	        	
	        	logger.error("Unable to parse string to a double for the amount of conversion.");
	            
	        } catch (JsonProcessingException e) {

	        	logger.fatal("Unable to parse the JSON file.");

			} catch (NullPointerException | IOException e) {

				logger.fatal("Unable to access the transactions.txt file.");

	        }
		}
		bufferedReader.close();
		inputStream.close();
		inputStreamReader.close();
		logger.info("All transactions have been processed, and users.json has been updated for valid transactions.");
		logger.info("Shutting down application...");
    }

}
