package drysolid.abstractshop.generate;

import java.util.function.Function;

public class PriceFormatterImpl implements PriceFormatter {
    
    Function<Long, String> format;

    @Override
    public String format(long price) {
        return format.apply(price);
    }

    @Override
    public void setFormatFunction(Function<Long, String> format) {
        this.format = format;
    }
    
}
