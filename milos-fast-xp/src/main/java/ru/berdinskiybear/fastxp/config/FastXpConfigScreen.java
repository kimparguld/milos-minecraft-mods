package ru.berdinskiybear.fastxp.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.config.option.CyclingOption;
import net.uku3lig.ukulib.config.option.IntSliderOption;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;
import net.uku3lig.ukulib.config.serialization.ConfigSerializer;
import ru.berdinskiybear.fastxp.FastXpMod;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-only config screen. ukulib is a {@code "environment": "client"} mod, so nothing outside
 * this class (and {@link UkulibIntegration}, reached only through ukulib's own client-side
 * entrypoint lookup) may touch a ukulib class -- see {@link FastXpConfigIO}.
 */
public class FastXpConfigScreen extends AbstractConfigScreen<FastXpConfig> {
    protected FastXpConfigScreen(Screen parent) {
        super("fastxp.config", parent, createManager());
    }

    /**
     * Builds the ukulib manager the widgets edit, lazily and client-side only. It is seeded with
     * the live config instance (rather than a fresh read from disk) so the screen opens on
     * truly-current values, and it edits that instance in place.
     */
    private static ConfigManager<FastXpConfig> createManager() {
        return new ConfigManager<>(new Serializer(), FastXpMod.config());
    }

    /**
     * Bridges ukulib's serializer interface onto {@link FastXpConfigIO}, so there is exactly one
     * config file format and one piece of I/O logic.
     * <p>
     * This is also the sync point that makes edits take effect without a restart: every ukulib
     * write path funnels through {@link #serialize(FastXpConfig)} -- the Done/Escape path via
     * {@code BaseConfigScreen.removed() -> manager.saveConfig()}, and the Reset button via
     * {@code manager.resetConfig() -> replaceConfig() -> serialize()}. Hooking {@code removed()}
     * instead would miss the Reset button, which calls {@code setScreen(parent)} (and therefore
     * {@code removed()}) <em>before</em> it resets and saves the config.
     */
    private static final class Serializer implements ConfigSerializer<FastXpConfig> {
        @Override
        public FastXpConfig deserialize() {
            return FastXpConfigIO.load();
        }

        @Override
        public void serialize(FastXpConfig config) {
            FastXpConfigIO.save(config);
            FastXpMod.reloadConfig();
        }

        @Override
        public FastXpConfig makeDefault() {
            return new FastXpConfig();
        }
    }

    @Override
    protected WidgetCreator[] getWidgets(FastXpConfig config) {
        List<WidgetCreator> widgets = new ArrayList<>();

        widgets.add(CyclingOption.ofBoolean("fastxp.option.xpCreationEnabled", config.isXpCreationEnabled(), config::setXpCreationEnabled));
        widgets.add(new IntSliderOption("fastxp.option.xpCreationMultiplier", config.getXpCreationMultiplier(), config::setXpCreationMultiplier,
                i -> Component.literal("x" + i), 1, 10, 1));

        widgets.add(CyclingOption.ofBoolean("fastxp.option.xpPickupEnabled", config.isXpPickupEnabled(), config::setXpPickupEnabled));
        widgets.add(new IntSliderOption("fastxp.option.xpPickupOrbsPerTick", config.getXpPickupOrbsPerTick(), config::setXpPickupOrbsPerTick,
                i -> Component.literal(i + "/tick"), 1, 16, 1));

        widgets.add(CyclingOption.ofBoolean("fastxp.option.autoThrowEnabled", config.isAutoThrowEnabled(), config::setAutoThrowEnabled));
        widgets.add(new IntSliderOption("fastxp.option.autoThrowDelayTicks", config.getAutoThrowDelayTicks(), config::setAutoThrowDelayTicks,
                i -> Component.literal(i + "t"), 1, 20, 1));

        for (String itemId : KNOWN_PROJECTILES) {
            widgets.add(CyclingOption.ofBoolean(
                    projectileTranslationKey(itemId),
                    config.isProjectileEnabled(itemId),
                    enabled -> config.setProjectileEnabled(itemId, enabled)
            ));
        }

        return widgets.toArray(new WidgetCreator[0]);
    }

    private static final List<String> KNOWN_PROJECTILES = List.of(
            "minecraft:snowball",
            "minecraft:egg",
            "minecraft:ender_pearl",
            "minecraft:splash_potion",
            "minecraft:lingering_potion",
            "minecraft:trident",
            "minecraft:experience_bottle"
    );

    private static String projectileTranslationKey(String itemId) {
        return "fastxp.option.projectile." + itemId.substring(itemId.indexOf(':') + 1);
    }
}
