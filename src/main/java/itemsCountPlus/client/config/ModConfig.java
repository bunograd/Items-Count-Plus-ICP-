package itemsCountPlus.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "items_count_plus")
public class ModConfig implements ConfigData {

    // MOVED TO THE VERY TOP: Main toggle buttons for indicators
    @ConfigEntry.Category("main")
    public boolean leftHandVisible = true;

    @ConfigEntry.Category("main")
    public boolean rightHandVisible = true;

    // CATEGORY FOR LEFT HAND (Collapsible folder under the main buttons)
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("left_hand_settings")
    public HandSettings leftHand = new HandSettings(0x0000FF); // Blue by default

    // CATEGORY FOR RIGHT HAND (Collapsible folder under the main buttons)
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("right_hand_settings")
    public HandSettings rightHand = new HandSettings(0xFF0000); // Red by default

    public static class HandSettings {
        // The visible field has been removed from inside, as the buttons are now at the very top of the main screen!

        @ConfigEntry.Gui.Tooltip
        public HeightPos heightPos = HeightPos.HOTBAR;

        @ConfigEntry.Gui.Tooltip
        public LengthPos lengthPos = LengthPos.SIDE;

        @ConfigEntry.ColorPicker
        public int letterColor;

        public HandSettings(int defaultColor) {
            this.letterColor = defaultColor;
        }
    }

    public enum HeightPos { HOTBAR, CENTER, TOP }
    public enum LengthPos { SIDE, CENTER }
}
