package drysolid.abstractshop.store;

import drysolid.abstractshop.generate.Lot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Данный класс реализует два интерфейса: Requests, Storeable. Таким образом объект
 * данного класса может быть типа Requests и отвечать на запросы, а может быть
 * типа Storeable и добавлять и удалять товары. Все будет зависеть от того, что будет
 * нужно вызывающему коду от этого объекта в зависимости от контекста. Принцип
 * сегрегации интерфейса
 * @author aurumbeats
 */
public class Store implements Requests, Storeable {
    
    private final List<Lot> store;
    private final Set<String> categories;
    private final Set<String> subCategories;

    public Store() {
        store = new ArrayList<>();
        categories = new HashSet<>();
        subCategories = new HashSet<>();
    }
    
    @Override
    public List<Lot> getAll() {
        return store;
    }

    @Override
    public List<Lot> getByCategory(String category, List<Lot> source) {
        return source.stream()
                .filter(x -> x.getCategory().equals(category))
                .toList();
    }

    @Override
    public List<Lot> getBySubCategory(String subCategory, List<Lot> source) {
        return source.stream()
                .filter(x -> x.getSubcategory().equals(subCategory))
                .toList();
    }

    @Override
    public List<Lot> getByAverageRating(double min, double max, List<Lot> source) {
        return source.stream()
                .filter(x -> x.getAverageRating() >= min)
                .filter(x -> x.getAverageRating() <= max)
                .toList();
    }

    @Override
    public List<Lot> getByPrice(long min, long max, List<Lot> source) {
        return source.stream()
                .filter(x -> x.getPrice() >= min)
                .filter(x -> x.getPrice() <= max)
                .toList();
    }

    @Override
    public List<Lot> getByName(String name, List<Lot> source) {
        return source.stream()
                .filter(x -> name.trim().toLowerCase().contains(x.getName().toLowerCase()))
                .toList();
    }

    @Override
    public List<Lot> getByKeyword(String keyword, List<Lot> source) {
        String formattedKeyword = keyword.trim().toLowerCase();
        return source.stream()
                .filter(x -> x.getName().toLowerCase().contains(formattedKeyword)
                        | x.getDescription().toLowerCase().contains(formattedKeyword))
                .toList();
    }

    @Override
    public Lot getById(int id) {
        return store.get(id);
    }

    @Override
    public void add(Lot lot) {
        String category = lot.getCategory();
        String subCategory = lot.getSubcategory();
        categories.add(category);
        subCategories.add(subCategory);
        store.add(lot);
    }

    @Override
    public boolean remove(Lot lot) {
        return store.remove(lot);
    }

    @Override
    public Lot removeById(int id) {
        return store.remove(id);
    }

    @Override
    public Set<String> getCategories() {
        return categories;
    }

    @Override
    public Set<String> getSubCategories() {
        return subCategories;
    }
    
}
