package ru.berdinskiybear.michud.config;

import net.minecraft.client.gui.screens.Screen;
import net.uku3lig.ukulib.config.option.CyclingOption;
import net.uku3lig.ukulib.config.option.TypedInputOption;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;
import ru.berdinskiybear.michud.MicHudMod;

import java.util.Optional;

public class MicHudConfigScreen extends AbstractConfigScreen<MicHudConfig> {
    protected MicHudConfigScreen(Screen parent) {
        super("michud.config", parent, MicHudMod.getManager());
    }

    @Override
    protected WidgetCreator[] getWidgets(MicHudConfig config) {
        return new WidgetCreator[]{
                CyclingOption.ofBoolean("michud.option.enabled", config.isEnabled(), config::setEnabled),
                CyclingOption.ofTranslatableEnum("michud.option.anchor", MicHudConfig.Anchor.class, config.getAnchor(), config::setAnchor),
                CyclingOption.ofTranslatableEnum("michud.option.side", MicHudConfig.Side.class, config.getSide(), config::setSide),
                new TypedInputOption<>("michud.option.offsetX", String.valueOf(config.getOffsetX()), config::setOffsetX, this::getInt),
                new TypedInputOption<>("michud.option.offsetY", String.valueOf(config.getOffsetY()), config::setOffsetY, this::getInt),
                new TypedInputOption<>("michud.option.iconSize", String.valueOf(config.getIconSize()), config::setIconSize, this::getInt),
        };
    }

    private Optional<Integer> getInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
