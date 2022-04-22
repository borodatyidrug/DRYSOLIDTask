package drysolid.abstractshop.generate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Базовый генератор лотов. Здесь реализован общий для все возможных генераторов
 * товаров функционал.
 * @author aurumbeats
 */
public abstract class BaseLotGenerator implements LotGenerator {
    
    protected Random random;

    public BaseLotGenerator() {
        random = new Random();
    }
    
    protected int distributionFunction(int x, int startDispersion) {
        return x * x + random.nextInt(x * x * x + startDispersion);
    }
    
    /**
     * @return Мапа, содержащая рейтинг в форме {Оценка -> Кол-во проголосовавших}
     */
    protected Map<Integer, Integer> generateRating() {
        Map<Integer, Integer> rating = new HashMap<>();
        for (int i = Lot.MIN_RATING; i <= Lot.MAX_RATING; i++) {
            rating.put(i, distributionFunction(i, 5));
        }
        return rating;
    }
    
    /**
     * @param probability Вероятность доступности для покупки (наличие)
     * @return Доступность для покупки (наличие)
     */
    protected boolean generateAvailability(int probability) {
        return random.nextInt(100) <= probability;
    }

    @Override
    public abstract boolean hasNext();

    @Override
    public abstract Lot generateLot();
    
}
