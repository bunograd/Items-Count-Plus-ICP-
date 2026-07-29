package itemsCountPlus.client;

import itemsCountPlus.client.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;

public class itemsCountPlusClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 1. Register Cloth Config configuration
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

        // 2. Register HUD rendering hook
        HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;

            if (player == null || client.options.hudHidden) return;

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            boolean isLeftHanded = player.getMainArm() == Arm.LEFT;

            ItemStack leftStack = isLeftHanded ? player.getMainHandStack() : player.getOffHandStack();
            ItemStack rightStack = isLeftHanded ? player.getOffHandStack() : player.getMainHandStack();

            if (config.leftHandVisible) {
                renderIndicator(context, client, leftStack, "hud.hand.left", config.leftHand, true);
            }
            if (config.rightHandVisible) {
                renderIndicator(context, client, rightStack, "hud.hand.right", config.rightHand, false);
            }

        });
    }

    private void renderIndicator(DrawContext context, MinecraftClient client, ItemStack stack, String langKey, ModConfig.HandSettings settings, boolean isLeftIndicator) {
        if (stack.isEmpty() || client.player == null) return;

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        // 1. Configure vertical alignment (Y)
        int y = 0;
        if (settings.heightPos == ModConfig.HeightPos.TOP) {
            y = 15;
        } else if (settings.heightPos == ModConfig.HeightPos.CENTER) {
            y = screenHeight / 2 - 4;
        } else if (settings.heightPos == ModConfig.HeightPos.HOTBAR) {
            y = screenHeight - 15;
        }

        // 2. Configure horizontal alignment (X)
        int x = 0;
        if (settings.lengthPos == ModConfig.LengthPos.SIDE) {
            x = isLeftIndicator ? 20 : screenWidth - 60;
        } else if (settings.lengthPos == ModConfig.LengthPos.CENTER) {
            if (settings.heightPos == ModConfig.HeightPos.HOTBAR) {
                x = isLeftIndicator ? (screenWidth / 2 - 91 - 70) : (screenWidth / 2 + 91 + 10);
            } else {
                x = isLeftIndicator ? (screenWidth / 2 - 50) : (screenWidth / 2 + 30);
            }
        }

        String letter = Text.translatable(langKey).getString();
        String valueText = "";
        boolean isDamageable = stack.isDamageable();

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

        // Calculate text dimensions
        String fullText = letter + ": " + valueText;
        int textWidth = client.textRenderer.getWidth(fullText);
        int textHeight = 8; // Standard font height

        // Configure padding for the background (making it slightly larger)
        int paddingX = 4; // Increased side padding
        int paddingYTop = 3; // Increased top padding

        // If the item has durability, expand the background from the bottom by another 4 pixels for the progress bar
        int paddingYBottom = isDamageable ? 6 : 3;

        // Render the enlarged semi-transparent black background
        context.fill(
                x - paddingX,
                y - paddingYTop,
                x + textWidth + paddingX,
                y + textHeight + paddingYBottom,
                0x7F000000
        );

        int argbColor = settings.letterColor | 0xFF000000;

        // Render the first letter (Custom color)
        context.drawTextWithShadow(client.textRenderer, letter, x, y, argbColor);

        // Render digits (White) with a colon
        int letterWidth = client.textRenderer.getWidth(letter);
        context.drawTextWithShadow(client.textRenderer, ": " + valueText, x + letterWidth, y, 0xFFFFFFFF);

        // 3. Render durability progress bar INSIDE the background on its bottom edge
        if (isDamageable) {
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            float durabilityPercent = (float) (maxDamage - currentDamage) / maxDamage;

            int barWidth = 25;
            int barHeight = 2;

            // Center the progress bar horizontally relative to the total width of the text background
            int barX = x + (textWidth / 2) - (barWidth / 2);

            // Lower the progress bar to the bottom edge (exactly beneath the text)
            int barY = y + 9;

            // Render the black outline of the durability bar
            context.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);

            // Calculate vanilla durability color (from green to red)
            int barColor = net.minecraft.util.math.MathHelper.hsvToRgb(Math.max(0.0F, durabilityPercent) / 3.0F, 1.0F, 1.0F) | 0xFF000000;
            int fillWidth = (int) (barWidth * durabilityPercent);

            // Render the bar fill
            context.fill(barX, barY, barX + fillWidth, barY + barHeight, barColor);
        }
    }
}
