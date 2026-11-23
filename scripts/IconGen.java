import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;

/** Draws the MEditor icon (1024x1024 PNG). Usage: java scripts/IconGen.java out.png */
public class IconGen {
    public static void main(String[] a) throws Exception {
        int S = 1024;
        BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // macOS icon grid: ~824px rounded square centered in 1024
        RoundRectangle2D bg = new RoundRectangle2D.Double(100, 100, 824, 824, 185, 185);
        g.setPaint(new GradientPaint(0, 100, new Color(0x3B82F6), 0, 924, new Color(0x1E3A8A)));
        g.fill(bg);

        // soft shadow + white page
        RoundRectangle2D page = new RoundRectangle2D.Double(262, 215, 500, 594, 44, 44);
        g.setColor(new Color(0, 0, 0, 55));
        g.fill(new RoundRectangle2D.Double(262, 235, 500, 594, 44, 44));
        g.setColor(new Color(0xFAFAFA));
        g.fill(page);

        // text lines
        g.setColor(new Color(0x94A3B8));
        int[] widths = {330, 270, 330, 200};
        for (int i = 0; i < widths.length; i++) {
            g.fill(new RoundRectangle2D.Double(322, 300 + i * 82, widths[i], 30, 15, 15));
        }
        // last line is being typed: highlight + caret
        g.setColor(new Color(255, 200, 80, 190));
        g.fill(new RoundRectangle2D.Double(322, 300 + 4 * 82 - 6, 150, 42, 10, 10));
        g.setColor(new Color(0x1E40AF));
        g.fill(new RoundRectangle2D.Double(494, 300 + 4 * 82 - 14, 14, 58, 7, 7));

        // short final line
        g.setColor(new Color(0x94A3B8));
        g.fill(new RoundRectangle2D.Double(322, 300 + 5 * 82, 130, 30, 15, 15));
        g.dispose();
        ImageIO.write(img, "png", new File(a[0]));
    }
}
