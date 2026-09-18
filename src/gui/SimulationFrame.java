package gui;

import models.Agent;
import models.Environment;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {

    private final Environment world;
    private final WorldPanel worldPanel;

    private final JLabel stepLabel   = new JLabel("Шаг: 0");
    private final JLabel plantLabel  = new JLabel("Растения: 0");
    private final JLabel rabbitLabel = new JLabel("Кролики: 0");
    private final JLabel wolfLabel   = new JLabel("Волки: 0");

    /** Информация о выбранном кликом агенте. */
    private final JLabel infoLabel = new JLabel("Кликните по агенту, чтобы увидеть его энергию");

    private final JButton startBtn = new JButton("Старт");
    private final JButton pauseBtn = new JButton("Пауза");
    private final JButton stepBtn  = new JButton("Шаг");

    private final JSlider speedSlider = new JSlider(1, 200, 30);

    private volatile boolean running = false;
    private volatile boolean alreadyStopped = false;
    private Thread loopThread;

    public SimulationFrame(Environment world, int cellSize) {
        super("Экосистема");
        this.world = world;
        this.worldPanel = new WorldPanel(world, cellSize);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(worldPanel, BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        updateStats();

        startBtn.addActionListener(e -> start());
        pauseBtn.addActionListener(e -> pause());
        stepBtn.addActionListener(e -> doOneStep());

        // реакция на клик по агенту
        worldPanel.setOnAgentClick(this::showAgentInfo);

        pauseBtn.setEnabled(false);
    }

    private JPanel buildControls() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.add(startBtn);
        p.add(pauseBtn);
        p.add(stepBtn);
        p.add(new JLabel("Скорость:"));
        p.add(speedSlider);
        p.add(stepLabel);
        p.add(plantLabel);
        p.add(rabbitLabel);
        p.add(wolfLabel);
        p.add(infoLabel);   // информация о клике
        return p;
    }

    /** Показ информации об агенте, по которому кликнули. */
    private void showAgentInfo(Agent a) {
        String type = switch (a.getType()) {
            case "Plant"  -> "Растение";
            case "Rabbit" -> "Кролик";
            case "Wolf"   -> "Волк";
            default       -> a.getType();
        };

        String text = String.format(
                "%s  |  энергия: %d  |  позиция: (%d, %d)",
                type, a.getEnergy(), a.getX(), a.getY());

        infoLabel.setText(text);
    }

    private void start() {
        if (running) return;
        running = true;
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);

        loopThread = new Thread(this::gameLoop, "sim-loop");
        loopThread.setDaemon(true);
        loopThread.start();
    }

    private void pause() {
        running = false;
        if (SwingUtilities.isEventDispatchThread()) {
            startBtn.setEnabled(true);
            pauseBtn.setEnabled(false);
        } else {
            SwingUtilities.invokeLater(() -> {
                startBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
            });
        }
    }

    private void doOneStep() {
        world.step();
        updateStats();
        worldPanel.refresh();
        checkExtinctionOnce();
    }

    private void gameLoop() {
        while (running) {
            world.step();

            final int plants  = world.count("Plant");
            final int rabbits = world.count("Rabbit");
            final int wolves  = world.count("Wolf");
            final int step    = world.getStep();

            SwingUtilities.invokeLater(() -> {
                stepLabel.setText("Шаг: " + step);
                plantLabel.setText("Растения: " + plants);
                rabbitLabel.setText("Кролики: " + rabbits);
                wolfLabel.setText("Волки: " + wolves);
                worldPanel.refresh();
            });

            String extinct = null;
            if (plants == 0)  extinct = "Растения вымерли";
            if (rabbits == 0) extinct = "Кролики вымерли";
            if (wolves == 0)  extinct = "Волки вымерли";

            if (extinct != null) {
                final String msg = extinct;
                final int finalStep = step;
                running = false;
                SwingUtilities.invokeLater(() -> {
                    startBtn.setEnabled(true);
                    pauseBtn.setEnabled(false);
                    if (!alreadyStopped) {
                        alreadyStopped = true;
                        JOptionPane.showMessageDialog(this,
                                msg + " на шаге " + finalStep,
                                "Симуляция завершена",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                return;
            }

            int delay = 210 - speedSlider.getValue();
            if (delay < 1) delay = 1;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void updateStats() {
        stepLabel.setText("Шаг: " + world.getStep());
        plantLabel.setText("Растения: " + world.count("Plant"));
        rabbitLabel.setText("Кролики: " + world.count("Rabbit"));
        wolfLabel.setText("Волки: " + world.count("Wolf"));
    }

    private void checkExtinctionOnce() {
        String extinct = null;
        if (world.count("Plant") == 0)  extinct = "Растения вымерли";
        if (world.count("Rabbit") == 0) extinct = "Кролики вымерли";
        if (world.count("Wolf") == 0)   extinct = "Волки вымерли";
        if (extinct != null && !alreadyStopped) {
            alreadyStopped = true;
            running = false;
            startBtn.setEnabled(true);
            pauseBtn.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    extinct + " на шаге " + world.getStep(),
                    "Симуляция завершена",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}