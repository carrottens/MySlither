package localserver;

import de.mat2095.my_slither.Food;

import java.util.ArrayList;
import java.util.Random;


/**
 * Game Sector used in slither.io protocol.
 */
public class Sector {

    static final int IN_ROW = 144;
    private static final int GENERATED_FOOD_MAX = 10;
    /**
     * Coordinates of a sector in a tile map.
     */
    private int x, y;


    private ArrayList<Food> food;
    /**
     * Constructor.
     *
     * Creates a new sector.
     * @param x
     * @param y
     */
    public Sector(int x, int y) {
        this.x = x;
        this.y = y;
        food = new ArrayList<>();
        generateFood();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private void generateFood() {
        Random random = new Random();
        int bound = (food.size() - GENERATED_FOOD_MAX) < 0 ? GENERATED_FOOD_MAX - food.size() : 0;
        int nFoodParticles = random.nextInt(bound);

        for(;0 != nFoodParticles; nFoodParticles--) {
            food.add(new Food(random.nextInt(300), random.nextInt(300), random.nextDouble(), false));
        }
    }
}
