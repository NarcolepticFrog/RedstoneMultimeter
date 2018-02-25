package narcolepticfrog.rsmm;

import com.mumfrey.liteloader.gl.GL;
import narcolepticfrog.rsmm.clock.SubtickClock;
import narcolepticfrog.rsmm.clock.SubtickTime;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.List;

public class MeterRenderer {

    private static final int TICK_WIDTH = 4; // The width of each tick in the display
    private static final int ROW_GAP = 1; // The vertical spacing between each meter
    private static final int NAME_TRACE_GAP = 3; // The amount of space between the names and meters
    private static final int SUBTICK_GAP = 3; // The amount of space between the regular meter and subtick meter.
    private static final int BORDER = 1;

    private static final int BACKGROUND_COLOR = 0xFF202020;
    private static final int MAJOR_GRID_COLOR = 0xFF606060;
    private static final int MINOR_GRID_COLOR = 0xFF404040;
    private static final int SELECTED_TICK_COLOR = 0xFFFFFFFF;

    private static final int POWERED_TEXT_COLOR = 0xFF000000;
    private static final int UNPOWERED_TEXT_COLOR = 0xFF707070;
    private static final int METER_NAME_COLOR = 0xFFFFFFFF;
    private static final int PAUSED_TEXT_COLOR = 0xFF000000;

    private SubtickClock clock;
    private int windowLength; // The number of ticks to show in the render
    private int windowStartTick; // The most recent tick to show in the render
    private String groupName = "";

