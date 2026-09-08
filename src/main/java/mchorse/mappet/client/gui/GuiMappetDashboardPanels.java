package mchorse.mappet.client.gui;

import mchorse.mappet.utils.MPIcons;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanels;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiMappetDashboardPanels extends GuiDashboardPanels
{
    private final GuiMappetDashboard dashboard;
    private boolean continuing;
    private static final String telega = "https://t.me/pok0roche";

    public GuiMappetDashboardPanels(class_310 mc, GuiMappetDashboard dashboard)
    {
        super(mc);
        this.dashboard = dashboard;
    }

    @Override
    public void setPanel(GuiDashboardPanel panel)
    {
        GuiElement current = this.view.delegate;
        if (!this.continuing && current == this.dashboard.settings && panel != current
            && this.dashboard.settings.hasUnsavedChanges())
        {
            this.dashboard.settings.requireChangeResolution(() -> this.continueSetPanel(panel));
            return;
        }

        if (!this.continuing && current == this.dashboard.clientSettings && panel != current
            && this.dashboard.clientSettings.hasUnsavedChanges())
        {
            this.dashboard.clientSettings.requireChangeResolution(() -> this.continueSetPanel(panel));
            return;
        }

        super.setPanel(panel);
    }

    private void continueSetPanel(GuiDashboardPanel panel)
    {
        this.continuing = true;
        try
        {
            super.setPanel(panel);
        }
        finally
        {
            this.continuing = false;
        }
    }

    public boolean requireSettingsResolution(Runnable continuation)
    {
        if (this.dashboard.settings != null && this.dashboard.settings.hasUnsavedChanges())
        {
            this.dashboard.settings.requireChangeResolution(continuation);
            return true;
        }

        if (this.dashboard.clientSettings != null && this.dashboard.clientSettings.hasUnsavedChanges())
        {
            this.dashboard.clientSettings.requireChangeResolution(continuation);
            return true;
        }

        return false;
    }
}
