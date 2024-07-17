package pl.javastart.streamstask;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamsTask {

    public static void main(String[] args) {
        StreamsTask streamsTask = new StreamsTask();
        streamsTask.run();
    }

    private void run() {
        List<User> users = new ArrayList<>();

        users.add(new User(1L, "Alicja", 20));
        users.add(new User(2L, "Dominik", 15));
        users.add(new User(3L, "Patrycja", 25));
        users.add(new User(4L, "Marcin", 30));
        users.add(new User(5L, "Tomek", 18));
        users.add(new User(6L, "Damian", 26));

        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(1L, "Buty", new BigDecimal("149.99"), ExpenseType.WEAR));
        expenses.add(new Expense(1L, "Sałatka", new BigDecimal("14.99"), ExpenseType.FOOD));
        expenses.add(new Expense(2L, "Bluza", new BigDecimal("100"), ExpenseType.WEAR));
        expenses.add(new Expense(2L, "Skarpetki", new BigDecimal("39"), ExpenseType.WEAR));
        expenses.add(new Expense(2L, "Pizza", new BigDecimal("25"), ExpenseType.FOOD));

        Collection<User> women = findWomen(users);
        Double averageMenAge = averageMenAge(users);
        Map<Long, List<Expense>> expensesByUserId_v1 = groupExpensesByUserIdVersion1(expenses);
        Map<Long, List<Expense>> expensesByUserId_v2 = groupExpensesByUserIdVersion2(users, expenses);
        Map<User, List<Expense>> expensesByUser_v1 = groupExpensesByUserVersion1(users, expenses);
        Map<User, List<Expense>> expensesByUser_v2 = groupExpensesByUserVersion2(users, expenses);
    }

    // metoda powinna zwracać listę kobiet (sprawdzając, czy imię kończy się na "a")
    Collection<User> findWomen(Collection<User> users) {
        return users.stream()
                .filter(u -> u.getName().endsWith("a"))
                .collect(Collectors.toList());
    }

    // metoda powinna zwracać średni wiek mężczyzn (sprawdzając, czy imię nie kończy się na "a")
    Double averageMenAge(Collection<User> users) {
        return users.stream()
                .filter(u -> !u.getName().endsWith("a"))
                .mapToInt(User::getAge)
                .average()
                .getAsDouble();
    }

    // metoda powinna zwracać wydatki zgrupowane po ID użytkownika
    // Collection<User> users wydał mi się niepotrzebny jako argument, dlatego go usunąłem
    Map<Long, List<Expense>> groupExpensesByUserIdVersion1(List<Expense> expenses) {
        return expenses.stream().collect(Collectors.groupingBy(Expense::getUserId));
    }

    // Druga wersja powyższej metody, tym razem zestawiająca w oryginalnej kolejności (dzięki LinkedHashMap)
    // wszystkich userów, nie tylko tych z jakimiś wydatkami
    Map<Long, List<Expense>> groupExpensesByUserIdVersion2(Collection<User> users, List<Expense> expenses) {
        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> expenses.stream()
                                .filter(expense -> expense.getUserId() == user.getId())
                                .collect(Collectors.toList()),
                        (x, y) -> y,
                        LinkedHashMap::new
                ));
    }

    // metoda powinna zwracać wydatki zgrupowane po użytkowniku
    // podobne do poprzedniego, ale trochę trudniejsze
    Map<User, List<Expense>> groupExpensesByUserVersion1(Collection<User> users, List<Expense> expenses) {
        return expenses.stream()
                .collect(Collectors.groupingBy(expense -> users.stream()
                        .filter(u -> u.getId() == expense.getUserId())
                        .findFirst()
                        .orElseThrow()
                ));
    }

//    Druga wersja powyższej metody, tym razem zestawiająca w oryginalnej kolejności (dzięki LinkedHashMap)
//    wszystkich userów, nie tylko tych z jakimiś wydatkami
    Map<User, List<Expense>> groupExpensesByUserVersion2(Collection<User> users, List<Expense> expenses) {
        return users.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        user -> expenses.stream()
                                .filter(expense -> expense.getUserId() == user.getId())
                                .collect(Collectors.toList()),
                        (x, y) -> y,
                        LinkedHashMap::new
                ));
    }
}
