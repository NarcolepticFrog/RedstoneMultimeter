package narcolepticfrog.rsmm;

import narcolepticfrog.rsmm.meterable.Meter;
import narcolepticfrog.rsmm.util.Trace;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Collection;
import java.util.List;

public class MeterRenderer {

    private static final int TICK_WIDTH = 3; // The width of each tick in the display
    private static final int ROW_GAP = 1; // The vertical spacing between each meter
    private static final int NAME_TRACE_GAP = 3; // The amount of space between the names and meters
    private static final int BORDER = 1;

    private static final int BACKGROUND_COLOR = 0xFF202020;
    private static final int MAJOR_GRID_COLOR = 0xFF606060;
    private static final int MINOR_GRID_COLOR = 0xFF404040;

    private static final int POWERED_TEXT_COLOR = 0xFF000000;
    private static final int UNPOWERED_TEXT_COLOR = 0xFF707070;
    private static final int METER_NAME_COLOR = 0xFFFFFFFF;
    private static final int PAUSED_TEXT_COLOR = 0xFF000000;

    private int windowLength; // The number of ticks to show in the render
    private int windowStartTick; // The most recent tick to show in the render

    public MeterRenderer(int windowLength) {
        setWindowLength(windowLength);
        setWindowStartTick(0);
    }

    public void setWindowLength(int windowLength) {
        this.windowLength = windowLength;
    }

    public int getWindowLength() {
        return windowLength;
    }

    public void setWindowStartTick(int windowStartTick) {
        this.windowStartTick = windowStartTick;
    }

    public int getWindowStartTick() {
        return windowStartTick;
    }

    public void renderMeterHighlights(Collection<Meter> meters, float partialTicks) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        for (Meter m : meters) {
            if (player.dimension == m.getDimension()) {
                int color = m.getColor();

                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;

                AxisAlignedBB aabb = Block.FULL_BLOCK_AABB.offset(m.getPosition()).offset(-dx, -dy, -dz).grow(0.002);
                RenderGlobal.renderFilledBox(aabb, r, g, b, 0.5F);
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Renders the GUI overlay.
     */
    public void renderMeterTraces(List<Meter> meters, boolean paused) {

        if (meters.size() == 0) {
            return;
        }

        int namesWidth = this.namesWidth(meters);
        int totalWidth = this.totalWidth(namesWidth);
        int totalHeight = this.totalHeight(meters.size());

        renderBackground(totalWidth, totalHeight);
        renderNames(namesWidth, meters);
        renderPoweredRectangles(totalWidth, meters);
        renderMeterGrid(namesWidth, totalWidth, totalHeight, meters.size());
        renderPulseDurations(totalWidth, paused, meters);
        renderPauseNotification(totalHeight, paused);

    }

    /**
     * Returns the width (in pixels) of the longest name for any meter.
     */
    private int namesWidth(List<Meter> meters) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        int width = 0;
        for (Meter m : meters) {
            width = Math.max(width, fr.getStringWidth(m.getName()));
        }
        return width;
    }

    /**
     * Calculates the total width of the GUI overlay.
     */
    private int totalWidth(int namesWidth) {
        return 2*BORDER + namesWidth + NAME_TRACE_GAP + TICK_WIDTH*windowLength;
    }

    /**
     * Calculates the total height of the GUI overlay.
     */
    private int totalHeight(int numMeters) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        return 2*BORDER + fr.FONT_HEIGHT*numMeters + ((numMeters != 0) ? ROW_GAP*(numMeters-1) : 0);
    }

    /**
     * Draws the background for the GUI overlay.
     */
    private void renderBackground(int width, int height) {
        Gui.drawRect(0, 0, width, height, BACKGROUND_COLOR);
    }

