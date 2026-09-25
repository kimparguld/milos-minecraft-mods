package se.guldbransen.milos.michud.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.uku3lig.ukulib.config.option.StringTranslatable;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MicHudConfig implements Serializable {
    private boolean enabled = true;
    private Anchor anchor = Anchor.TOP_CENTER;
    private Side side = Side.LEFT;
    private int offsetX = 0;
    private int offsetY = 0;
    private int iconSize = 16;

    @Getter
    @AllArgsConstructor
    public enum Anchor implements StringTranslatable {
        TOP_CENTER("top_center", "michud.option.topCenter"),
        TOP("top", "michud.option.top"),
        BOTTOM("bottom", "michud.option.bottom"),
        HOTBAR("hotbar", "michud.option.hotbar");

        private final String name;
        private final String translationKey;
    }

    @Getter
    @AllArgsConstructor
    public enum Side implements StringTranslatable {
        LEFT("left", "options.mainHand.left"),
        RIGHT("right", "options.mainHand.right");

        private final String name;
        private final String translationKey;
    }
}
