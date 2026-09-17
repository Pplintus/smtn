package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Wolf extends Agent {

    protected int visRange = 5;   // зрение у волка больше, чем у кролика, ибо кролики поахуевали
    public static int currentMax = 30;
    public static int currentDivideThreshold = 22;

    Wolf(int x, int y, int energy, Agent[][] field, int range) {
        super(x, y, energy, field, "Wolf", "Rabbit", range);
    }




    @Override
    public void act() {
        List<int[]> found = look("Rabbit");
        if (!found.isEmpty()) {
            int[] t = near(found);
            if (t != null) {
                move(t[0] - x, t[1] - y);
                finishTurn();
                return;
            }
        }

        // Кроликов не видно — блуждаем
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        int[] d = dirs[ThreadLocalRandom.current().nextInt(dirs.length)];
        move(d[0], d[1]);

        finishTurn();
    }

    @Override
    protected void eat(Agent prey) {
        if (prey == null || !prey.isAlive()) return;

        int px = prey.getX();
        int py = prey.getY();

        // Волк получает энергию кролика + бонус за охоту
        int bonus = 3;
        energy += prey.getEnergy() + bonus;

        field[x][y] = null;
        field[px][py] = null;
        prey.die();

        x = px; y = py;
        field[x][y] = this;
        energy -= 1;
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
                        && "Wolf".equals(field[i][j].getType()))
                    count++;
        return count < currentMax;
    }

    @Override
    protected Agent createChild(int cx, int cy, int e) {
        return new Wolf(cx, cy, e, field, range);
    }

    private boolean inBounds(int i, int j) {
        return i >= 0 && i < range && j >= 0 && j < range;
    }
}