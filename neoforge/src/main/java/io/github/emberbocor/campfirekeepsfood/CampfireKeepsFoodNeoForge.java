package io.github.emberbocor.campfirekeepsfood;

import io.github.emberbocor.campfirekeepsfood.config.ServerConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(CampfireKeepsFood.MODID)
public class CampfireKeepsFoodNeoForge {
    public CampfireKeepsFoodNeoForge() {
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load());
    }
}
