package io.github.emberbocor.campfirekeepsfood.config;

import java.nio.file.Path;
import java.util.Set;

import org.slf4j.helpers.MessageFormatter;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import io.github.emberbocor.campfirekeepsfood.CampfireKeepsFood;

final class ServerConfigReader {
    private static final Set<String> KEYS = Set.of("cookInStages", "maxStages");

    private final Path file;

    ServerConfigReader(Path file) {
        this.file = file;
    }

    ServerConfig.Settings read(UnmodifiableConfig config) {
        ServerConfig.Settings defaults = ServerConfig.DEFAULTS;
        if (config == null) {
            return defaults;
        }
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            if (!KEYS.contains(entry.getKey())) {
                warn("unknown option '{}', ignored", entry.getKey());
            }
        }
        return new ServerConfig.Settings(cookInStages(config, defaults.cookInStages()), maxStages(config, defaults.maxStages()));
    }

    private boolean cookInStages(UnmodifiableConfig config, boolean fallback) {
        Object value = config.get("cookInStages");
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean stages) {
            return stages;
        }
        warn("cookInStages must be true or false, got '{}'; using {}", value, fallback);
        return fallback;
    }

    private int maxStages(UnmodifiableConfig config, int fallback) {
        Object value = config.get("maxStages");
        if (value == null) {
            return fallback;
        }
        if ((value instanceof Integer || value instanceof Long)
                && ((Number) value).longValue() >= 1 && ((Number) value).longValue() <= 64) {
            return ((Number) value).intValue();
        }
        warn("maxStages must be a whole number from 1 to 64, got '{}'; using {}", value, fallback);
        return fallback;
    }

    private void warn(String message, Object... args) {
        CampfireKeepsFood.LOGGER.warn("{}: {}", file, MessageFormatter.arrayFormat(message, args).getMessage());
    }
}
