package data;

import com.github.javafaker.Faker;
import lombok.Data;
import lombok.Value;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataHelper {
    private static final Faker faker = new Faker(new Locale("en"));

    private static Random rnd = new Random();

    private DataHelper() {
    }

    public static String generateName() {
        return faker.name().firstName();
    }

    public static String generatePassword() {
        return faker.internet().password();
    }

    public static UserInfo generateUser() {
        return new UserInfo(generateName(), generatePassword());
    }

    public static UserInfo generatePasswordForUser() {
        return new UserInfo("petya", generatePassword());
    }

    public static String hiddenCard(String cardNumber) {
        return String.format("**** **** **** %s", cardNumber.substring(cardNumber.length() - 4));
    }

    public static int getTransferAmount(int amount) {
        if (amount < 0) {
            throw new AmountLessZeroException(
                "На карте баланс меньше 0"
            );
        }
        return rnd.nextInt(amount);
    }

    public static List<Card> chooseCard(List<Card> cards) {
        int rand = rnd.nextInt(2);
        Card cardTo = cards.get(rand);
        Card cardFrom = cards.get(1 - rand);
        return List.of(cardTo, cardFrom);
    }

    public static String generateCreditCardNumber() {
        return faker.business().creditCardNumber();
    }

    public static UserInfo getCorrectUserLogInInfo() {
        return new UserInfo("vasya", "qwerty123");
    }

    public static VerificationBody getCorrectVerification(String user, String code) {
        return new VerificationBody(user, code);
    }

    public static String generateCode() {
        return faker.numerify("######");
    }

    @Value
    public static class UserInfo {
        String login;
        String password;
    }

    @Value
    public static class VerificationBody {
        String login;
        String code;
    }

    @Value
    public static class Card {
        String number;
    }

    public static class CardInfo {
        private CardInfo() {
        }

        public static Card getCorrectCard1() {
            return new Card("5559 0000 0000 0001");
        }

        public static Card getCorrectCard2() {
            return new Card("5559 0000 0000 0002");
        }

        public static Card getIncorrectCard2() {
            return new Card(generateCreditCardNumber());
        }
    }

    @Value
    public static class Transfer {
        String from;
        String to;
        int amount;
    }

    public static class TransferInfo {
        private TransferInfo() {
        }

        public static Transfer createTransferInfo(String from, String to, int amount) {
            return new Transfer(from, to, amount);
        }
    }

    @Value
    public static class TransferWithoutBalance {
        String from;
        String to;
    }

    public static class TransferWithoutBalanceInfo {
        private TransferWithoutBalanceInfo() {
        }

        public static TransferWithoutBalance createTransferWithoutBalanceInfo(String from, String to) {
            return new TransferWithoutBalance(from, to);
        }
    }
}
