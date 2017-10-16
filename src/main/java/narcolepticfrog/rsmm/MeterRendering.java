package narcolepticfrog.rsmm;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class MeterRendering {

    public static void renderMeterHighlights(List<Meter> meters, float partialTicks) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        for (Meter m : meters) {
            int color = m.getColor();

            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;

            AxisAlignedBB aabb = Block.FULL_BLOCK_AABB.offset(m.getPosition()).offset(-dx, -dy, -dz).grow(0.002);
            RenderGlobal.renderFilledBox(aabb, r, g, b, 0.5F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void renderMeterTraces(List<Meter> meters, boolean paused) {
        if (meters.size() == 0) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRenderer;

        // Compute the maximum duration of the meters and the maximum width of the names in px.
        int maxNameWidth = 0;
        int maxDuration = 0;
        for (Meter m : meters) {
            maxNameWidth = Math.max(maxNameWidth, fr.getStringWidth(m.getName()));
            maxDuration = Math.max(maxDuration, m.getDuration());
        }
        final int tickWidth = 3;
        final int traceWidth = tickWidth * maxDuration;
        final int rowHeight = fr.FONT_HEIGHT;
        final int nameTraceGap = 3;
        final int rowGap = 1;
        final int border = 1;

        final int totalWidth = border + maxNameWidth + nameTraceGap + traceWidth + border;
        final int totalHeight = border + meters.size()*rowHeight + (meters.size()-1)*rowGap + border;

        // Draw the background
        Gui.drawRect(0,0, totalWidth, totalHeight, 0xAA000000);

        // Draw the meter traces
        for (int i = 0; i < meters.size(); i++) {
            Meter m = meters.get(i);
            int yTop = border + i*rowHeight + ((i != 0) ? i*rowGap : 0);
            int yBot = yTop + rowHeight;

            Trace<Boolean> trace = m.getTrace();
            for (int t = 0; t < trace.size(); t++) {
                if (trace.get(t)) {
                    int xLeft = totalWidth - border - (t+1)*tickWidth;
                    int xRight = xLeft + tickWidth;
                    Gui.drawRect(xLeft, yTop, xRight, yBot, m.getColor());
                }
            }
        }

        // Draw the grid over the traces
        for (int i = 0; i <= meters.size(); i++) {
            int y = i*rowHeight + ((i != 0) ? i * rowGap : 0);
            Gui.drawRect(border + maxNameWidth + nameTraceGap, y, totalWidth, y+1, 0xFF404040);
        }

        for (int t = 0; t <= maxDuration; t++) {
            int x = totalWidth - border - t*tickWidth;
            int color = 0xFF404040;
            if (t % 5 == 0) {
                color = 0xFF606060;
            }
            Gui.drawRect(x, 0, x+1, totalHeight, color);
        }

        // Draw long pulse durations

        for (int i = 0; i < meters.size(); i++) {
            Meter m = meters.get(i);
            Trace<Boolean> trace = m.getTrace();

            if (trace.size() == 0)
                continue;

            int pulseLength = 0;
            for (int t = 0; t <= trace.size(); t++) {
                if ((t == trace.size() || !trace.get(t)) && pulseLength > 5) {
                    int xLeft = totalWidth - border - t*tickWidth;
                    int yTop = border + i*rowHeight + ((i != 0) ? i*rowGap : 0);
                    String length = "" + pulseLength;
                    int width = fr.getStringWidth(length);
                    width = width + (width%tickWidth);
                    Gui.drawRect(xLeft, yTop, xLeft+width+1, yTop + fr.FONT_HEIGHT, m.getColor());
                    fr.drawString(length, xLeft+1, yTop+1, 0xFF404040);
                }
                if (t < trace.size() && trace.get(t)) {
                    pulseLength += 1;
                } else {
                    pulseLength = 0;
                }
            }
        }

        // Render meter names
        for (int i = 0; i < meters.size(); i++) {
            Meter m = meters.get(i);
            int nameWidth = fr.getStringWidth(m.getName());
            int xPos = border + maxNameWidth - nameWidth;
            int yPos = border + i*rowHeight + ((i != 0) ? i*rowGap : 0) + rowGap;
            fr.drawString(m.getName(), xPos, yPos, 0xFFFFFFFF);
        }

        // Display paused state
        if (paused) {
            fr.drawString("Paused.", border, totalHeight + 3, 0xFF000000);
        }

    }

}
