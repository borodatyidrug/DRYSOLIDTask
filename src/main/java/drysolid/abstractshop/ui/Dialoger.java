package drysolid.abstractshop.ui;

import drysolid.abstractshop.generate.Lot;
import drysolid.abstractshop.generate.PriceFormatter;
import drysolid.abstractshop.store.Requests;
import drysolid.abstractshop.store.Storeable;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

public class Dialoger implements Dialog {

    protected final String GREETING = "Приветствуем вас в консольном магазине \"Кулхацкер\"!\n";
    protected final String BYE = "Прощайте, Ибрагим-паша из Парги...";
    protected final String MAIN_MENU = "Введите номер желаемого действия:\n\n"
            + "1. \tВывести все товары выборки на экран\n"
            + "2. \tФильтр выборки по категории\n"
            + "3. \tФильтр выборки по подкатегории\n"
            + "4. \tФильтр по диапазону значений среднего рейтинга. (Возможные значения - от "
            + Lot.MIN_RATING + " до " + Lot.MAX_RATING + " включительно)\n"
            + "5. \tФильтр по диапазону значений цены\n"
            + "6. \tФильтр по наименованию товара\n"
            + "7. \tФильтр по ключевому слову\n"
            + "8. \tПоиск по артикулу\n"
            + "9. \tУказать артикулы, количества и добавить соответствующие товары в корзину\n"
            + "10. \tВывести на экран содержимое вашей корзины\n"
            + "11. \tОчистить корзину\n"
            + "12. \tВернуться к предыдущей выборке\n"
            + "13. \tСбросить текущую выборку и начать сначала\n"
            + "14. \tПоставить оценку товару с указанным артикулом\n"
            + "15. \tВыход\n";
    protected final String PRINT_MENU = "С какими полями вывести текущую выборку?\n\n"
            + "1. \tНаименование, артикул, цену\n"
            + "2. \tНаименование, артикул, цену, описание, характиеристики\n"
            + "3. \tВсе поля\n";
    
    protected final String ERROR_0 = "Вы ввели некорректное значение. Повторите ввод";
    
    // Нам магии - не надо. Необходимые константы - здесь. Магических чисел да
    // отвергнемся!
    protected final int HISTORY_DEPTH = 20;
    protected final int FIRST_ITEM_MAIN_MENU_N = 1;
    protected final int FIRST_ITEM_PRINT_MENU_N = 1;
    protected final int MAIN_MENU_ITEMS = 15;
    protected final int PRINT_MENU_ITEMS = 3;
    /**
     * Хранилище товаров, каталог
     */
    protected Requests store;
    protected InputStream in;
    protected PrintStream out;
    protected Scanner sc;
    /**
     * Хранит текущую выборку товаров
     */
    protected List<Lot> currentLotList;
    /**
     * Хранит историю выборок
     */
    protected Deque<List<Lot>> history;
    /**
     * Объект для форматирования цены
     */
    protected PriceFormatter formatter;
    /**
     * Список товаров в корзине
     */
    protected List<Lot> lotCart;
    /**
     * Список количеств товаров в корзине
     */
    protected List<Integer> cartLotsCounts;
    
    public Dialoger (Requests store, InputStream in, PrintStream out, PriceFormatter formatter) {
        this.formatter = formatter;
        this.store = store;
        this.in = in;
        this.out = out;
        this.currentLotList = store.getAll();
        history = new ArrayDeque<>(HISTORY_DEPTH);
        this.lotCart = new ArrayList<>();
        this.cartLotsCounts = new ArrayList<>();
        sc = new Scanner(in);
    }
    
    @Override
    public void start() {
        out.println(GREETING);
        mainMenu();
        out.println(BYE);
        sc.close();
    }

    /**
     * Устанавливает, если нужно, новый пользовательский форматтер цены
     * @param formatter Форматтер цены
     */
    @Override
    public void setPriceFormatter(PriceFormatter formatter) {
        this.formatter = formatter;
    }
   
