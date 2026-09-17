package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Rabbit extends Agent {


    protected int visRange = 3;   // кролик видит растение на 3

    public static int currentMax = 200;
    public static int currentDivideThreshold = 18;

    Rabbit(int x, int y, int energy, Agent[][] field, int range) {
        super(x, y, energy, field, "Rabbit", "Plant", range);
    }

    @Override
    public void act() {
        // 1. Угроза — бежим
        List<int[]> threats = look("Wolf");
        if (!threats.isEmpty()) {
            int[] t = near(threats);
            if (t != null) {
                move(x - t[0], y - t[1]);
                finishTurn();
                return;
            }
        }

        // 2. Еда — идём
        List<int[]> found = look("Plant");
        if (!found.isEmpty()) {
            int[] t = near(found);
            if (t != null) {
                move(t[0] - x, t[1] - y);
                finishTurn();
                return;
            }
        }

        // 3. Свободное блуждание

        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        int[] d = dirs[ThreadLocalRandom.current().nextInt(dirs.length)];
        move(d[0], d[1]);

        finishTurn();
    }

    private void finishTurn() {
        if (energy <= 0) { die(); return; }
        if (energy >= currentDivideThreshold /*&& energy > max_nrg*/) {
            div();
        }
    }

    @Override
    protected List<int[]> look(String type) {
        List<int[]> found = new ArrayList<>();
        for (int i = x - visRange; i <= x + visRange; i++)
            for (int j = y - visRange; j <= y + visRange; j++)
                if (inBounds(i, j) && field[i][j] != null
                        && field[i][j].isAlive()
                        && type.equals(field[i][j].getType()))
                    found.add(new int[]{i, j});
        return found;
    }

    @Override
    protected int[] near(List<int[]> found) {
        int[] best = null;
        double bestLen = Double.MAX_VALUE;
        for (int[] p : found) {
            double d = len(p[0], p[1]);
            if (d < bestLen) { bestLen = d; best = p; }
        }
        return best;
    }

    @Override
    protected boolean canDivide() {
        int count = 0;
        for (int i = 0; i < range; i++)
            for (int j = 0; j < range; j++)
                if (field[i][j] != null && field[i][j].isAlive()
                        && "Rabbit".equals(field[i][j].getType()))
                    count++;
        return count < currentMax;
    }

    @Override
    protected Agent createChild(int cx, int cy, int e) {
        return new Rabbit(cx, cy, e, field, range);
    }

    private boolean inBounds(int i, int j) {
        return i >= 0 && i < range && j >= 0 && j < range;
    }
}