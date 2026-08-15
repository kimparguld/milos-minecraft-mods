package ru.berdinskiybear.michud.icon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IconFileResolverTest {
    @TempDir
    Path dir;

    @Test
    void missingFileReturnsEmpty() {
        Path file = dir.resolve("muted.png");
        assertTrue(IconFileResolver.resolveValidOverride(file).isEmpty());
    }

    @Test
    void corruptFileReturnsEmpty() throws IOException {
        Path file = dir.resolve("muted.png");
        Files.writeString(file, "this is not a png");
        assertTrue(IconFileResolver.resolveValidOverride(file).isEmpty());
    }

    @Test
    void validPngReturnsThePath() throws IOException {
        Path file = dir.resolve("muted.png");
        byte[] pngMagicPlusJunk = new byte[]{
                (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n',
                0, 0, 0, 0
        };
        Files.write(file, pngMagicPlusJunk);

        assertEquals(file, IconFileResolver.resolveValidOverride(file).orElseThrow());
    }
}
