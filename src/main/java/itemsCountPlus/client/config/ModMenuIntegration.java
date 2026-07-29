package itemsCountPlus.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Using AutoConfigClient to get the configuration screen without warnings
        return parent -> AutoConfigClient.getConfigScreen(ModConfig.class, parent).get();
    }
}
