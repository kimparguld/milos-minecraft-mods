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

    // Render happens up to hundreds of times per second; there is no need to
    // re-stat/re-read the override file more often than this. See I3.
    private static final long THROTTLE_NANOS = 500_000_000L;

    private static final Map<MicState, Identifier> BUNDLED = new EnumMap<>(MicState.class);

    static {
        BUNDLED.put(MicState.UNMUTED, Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "icons/unmuted.png"));
        BUNDLED.put(MicState.MUTED, Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "icons/muted.png"));
        BUNDLED.put(MicState.DISABLED, Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "icons/disabled.png"));
        BUNDLED.put(MicState.DISCONNECTED, Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "icons/disconnected.png"));
    }

    private static final Map<MicState, LoadedOverride> overrides = new EnumMap<>(MicState.class);
    private static final Map<MicState, FileTime> lastWarnedMtime = new EnumMap<>(MicState.class);
    // Negative cache: the mtime of a file we already tried and failed to decode as a PNG,
    // so we don't re-run the native decode every throttle tick until the file changes. See I2.
    private static final Map<MicState, FileTime> lastFailedDecodeMtime = new EnumMap<>(MicState.class);

    // Throttle bookkeeping for I3: last time (System.nanoTime()) the filesystem was
    // actually checked for this state, and the result returned at that check.
    private static final Map<MicState, Long> lastCheckNanos = new EnumMap<>(MicState.class);
    private static final Map<MicState, IconTexture> lastResult = new EnumMap<>(MicState.class);

    private record LoadedOverride(Identifier id, DynamicTexture texture, FileTime mtime, int width, int height) {
    }

    public record IconTexture(Identifier id, int width, int height) {
    }

    private IconLoader() {
    }

    public static Path overridePath(MicState state) {
        return overrideDir().resolve(state.name().toLowerCase(Locale.ROOT) + ".png");
    }

    public static Path overrideDir() {
        return FabricLoader.getInstance().getConfigDir()
                .resolve(MicHudMod.MOD_ID)
                .resolve("icons");
    }

    public static IconTexture resolve(MicState state) {
        long now = System.nanoTime();
        Long last = lastCheckNanos.get(state);
        IconTexture cached = lastResult.get(state);
        if (last != null && cached != null && (now - last) < THROTTLE_NANOS) {
            return cached;
        }

        lastCheckNanos.put(state, now);
        IconTexture result = resolveFromDisk(state);
        lastResult.put(state, result);
        return result;
    }

    private static IconTexture resolveFromDisk(MicState state) {
        Path file = overridePath(state);
        Optional<Path> valid = IconFileResolver.resolveValidOverride(file);

        if (valid.isEmpty()) {
            if (Files.isRegularFile(file)) {
                warnOnce(state, file, tryGetMtime(file), null);
            }
            evict(state);
            return bundled(state);
        }

        FileTime mtime = tryGetMtime(file);
        if (mtime == null) {
            // file disappeared between the isRegularFile check above and here
            evict(state);
            return bundled(state);
        }

        LoadedOverride cachedOverride = overrides.get(state);
        if (cachedOverride != null && cachedOverride.mtime().equals(mtime)) {
            return new IconTexture(cachedOverride.id(), cachedOverride.width(), cachedOverride.height());
        }

        if (mtime.equals(lastFailedDecodeMtime.get(state))) {
            // Already tried and failed to decode this exact file version; don't
            // re-attempt the native decode until the file actually changes.
            evict(state);
            return bundled(state);
        }

        NativeImage image;
        try (InputStream in = Files.newInputStream(file)) {
            image = NativeImage.read(in);
        } catch (IOException e) {
            lastFailedDecodeMtime.put(state, mtime);
            warnOnce(state, file, mtime, e);
            evict(state);
            return bundled(state);
        }

        Identifier id = Identifier.fromNamespaceAndPath(MicHudMod.MOD_ID, "override/" + state.name().toLowerCase(Locale.ROOT));
        DynamicTexture texture = new DynamicTexture(() -> "milos-mic-hud override " + state.name(), image);
        // register() already closes/replaces whatever texture was previously registered
        // at this id, so there is no need to separately close cachedOverride's texture.
        Minecraft.getInstance().getTextureManager().register(id, texture);

        overrides.put(state, new LoadedOverride(id, texture, mtime, image.getWidth(), image.getHeight()));
        return new IconTexture(id, image.getWidth(), image.getHeight());
    }

    private static FileTime tryGetMtime(Path file) {
        try {
            return Files.getLastModifiedTime(file);
        } catch (IOException e) {
            return null;
        }
    }

    private static void warnOnce(MicState state, Path file, FileTime mtime, IOException decodeFailure) {
        if (mtime == null || mtime.equals(lastWarnedMtime.get(state))) {
            return;
        }
        lastWarnedMtime.put(state, mtime);
        if (decodeFailure != null) {
            log.warn("Failed to load mic HUD icon override for {} at {}, using default", state, file, decodeFailure);
        } else {
            log.warn("Mic HUD icon override for {} at {} is not a valid PNG, using default", state, file);
        }
    }

    private static void evict(MicState state) {
        LoadedOverride stale = overrides.remove(state);
        if (stale != null) {
            Minecraft.getInstance().getTextureManager().release(stale.id());
        }
    }

    private static IconTexture bundled(MicState state) {
        return new IconTexture(BUNDLED.get(state), BUNDLED_SIZE, BUNDLED_SIZE);
    }
}
