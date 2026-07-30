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
import net.minecraft.util.Arm;

public class itemsCountPlusClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 1. Register Cloth Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

        // 2. Register HUD rendering callback hook
        HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;

            if (player == null || client.options.hudHidden) return;

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            boolean isLeftHanded = player.getMainArm() == Arm.LEFT;

            ItemStack leftStack = isLeftHanded ? player.getMainHandStack() : player.getOffHandStack();
            ItemStack rightStack = isLeftHanded ? player.getOffHandStack() : player.getMainHandStack();

            // Render the left indicator using the decoupled utility architecture
            if (config.leftHandVisible) {
                HudRenderUtils.renderIndicator(context, client, leftStack, "hud.hand.left", config.leftHand, true, config.useIcons);
            }

            // Render the right indicator using the decoupled utility architecture
            if (config.rightHandVisible) {
                HudRenderUtils.renderIndicator(context, client, rightStack, "hud.hand.right", config.rightHand, false, config.useIcons);
            }
        });
    }
}
