package drysolid.abstractshop.generate;

import java.util.Map;

public class LotBuilder {
    
    protected String name;
    protected String description;
    protected long price;
    protected int rest;
    protected boolean isAvailable;
    protected Map<Integer, Integer> rating;
    protected Map<String, String> specification;
    protected String category;
    protected String subCategory;
    protected PriceFormatter formatter;
    
    private String stringValidator(String field) {
        if (!field.isBlank()) {
            return field;
        } else {
            throw new IllegalArgumentException("Недопустимое значение. Поле не должно быть пустым.");
        }
    }
    
    public LotBuilder setName(String name) {
        this.name = stringValidator(name);
        return this;
    }

    public LotBuilder setDescription(String description) {
        this.description = stringValidator(description);
        return this;
    }

    public LotBuilder setPrice(long price) {
        this.price = price;
        return this;
    }

    public LotBuilder setRest(int rest) {
        this.rest = rest;
        return this;
    }

    public LotBuilder setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
        return this;
    }

    public LotBuilder setRating(Map<Integer, Integer> rating) {
        this.rating = rating;
        return this;
    }

    public LotBuilder setSpecification(Map<String, String> specification) {
        this.specification = specification;
        return this;
    }

    public LotBuilder setCategory(String category) {
        this.category = stringValidator(category);
        return this;
    }

    public LotBuilder setSubCategory(String subCategory) {
        this.subCategory = stringValidator(subCategory);
        return this;
    }

    public LotBuilder setFormatter(PriceFormatter formatter) {
        this.formatter = formatter;
        return this;
    }
    
    public Lot build() {
        if (name == null || description == null || rating == null
                || category == null || subCategory == null
                || formatter == null) {
            throw new IllegalStateException("Не все обязательные поля инициализированы!");
        } else {
            return new LotImpl(
                name,
                description,
                price,
                rest,
                isAvailable,
                rating,
                specification,
                category,
                subCategory,
                formatter
            );
        }
    }
}
