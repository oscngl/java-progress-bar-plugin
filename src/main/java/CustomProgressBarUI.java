import com.intellij.openapi.ui.GraphicsConfig;
import com.intellij.ui.JBColor;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.GraphicsUtil;
import com.intellij.util.ui.JBUI;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class CustomProgressBarUI extends BasicProgressBarUI {

    private static final JBColor TRANSPARENT = new JBColor(new Color(0f, 0f, 0f, 0f), new Color(0f, 0f, 0f, 0f));

    public CustomProgressBarUI() {}

    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        c.setBorder(JBUI.Borders.empty().asUIResource());
        return new CustomProgressBarUI();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(super.getPreferredSize(c).width, JBUIScale.scale(20));
    }

    private int offset = 0;
    private volatile int velocity = 1;

    @Override
    protected void paintIndeterminate(Graphics graphics, JComponent jComponent) {
        if (!(graphics instanceof Graphics2D graphics2D)) {
            return;
        }

        Insets barInsets = progressBar.getInsets();
        int barRectWidth = progressBar.getWidth() - (barInsets.right + barInsets.left);
        int barRectHeight = progressBar.getHeight() - (barInsets.top + barInsets.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        graphics2D.setColor(TRANSPARENT);
        int width = jComponent.getWidth();
        int height = jComponent.getPreferredSize().height;

        if (!isEven(jComponent.getHeight() - height)) {
            height++;
        }

        if (jComponent.isOpaque()) {
            graphics2D.fillRect(0, (jComponent.getHeight() - height) / 2, width, height);
        }

        graphics2D.setColor(TRANSPARENT);

        final GraphicsConfig config = GraphicsUtil.setupAAPainting(graphics2D);

        graphics2D.translate(0, (jComponent.getHeight() - height) / 2);

        final float r = JBUIScale.scale(8f);
        final Area containingRoundRect = new Area(new RoundRectangle2D.Float(1f, 1f, width - 2f, height - 2f, r, r));
        graphics2D.fill(containingRoundRect);

        offset += velocity;
        if (offset <= 2) {
            offset = 2;
            velocity = 1;
        } else if (offset >= width - JBUIScale.scale(10)) {
            offset = width - JBUIScale.scale(10);
            velocity = -1;
        }

        Area area = new Area(new Rectangle2D.Float(0, 0, width, height));
        area.subtract(new Area(new RoundRectangle2D.Float(1f, 1f, width - 2f, height - 2f, r, r)));

        if (jComponent.isOpaque()) {
            graphics2D.fill(area);
        }

        final float r2 = JBUIScale.scale(9f);
        area.subtract(new Area(new RoundRectangle2D.Float(0, 0, width, height, r2, r2)));
        jComponent.getParent();

        if (jComponent.isOpaque()) {
            graphics2D.fill(area);
        }

        ProgressBarIcons.JAVA_ICON.paintIcon(progressBar, graphics2D, offset - JBUI.scale(5), JBUI.scale(1));

        graphics2D.draw(new RoundRectangle2D.Float(1f, 1f, width - 2f - 1f, height - 2f - 1f, r, r));
        graphics2D.translate(0, -(jComponent.getHeight() - height) / 2);

        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
                paintString(graphics2D, barInsets.left, barInsets.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width);
            } else {
                paintString(graphics2D, barInsets.left, barInsets.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height);
            }
        }

        config.restore();
    }

    @Override
    protected void paintDeterminate(Graphics graphics, JComponent jComponent) {
        if (!(graphics instanceof Graphics2D graphics2D)) {
            return;
        }

        if (progressBar.getOrientation() != SwingConstants.HORIZONTAL
                || !jComponent.getComponentOrientation().isLeftToRight()) {
            super.paintDeterminate(graphics, jComponent);
            return;
        }

        final GraphicsConfig config = GraphicsUtil.setupAAPainting(graphics);

        Insets barInsets = progressBar.getInsets();
        int barWidth = progressBar.getWidth();
        int height = progressBar.getPreferredSize().height;

        if (!isEven(jComponent.getHeight() - height)) {
            height++;
        }

        int barRectWidth = barWidth - (barInsets.right + barInsets.left);
        int barRectHeight = height - (barInsets.top + barInsets.bottom);

        if (barRectWidth <= 0
                || barRectHeight <= 0) {
            return;
        }

        int amountFull = getAmountFull(barInsets, barRectWidth, barRectHeight);

        graphics.setColor(TRANSPARENT);

        if (jComponent.isOpaque()) {
            graphics.fillRect(0, 0, barWidth, height);
        }

        final float r = JBUIScale.scale(8f);
        final float r2 = JBUIScale.scale(9f);
        final float off = JBUIScale.scale(1f);

        graphics2D.translate(0, (jComponent.getHeight() - height) / 2);
        graphics2D.setColor(TRANSPARENT);

        graphics2D.fill(new RoundRectangle2D.Float(0, 0, barWidth - off, height - off, r2, r2));
        graphics2D.setColor(TRANSPARENT);

        graphics2D.fill(new RoundRectangle2D.Float(off, off, barWidth - 2f * off - off, height - 2f * off - off, r, r));
        graphics2D.translate(0, -(jComponent.getHeight() - height) / 2);

        ProgressBarIcons.JAVA_ICON.paintIcon(progressBar, graphics2D, amountFull - JBUIScale.scale(5), -JBUIScale.scale(1));

        graphics2D.fill(new RoundRectangle2D.Float(2f * off, 2f * off,
                amountFull - JBUIScale.scale(5f), height - JBUIScale.scale(5f),
                JBUIScale.scale(7f), JBUIScale.scale(7f)));
        graphics2D.translate(0, -(jComponent.getHeight() - height) / 2);

        if (progressBar.isStringPainted()) {
            paintString(graphics, barInsets.left, barInsets.top, barRectWidth, barRectHeight, amountFull, barInsets);
        }

        config.restore();
    }

    private void paintString(Graphics graphics, int x, int y, int w, int h, int fillStart, int amountFull) {
        if (!(graphics instanceof Graphics2D graphics2D)) {
            return;
        }

        String progressString = progressBar.getString();
        Point renderLocation = getStringPlacement(graphics2D, progressString, x, y, w, h);

        graphics2D.setFont(progressBar.getFont());
        Rectangle oldClip = graphics2D.getClipBounds();

        if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
            graphics2D.setColor(TRANSPARENT);
            BasicGraphicsUtils.drawString(progressBar, graphics2D, progressString, renderLocation.x, renderLocation.y);

            graphics2D.setColor(TRANSPARENT);
            graphics2D.clipRect(fillStart, y, amountFull, h);
            BasicGraphicsUtils.drawString(progressBar, graphics2D, progressString, renderLocation.x, renderLocation.y);
        } else {
            graphics2D.setColor(TRANSPARENT);
            AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 2);
            graphics2D.setFont(progressBar.getFont().deriveFont(rotate));

            renderLocation = getStringPlacement(graphics2D, progressString, x, y, w, h);
            BasicGraphicsUtils.drawString(progressBar, graphics2D, progressString, renderLocation.x, renderLocation.y);

            graphics2D.setColor(TRANSPARENT);
            graphics2D.clipRect(x, fillStart, w, amountFull);
            BasicGraphicsUtils.drawString(progressBar, graphics2D, progressString, renderLocation.x, renderLocation.y);
        }

        graphics2D.setClip(oldClip);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isEven(int value) {
        return value % 2 == 0;
    }

}
