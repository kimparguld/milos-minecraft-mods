package se.guldbransen.milos.fastxp.config;

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

/**
 * First-party config file I/O: plain JDK file access plus Gson (which ships with Minecraft on
 * both the client and the dedicated server).
 * <p>
 * Deliberately free of any ukulib or Minecraft-client reference so the common mod initializer can
 * load a config on a dedicated server, where ukulib (a {@code "environment": "client"} mod) is not
 * present at all.
 */
@Slf4j
public final class FastXpConfigIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "milos-fast-xp.json";

    private FastXpConfigIO() {
    }

    /**
     * @return the default config file location, {@code config/milos-fast-xp.json}
     */
    public static Path defaultPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    /**
     * Reads the config from the default location.
     *
     * @return the stored config, or a freshly written default one
     */
    public static FastXpConfig load() {
        return load(defaultPath());
    }

    /**
     * Writes the config to the default location.
     *
     * @param config the config to write
     */
    public static void save(FastXpConfig config) {
        save(config, defaultPath());
    }

    /**
     * Reads the config from the given file. A missing or corrupted file falls back to the defaults
     * and rewrites the file with them.
     *
     * @param path the config file
     * @return the stored config, or a freshly written default one
     */
    public static FastXpConfig load(Path path) {
        if (!Files.isRegularFile(path)) {
            FastXpConfig defaults = new FastXpConfig();
            save(defaults, path);
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            FastXpConfig config = GSON.fromJson(reader, FastXpConfig.class);
            if (config == null) {
                throw new IOException("config file is empty");
            }
            return sanitize(config);
        } catch (Exception e) {
            log.warn("A corrupted configuration file was found at {}, overwriting it with the default config", path, e);
            FastXpConfig defaults = new FastXpConfig();
            save(defaults, path);
            return defaults;
        }
    }

    /**
     * Writes the config to the given file, creating parent directories as needed.
     *
     * @param config the config to write
     * @param path   the config file
     */
    public static void save(FastXpConfig config, Path path) {
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

    /**
     * Repairs a deserialized config: Gson happily leaves a collection null when the file says
     * {@code null}, and the whitelist has to stay mutable for the config screen to edit it.
     */
    private static FastXpConfig sanitize(FastXpConfig config) {
        if (config.getAutoThrowProjectiles() == null) {
            config.setAutoThrowProjectiles(new ArrayList<>(new FastXpConfig().getAutoThrowProjectiles()));
        } else {
            config.setAutoThrowProjectiles(new ArrayList<>(config.getAutoThrowProjectiles()));
        }
        return config;
    }
}
