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
        Map<Long, List<Expense>> expensesByUserIdVersion1 = groupExpensesByUserIdVersion1(expenses);
        Map<Long, List<Expense>> expensesByUserIdVersion2 = groupExpensesByUserIdVersion2(users, expenses);
        Map<User, List<Expense>> expensesByUserVersion1 = groupExpensesByUserVersion1(users, expenses);
        Map<User, List<Expense>> expensesByUserVersion2 = groupExpensesByUserVersion2(users, expenses);
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
                                .filter(expense -> Objects.equals(expense.getUserId(), user.getId()))
                                .collect(Collectors.toList()),
                        (x, y) -> y,
                        LinkedHashMap::new
                ));
    }

    // metoda powinna zwracać wydatki zgrupowane po użytkowniku
    // podobne do poprzedniego, ale trochę trudniejsze
    Map<User, List<Expense>> groupExpensesByUserVersion1(Collection<User> users, List<Expense> expenses) {
        Map<Long, User> usersGroupedById = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));

        return expenses.stream()
                .collect(Collectors.groupingBy(expense -> usersGroupedById.get(expense.getUserId())));
    }

//    Druga wersja powyższej metody, tym razem zestawiająca w oryginalnej kolejności (dzięki LinkedHashMap)
//    wszystkich userów, nie tylko tych z jakimiś wydatkami
    Map<User, List<Expense>> groupExpensesByUserVersion2(Collection<User> users, List<Expense> expenses) {
        Map<Long, List<Expense>> expensesGroupedById = expenses.stream().collect(Collectors.groupingBy(Expense::getUserId));
        
        return users.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        user -> expensesGroupedById
                                .get(user.getId()) == null ? new ArrayList<>() : expensesGroupedById.get(user.getId()),
                        (x, y) -> y,
                        LinkedHashMap::new
                ));
    }
}
