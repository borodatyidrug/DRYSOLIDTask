package drysolid.abstractshop.generate;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Генератор продуктов питания. Отвечает ТОЛЬКО за это (как и - LotBuilder). Принцип единственной ответственности
 * @author aurumbeats
 */
public class FoodGenerator extends BaseLotGenerator {

    private final String[] SUB_CATEGORIES = {"Хлеб", "Молоко", "Мясо", "Водка"};
    private final String[] ROOTS = {"Зад", "Пуп", "Пер", "Ука"};
    private final String[] SUFFIXES = {"ком", "дон", "вар", "нев"};
    private final String[] MALE_ENDINGS = {"екский", "онский", "уйский", "ский"};
    private final String[] NEUTER_ENDINGS = {"екское", "онское", "ойское", "ское"};
    private final String[] FEMALE_ENDINGS = {"екская", "онская", "ойская", "ская"};
    private final String[] MEAT_TYPES = {"Коровятина", "Свинятина", "Бобрятина", "Змеятина"};
    private final String[] VOLUMES = {"0,5", "1,0", "1,5", "2,0"};
    
    private final List<String> maleNames;
    private final List<String> femaleNames;
    private final List<String> neuterNames;
    
    private int breadCount;
    private int milkCount;
    private int meatCount;
    private int vodkaCount;
    private int length;
    
    private PriceFormatter formatter;
    private final Function<Long, String> format;
    private final LotBuilder lotBuilder;

    public FoodGenerator() {
        
        super();
        
        maleNames = new ArrayList<>();
        femaleNames = new ArrayList<>();
        neuterNames = new ArrayList<>();
        
        breadCount = 0;
        milkCount = 0;
        meatCount = 0;
        vodkaCount = 0;
        
        nameGenerator(ROOTS, SUFFIXES, MALE_ENDINGS, maleNames);
        nameGenerator(ROOTS, SUFFIXES, FEMALE_ENDINGS, femaleNames);
        nameGenerator(ROOTS, SUFFIXES, NEUTER_ENDINGS, neuterNames);
        
        length = maleNames.size();
        
        /**
         * Формат вывода цены по-умолчанию
         */
        format = p -> {
            String stringValue = p.toString();
            String fractPart = stringValue.substring(stringValue.length() - 2, stringValue.length());
            String intPart = stringValue.substring(0, stringValue.length() - 2);
            return intPart + "," + fractPart + Currency.getInstance(Locale.getDefault()).getSymbol();
        };
        
        formatter = new PriceFormatterImpl();
        formatter.setFormatFunction(format);
        lotBuilder = new LotBuilder();
    }
    /**
     * Декартово произведение векторов с частями слов (корни, окончания, суффиксы),
     * дающее список всех возможных комбинаций их элементов.
     * @param r
     * @param s
     * @param e
     * @param list Список, в который будут добавлены все комбинации
     */
    private void nameGenerator(String[] r, String[] s, String[] e, List<String> list) {
        for (String x : r) {
            for (String y : s) {
                for (String z : e) {
                    list.add(x + y + z);
                }
            }
        }
    }

