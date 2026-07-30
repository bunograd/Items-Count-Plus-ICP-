package itemsCountPlus.client;

import itemsCountPlus.client.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class HudRenderUtils {

    public static void renderIndicator(DrawContext context, MinecraftClient client, ItemStack stack, String langKey, ModConfig.HandSettings settings, boolean isLeftIndicator, boolean useIcons) {
        if (stack.isEmpty() || client.player == null) return;

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int x = 0;
        int y = 0;

        switch (settings.position) {
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

        // Calculate durability or total item count in the whole inventory
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

        // Calculate dimensions of UI elements
        int leftElementWidth = useIcons ? 14 : client.textRenderer.getWidth(letter);
        String numberPart = ": " + valueText;
        int textWidth = leftElementWidth + client.textRenderer.getWidth(numberPart);
        int textHeight = 8;

        // Padding for the semi-transparent background plate
        int paddingX = 4;
        int paddingYTop = useIcons ? 5 : 3;
        int paddingYBottom = isDamageable ? 5 : (useIcons ? 5 : 3);

        // Draw the semi-transparent background (0x7F000000)
        context.fill(
                x - paddingX,
                y - paddingYTop,
                x + textWidth + paddingX,
                y + textHeight + paddingYBottom,
                0x7F000000
        );

        // Render the icon or the letter
        if (useIcons) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float) x, (float) (y - 3));
            // Increased scale to 0.75F
            context.getMatrices().scale(0.75F, 0.75F);

            context.drawItem(stack, 0, 0);
            context.getMatrices().popMatrix();
        } else {
            int argbColor = settings.letterColor | 0xFF000000;
            context.drawTextWithShadow(client.textRenderer, letter, x, y, argbColor);
        }

        // Draw the text value of count/durability
        context.drawTextWithShadow(client.textRenderer, numberPart, x + leftElementWidth, y, 0xFFFFFFFF);

        // Draw the durability progress bar on the bottom edge of the plate
        if (isDamageable) {
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            float durabilityPercent = (float) (maxDamage - currentDamage) / maxDamage;

            int barWidth = 25;
            int barHeight = 2;
            int barX = x + (textWidth / 2) - (barWidth / 2); // Centering
            int barY = useIcons ? y + 11 : y + 9;;

            // Durability bar outline
            context.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);

            // Smooth color transition (from green to red)
            int barColor = net.minecraft.util.math.MathHelper.hsvToRgb(Math.max(0.0F, durabilityPercent) / 3.0F, 1.0F, 1.0F) | 0xFF000000;
            int fillWidth = (int) (barWidth * durabilityPercent);

            context.fill(barX, barY, barX + fillWidth, barY + barHeight, barColor);
        }
    }
}
