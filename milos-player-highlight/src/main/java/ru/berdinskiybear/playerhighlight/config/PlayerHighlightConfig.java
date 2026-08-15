package ru.berdinskiybear.playerhighlight.config;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

@Getter
@Setter
public class PlayerHighlightConfig implements Serializable {
    private List<TrackedPlayer> entries = new ArrayList<>();

    public OptionalInt findColor(String playerName) {
        for (TrackedPlayer entry : entries) {
            if (entry != null && playerName.equalsIgnoreCase(entry.getName())) {
                return OptionalInt.of(entry.getColor());
            }
        }
        return OptionalInt.empty();
    }
}
