package me.cousinss;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Dice extends Number {

    private static final Map<Integer, Integer> ROLL_DOTS;

    static {
        Map<Integer, Integer> dots = new HashMap<>();
        for(int i = 0; i <= 12; i++) {
            dots.put(i, 6-Math.abs(7-i));
        }
        ROLL_DOTS = Collections.unmodifiableMap(dots);
    }

    private final int die1;
    private final int die2;

    public Dice(Random random) {
        this.die1 = random.nextInt(6) + 1;
        this.die2 = random.nextInt(6) + 1;
    }

    public int sum() {
        return this.intValue();
    }

    @Override
    public int intValue() {
        return die1 + die2;
    }

    @Override
    public long longValue() {
        return (long)intValue();
    }

    @Override
    public float floatValue() {
        return (float)intValue();
    }

    @Override
    public double doubleValue() {
        return (double)intValue();
    }

    public int getFirst() {
        return die1;
    }

    public int getSecond() {
        return die2;
    }

    public int[] asArray() {
        return new int[] {die1, die2};
    }

    static int getRollDots(int rollValue) {
        return ROLL_DOTS.getOrDefault(rollValue, 0);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.asArray()) + " (" + this.intValue() + ")";
    }
}
