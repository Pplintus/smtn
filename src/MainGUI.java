import gui.SimulationFrame;
import models.Environment;

import javax.swing.*;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int range = 30;
            Environment world = new Environment(range, 300, 150, 22);
            int cellSize = 20;
            new SimulationFrame(world, cellSize).setVisible(true);
        });
    }
}