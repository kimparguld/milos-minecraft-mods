package ru.berdinskiybear.playerhighlight.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrackedPlayer implements Serializable {
    private String name = "";
    private int color = 0xFFFF5555;
}
