package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Plant extends Agent {

    public static int currentMax = ThreadLocalRandom.current().nextInt(8, 12);
    private static final double max_destiny = 0.3; // максимально 30% поля занято травой

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

    @Override
    public void div() {
        if (!canDivide()) return;

        List<int[]> free = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                if (field[i][j] == null) {
                    free.add(new int[]{i, j});
                }
            }
        }
        if (free.isEmpty()) return;

        int[] spot = free.get(ThreadLocalRandom.current().nextInt(free.size()));
        int childEnergy = energy / 2;
        energy -= childEnergy;
        field[spot[0]][spot[1]] = createChild(spot[0], spot[1], childEnergy);
    }

    @Override
    protected boolean canDivide() {
        int count = 0;
        for (int i = 0; i < range; i++)
            for (int j = 0; j < range; j++)
                if (field[i][j] != null && field[i][j].isAlive()
                        && "Plant".equals(field[i][j].getType()))
                    count++;
        return (double) count / (range * range) < max_destiny;
    }
    @Override protected List<int[]> look(String type) { return new ArrayList<>(); }
    @Override protected int[] near(List<int[]> found) { return null; }
    @Override protected Agent createChild(int cx, int cy, int e) {
        return new Plant(cx, cy, e, field, range);
    }
}