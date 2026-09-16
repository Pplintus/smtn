import models.Environment;

public class Main {
    public static void main(String[] args) throws InterruptedException {

//        Environment world = new Environment(30, 300, 80, 12);
//
//        for (int step = 1; step <= 1000; step++) {
//            world.simulation();
//
//            if (step % 1 == 0) {
//                System.out.println("Шаг " + step);
//                world.show();
//                System.out.println();
//                printStats(world);
//            } else if (step % 500 == 0) {
//                System.out.printf("Шаг %d — Растения: %d, Кролики: %d, Волки: %d%n",
//                        step,
//                        world.count("Plant"),
//                        world.count("Rabbit"),
//                        world.count("Wolf"));
//            }
//
//            if (world.count("Rabbit") + world.count("Wolf") == 0) {
//                System.out.println("Все животные вымерли на шаге " + step);
//                break;
//            }
//            if (world.count("Wolf") == 0) {
//                System.out.println("Волки вымерли на шаге " + step);
//                break;
//            }
//            if (world.count("Rabbit") == 0) {
//                System.out.println("Кролики вымерли на шаге " + step);
//                break;
//            }
//        }

        Environment world = new Environment(30, 300, 80, 12);

        for (int step = 1; step <= 1000; step++) {
            world.simulation();

            // Очистить экран и вернуть курсор в начало
            System.out.print("\033[H\033[2J");
            System.out.flush();

            // Заголовок
            System.out.println("═══════════════════════════════════════════");
            System.out.printf("  Шаг: %d%n", step);
            System.out.println("═══════════════════════════════════════════");

            // Поле
            world.show();

            // Статистика
            printStats(world);

            // Условия выхода
            int rabbits = world.count("Rabbit");
            int wolves  = world.count("Wolf");
            if (rabbits + wolves == 0) {
                System.out.println("\n☠ Все животные вымерли на шаге " + step);
                break;
            }
            if (wolves == 0) {
                System.out.println("\n🐺 Волки вымерли на шаге " + step);
                break;
            }
            if (rabbits == 0) {
                System.out.println("\n🐇 Кролики вымерли на шаге " + step);
                break;
            }

            // Пауза, чтобы глаз успевал следить
            try {
                Thread.sleep(100); // 100 мс = 10 кадров/сек. Поставьте 50–200 под вкус
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

//        for (int i = 1; i <= 5; i++){
//            System.out.println("Попытка симуляции " + i );
//            Environment world = new Environment(30, 300, 80, 12);
//            for (int step = 2; step <= 50000; step++) {
//                world.simulation();
//
////                if (step % 500 == 0) {
////                    System.out.println("Шаг " + step);
////                    world.show();
////                    System.out.println();
////                    printStats(world);
////                } else if (step % 500 == 0) {
////                    System.out.printf("Шаг %d — Растения: %d, Кролики: %d, Волки: %d%n",
////                            step,
////                            world.count("Plant"),
////                            world.count("Rabbit"),
////                            world.count("Wolf"));
////                }
//
//                if (world.count("Plant")  == 0) {
//                    System.out.println("Трава сгнила на шаге " + step);
//                    break;
//                }
//                if (world.count("Wolf") == 0) {
//                    System.out.println("Волки вымерли на шаге " + step);
//                    break;
//                }
//                if (world.count("Rabbit") == 0) {
//                    System.out.println("Кролики вымерли на шаге " + step);
//                    break;
//                }
//            }
//
//        }
    }

    private static void printStats(Environment world) {
        System.out.printf("Растения: %d, Кролики: %d, Волки: %d%n%n",
                world.count("Plant"),
                world.count("Rabbit"),
                world.count("Wolf"));
    }
}