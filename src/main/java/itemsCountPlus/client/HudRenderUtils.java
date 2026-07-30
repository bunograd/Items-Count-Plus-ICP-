package itemsCountPlus.client;

import itemsCountPlus.client.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class HudRenderUtils {

    public static void renderIndicator(DrawContext context, MinecraftClient client, ItemStack stack, String langKey, ModConfig.HandSettings settings, boolean isLeftIndicator, boolean useIcons) {
        if (stack.isEmpty() || client.player == null) return;

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int x = 0;
        int y = 0;

        switch (settings.mainSettings.position) {
            case TOP_SIDE:
                y = 15;
                x = isLeftIndicator ? 20 : screenWidth - 60;
                break;
            case TOP_CENTER:
                y = 15;
                x = isLeftIndicator ? (screenWidth / 2 - 50) : (screenWidth / 2 + 30);
                break;
            case CENTER_SIDE:
                y = screenHeight / 2 - 4;
                x = isLeftIndicator ? 20 : screenWidth - 60;
                break;
            case CENTER_CENTER:
                y = screenHeight / 2 - 4;
                x = isLeftIndicator ? (screenWidth / 2 - 50) : (screenWidth / 2 + 30);
                break;
            case HOTBAR_SIDE:
                y = screenHeight - 15;
                x = isLeftIndicator ? 20 : screenWidth - 60;
                break;
            case HOTBAR_CENTER:
                y = screenHeight - 15;
                x = isLeftIndicator ? (screenWidth / 2 - 91 - 80) : (screenWidth / 2 + 91 + 10);
                break;
        }

        String letter = Text.translatable(langKey).getString();
        String valueText = "";
        boolean isDamageable = stack.isDamageable();

        // Calculation of durability or total item count in inventory (Fully compatible with 1.21.x)
        if (isDamageable) {
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            valueText = String.valueOf(maxDamage - currentDamage);
        } else {
            int totalCount = 0;
            for (int i = 0; i < client.player.getInventory().size(); i++) {
                ItemStack invStack = client.player.getInventory().getStack(i);
                if (!invStack.isEmpty() && invStack.getItem() == stack.getItem()) {
                    totalCount += invStack.getCount();
                }
            }
            valueText = String.valueOf(totalCount);
        }

        // Aligning UI dimensions (Icon takes default 16x16 or 11x11 depending on the grid)
        int leftElementWidth = useIcons ? 14 : client.textRenderer.getWidth(letter);
        String numberPart = ": " + valueText;
        int textWidth = leftElementWidth + client.textRenderer.getWidth(numberPart);
        int textHeight = 8;

        // Configuring padding for the background plates
        int paddingX = 4;
        int paddingYTop = useIcons ? 4 : 3;
        int paddingYBottom = isDamageable ? 5 : (useIcons ? 4 : 3);

        // Rendering the translucent background plate (0x7F000000)
        if (settings.background.drawBackground) {
            // Extract opacity (Alpha channel) from background settings
            int alpha = settings.background.backgroundOpacity << 24;
            // Extract the player-selected RGB color, clearing any potential old alpha bits
            int rgb = settings.background.backgroundColor & 0x00FFFFFF;
            // Combine alpha channel and color into final ARGB format for Minecraft
            int finalBgColor = alpha | rgb;

            context.fill(
                    x - paddingX,
                    y - paddingYTop,
                    x + textWidth + paddingX,
                    y + textHeight + paddingYBottom,
                    finalBgColor
            );
        }

        // Safe rendering of an icon or letter without using MatrixStack
        if (useIcons) {
            // Pass coordinates directly to DrawContext. In 1.21.1 drawItem renders
            // the item at its original size. Using y - 4 for height offset.
            context.drawItem(stack, x, y - 4);
        } else {
            int argbColor = settings.mainSettings.letterColor | 0xFF000000;
            context.drawTextWithShadow(client.textRenderer, letter, x, y, argbColor);
        }

        // Rendering the text value (counter / durability)
        context.drawTextWithShadow(client.textRenderer, numberPart, x + leftElementWidth, y, 0xFFFFFFFF);

        // Durability bar indicator
        if (isDamageable) {
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            float durabilityPercent = (float) (maxDamage - currentDamage) / maxDamage;

            int barWidth = 25;
            int barHeight = 2;
            int barX = x + (textWidth / 2) - (barWidth / 2); // Centering
            int barY = useIcons ? y + 11 : y + 9;

            // Durability bar outline
            context.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);

            // Smooth color transition (from green to red)
            int barColor = MathHelper.hsvToRgb(Math.max(0.0F, durabilityPercent) / 3.0F, 1.0F, 1.0F) | 0xFF000000;
            int fillWidth = (int) (barWidth * durabilityPercent);

            context.fill(barX, barY, barX + fillWidth, barY + barHeight, barColor);
        }
    }
}