    /**
     * Draws the names of the meters on the left hand side of the overlay.
     */
    private void renderNames(int namesWidth, List<Meter> meters) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        for (int i = 0; i < meters.size(); i++) {
            Meter m = meters.get(i);
            int width = fr.getStringWidth(m.getName());
            int xPos = BORDER + namesWidth - width + 1;
            int yPos = BORDER + i*fr.FONT_HEIGHT + ((i != 0) ? i*ROW_GAP : 0) + 1;
            fr.drawString(m.getName(), xPos, yPos, METER_NAME_COLOR);
        }
    }

    /**
     * Draws a colored bar for each powered interval for each meter within the window
     */
    private void renderPoweredRectangles(int totalWidth, List<Meter> meters) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        for (int i = 0; i < meters.size(); i++) {
            Meter m = meters.get(i);
            int top = BORDER + i*fr.FONT_HEIGHT + ((i != 0) ? i*ROW_GAP : 0);
            int bot = top + fr.FONT_HEIGHT;

            Trace<Meter.PowerInterval> intervals = m.getPowerIntervals();
            for (int t = 0; t < intervals.size(); t++) {
                Meter.PowerInterval interval = intervals.get(t);
                if (interval.isPowered()) {
                    int shiftedStart = windowStartTick - interval.getStartTick();
                    int shiftedEnd = windowStartTick - interval.getEndTick();
                    // Skip any blocks that start after the window
                    if (shiftedStart < 0) {
                        continue;
                    }
                    // Stop rendering once the bar is completely before the window
                    if (shiftedEnd > windowLength) {
                        break;
                    }
                    // Adjust the start and end to be within the window
                    int clippedStart = Math.min(shiftedStart, windowLength-1);
                    int clippedEnd = Math.max(shiftedEnd, 0);

                    int left = totalWidth - BORDER - (clippedStart+1)*TICK_WIDTH;
                    int right = totalWidth - BORDER - (clippedEnd)*TICK_WIDTH;
                    Gui.drawRect(left, top, right, bot, m.getColor());
                }
            }
        }
    }

    private void renderMeterGrid(int namesWidth, int totalWidth, int totalHeight, int numMeters) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        // Draw horizontal lines
        for (int i = 0; i <= numMeters; i++) {
            int y = i*fr.FONT_HEIGHT + ((i != 0) ? i * ROW_GAP : 0);
            Gui.drawRect(BORDER + namesWidth + NAME_TRACE_GAP, y, totalWidth, y+1, MINOR_GRID_COLOR);
        }
        // Draw vertical lines
        for (int t = 0; t <= windowLength; t++) {
            int x = totalWidth - BORDER - t*TICK_WIDTH;
            int color = MINOR_GRID_COLOR;
            if (t % 5 == 0) {
                color = MAJOR_GRID_COLOR;
            }
            Gui.drawRect(x, 0, x+1, totalHeight, color);
        }
    }

    private void renderPulseDurations(int totalWidth, boolean paused, List<Meter> meters) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        for (int i = 0; i < meters.size(); i++) {
            Meter m = meters.get(i);
            int top = BORDER + i*fr.FONT_HEIGHT + ((i != 0) ? i*ROW_GAP : 0);
            int bot = top + fr.FONT_HEIGHT;

            Trace<Meter.PowerInterval> powerIntervals = m.getPowerIntervals();
            for (int t = 1; t < powerIntervals.size(); t++) {
                Meter.PowerInterval interval = powerIntervals.get(t);
                int timeSinceStart = (int)(windowStartTick - interval.getStartTick());
                int timeSinceEnd = (int)(windowStartTick - interval.getEndTick());
                int length = timeSinceStart - timeSinceEnd + 1;
                // Don't render anything outside the history
                if (timeSinceEnd > windowLength) {
                    break;
                }
                if (timeSinceStart < 0) {
                    continue;
                }
                timeSinceStart = Math.min(windowLength-1, timeSinceStart);

                if (length >= 5) {
                    int left = totalWidth - BORDER - (timeSinceStart + 1) * TICK_WIDTH + 1;
                    int right = totalWidth - BORDER - timeSinceEnd * TICK_WIDTH;
                    String lengthStr = "" + length;
                    int width = fr.getStringWidth(lengthStr) + 1;

                    if (left + width + 1 <= totalWidth && left + width + 1 <= right) {

                        int color = m.getColor();
                        int textColor = POWERED_TEXT_COLOR;
                        if (!interval.isPowered()) {
                            color = BACKGROUND_COLOR;
                            textColor = UNPOWERED_TEXT_COLOR;
                        }

                        Gui.drawRect(left, top, left + width + 1, bot, color);
                        fr.drawString(lengthStr, left + 1, top + 1, textColor);
                    }
                }
            }
        }
    }

    private void renderPauseNotification(int totalHeight, boolean paused) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        if (paused) {
            fr.drawString(I18n.format("redstonemultimeter.ui.paused"), BORDER, totalHeight + 3, PAUSED_TEXT_COLOR);
        }
    }

}
