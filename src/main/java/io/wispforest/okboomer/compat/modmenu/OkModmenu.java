package io.wispforest.okboomer.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.wispforest.okboomer.OkBoomer;
import io.wispforest.owo.config.ui.ConfigScreen;

public class OkModmenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(OkBoomer.CONFIG, parent);
    }
}