    public MeterRenderer(SubtickClock clock, int windowLength) {
        this.clock = clock;
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

    public int getSelectedTick() {
        return windowStartTick - windowLength/4;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void renderMeterHighlights(Collection<Meter> meters, float partialTicks) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        for (Meter m : meters) {
            if (player.dimension == m.getDimension()) {
                int color = m.getColor();

                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;

                AxisAlignedBB aabb = Block.FULL_BLOCK_AABB.offset(m.getDimPos().getPos()).offset(-dx, -dy, -dz).grow(0.002);
                RenderGlobal.renderFilledBox(aabb, r, g, b, 0.5F);
                if (m.isMovable()) {
                    RenderGlobal.drawBoundingBox(aabb.minX, aabb.minY, aabb.minZ,
                            aabb.maxX, aabb.maxY, aabb.maxZ,
                            r, g, b, 2);
                }
            }
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

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
        renderSelectedTickMarker(totalWidth, totalHeight);
        renderSubtick(totalWidth, totalHeight, meters, paused);

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

            for (int t = windowStartTick; t > windowStartTick - windowLength; t--) {
                int left = totalWidth - BORDER - (windowStartTick - t + 1)*TICK_WIDTH;
                int right = totalWidth - BORDER - (windowStartTick - t)*TICK_WIDTH;

                if (m.wasPoweredEntireTick(t)) {
                    Gui.drawRect(left, top, right, bot, m.getColor());
                } else if (m.wasPoweredAtStart(t)) {
                    Gui.drawRect(left, top, right, bot, m.getColor());
                    Gui.drawRect(left+2, top+1, right-1, bot-1, BACKGROUND_COLOR);
                } else if (m.wasPoweredDuring(t)) {
                    Gui.drawRect(left+2, top+1, right-1, bot-1, m.getColor());
                }

                if (m.movedDuring(t)) {
                    int height = (top + bot) / 2;
                    Gui.drawRect(left, height-1, right, height, m.getColor());
                    Gui.drawRect(left, height, right, height+1, 0xFFFFFFFF);
                    Gui.drawRect(left, height+1, right, height+2, m.getColor());
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

    private void renderSelectedTickMarker(int totalWidth, int totalHeight) {
        int t = getSelectedTick();
        int left = totalWidth - BORDER - (windowStartTick - t)*TICK_WIDTH;
        int right = totalWidth - BORDER - (windowStartTick - t + 1)*TICK_WIDTH;
        int top = 0;
        int bottom = totalHeight;

        Gui.drawRect(left, top, left+1, bottom, SELECTED_TICK_COLOR);
        Gui.drawRect(right, top, right+1, bottom, SELECTED_TICK_COLOR);
        Gui.drawRect(left, top, right, top+1, SELECTED_TICK_COLOR);
        Gui.drawRect(left, bottom-1, right, bottom, SELECTED_TICK_COLOR);
    }

    private void renderSubtick(int totalWidth, int totalHeight, List<Meter> meters, boolean paused) {
        int tick = getSelectedTick();
        int numSubticks = clock.tickLength(tick);
        if (numSubticks == 0 || !paused) {
            return;
        }

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        Gui.drawRect(totalWidth+SUBTICK_GAP, 0,
                totalWidth + SUBTICK_GAP + numSubticks*TICK_WIDTH, totalHeight, BACKGROUND_COLOR);

        for (int i = 0; i < meters.size(); i++) {
            Meter m = meters.get(i);
            int top = BORDER + i*fr.FONT_HEIGHT + ((i != 0) ? i*ROW_GAP : 0);
            int bot = top + fr.FONT_HEIGHT;

            for (int subtick = 0; subtick < numSubticks; subtick++) {
                SubtickTime time = new SubtickTime(tick, subtick);

                Meter.StateChange stateChange = m.getStateChange(time);
                int left = totalWidth + SUBTICK_GAP + subtick*TICK_WIDTH;
                int right = totalWidth + SUBTICK_GAP + (subtick + 1)*TICK_WIDTH;

                if (stateChange == null) {
                    if (m.wasPoweredAt(time)) {
                        Gui.drawRect(left, top, right, bot, m.getColor());
                    }
                } else if (stateChange.getState()) {
                    Gui.drawRect(left+2, top+1, right-1, bot-1, m.getColor());
                } else {
                    Gui.drawRect(left, top, right, bot, m.getColor());
                    Gui.drawRect(left+2, top+1, right-1, bot-1, BACKGROUND_COLOR);
                }

                if (m.movedAtTime(time)) {
                    int height = (top+bot)/2;
                    Gui.drawRect(left, height-1, right, height, m.getColor());
                    Gui.drawRect(left, height, right, height+1, 0xFFFFFFFF);
                    Gui.drawRect(left, height+1, right, height+2, m.getColor());
                }
            }

        }

        // Draw horizontal lines
        for (int i = 0; i <= meters.size(); i++) {
            int y = i*fr.FONT_HEIGHT + ((i != 0) ? i * ROW_GAP : 0);
            Gui.drawRect(totalWidth+SUBTICK_GAP, y,
                    totalWidth+SUBTICK_GAP+numSubticks*TICK_WIDTH, y+1, MINOR_GRID_COLOR);
        }
        // Draw vertical lines
        for (int t = 0; t <= numSubticks; t++) {
            int x = totalWidth + SUBTICK_GAP + t*TICK_WIDTH;
            Gui.drawRect(x, 0, x+1, totalHeight, MINOR_GRID_COLOR);
        }
    }

    private void renderPulseDurations(int totalWidth, boolean paused, List<Meter> meters) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        for (int i = 0; i < meters.size(); i++) {
            Meter m = meters.get(i);
            int top = BORDER + i * fr.FONT_HEIGHT + ((i != 0) ? i * ROW_GAP : 0);
            int bot = top + fr.FONT_HEIGHT;

            for (int t = windowStartTick - windowLength + 1; t <= windowStartTick; t++) {
                SubtickTime stt = clock.firstTimeOfTick(t);

                Meter.StateChange mostRecentChange = m.mostRecentChange(stt);
                if (mostRecentChange != null && mostRecentChange.getTime().getTick() == t-1) {
                    int duration = m.stateDuration(stt);
                    if (duration > 5) {
                        int left = totalWidth - BORDER - (windowStartTick - t + 1) * TICK_WIDTH + 1;
                        String durationStr = "" + duration;
                        int width = fr.getStringWidth(durationStr) + 1;

                        int color = m.getColor();
                        int textColor = POWERED_TEXT_COLOR;
                        if (!mostRecentChange.getState()) {
                            color = BACKGROUND_COLOR;
                            textColor = UNPOWERED_TEXT_COLOR;
                        }
                        Gui.drawRect(left, top, left + width + 1, bot, color);
                        fr.drawString(durationStr, left + 1, top + 1, textColor);
                    }
                }
            }
        }
    }

    private void renderPauseNotification(int totalHeight, boolean paused) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        String text = groupName;
        if (paused){
            text = text + " (" + I18n.format("redstonemultimeter.ui.paused") + ")";
        }
        fr.drawString(text, BORDER, totalHeight + 3, PAUSED_TEXT_COLOR);
    }

}
