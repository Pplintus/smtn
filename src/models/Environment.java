package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Environment {
    int range;
    int all_Ag;
    int num_P;
    int num_R;
    int num_W;

    public Agent[][] field;

    private int stepCounter = 0;


    public Environment(int range, int num_P, int num_R, int num_W) {
        this.range = range;
        this.num_P = num_P;
        this.num_R = num_R;
        this.num_W = num_W;
        this.all_Ag = num_P + num_R + num_W;

        this.field = new Agent[range][range];

        if (all_Ag > range * range) {
            throw new IllegalArgumentException(
                    "Слишком много агентов: " + all_Ag + " > " + (range * range)
            );
        }

        placeAgents(num_P, "Plant", 8);
        placeAgents(num_R, "Rabbit", 15);
        placeAgents(num_W, "Wolf", 15);
    }

    public Environment(int range) {
        this(range, range, range, range);
    }


    private void placeAgents(int count, String type, int startEnergy) {
        int placed = 0;
        int safetyCounter = 0;
        int maxAttempts = range * range * 100; // заглушка, если в начальных условиях слишком много агентов

        while (placed < count && safetyCounter < maxAttempts) {
            int dx = ThreadLocalRandom.current().nextInt(0, range);
            int dy = ThreadLocalRandom.current().nextInt(0, range);
            safetyCounter++;

            if (field[dx][dy] == null) {
                int jitter = ThreadLocalRandom.current().nextInt(-2, 3);
                int startNrg = Math.max(1, startEnergy + jitter);
                field[dx][dy] = createAgent(type, dx, dy, startNrg);
                placed++;
            }
        }
    }

    private Agent createAgent(String type, int x, int y, int energy) {
        switch (type) {
            case "Plant":  return new Plant(x, y, energy, field, range);
            case "Rabbit": return new Rabbit(x, y, energy, field, range);
            case "Wolf":   return new Wolf(x, y, energy, field, range);
            default: throw new IllegalArgumentException(type);
        }
    }

    public void showOne(int maxIter){
        for (int step = 1; step <= maxIter; step++) {
            simulation();

            System.out.println("Шаг " + step);
            show();
            System.out.println();
            printStats();

            if (count("Plant")  == 0) {
                System.out.println("Растения вымерли на шаге " + step);
                break;
            }
            if (count("Wolf") == 0) {
                System.out.println("Волки вымерли на шаге " + step);
                break;
            }
            if (count("Rabbit") == 0) {
                System.out.println("Кролики вымерли на шаге " + step);
                break;
            }
        }
    }

    public void showMore(int maxIter){
        for (int step = 1; step <= maxIter; step++) {
            simulation();

            if (count("Plant")  == 0) {
                System.out.println("Растения вымерли на шаге " + step);
                break;
            }
            if (count("Wolf") == 0) {
                System.out.println("Волки вымерли на шаге " + step);
                break;
            }
            if (count("Rabbit") == 0) {
                System.out.println("Кролики вымерли на шаге " + step);
                break;
            }
        }
    }

    private void printStats() {
        System.out.printf("Растения: %d, Кролики: %d, Волки: %d%n%n",
                count("Plant"),
                count("Rabbit"),
                count("Wolf"));
    }


    public void simulation() {
        stepCounter++;

        // создаем копию и проходимся по ней. Копия - список. Оригинал - двумерный массив
        List<Agent> snapshot = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                if (field[i][j] != null && field[i][j].isAlive()) {
                    snapshot.add(field[i][j]);
                }
            }
        }

        for (Agent agent : snapshot) {
            if (agent.isAlive()) {
                agent.act();
            }
        }

    }

    public void show() {
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                String cellContent = " "; // По умолчанию клетка пустая (один пробел)

                if (field[i][j] != null && field[i][j].isAlive()) {
                    switch (field[i][j].getType()) {
                        case "Plant":
                            cellContent = "*";  // Plant
                            break;
                        case "Rabbit":
                            cellContent = "T";  // Rabbit
                            break;
                        case "Wolf":
                            cellContent = "X";  // Wolf
                            break;
                    }
                }

                // %-3s означает: вывести строку, выровнять по левому краю
                // и дополнить пробелами до строго 3 символов в ширину
                System.out.printf("|%-3s", cellContent);
            }
            System.out.printf("|%n");
        }
    }



    public int count(String type) {
        int c = 0;
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                if (field[i][j] != null && field[i][j].isAlive()
                        && type.equals(field[i][j].getType())) {
                    c++;
                }
            }
        }
        return c;
    }

    public int getRange() { return range; }

    public int getStep() { return stepCounter; }

    public void step() { simulation(); }
}