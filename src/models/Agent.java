package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Agent {
    protected int x, y;
    protected int startEnergy;
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
        this.startEnergy = energy;
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
        int tx, ty;
        if(Math.abs(dx)>=Math.abs(dy)){ tx = Integer.signum(dx);; ty = 0;
        } else {tx = 0; ty = Integer.signum(dy);}

        int[][] directions = {
                {tx, ty},
                {-tx, -ty},
                {ty, -tx},
                {-ty, tx}
        };

        for (int i = 0; i < directions.length; i++) {
            int nx = x + directions[i][0];
            int ny = y + directions[i][1];

            if (nx >= 0 && nx < range && ny >= 0 && ny < range) {
                if (field[nx][ny] == null) {
                    field[x][y] = null;
                    x = nx;
                    y = ny;
                    field[x][y] = this;
                    return;
                } else if (field[nx][ny].isAlive() && field[nx][ny].getType().equals(preyType)) {
                    eat(field[nx][ny]);
                    return;
                }
            }
        }
        // Если все направления заняты, остаёмся на месте
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
    }

    public void die() {
        alive = false;
        if (x >= 0 && x < range && y >= 0 && y < range && field[x][y] == this) {
            field[x][y] = null;
        }
    }


    public void div() {

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        List<int[]> free = new ArrayList<>();
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (nx >= 0 && nx < range && ny >= 0 && ny < range
                    && field[nx][ny] == null) {
                free.add(new int[]{nx, ny});
            }
        }
        if (free.isEmpty()) { energy = startEnergy;return; }

        int[] spot = free.get(ThreadLocalRandom.current().nextInt(free.size()));
        int childEnergy = startEnergy;
        energy = startEnergy;
        field[spot[0]][spot[1]] = createChild(spot[0], spot[1], childEnergy);
    }


    protected double len(int dx, int dy) {
        return Math.sqrt(Math.pow(x - dx, 2) + Math.pow(y - dy, 2));
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getEnergy() { return energy; }
    public boolean isAlive() { return alive; }
    public String getType() { return type; }
}