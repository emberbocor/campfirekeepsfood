package io.github.emberbocor.campfirekeepsfood;

import io.github.emberbocor.campfirekeepsfood.config.ServerConfig;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CampfireKeepsFood.MODID)
public class CampfireKeepsFoodForge {
    public CampfireKeepsFoodForge() {
        ServerAboutToStartEvent.BUS.addListener(event -> ServerConfig.load());
    }
}
