package drysolid.abstractshop.store;

import drysolid.abstractshop.generate.Lot;
import java.util.List;

public interface Requests {
    
    public List<Lot> getAll();
    public List<Lot> getByCategory(String category, List<Lot> source);
    public List<Lot> getBySubCategory(String subCategory, List<Lot> source);
    public List<Lot> getByAverageRating(double min, double max, List<Lot> source);
    public List<Lot> getByPrice(long min, long max, List<Lot> source);
    public List<Lot> getByName(String name, List<Lot> source);
    public List<Lot> getByKeyword(String name, List<Lot> source);
    public Lot getById(int id);
}
