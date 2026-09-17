package gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class Sprites {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    private Sprites() {}

    public static BufferedImage get(String name) {
        return CACHE.computeIfAbsent(name, n -> {
            String path = "/" + n + ".png";
            try (InputStream in = Sprites.class.getResourceAsStream(path)) {
                if (in == null) {
                    System.err.println("Не найден ресурс: " + path);
                    return null;
                }
                return ImageIO.read(in);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static BufferedImage getScaled(String name, int w, int h) {
        BufferedImage src = get(name);
        if (src == null) return null;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }
}