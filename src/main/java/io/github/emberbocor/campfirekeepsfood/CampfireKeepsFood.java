package io.github.emberbocor.campfirekeepsfood;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(CampfireKeepsFood.MODID)
public class CampfireKeepsFood {
    public static final String MODID = "campfirekeepsfood";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CampfireKeepsFood(IEventBus modEventBus, ModContainer modContainer) {
    }
}
