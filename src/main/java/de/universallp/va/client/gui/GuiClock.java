package de.universallp.va.client.gui;

import de.universallp.va.core.network.PacketHandler;
import de.universallp.va.core.network.messages.MessageSetFieldServer;
import de.universallp.va.core.tile.TileClock;
import de.universallp.va.core.util.libs.LibLocalization;
import de.universallp.va.core.util.libs.LibNames;
import de.universallp.va.core.util.libs.LibResources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

/**
 * Created by universallp on 22.10.2016 21:02.
 * This file is part of VanillaAutomation which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/VanillaAutomation
 */
public class GuiClock extends GuiScreen {

    private int tickdelay;
    private TileClock te;

    public GuiClock(TileClock te) {
        this.te = te;
        this.tickdelay = te.getTickDelay();
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiButton[] buttons = new GuiButton[4];

        int x = this.width / 2;
        int y = this.height / 2 + 2;
        buttons[0] = new GuiButton(0, x - 75, y, 30, 20, "-10");
        buttons[1] = new GuiButton(1, x - 35, y, 30, 20, "-1");
        buttons[2] = new GuiButton(2, x + 5, y, 30, 20, "+1");
        buttons[3] = new GuiButton(3, x + 45, y, 30, 20, "+10");

        for (GuiButton btn : buttons)
            buttonList.add(btn);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:
                tickdelay -= (tickdelay - 10 > 0 ? 10 : 0);
                break;
            case 1:
                tickdelay -= (tickdelay - 1 > 0 ? 1 : 0);
                break;
            case 2:
                tickdelay += (tickdelay + 10 <= 1000 ? 1 : 0);
                break;
            case 3:
                tickdelay += (tickdelay + 1 <= 1000 ? 10 : 0);
        }

        PacketHandler.sendToServer(new MessageSetFieldServer(0, tickdelay, te.getPos()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(LibResources.GUI_CLOCK);
        int i = (this.width - 176) / 2;
        int j = (this.height - 70) / 2;
        drawTexturedModalRect(i, j, 0, 0, 176, 70);
        String s = I18n.format(LibLocalization.GUI_CLOCK);
        int l = fontRendererObj.getStringWidth(s);
        fontRendererObj.drawString(s, width / 2 - l / 2, height / 2 - 28, LibNames.TEXT_COLOR);
        s = I18n.format(LibLocalization.GUI_CLOCK_DELAY) + ": " + String.valueOf(tickdelay) + " " + I18n.format(LibLocalization.GUI_CLOCK_TICKS);
        l = fontRendererObj.getStringWidth(s);
        fontRendererObj.drawString(s, width / 2 - l / 2, height / 2 - 15, LibNames.TEXT_COLOR);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
