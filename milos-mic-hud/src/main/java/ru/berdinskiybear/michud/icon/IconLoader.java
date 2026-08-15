package ru.berdinskiybear.michud.icon;

import com.mojang.blaze3d.platform.NativeImage;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import ru.berdinskiybear.michud.MicHudMod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
public final class IconLoader {
    private static final int BUNDLED_SIZE = 16;

    private static final Map<MicState, Identifier> BUNDLED = new EnumMap<>(MicState.class);

    static {
        BUNDLED.put(MicState.UNMUTED, Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "icons/unmuted.png"));
        BUNDLED.put(MicState.MUTED, Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "icons/muted.png"));
        BUNDLED.put(MicState.DISABLED, Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "icons/disabled.png"));
        BUNDLED.put(MicState.DISCONNECTED, Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "icons/disconnected.png"));
    }

    private static final Map<MicState, LoadedOverride> overrides = new EnumMap<>(MicState.class);
    private static final Map<MicState, FileTime> lastWarnedMtime = new EnumMap<>(MicState.class);

    private record LoadedOverride(Identifier id, DynamicTexture texture, FileTime mtime, int width, int height) {
    }

    public record IconTexture(Identifier id, int width, int height) {
    }

    private IconLoader() {
    }

    public static Path overridePath(MicState state) {
        return FabricLoader.getInstance().getConfigDir()
                .resolve(MicHudMod.MOD_ID)
                .resolve("icons")
                .resolve(state.name().toLowerCase(Locale.ROOT) + ".png");
    }

    public static IconTexture resolve(MicState state) {
        Path file = overridePath(state);
        Optional<Path> valid = IconFileResolver.resolveValidOverride(file);

        if (valid.isEmpty()) {
            if (Files.isRegularFile(file)) {
                warnOnce(state, file);
            }
            evict(state);
            return bundled(state);
        }

        try {
            FileTime mtime = Files.getLastModifiedTime(file);
            LoadedOverride cached = overrides.get(state);
            if (cached != null && cached.mtime().equals(mtime)) {
                return new IconTexture(cached.id(), cached.width(), cached.height());
            }

            NativeImage image;
            try (InputStream in = Files.newInputStream(file)) {
                image = NativeImage.read(in);
            }

            Identifier id = Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "override/" + state.name().toLowerCase(Locale.ROOT));
            DynamicTexture texture = new DynamicTexture(() -> "milos-mic-hud override " + state.name(), image);
            Minecraft.getInstance().getTextureManager().register(id, texture);

            if (cached != null) {
                cached.texture().close();
            }

            overrides.put(state, new LoadedOverride(id, texture, mtime, image.getWidth(), image.getHeight()));
            return new IconTexture(id, image.getWidth(), image.getHeight());
        } catch (IOException e) {
            log.warn("Failed to load mic HUD icon override for {} at {}, using default", state, file, e);
            evict(state);
            return bundled(state);
        }
    }

    private static void warnOnce(MicState state, Path file) {
        try {
            FileTime mtime = Files.getLastModifiedTime(file);
            if (mtime.equals(lastWarnedMtime.get(state))) {
                return;
            }
            lastWarnedMtime.put(state, mtime);
            log.warn("Mic HUD icon override for {} at {} is not a valid PNG, using default", state, file);
        } catch (IOException ignored) {
            // file disappeared between the isRegularFile check and here; nothing to warn about
        }
    }

    private static void evict(MicState state) {
        LoadedOverride stale = overrides.remove(state);
        if (stale != null) {
            stale.texture().close();
        }
    }

    private static IconTexture bundled(MicState state) {
        return new IconTexture(BUNDLED.get(state), BUNDLED_SIZE, BUNDLED_SIZE);
    }
}
