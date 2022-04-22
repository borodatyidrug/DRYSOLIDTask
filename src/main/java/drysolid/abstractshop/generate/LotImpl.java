package drysolid.abstractshop.generate;

import java.util.Map;
import java.util.Set;

public class LotImpl implements Lot {
    
    protected static Map<Integer, Lot> lotStore;
    protected static int idCount = 0;
    
    protected String name;
    protected String description;
    protected final int id;
    protected long price;
    protected int rest;
    protected boolean isAvailable;
    protected Map<Integer, Integer> rating;
    protected Map<String, String> specification;
    protected String category;
    protected String subCategory;
    protected PriceFormatter formatter;

    public LotImpl(
            String name, 
            String description, 
            long price, 
            int rest, 
            boolean isAvailable, 
            Map<Integer, Integer> rating, 
            Map<String, String> specification, 
            String category,
            String subCategory,
            PriceFormatter formatter) {
        this.name = name;
        this.description = description;
        this.id = idCount++;
        this.price = price;
        this.rest = rest;
        this.isAvailable = isAvailable;
        this.rating = rating;
        this.specification = specification;
        this.category = category;
        this.subCategory = subCategory;
        this.formatter = formatter;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public int getRest() {
        return rest;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public Map<Integer, Integer> getRating() {
        return rating;
    }

    @Override
    public Map<String, String> getSpecification() {
        return specification;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public void setRest(int rest) {
        this.rest = rest;
    }

    @Override
    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public void addScore(int score) {
        if (rating.containsKey(score)) {
            rating.put(score, rating.remove(score) + 1);
        } else {
            throw new IllegalArgumentException("Оценка вне области допустимых значений!"
                    + " Допустимые значения - от " + Lot.MIN_RATING + " до "
                    + Lot.MAX_RATING + " включительно.");
        }
    }

    @Override
    public void setSpecification(Map<String, String> specification) {
        this.specification = specification;
    }

    @Override
    public void setCategory(String category, String subCategory) {
        this.category = category;
        this.subCategory = subCategory;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getSubcategory() {
        return subCategory;
    }
    
    @Override
    public double getAverageRating() {
        double sum = 0;
        int voted = 0;
        Set<Map.Entry<Integer, Integer>> entrySet = rating.entrySet();
        for (var e : entrySet) {
            voted += e.getValue();
            sum += e.getValue() * e.getKey();
        }
        return sum / voted;
    }
    
    @Override
    public String toString() {
        return "Категория товара: " + category + "/" + subCategory + "\n"
                + "Наименование: " + name + "\n"
                + "Артикул: " + id + "\n"
                + "Описание: \n" + description + "\n"
                + (specification == null ? "" : "Характеристики: " + specification) + "\n"
                + "Цена: " + formatter.format(price) + "\n"
                + "Остаток на складе: " + rest + "\n"
                + "Наличие: " + (isAvailable ? "да" : "нет") + "\n"
                + "Оценки: " + rating + "\n"
                + "Рейтинг: " + getAverageRating();
    }
}
