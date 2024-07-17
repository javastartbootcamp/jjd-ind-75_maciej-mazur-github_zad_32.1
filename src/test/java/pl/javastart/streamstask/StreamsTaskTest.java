package pl.javastart.streamstask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class StreamsTaskTest {
    private StreamsTask streamsTask;
    private List<User> users;
    private List<Expense> expenses;
    // Poniższe zmienne wyciągnąłem tu do pól, by ułatwić sobie odwoływanie się do nich później w testach
    private User user1;
    private User user2;
    private Expense expense1;
    private Expense expense2;
    private Expense expense3;
    private Expense expense4;
    private Expense expense5;

    @BeforeEach
    void init() {
        streamsTask = new StreamsTask();

        users = new ArrayList<>();

        user1 = new User(1L, "Alicja", 20);
        user2 = new User(2L, "Dominik", 15);

        users.add(user1);
        users.add(user2);
        users.add(new User(3L, "Patrycja", 25));
        users.add(new User(4L, "Marcin", 30));
        users.add(new User(5L, "Tomek", 18));
        users.add(new User(6L, "Damian", 26));

        expenses = new ArrayList<>();

        expense1 = new Expense(1L, "Buty", new BigDecimal("149.99"), ExpenseType.WEAR);
        expense2 = new Expense(1L, "Sałatka", new BigDecimal("14.99"), ExpenseType.FOOD);
        expense3 = new Expense(2L, "Bluza", new BigDecimal("100"), ExpenseType.WEAR);
        expense4 = new Expense(2L, "Skarpetki", new BigDecimal("39"), ExpenseType.WEAR);
        expense5 = new Expense(2L, "Pizza", new BigDecimal("25"), ExpenseType.FOOD);

        expenses.add(expense1);
        expenses.add(expense2);
        expenses.add(expense3);
        expenses.add(expense4);
        expenses.add(expense5);
    }

    @Test
    void shouldFilterWomenCorrectly() {

        // when
        Collection<User> women = streamsTask.findWomen(users);

        // then
        for (User woman : women) {
            assertThat(woman.getName()).endsWith("a");
        }
    }

    @Test
    void shouldCalculateAverageAgeForMen() {

        // when
        Double avgManAge = streamsTask.averageMenAge(users);

        // then
        assertThat(avgManAge).isEqualTo(22.25);
    }

    @Test
    void shouldGroupExpensesByUserId_v1() {

        // when
        Map<Long, List<Expense>> expensesByUserId = streamsTask.groupExpensesByUserId_v1(expenses);
        String user1ExpensesConcatenatedNames = getConcatenatedExpenseNames(1L, expensesByUserId);
        String user2ExpensesConcatenatedNames = getConcatenatedExpenseNames(2L, expensesByUserId);

        // then
        assertThat(expensesByUserId.size()).isEqualTo(2);
        assertThat(expensesByUserId.get(1L).size()).isEqualTo(2);
        assertThat(expensesByUserId.get(2L).size()).isEqualTo(3);
        assertThat(user1ExpensesConcatenatedNames).contains("Buty");
        assertThat(user1ExpensesConcatenatedNames).contains("Sałatka");
        assertThat(user2ExpensesConcatenatedNames).contains("Bluza");
        assertThat(user2ExpensesConcatenatedNames).contains("Skarpetki");
        assertThat(user2ExpensesConcatenatedNames).contains("Pizza");
    }

    @Test
    void shouldGroupExpensesByUserId_v2() {

        // when
        Map<Long, List<Expense>> expensesByUserId = streamsTask.groupExpensesByUserId_v2(users, expenses);
        String user1ExpensesConcatenatedNames = getConcatenatedExpenseNames(1L, expensesByUserId);
        String user2ExpensesConcatenatedNames = getConcatenatedExpenseNames(2L, expensesByUserId);

        // then
        assertThat(expensesByUserId.size()).isEqualTo(6);
        assertThat(expensesByUserId.get(1L).size()).isEqualTo(2);
        assertThat(expensesByUserId.get(2L).size()).isEqualTo(3);
        assertThat(user1ExpensesConcatenatedNames).contains("Buty");
        assertThat(user1ExpensesConcatenatedNames).contains("Sałatka");
        assertThat(user2ExpensesConcatenatedNames).contains("Bluza");
        assertThat(user2ExpensesConcatenatedNames).contains("Skarpetki");
        assertThat(user2ExpensesConcatenatedNames).contains("Pizza");

        for (long i = 3; i <= 6; i++) {
            assertThat(expensesByUserId.get(i).size()).isEqualTo(0);
        }
    }

    @Test
    void shouldGroupExpensesByUser_v1() {

        //when
        Map<User, List<Expense>> expensesByUser = streamsTask.groupExpensesByUser_v1(users, expenses);

        // then
        assertThat(expensesByUser.size()).isEqualTo(2);
        assertThat(expensesByUser.get(user1).size()).isEqualTo(2);
        assertThat(expensesByUser.get(user2).size()).isEqualTo(3);
        assertThat(expensesByUser.get(user1)).contains(expense1, expense2);
        assertThat(expensesByUser.get(user2)).contains(expense3, expense4, expense5);
    }

    @Test
    void shouldGroupExpensesByUser_v2() {
        //when
        Map<User, List<Expense>> expensesByUser = streamsTask.groupExpensesByUser_v2(users, expenses);

        // then
        assertThat(expensesByUser.size()).isEqualTo(6);
        assertThat(expensesByUser.get(user1).size()).isEqualTo(2);
        assertThat(expensesByUser.get(user2).size()).isEqualTo(3);
        assertThat(expensesByUser.get(user1)).contains(expense1, expense2);
        assertThat(expensesByUser.get(user2)).contains(expense3, expense4, expense5);

        for (Map.Entry<User, List<Expense>> entry : expensesByUser.entrySet()) {
            if (!entry.getKey().equals(user1) && !entry.getKey().equals(user2)) {
                assertThat(entry.getValue().size()).isEqualTo(0);
            }
        }
    }

    private String getConcatenatedExpenseNames(long userId, Map<Long, List<Expense>> expensesByUserId) {
        return expensesByUserId.entrySet().stream()
                .filter(entry -> entry.getKey() == userId)
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .map(Expense::getName)
                .collect(Collectors.joining(" "));
    }
}