    @Override
    public void setPriceFormatter(PriceFormatter formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public boolean hasNext() {
        return breadCount < length || milkCount < length || meatCount < length
                || vodkaCount < length;
    }

    @Override
    public Lot generateLot() {
        
        String randomCategory = SUB_CATEGORIES[random.nextInt(SUB_CATEGORIES.length)];
        
        switch (randomCategory) {
            
            case "Хлеб":
                
                if (breadCount < length) {
                    
                    String name = maleNames.get(breadCount);
                    Map<String, String> specification = new HashMap<>();
                    specification.put("Mука", "Высший сорт");
                    specification.put("Масса", random.nextInt(350, 500) + " г.");
                    
                    breadCount++;
                    
                    return lotBuilder
                            .setCategory("Продукты питания")
                            .setSubCategory("Хлеб")
                            .setName("Хлеб " + name)
                            .setDescription("Хлеб " + name + " обладает прекрасным вкусом, \n"
                                    + "хрустящей корочкой и незабываемым ароматом, словно \n"
                                    + "он только из Русской печи вышел!")
                            .setSpecification(specification)
                            .setRating(generateRating())
                            .setRest(random.nextInt(20, 50))
                            .setIsAvailable(generateAvailability(80))
                            .setPrice(random.nextInt(3000, 8000))
                            .setFormatter(formatter)
                            .build();
                }
            case "Молоко":
                
                if (milkCount < length) {
                    
                    String name = neuterNames.get(milkCount);
                    Map<String, String> specification = new HashMap<>();
                    specification.put("Жирность", random.nextInt(1, 5) + "%");
                    specification.put("Производитель", "Корова (настоящая!)");
                    specification.put("Maccа", (800 + random.nextInt(4) * 100) + " г.");
                    
                    milkCount++;
                    
                    return lotBuilder
                            .setCategory("Продукты питания")
                            .setSubCategory("Молоко")
                            .setName("Молоко " + name)
                            .setDescription("Молоко " + name + " обладает прекрасным вкусом, \n"
                                    + "непревзойденным качеством и выдавлено из настоящей коровы, \n"
                                    + "а не синтезировано из технического пальмового жира!")
                            .setSpecification(specification)
                            .setRating(generateRating())
                            .setRest(random.nextInt(5))
                            .setIsAvailable(generateAvailability(96))
                            .setPrice(random.nextInt(8000, 12000))
                            .setFormatter(formatter)
                            .build();
                }
            case "Мясо":
                
                if (meatCount < length) {
                    
                    String name = neuterNames.get(meatCount);
                    Map<String, String> specification = new HashMap<>();
                    specification.put("Производитель", "КФХ \"" + name + "\"");
                    specification.put("Тип", MEAT_TYPES[random.nextInt(MEAT_TYPES.length)]);
                    specification.put("Масса", random.nextInt(30, 300) + " кг");
                    specification.put("Тип отпуска товара", "Живьем");
                    
                    meatCount++;
                    
                    return lotBuilder
                            .setCategory("Продукты питания")
                            .setSubCategory("Мясо")
                            .setName("Мясо " + name)
                            .setDescription("Мясо " + name + " обладает прекрасным вкусом, \n"
                                    + "непревзойденным качеством и отпускается только живьем, \n"
                                    + "а не синтезировано из полимерных волокон в лабораториях КНР!")
                            .setSpecification(specification)
                            .setRating(generateRating())
                            .setRest(random.nextInt(1000))
                            .setIsAvailable(generateAvailability(96))
                            .setPrice(random.nextInt(10_000_00, 100_000_00))
                            .setFormatter(formatter)
                            .build();
                }
            case "Водка":
                
                if (vodkaCount < length) {
                    
                    String name = femaleNames.get(vodkaCount);
                    Map<String, String> specification = new HashMap<>();
                    specification.put("Содержание этилового спирта", random.nextInt(40, 100) + "%");
                    specification.put("Объем", VOLUMES[random.nextInt(VOLUMES.length)] + " л");
                    
                    vodkaCount++;
                    
                    return lotBuilder
                            .setCategory("Продукты питания")
                            .setSubCategory("Водка")
                            .setName("Водка " + name)
                            .setDescription("Водка " + name + " обладает вкусом этилового спирта, \n"
                                    + "непревзойденным качеством и дарит забываемые ощущения, \n"
                                    + "нежелательные приключения и обманчивые эмоции")
                            .setSpecification(specification)
                            .setRating(generateRating())
                            .setRest(random.nextInt(1_000_000))
                            .setIsAvailable(generateAvailability(96))
                            .setPrice(random.nextInt(100_00, 1000_00))
                            .setFormatter(formatter)
                            .build();
                }
            default :
                    String name = "Имя";
                    Map<String, String> specification = new HashMap<>();
                    specification.put("Параметр1", "Значение1");
                    specification.put("Параметр2", "Значение2");
                    
                    return lotBuilder
                            .setCategory("Категория")
                            .setSubCategory("Подкатегория")
                            .setName("Продукт " + name)
                            .setDescription("Продукт " + name + " обладает прекрасным чем-либо, \n"
                                    + "непревзойденным чем-либо и дарит что-либо, \n"
                                    + "кое-что и нечто еще")
                            .setSpecification(specification)
                            .setRating(generateRating())
                            .setRest(random.nextInt(1_000_000))
                            .setIsAvailable(generateAvailability(96))
                            .setPrice(random.nextInt(100_00, 1000_00))
                            .setFormatter(formatter)
                            .build();
        }
    }
    
}
