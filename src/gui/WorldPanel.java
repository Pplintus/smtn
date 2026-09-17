package gui;

import models.Agent;
import models.Environment;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WorldPanel extends JPanel {
    private final Environment world;
    private final int cellSize;

    private final BufferedImage plantImg;
    private final BufferedImage rabbitImg;
    private final BufferedImage wolfImg;
    private final BufferedImage dirtImg;

    public WorldPanel(Environment world, int cellSize) {
        this.world = world;
        this.cellSize = cellSize;

        this.plantImg  = Sprites.getScaled("plant",  cellSize, cellSize);
        this.rabbitImg = Sprites.getScaled("rabbit", cellSize, cellSize);
        this.wolfImg   = Sprites.getScaled("wolf",   cellSize, cellSize);
        this.dirtImg   = Sprites.getScaled("dirt",   cellSize, cellSize);

        setPreferredSize(new Dimension(
                world.getRange() * cellSize,
                world.getRange() * cellSize));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int range = world.getRange();
        Agent[][] field = world.field;

        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                Agent a = field[i][j];
                int px = j * cellSize;
                int py = i * cellSize;

                if (a == null || !a.isAlive()) {
                    // пустая клетка → земля
                    if (dirtImg != null) {
                        g2.drawImage(dirtImg, px, py, null);
                    } else {
                        g2.setColor(new Color(210, 190, 160));
                        g2.fillRect(px, py, cellSize, cellSize);
                    }
                } else {
                    // сначала земля под агентом — чтобы фон не был «дырявым»
                    if (dirtImg != null) {
                        g2.drawImage(dirtImg, px, py, null);
                    } else {
                        g2.setColor(new Color(210, 190, 160));
                        g2.fillRect(px, py, cellSize, cellSize);
                    }

                    BufferedImage img = switch (a.getType()) {
                        case "Plant"  -> plantImg;
                        case "Rabbit" -> rabbitImg;
                        case "Wolf"   -> wolfImg;
                        default       -> null;
                    };

                    if (img != null) {
                        g2.drawImage(img, px, py, null);
                    } else {
                        g2.setColor(Color.MAGENTA);
                        g2.fillRect(px, py, cellSize, cellSize);
                    }
                }

                g2.setColor(new Color(220, 220, 220));
                g2.drawRect(px, py, cellSize, cellSize);
            }
        }

        g2.dispose();
    }

    public void refresh() { repaint(); }
}