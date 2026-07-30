package itemsCountPlus.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "items_count_plus")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("main")
    public boolean leftHandVisible = true;

    @ConfigEntry.Category("main")
    public boolean rightHandVisible = true;

    @ConfigEntry.Category("main")
    public boolean useIcons = false;

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("left_hand_settings")
    public HandSettings leftHand = new HandSettings(0x0000FF, PositionGrid.HOTBAR_SIDE);

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("right_hand_settings")
    public HandSettings rightHand = new HandSettings(0xFF0000, PositionGrid.HOTBAR_SIDE);

    public static class HandSettings {

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @ConfigEntry.Gui.Tooltip
        public PositionGrid position = PositionGrid.HOTBAR_SIDE;

        @ConfigEntry.ColorPicker
        public int letterColor;

        // Required no-args constructor for Cloth Config reflection operations
        public HandSettings() {
        }

        public HandSettings(int defaultColor, PositionGrid defaultPos) {
            this.letterColor = defaultColor;
            this.position = defaultPos;
        }
    }

    public enum PositionGrid {
        TOP_SIDE,    TOP_CENTER,
        CENTER_SIDE, CENTER_CENTER,
        HOTBAR_SIDE, HOTBAR_CENTER
    }
}
