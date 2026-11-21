package io.github.emberbocor.campfirekeepsfood.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import io.github.emberbocor.campfirekeepsfood.CampfireKeepsFood;

final class ServerConfigFile {
    static final String FILE_NAME = CampfireKeepsFood.MODID + "-server.toml";

    private static final String DEFAULT_TEXT = """
            # Campfire Keeps Food server config.
            # Created on the first server or world start.
            # Restart the server or reopen the singleplayer world to apply changes.

            # Cook results that still have a campfire recipe again on the same campfire, stage after stage,
            # until a result has no campfire recipe left. False throws such results off, as in vanilla.
            cookInStages = false

            # Most cooking stages per item placed on the campfire, counting the first one. Whole number from 1 to 64.
            # The result of the last stage stays on the campfire as cooked food. Only used when cookInStages is true.
            maxStages = 8
            """;

    private ServerConfigFile() {
    }

    static Optional<UnmodifiableConfig> load(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.writeString(file, DEFAULT_TEXT, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
                CampfireKeepsFood.LOGGER.info("Created {}", file);
            } catch (IOException e) {
                CampfireKeepsFood.LOGGER.warn("Could not create {}, using the default settings: {}", file, e.getMessage());
            }
            return parse(file, DEFAULT_TEXT);
        }

        try {
            String text = Files.readString(file, StandardCharsets.UTF_8);
            return parse(file, text.startsWith("﻿") ? text.substring(1) : text);
        } catch (IOException e) {
            CampfireKeepsFood.LOGGER.warn("Could not read {}, using the default settings; file left unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<UnmodifiableConfig> parse(Path file, String text) {
        try {
            return Optional.of(TomlFormat.instance().createParser().parse(text));
        } catch (RuntimeException e) {
            CampfireKeepsFood.LOGGER.warn("Could not parse {}, using the default settings; file left unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }
}
