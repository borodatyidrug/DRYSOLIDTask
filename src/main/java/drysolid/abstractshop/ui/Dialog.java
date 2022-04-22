package drysolid.abstractshop.ui;

import drysolid.abstractshop.generate.PriceFormatter;

public interface Dialog {
    public void start();
    public void setPriceFormatter(PriceFormatter formatter);
}
