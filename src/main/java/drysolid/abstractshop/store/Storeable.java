package drysolid.abstractshop.store;

import drysolid.abstractshop.generate.Lot;
import java.util.Set;

public interface Storeable {
    public void add(Lot lot);
    public boolean remove(Lot lot);
    public Lot removeById(int id);
    public Set<String> getCategories();
    public Set<String> getSubCategories();
}
