//import models.Environment;

//public class Main {
//    public static void main(String[] args) throws InterruptedException {
//
////        Environment world = new Environment(30, 300, 80, 12);
////        world.showOne(1000);
//
//
//
//        for (int i = 1; i <= 5; i++){
//            System.out.println("Попытка симуляции " + i );
//            Environment world = new Environment(30, 300, 80, 12);
//            world.showMore(100000);
//        }
//    }
//
//
//}

import gui.SimulationFrame;
import models.Environment;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int range = 30;
            Environment world = new Environment(range, 300, 150, 12);
            int cellSize = 20;
            new SimulationFrame(world, cellSize).setVisible(true);
        });
    }
}