package drysolid.abstractshop;

import drysolid.abstractshop.generate.FoodGenerator;
import drysolid.abstractshop.generate.Lot;
import drysolid.abstractshop.generate.LotGenerator;
import drysolid.abstractshop.generate.PriceFormatter;
import drysolid.abstractshop.generate.PriceFormatterImpl;
import drysolid.abstractshop.store.Store;
import drysolid.abstractshop.ui.Dialoger;
import java.util.Currency;
import java.util.Locale;
import java.util.function.Function;

public class AbstractShop {

    public static void main(String[] args) {
        
        // Задаем пользовательскую функцию форматирования цены. Для изменения способа форматирования цены
        // при ее выводе на консоль передадим ее форматтеру, который передадим генератору товаров и классу,
        // реализующему пользовательский интерфейс в консоли.
        // Генератор товаров, в свою очередь, передаст форматтер билдеру товаров и самому товару для toString().
        // Принцип открытости/закрытости. В код вывода полей товара не лезем, но можем изменить формат извне,
        // расширить возможности форматирования
        Function<Long, String> format = p -> {
            String stringValue = p.toString();
            String fractPart = stringValue.substring(stringValue.length() - 2, stringValue.length());
            String intPart = stringValue.substring(0, stringValue.length() - 2);
            return intPart + "," + fractPart + Currency.getInstance(Locale.getDefault()).getSymbol();
        };
        // Передаем ее форматтеру
        PriceFormatter formatter = new PriceFormatterImpl();
        formatter.setFormatFunction(format);
        
        // Создаем хранилище товаров
        Store store = new Store();
        
        // Создаем генератор товаров, который генерирует продукты питания. При необходимости, можно здесь
        // создать любой другой генератор товаров, реализующий интерфейс LotGenerator. Принцип инверсии
        // зависимостей.
        LotGenerator food = new FoodGenerator();
        
        // Установим для генерации лотов пользовательский форматтер цены
        food.setPriceFormatter(formatter);
        
        // Наполняем хранилище генерируемыми лотами
        while (food.hasNext()) {
            Lot newLot = food.generateLot();
            store.add(newLot);
        }
        
        // Объект, реализующий пользовательский интерфейс. Кстати, может работать
        // с любыми доступными потоками ввода-вывода. Например, к нему можно прицепить
        // сокет и работать с ним по сети.
        Dialoger dialoger = new Dialoger(store, System.in, System.out, formatter);
        dialoger.start();
    }
}
