import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Element;

public class TextLineNumber extends JComponent {
    private final JTextArea textArea;

    public TextLineNumber(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(120, 120, 120));

        int start = textArea.viewToModel(g.getClipBounds().getLocation());
        int end = textArea.viewToModel(new Point(g.getClipBounds().x, g.getClipBounds().y + g.getClipBounds().height));

        Element root = textArea.getDocument().getDefaultRootElement();

        while (start <= end) {
            int line = root.getElementIndex(start);
            if (line < 0) break;
            try {
                String lineNumber = String.valueOf(line + 1);
                int y = textArea.modelToView(start).y + textArea.getFontMetrics(textArea.getFont()).getAscent();
                g.drawString(lineNumber, getWidth() - (getFontMetrics(getFont()).stringWidth(lineNumber) + 8), y);
                start = root.getElement(line).getEndOffset();
            } catch (Exception e) {
                break;
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, textArea.getHeight());
    }
}