    protected void printSelection(List<Lot> lots, int format) {
        switch (format) {
            case 1 -> lots.stream()
                    .forEach(l -> {
                        out.println("Наименование: " + l.getName());
                        out.println("Артикул: " + l.getId());
                        out.println("Цена: " + formatter.format(l.getPrice()));
                        out.println();
                    });
            case 2 -> lots.stream()
                    .forEach(l -> {
                        out.println("Наименование: " + l.getName());
                        out.println("Артикул: " + l.getId());
                        out.println("Цена: " + formatter.format(l.getPrice()));
                        out.println("Описание:\n" + l.getDescription());
                        out.println("Характеристики: " + l.getSpecification());
                        out.println();
                    });
            case 3 -> lots.stream()
                    .forEach(l -> {
                        out.println("Категория товара: " + l.getCategory() + "/" + l.getSubcategory());
                        out.println("Наименование: " + l.getName());
                        out.println("Артикул: " + l.getId());
                        out.println("Цена: " + formatter.format(l.getPrice()));
                        out.println("Описание:\n" + l.getDescription());
                        out.println("Характеристики: " + l.getSpecification());
                        out.println("Остаток на складе: " + l.getRest());
                        out.println("Наличие: " + (l.isAvailable() ? "да" : "нет"));
                        out.println("Оценки: " + l.getRating());
                        out.println("Рейтинг: " + l.getAverageRating() + " из " + Lot.MAX_RATING);
                        out.println();
                    });
        }
    }
    /**
     * Парсит, если - возможно, числовое значение из строки, и проверяет его на
     * принадлежность заданному интервалу значений. Эта операция повторяется везде,
     * где осуществляется ввод чисел - выбор пункта меню, указание артикулов, количеств,
     * оценок и т.д. Принцип DRY.
     * @param input Входная строка
     * @param min Левая граница интервала (включительно)
     * @param max Правая граница интервала (включительно)
     * @return Числовое значение - результат парсинга из входной строки, в противном
     * случае -1.
     */
    protected int validateNumericInput(String input, int min, int max) {
        int value;
        try {
            value = Integer.parseInt(input.trim());
            if (value >= min && value <= max) {
                return value;
            } else {
                return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    protected long validateNumericInput(String input, long min, long max) {
        long value;
        try {
            value = Long.parseLong(input.trim());
            if (value >= min && value <= max) {
                return value;
            } else {
                return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    protected double validateNumericInput(String input, double min, double max) {
        double value;
        try {
            value = Double.parseDouble(input.trim());
            if (value >= min && value <= max) {
                return value;
            } else {
                return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    protected List<String> printAvailableCategories() {
        List<String> categories = new ArrayList<>();
        ((Storeable) store).getCategories().stream()
                .forEach(c -> categories.add(c));
        for (int i = 0; i < categories.size(); i++) {
            out.println(i + 1 + ". " + categories.get(i));
        }
        return categories;
    }
    
    protected List<String> printAvailableSubCategories() {
        List<String> subCategories = new ArrayList<>();
        ((Storeable) store).getSubCategories().stream()
                .forEach(c -> subCategories.add(c));
        for (int i = 0; i < subCategories.size(); i++) {
            out.println(i + 1 + ". " + subCategories.get(i));
        }
        return subCategories;
    }
    
    protected int printCountOfSelectedLots(List<Lot> lots) {
        out.println("Выбрано " + lots.size() + " элементов");
        return lots.size();
    }
    
    protected void addToCart(int id, int count) {
        Lot foundLot = store.getById(id);
        lotCart.add(foundLot);
        cartLotsCounts.add(count);
    }
    
    protected void printCart() {
        try {
            out.println("ВАША КОРЗИНА:\n");
            long totalPrice = 0;
            for (int i = 0; i < lotCart.size(); i++) {
                Lot currLot = lotCart.get(i);
                int currLotCount = cartLotsCounts.get(i);
                long currPrice = currLot.getPrice();
                long currTotalPrice = currPrice * currLotCount;
                totalPrice += currTotalPrice;
                out.println("Позиция №" + (i + 1));
                out.println("\tНаименование: " + currLot.getName());
                out.println("\tАртикул: " + currLot.getId());
                out.println("\tЦена: " + formatter.format(currPrice));
                out.println("\tКоличество в корзине: " + currLotCount + " шт.");
                out.println("\tСтоимость: " + formatter.format(currTotalPrice));
                out.println();
            }
            out.println("ИТОГ: " + formatter.format(totalPrice) + "\n");
        } catch (IndexOutOfBoundsException e) {
            out.println("Ваша корзина - пуста.");
        }
    }
    
    protected void mainMenu() {
        int choice1;
        String inputLine;
        while(true) {
            out.println(MAIN_MENU);
            inputLine = sc.nextLine();
            choice1 = validateNumericInput(inputLine, FIRST_ITEM_MAIN_MENU_N, MAIN_MENU_ITEMS);
            if (choice1 > 0) {
                switch (choice1) {
                    case 1 -> {
                        while (true) {
                            out.println(PRINT_MENU);
                            inputLine = sc.nextLine();
                            choice1 = validateNumericInput(inputLine, FIRST_ITEM_PRINT_MENU_N, PRINT_MENU_ITEMS);
                            if (choice1 > 0) {
                                printSelection(currentLotList, choice1);
                                mainMenu();
                                break;
                            } else {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    case 2 -> {
                        while (true) {
                            out.println("Введите категорию товара:");
                            List<String> categories = printAvailableCategories();
                            int maxItems = categories.size();
                            inputLine = sc.nextLine();
                            choice1 = validateNumericInput(inputLine, 1, maxItems);
                            if (choice1 > 0) {
                                currentLotList = store.getByCategory(categories.get(choice1 - 1), currentLotList);
                                history.addFirst(new ArrayList<>(currentLotList));
                                printCountOfSelectedLots(currentLotList);
                                mainMenu();
                                break;
                            } else {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    case 3 -> {
                        while (true) {
                            out.println("Введите подкатегорию товара:");
                            List<String> subCategories = printAvailableSubCategories();
                            int maxItems = subCategories.size();
                            inputLine = sc.nextLine();
                            choice1 = validateNumericInput(inputLine, 1, maxItems);
                            if (choice1 > 0) {
                                currentLotList = store.getBySubCategory(subCategories.get(choice1 - 1), currentLotList);
                                history.addFirst(new ArrayList<>(currentLotList));
                                printCountOfSelectedLots(currentLotList);
                                mainMenu();
                                break;
                            } else {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    case 4 -> {
                        while (true) {
                            out.println("Введите диапазон рейтинга товара (от " + Lot.MIN_RATING + " до " + Lot.MAX_RATING + " включительно) в \n"
                                    + "виде двух положительных вещественных чисел через пробел:");
                            String[] inputArray = sc.nextLine().split(" ");
                            double min, max;
                            try {
                                // Чтобы компилятор выбрал правильный валидатор, то здесь в параметрах - явные приведения типов
                                min = validateNumericInput(inputArray[0], (double) Lot.MIN_RATING, (double) Lot.MAX_RATING);
                                max = validateNumericInput(inputArray[1], (double) Lot.MIN_RATING, (double) Lot.MAX_RATING);
                                if (min >= 0 && max >= 0) {
                                    currentLotList = store.getByAverageRating(min, max, currentLotList);
                                    history.addFirst(new ArrayList<>(currentLotList));
                                    printCountOfSelectedLots(currentLotList);
                                    mainMenu();
                                    break;
                                } else {
                                    out.println(ERROR_0);
                                }
                            } catch (IndexOutOfBoundsException e) {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    
                    case 5 -> {
                        while (true) {
                            out.println("Введите дипапазон цены товара в виде двух положительных вещественных чисел через пробел:");
                            String[] inputArray = sc.nextLine().split(" ");
                            long min, max;
                            try {
                                min = (long) (100 * validateNumericInput(inputArray[0], 0, Double.MAX_VALUE));
                                max = (long) (100 * validateNumericInput(inputArray[1], 0, Double.MAX_VALUE));
                                if (min >= 0 && max >= 0) {
                                    currentLotList = store.getByPrice(min, max, currentLotList);
                                    history.addFirst(new ArrayList<>(currentLotList));
                                    printCountOfSelectedLots(currentLotList);
                                    mainMenu();
                                    break;
                                } else {
                                    out.println(ERROR_0);
                                }
                            } catch (IndexOutOfBoundsException e) {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    case 6 -> {
                        while (true) {
                            out.println("Введите наименование товара:");
                            inputLine = sc.nextLine().trim();
                            if (!inputLine.isBlank()) {
                                currentLotList = store.getByName(inputLine, currentLotList);
                                history.addFirst(new ArrayList<>(currentLotList));
                                printCountOfSelectedLots(currentLotList);
                                mainMenu();
                                break;
                            } else {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    case 7 -> {
                        while (true) {
                            out.println("Введите ключевое слово:");
                            inputLine = sc.nextLine().trim();
                            if (!inputLine.isBlank()) {
                                currentLotList = store.getByKeyword(inputLine, currentLotList);
                                history.addFirst(new ArrayList<>(currentLotList));
                                printCountOfSelectedLots(currentLotList);
                                mainMenu();
                                break;
                            } else {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    case 8 -> {
                        while (true) {
                            out.println("Введите артикул в виде целого положительного числа:");
                            try {
                                int id = validateNumericInput(sc.nextLine(), 0, Integer.MAX_VALUE);
                                if (id >= 0) {
                                    Lot foundLot = store.getById(id);
                                    currentLotList = new ArrayList<>();
                                    currentLotList.add(foundLot);
                                    history.addFirst(new ArrayList<>(currentLotList));
                                    printCountOfSelectedLots(currentLotList);
                                    break;
                                } else {
                                    out.println(ERROR_0);
                                }
                            } catch (IndexOutOfBoundsException e) {
                                out.println("Товар с данным артикулом не найден.");
                                break;
                            }
                        }
                        mainMenu();
                    }
                    case 9 -> {
                        while (true) {
                            out.println("Введите список товаров для добавления в корзину в формате АРТИКУЛ/КОЛИЧЕСТВО через пробел (например: 45/5 12/33 102/1):");
                            int addedCount = 0;
                            int id, count;
                            inputLine = sc.nextLine().trim();
                            if (!inputLine.isBlank()) {
                                String[] inputArray = inputLine.trim().split(" ");
                                try {
                                    for (var x : inputArray) {
                                        String[] entry = x.split("/");
                                        id = validateNumericInput(entry[0], 0, Integer.MAX_VALUE);
                                        count = validateNumericInput(entry[1], 0, Integer.MAX_VALUE);
                                        if(count > 0 && id >= 0) {
                                            addToCart(id, count);
                                            addedCount++;
                                        } else {
                                            out.println(ERROR_0);
                                        }
                                    }
                                } catch (Exception e) {
                                    out.println("Товар не добавлен в корзину, т.к. не найден в каталоге");
                                }
                                out.println("В корзину добавлено " + addedCount + " позиций");
                                mainMenu();
                                break;
                            } else {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    case 10 -> {
                        printCart();
                        mainMenu();
                    }
                    case 11 -> {
                        lotCart.clear();
                        cartLotsCounts.clear();
                        out.println("Все товары удалены из вашей корзины. Ваша корзина - пуста.\n");
                        mainMenu();
                    }
                    case 12 -> {
                        List<Lot> rollback;
                        while(true) {
                            if (history.isEmpty()) {
                                out.println("Достигнуто дно стека истории выборок. Дальше откатываться - некуда. Оставляем все, как есть.\n"
                                    + "При необходимости можете сбросить текущую выборку и вернуться к началу, выбрав сответствующий пункт меню.\n");
                                break;
                            } else {
                                rollback = history.pollFirst();
                                if (!rollback.equals(currentLotList)) {
                                    currentLotList = rollback;
                                    out.println("Произведен откат к предыдущей выборке\n");
                                    break;
                                }
                            }
                        }
                        mainMenu();
                    }
                    case 13 -> {
                        currentLotList = store.getAll();
                        history.add(new ArrayList(currentLotList));
                        out.println("Произведен сброс текущей выборки. Вы вернулись в начало. Выбирайте ваши товары.\n");
                        mainMenu();
                    }
                    case 14 -> {
                        while (true) {
                            out.println("Введите оценки для товаров в формате АРТИКУЛ/ОЦЕНКА через пробел (например: 12/1 233/5 137/4):");
                            int ratedCount = 0;
                            Lot currLot;
                            inputLine = sc.nextLine();
                            if (!inputLine.isBlank()) {
                                String[] inputArray = inputLine.trim().split(" ");
                                try {
                                    for (var x : inputArray) {
                                        int id, rate;
                                        String[] entry = x.split("/");
                                        id = validateNumericInput(entry[0], 0, Integer.MAX_VALUE);
                                        rate = validateNumericInput(entry[1], Lot.MIN_RATING, Lot.MAX_RATING);
                                        if(rate >= 0 && id >= 0) {
                                            currLot = store.getById(id);
                                            currLot.addScore(rate);
                                            out.println("Товар c артикулом " + id + " получил от вас оценку \"" + rate + "\"");
                                            ratedCount++;
                                        } else {
                                            out.println(ERROR_0);
                                        }
                                    }
                                } catch (Exception e) {
                                        out.println(ERROR_0);
                                }
                                out.println("Оценено " + ratedCount + " товаров");
                                mainMenu();
                                break;
                            } else {
                                out.println(ERROR_0);
                            }
                        }
                    }
                    case 15 -> { break; }
                }
                break;
            } else {
                out.println(ERROR_0);
            }
        }
    }
}
