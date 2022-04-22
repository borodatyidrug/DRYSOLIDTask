package drysolid.abstractshop.generate;

import java.util.Map;
/**
 * Интерфейс товара с геттерами и сеттерами
 * @author aurumbeats
 */
public interface Lot {
    
    /**
     * Принцип отсутствия магических чисел в коде реализуется здесь. Если когда-либо
     * потребуется изменить верхнюю и нижнюю границы оценок в системе рейтинга товара,
     * то это - единственное место в коде, где это нужно будет сделать. Все имплементаторы
     * автоматически раелизуют данное изменение.
     */
    public final int MAX_RATING = 5;
    public final int MIN_RATING = 0;
    
    public String getName();
    public String getDescription();
    public int getId();
    public long getPrice();
    /**
     * Остаток товара на складе
     * @return Остаток
     */
    public int getRest();
    /**
     * Наличие товара
     * @return В наличии или нет
     */
    public boolean isAvailable();
    /**
     * Рейтинг. Отображение "Оценка" -> "Количество поставивших эту оценку"
     * @return
     */
    public Map<Integer, Integer> getRating();
    public double getAverageRating();
    /**
     * Возвращает характеристику товара, если таковая задана
     * @return Отображение "Характеристика" -> "Значение"
     */
    public Map<String, String> getSpecification();
    public String getCategory();
    public String getSubcategory();
    //public LotBuilder getBuilder();
    
    public void setName(String name);
    public void setDescription(String description);
    public void setPrice(long price);
    public void setRest(int rest);
    public void setAvailable(boolean isAvailable);
    /**
     * Добавляет товару оценку в интервале [0; 5]
     * @param score Оценка
     */
    public void addScore(int score);
    public void setSpecification(Map<String, String> specification);
    public void setCategory(String category, String subcategory);
}
