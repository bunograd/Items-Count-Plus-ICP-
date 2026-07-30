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

        // Расчет прочности или общего количества предметов в инвентаре (Полностью совместимо с 1.21.x)
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

        // Выравнивание габаритов интерфейса (Иконка занимает стандартные 16х16 или 11х11 в зависимости от сетки)
        int leftElementWidth = useIcons ? 14 : client.textRenderer.getWidth(letter);
        String numberPart = ": " + valueText;
        int textWidth = leftElementWidth + client.textRenderer.getWidth(numberPart);
        int textHeight = 8;

        // Настройка отступов для заднего фона
        int paddingX = 4;
        int paddingYTop = useIcons ? 4 : 3;
        int paddingYBottom = isDamageable ? 5 : (useIcons ? 4 : 3);

        // Отрисовка полупрозрачной плашки фона (0x7F000000)
        context.fill(
                x - paddingX,
                y - paddingYTop,
                x + textWidth + paddingX,
                y + textHeight + paddingYBottom,
                0x7F000000
        );

        // Безопасный рендеринг иконки или буквы без использования MatrixStack
        if (useIcons) {
            // Напрямую передаем координаты в DrawContext. В 1.21.11 drawItem отрисовывает
            // предмет в оригинальном размере. Для смещения по высоте используем y - 4.
            context.drawItem(stack, x, y - 4);
        } else {
            int argbColor = settings.letterColor | 0xFF000000;
            context.drawTextWithShadow(client.textRenderer, letter, x, y, argbColor);
        }

        // Отрисовка текстового значения (счетчик / прочность)
        context.drawTextWithShadow(client.textRenderer, numberPart, x + leftElementWidth, y, 0xFFFFFFFF);

        // Индикатор полоски прочности
        if (isDamageable) {
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            float durabilityPercent = (float) (maxDamage - currentDamage) / maxDamage;

            int barWidth = 25;
            int barHeight = 2;
            int barX = x + (textWidth / 2) - (barWidth / 2); // Центрирование
            int barY = useIcons ? y + 11 : y + 9;

            // Обводка полоски прочности
            context.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);

            // Плавный переход цвета (от зеленого к красному)
            int barColor = MathHelper.hsvToRgb(Math.max(0.0F, durabilityPercent) / 3.0F, 1.0F, 1.0F) | 0xFF000000;
            int fillWidth = (int) (barWidth * durabilityPercent);

            context.fill(barX, barY, barX + fillWidth, barY + barHeight, barColor);
        }
    }
}
