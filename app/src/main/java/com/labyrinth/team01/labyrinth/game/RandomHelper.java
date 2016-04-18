package com.labyrinth.team01.labyrinth.game;

import java.util.Random;

/**
 * Created by Андрей on 15.02.2016.
 */
public class RandomHelper {
    private static final Random RND = new Random(System.currentTimeMillis());

    public static int sRand(int min, int max){
        return min + RND.nextInt(max - min + 1);
    }

    private long seed;
    private Random rnd;
    public RandomHelper(){
        this.seed = System.currentTimeMillis();
        rnd = new Random(seed);
    }
    public RandomHelper(long seed){
        this.seed = seed;
        rnd = new Random(seed);
    }
    public long getSeed(){
        return seed;
    }

    public int rand(int min, int max){
        return min + rnd.nextInt(max - min + 1);
    }

    public boolean isRandom(int amount, int maxAmount){
        return rand(1, maxAmount) <= amount;
    }
}
