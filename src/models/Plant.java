package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Plant extends Agent {

    public static int currentMax = ThreadLocalRandom.current().nextInt(8, 12);

    Plant(int x, int y, int energy, Agent[][] field, int range) {
        super(x, y, energy, field, "Plant", null, range);
    }

    @Override
    public void act() {
        energy++;
        if(energy>currentMax){
            div();
        }

    }

    @Override protected List<int[]> look(String type) { return new ArrayList<>(); }
    @Override protected int[] near(List<int[]> found) { return null; }
    @Override protected Agent createChild(int cx, int cy, int e) {
        return new Plant(cx, cy, e, field, range);
    }
}