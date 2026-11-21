package io.github.emberbocor.campfirekeepsfood.config;

import java.nio.file.Path;

import io.github.emberbocor.campfirekeepsfood.platform.Services;

public final class ServerConfig {
    static final Settings DEFAULTS = new Settings(false, 8);

    private static volatile Settings settings = DEFAULTS;

    private ServerConfig() {
    }

    public static void load() {
        Path file = Services.PLATFORM.getConfigDirectory().resolve(ServerConfigFile.FILE_NAME);
        settings = new ServerConfigReader(file).read(ServerConfigFile.load(file).orElse(null));
    }

    public static Settings settings() {
        return settings;
    }

    public record Settings(boolean cookInStages, int maxStages) {
    }
}
