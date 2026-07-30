package itemsCountPlus.client;

import itemsCountPlus.client.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.lwjgl.glfw.GLFW;

public class itemsCountPlusClient implements ClientModInitializer {

    // Declare our key binding
    private static KeyBinding openConfigKeyBinding;

    @Override
    public void onInitializeClient() {
        // 1. Register Cloth Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

        // SAFE GUI PROVIDER FOR HIDING A FIELD
        // If ModMenu is NOT installed, we intercept our field by name and prevent Cloth Config from rendering it
        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("modmenu")) {
            AutoConfig.getGuiRegistry(ModConfig.class).registerPredicateProvider(
                    (i18n, field, config, defaults, guiProvider) -> java.util.Collections.emptyList(),
                    field -> field.getName().equals("lockKeybindWithoutModMenu")
            );
        }

        // 2. Register the grave accent key (GLFW_KEY_GRAVE_ACCENT) in our mod's category
        openConfigKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.items_count_plus.open_config", // Translation key for the key binding name
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT, // Key code for ` / ~ / Grave Accent
                "category.items_count_plus.general" // Category in vanilla controls settings
        ));

        // 3. Register the key press check on every game tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKeyBinding.wasPressed()) {
                if (client.player == null) continue;

                ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                boolean hasModMenu = FabricLoader.getInstance().isModLoaded("modmenu");

                // EXACTLY WHAT IS NEEDED: Block the key binding ONLY if the setting is enabled AND the player HAS ModMenu!
                if (config.lockKeybindWithoutModMenu && hasModMenu) {
                    continue; // Skip opening, since the player can configure everything via ModMenu
                }

                // If the blocking conditions are not met (or ModMenu is missing) — safely open the screen
                MinecraftClient.getInstance().setScreen(
                        AutoConfig.getConfigScreen(ModConfig.class, client.currentScreen).get()
                );
            }
        });

        // 4. Register HUD rendering callback hook
        HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;

            if (player == null || client.options.hudHidden) return;

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            boolean isLeftHanded = player.getMainArm() == Arm.LEFT;

            ItemStack leftStack = isLeftHanded ? player.getMainHandStack() : player.getOffHandStack();
            ItemStack rightStack = isLeftHanded ? player.getOffHandStack() : player.getMainHandStack();

            if (config.leftHandVisible) {
                HudRenderUtils.renderIndicator(context, client, leftStack, "hud.hand.left", config.leftHand, true, config.useIcons);
            }

            if (config.rightHandVisible) {
                HudRenderUtils.renderIndicator(context, client, rightStack, "hud.hand.right", config.rightHand, false, config.useIcons);
            }
        });
    }
}
