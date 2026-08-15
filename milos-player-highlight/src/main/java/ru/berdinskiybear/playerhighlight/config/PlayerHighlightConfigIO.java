package ru.berdinskiybear.playerhighlight.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@Slf4j
public final class PlayerHighlightConfigIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "milos-player-highlight.json";

    private PlayerHighlightConfigIO() {
    }

    public static Path defaultPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static PlayerHighlightConfig load() {
        return load(defaultPath());
    }

    public static void save(PlayerHighlightConfig config) {
        save(config, defaultPath());
    }

    public static PlayerHighlightConfig load(Path path) {
        if (!Files.isRegularFile(path)) {
            PlayerHighlightConfig defaults = new PlayerHighlightConfig();
            save(defaults, path);
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            PlayerHighlightConfig config = GSON.fromJson(reader, PlayerHighlightConfig.class);
            if (config == null) {
                throw new IOException("config file is empty");
            }
            return sanitize(config);
        } catch (Exception e) {
            log.warn("A corrupted configuration file was found at {}, overwriting it with the default config", path, e);
            PlayerHighlightConfig defaults = new PlayerHighlightConfig();
            save(defaults, path);
            return defaults;
        }
    }

    public static void save(PlayerHighlightConfig config, Path path) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception e) {
            log.warn("Could not write config to {}", path, e);
        }
    }

    private static PlayerHighlightConfig sanitize(PlayerHighlightConfig config) {
        if (config.getEntries() == null) {
            config.setEntries(new ArrayList<>());
        } else {
            config.setEntries(new ArrayList<>(config.getEntries()));
        }
        return config;
    }
}
