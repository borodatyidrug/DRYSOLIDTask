package drysolid.abstractshop.generate;

import java.util.function.Function;

/**
 * Форматирует цену для ее вывода в необходимом виде
 * @author aurumbeats
 */
public interface PriceFormatter {
    /**
     * Форматирует цену в соответствии с переданным в виде лямбды способом форматирования
     * @param price Цена
     * @return Строка с отформатированной ценой
     */
    public String format(long price);
    /**
     * Устанавливает функцию форматирования цены
     * @param format 
     */
    public void setFormatFunction(Function<Long, String> format);
}
