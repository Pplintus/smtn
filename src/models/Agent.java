package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Agent {
    protected int x, y;
    protected int energy;
    protected int max_nrg = 12;
    // protected int v_range = 2; пока закомментим, все равно значения у кроликов и волков разные
    protected boolean alive = true;
    protected Agent[][] field;
    protected String type;
    protected String preyType;
    protected int range;

    public Agent(int x, int y, int energy, Agent[][] field, String type,
                 String preyType, int range) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.field = field;
        this.type = type;
        this.preyType = preyType;
        this.range = range;
    }

    public abstract void act();
    protected abstract List<int[]> look(String type);
    protected abstract int[] near(List<int[]> found);
    protected abstract Agent createChild(int cx, int cy, int childEnergy);



    protected void move(int dx, int dy) {
        int mainX, mainY, sideX, sideY;
        if (Math.abs(dx) >= Math.abs(dy)) {
            mainX = Integer.signum(dx); mainY = 0;
            sideX = 0; sideY = Integer.signum(dy);
        } else {
            mainX = 0; mainY = Integer.signum(dy);
            sideX = Integer.signum(dx); sideY = 0;
        }

        List<int[]> candidates = new ArrayList<>();
        candidates.add(new int[]{mainX, mainY});
        if (sideX != 0 || sideY != 0) {
            candidates.add(new int[]{sideX, sideY});
        }
        candidates.add(new int[]{mainX + sideX, mainY + sideY});
        candidates.add(new int[]{-mainX, -mainY});

        for (int[] c : candidates) {
            int sx = c[0], sy = c[1];
            if (sx == 0 && sy == 0) continue;

            int nx = x + sx;
            int ny = y + sy;

            if (nx < 0 || nx >= range || ny < 0 || ny >= range) continue;

            Agent other = field[nx][ny];

            if (other == null) {
                field[x][y] = null;
                x = nx; y = ny;
                field[x][y] = this;
                energy -= 1;
                return;
            }

            if (other.isAlive() && preyType != null
                    && other.getType().equals(preyType)) {
                eat(other);
                return;
            }
        }
        // Все заняты — стоим, не тратим
    }

    protected void eat(Agent prey) {
        if (prey == null || !prey.isAlive()) return;

        int px = prey.getX();
        int py = prey.getY();

        energy += prey.getEnergy();
        field[x][y] = null;
        field[px][py] = null;
        prey.die();

        x = px; y = py;
        field[x][y] = this;
        energy -= 1;
    }

    public void die() {
        alive = false;
        if (x >= 0 && x < range && y >= 0 && y < range && field[x][y] == this) {
            field[x][y] = null;
        }
    }

    protected static final int CHILD_BUFFER = 4; // дополнительная энергия к ребенку, чтоб была не половина

    public void div() {
        if (!canDivide()) return;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        List<int[]> free = new ArrayList<>();
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (nx >= 0 && nx < range && ny >= 0 && ny < range
                    && field[nx][ny] == null) {
                free.add(new int[]{nx, ny});
            }
        }
        if (free.isEmpty()) return;

        int[] spot = free.get(ThreadLocalRandom.current().nextInt(free.size()));
        int childEnergy = energy / 2 + CHILD_BUFFER;
        if (childEnergy >= energy) childEnergy = Math.max(1, energy - 1);
        energy -= childEnergy;
        field[spot[0]][spot[1]] = createChild(spot[0], spot[1], childEnergy);
    }

    protected boolean canDivide() { return true; }

    protected double len(int dx, int dy) {
        return Math.sqrt(Math.pow(x - dx, 2) + Math.pow(y - dy, 2));
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getEnergy() { return energy; }
    public boolean isAlive() { return alive; }
    public String getType() { return type; }
}