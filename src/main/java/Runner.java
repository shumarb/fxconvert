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
 * after which the currencies and amounts in the user's wallet are updated.
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
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
	 * Constant variables named according to the files it is accessing.
	 */
	private static final String FX_RATES_FILE = "src/main/resources/fx_rates.json";
	private static final String TRANSACTIONS_FILE = "src/main/resources/transactions.txt";
	private static final String USERS_FILE = "src/main/resources/users.json";
	
	/**
	 * Logger to log message on validity of every transaction.
	 */
	private static final Logger logger = LogManager.getLogger(Runner.class);
	
	/**
	 * List of users from Users.json.
	 */
	public static List <User> users = new ArrayList <> ();
	
	/**
	 * List of currency, and it's respective currency object from fx_rate.json.
	 */
	public static Map <String, Currency> currencies = new HashMap <> ();
	
	/**
	 * Currency conversion involving USD.
	 * 
	 * @param currencyType 	The type of currency involving USD.
	 * @param currency 		The currency to apply conversion on.
	 * @param amount 		The amount involved in the conversion.
	 * @return 				The amount of currency converted to USD.
	 */
	private static double conversionInvolvingUsd(String currencyType, String currency, double amount) {
        if (currencyType.equals("convert to USD")) {
			return amount * currencies.get(currency).getInverseRate();
		}
		return amount * currencies.get(currency).getRate();
	}

	/**
	 * Convert the user's fromCurrency to the toCurrency, and update the values of the currencies in the user's wallet.
	 * 
	 * @param user 					The user involved in the conversion.
	 * @param  fromCurrency 		The currency to be converted from.
	 * @param toCurrency   			The currency to be converted to.
	 * @param amount 				The amount for conversion.
	 * @throws StreamReadException 	The exception thrown if there is an error reading the JSON stream.
	 * @throws DatabindException 	The exception thrown if there is an error binding the JSON data to the object model.
	 * @throws IOException 			The exception thrown if there is an error reading or writing to the file system.
	 */
	public static void currencyConversion(User user, String fromCurrency, String toCurrency, double amount) throws StreamReadException, DatabindException, IOException {
		DecimalFormat df = new DecimalFormat("#.##");
		double amountToIncreaseToCurrencyBy;
		
		if (toCurrency.equals("usd")) {
			amountToIncreaseToCurrencyBy = conversionInvolvingUsd("convert to USD", fromCurrency, amount);

		} else if (fromCurrency.equals("usd")) {
			amountToIncreaseToCurrencyBy = conversionInvolvingUsd("convert from USD", toCurrency, amount);

		} else {
			double amountToConvertInUsd = conversionInvolvingUsd("convert to USD", fromCurrency, amount);
			amountToIncreaseToCurrencyBy = conversionInvolvingUsd("convert from USD", toCurrency, amountToConvertInUsd);
		}

		user.updatesWallet(fromCurrency, toCurrency, amount, amountToIncreaseToCurrencyBy);
        logger.info("Valid Transaction: Success! Converted {}{} to {}{} for {}.", fromCurrency, df.format(amount), toCurrency, df.format(amountToIncreaseToCurrencyBy), user.getName());
		
		// Update user's profile in users.json with updated values and currencies in wallet.
		serialization();
	}
	
	/**
	 * Checks if a user has enough value in the FROM currency for conversion.
	 * 
	 * @param user 										The user involved in the currency conversion.
	 * @param fromCurrency 								The currency to be converted from.
	 * @param amountToConvert 							The amount of currency to be converted from.
	 * @throws InsufficientAmountForConversionException The exception thrown if the amount for conversion is more than the amount of the FROM currency in the user's wallet.
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
	 * @param user 							The user involved in the transaction.
	 * @param fromCurrency 					The convert to convert from.
	 * @throws UserHasNoCurrencyException 	The exception thrown if the user does not have the FROM currency in his/her wallet.
	 */
	public static void doesUserHaveCurrency(User user, String fromCurrency) throws UserHasNoCurrencyException {
		if (!user.isCurrencyInWallet(fromCurrency)) {
			throw new UserHasNoCurrencyException();
		}
	}

	/**
	 * Checks if an amount to be converted is valid.
	 * 
	 * @param amountToConvert 			The amount involved in a conversion.
	 * @throws InvalidAmountException 	The exception thrown if the amount to convert is less than or equal to 0.
	 */
	public static void isValidAmount(double amountToConvert) throws InvalidAmountException {
		if (amountToConvert <= 0) {
			throw new InvalidAmountException();
		}
	}

	/**
	 * Checks if a currency exists in the forex exchange (fx_rates.json).
	 * 
	 * @param currency 						The currency provided in the transaction.
	 * @throws InvalidCurrencyException 	The exception thrown if the currency provided does not exist.
	 */
	public static void isValidCurrency(String currency) throws InvalidCurrencyException {
        if (!currency.equals("usd") && !currencies.containsKey(currency)) {
			throw new InvalidCurrencyException();
		}
	}

	/**
	 * Checks if two currencies are the same.
	 * 
	 * @param toCurrency 				The currency to be converted to.
	 * @param fromCurrency 				The currency to be converted from.
	 * @throws SameCurrencyException 	The exception thrown if the 2 currencies provided for conversion are the same.
	 */
	public static void isSameCurrency(String toCurrency, String fromCurrency) throws SameCurrencyException {
		if (toCurrency.equals(fromCurrency)) {
			throw new SameCurrencyException();
		}
	}

	/**
	 * Check if the user exists.
	 * 
	 * @param name 						The name of the user.
	 * @throws UserNotFoundException 	The exception thrown if the user cannot be found.
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
	 * @param transaction 							An array containing the components of a transaction.
	 * @throws InvalidNumberOfComponentsException 	The exception thrown if the transaction does not have 4 components.
	 */
	private static void isValidTransaction(String[] transaction) throws InvalidNumberOfComponentsException {
		if (transaction.length != 4) {
			throw new InvalidNumberOfComponentsException();
		}
	}
	
	/**
	 * Execution of the serialization for users.json after a valid transaction.
	 * 
	 * @throws DatabindException 	The exception thrown if there is an error binding the JSON data to the object model.
	 * @throws IOException 			The exception thrown if there is an error reading or writing to the file system.
	 */
	private static void serialization() throws DatabindException, IOException {
		File destination = new File(USERS_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(destination, users);
	}
	
	/**
	 * Execution of deserialization of fx_rates.json and users.json before processing transactions.
	 * 
	 * @throws StreamReadException 	The exception thrown if there is an error reading the JSON stream.
	 * @throws DatabindException 	The exception thrown if there is an error binding the JSON data to the object model.
	 * @throws IOException 			The exception thrown if there is an error reading or writing to the file system.
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
	 * @throws DatabindException 		Exception thrown if there is an error binding the JSON data to the object model.
	 * @throws IOException 				Exception thrown if there is an error reading or writing to the file system.
	 * @throws NumberFormatException 	Exception thrown if the string cannot be parsed to a double.
	 */
	public static void main(String[] args) throws IOException, NumberFormatException {
		double amount;
		String username = null;
		String transaction;
		String fromCurrency = null;
		String toCurrency;
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
