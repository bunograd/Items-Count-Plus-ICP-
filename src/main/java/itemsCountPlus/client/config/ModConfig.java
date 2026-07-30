package itemsCountPlus.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.loader.api.FabricLoader;

@Config(name = "items_count_plus")
public class ModConfig implements ConfigData {

    // --- MAIN SCREEN SETTINGS ---
    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.Tooltip
    public boolean useIcons = false;

    @ConfigEntry.Category("main")
    public boolean leftHandVisible = true;

    @ConfigEntry.Category("main")
    public boolean rightHandVisible = true;

    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.Tooltip
    public boolean lockKeybindWithoutModMenu = false;

    // --- LEFT HAND SETTINGS ---
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("left_hand_settings")
    public HandSettings leftHand = new HandSettings(0x0000FF, PositionGrid.HOTBAR_SIDE);

    // --- RIGHT HAND SETTINGS ---
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("right_hand_settings")
    public HandSettings rightHand = new HandSettings(0xFF0000, PositionGrid.HOTBAR_SIDE);

    public static class HandSettings {
        @ConfigEntry.Gui.CollapsibleObject
        public MainHandSettings mainSettings;

        @ConfigEntry.Gui.CollapsibleObject
        public BackgroundSettings background = new BackgroundSettings();

        public HandSettings() {
            this.mainSettings = new MainHandSettings();
        }

        public HandSettings(int defaultColor, PositionGrid defaultPos) {
            this.mainSettings = new MainHandSettings(defaultColor, defaultPos);
        }
    }

    public static class MainHandSettings {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @ConfigEntry.Gui.Tooltip
        public PositionGrid position = PositionGrid.HOTBAR_SIDE;

        // Return the standard Cloth Config tooltip via the @Gui.Tooltip annotation
        @ConfigEntry.ColorPicker
        @ConfigEntry.Gui.Tooltip
        public int letterColor;

        public MainHandSettings() {
            this.letterColor = 0xFFFFFF;
        }

        public MainHandSettings(int defaultColor, PositionGrid defaultPos) {
            this.letterColor = defaultColor;
            this.position = defaultPos;
        }
    }

    public static class BackgroundSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean drawBackground = true;

        @ConfigEntry.ColorPicker
        @ConfigEntry.Gui.Tooltip
        public int backgroundColor = 0x000000;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
        @ConfigEntry.Gui.Tooltip
        public int backgroundOpacity = 127;
    }

    public enum PositionGrid {
        TOP_SIDE,    TOP_CENTER,
        CENTER_SIDE, CENTER_CENTER,
        HOTBAR_SIDE, HOTBAR_CENTER
    }
}
