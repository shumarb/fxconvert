package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class UserTest {

    @Mock
    List<User> usersList;

    @Mock
    User user1;

    @BeforeEach
    void setUp() {
        usersList = new ArrayList<> ();
        user1 = new User("Luke");
    }

    @Test
    public void testUsersListIsEmptyWhenNoUsersAdded() {
        assertEquals(0, usersList.size());
    }

    @Test
    public void testCorrectUserNameReturned() {
        assertEquals("Luke", user1.getName());
    }

    @Test
    public void testIncorrectUserNameNotReturned() {
        assertNotEquals("Harry", user1.getName());
    }

    @Test
    public void testUsersListHasOneUserWhenOneUserAdded() {
        user1.addCurrencyToWallet("sgd", 10);
        user1.addCurrencyToWallet("myr", 50);
        usersList.add(user1);
        assertEquals(1, usersList.size());
    }

    @Test
    public void testUsersListDoesNotHaveMoreThanOneUserWhenOneUserAdded() {
        user1.addCurrencyToWallet("sgd", 10);
        user1.addCurrencyToWallet("myr", 50);
        usersList.add(user1);
        assertNotEquals(2, usersList.size());
    }

    @Test
    public void testCurrencyAddedToWalletReturnsTrueAfterAddingCurrencyToWallet() {
        user1.addCurrencyToWallet("cad", 42);
        assertTrue(user1.isCurrencyInWallet("cad"));
    }

    @Test
    public void testCurrencyAddedToWalletDoesNotReturnFalseAfterAddingCurrencyToWallet() {
        user1.addCurrencyToWallet("cad", 42);
        assertNotEquals(user1.isCurrencyInWallet("cad"), false);
    }

    @Test
    public void testCorrectCurrencyValueReturnedAfterAddingCurrencyToWallet() {
        user1.addCurrencyToWallet("idr", 1412);
        assertEquals(1412, user1.getCurrencyValueInWallet("idr"));
    }

    @Test
    public void testIncorrectCurrencyValueNotReturnedAfterAddingCurrencyToWallet() {
        user1.addCurrencyToWallet("idr", 1412);
        assertNotEquals(131, user1.getCurrencyValueInWallet("idr"));
    }

    @Test
    public void testCurrencyValueOfZeroRemovedAfterAddingCurrencyToWallet() {
        user1.addCurrencyToWallet("idr", 0);
        user1.removesCurrencyWithValueOfZero("idr");
        assertEquals(0, user1.getWallet().size());
    }

    @Test
    public void testCurrencyValueThatIsNotZeroIsNotRemovedAfterAddingCurrencyToWallet() {
        user1.addCurrencyToWallet("idr", 13130);
        user1.removesCurrencyWithValueOfZero("idr");
        assertNotEquals(0, user1.getWallet().size());
    }

    @Test
    public void testReturnValueIsZeroAfterAddingCurrencyWithValueZero() {
        user1.addCurrencyToWallet("myr", 0);
        assertEquals(0, user1.getCurrencyValueInWallet("myr"));
    }

    @Test
    public void testCurrencyValueIsZeroAfterAddingCurrencyWithValueZero() {
        user1.addCurrencyToWallet("myr", 0);
        assertTrue(user1.isValueOfCurrencyInWalletEqualToZero("myr"));
    }

    @Test
    public void testReturnValueIsNotZeroAfterAddingCurrencyWithNonZeroValue() {
        user1.addCurrencyToWallet("myr", 140);
        assertNotEquals(0, user1.getCurrencyValueInWallet("myr"));
    }

    @Test
    public void testDifferentUsersHaveDifferentWalletsAfterCallToReturnUserWalletInvoked() {
        User user2 = new User("Tim");
        user2.addCurrencyToWallet("eur", 1230);
        assertNotEquals(user2.getWallet(), user1.getWallet());
    }

    @Test
    public void testCurrencyValueIncreasedCorrectly() {
        user1.addCurrencyToWallet("sgd", 50);
        user1.increaseCurrencyValueInWallet("sgd", 110);
        assertEquals(160, user1.getCurrencyValueInWallet("sgd"));
    }

    @Test
    public void testCurrencyValueDidNotIncreasedIncorrectly() {
        user1.addCurrencyToWallet("sgd", 50);
        user1.increaseCurrencyValueInWallet("sgd", 110);
        assertNotEquals(12310, user1.getCurrencyValueInWallet("sgd"));
    }

    @Test
    public void testCurrencyValueDecreasedCorrectly() {
        user1.addCurrencyToWallet("sgd", 150);
        user1.increaseCurrencyValueInWallet("sgd", 110);
        assertEquals(260, user1.getCurrencyValueInWallet("sgd"));
    }

    @Test
    public void testCurrencyValueDidNotDecreasedInCorrectly() {
        user1.addCurrencyToWallet("sgd", 150);
        user1.increaseCurrencyValueInWallet("sgd", 110);
        assertNotEquals(11414, user1.getCurrencyValueInWallet("sgd"));
    }

}
