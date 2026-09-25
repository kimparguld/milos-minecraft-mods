package se.guldbransen.milos.michud.icon;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

public final class IconFileResolver {
    private static final byte[] PNG_MAGIC = {
            (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'
    };

    private IconFileResolver() {
    }

    /**
     * Returns {@code file} if it exists and starts with the PNG signature, otherwise empty.
     * Only checks the magic bytes — a full decode happens later, only for files this accepts.
     */
    public static Optional<Path> resolveValidOverride(Path file) {
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }

        try (InputStream in = Files.newInputStream(file)) {
            byte[] header = in.readNBytes(PNG_MAGIC.length);
            return Arrays.equals(header, PNG_MAGIC) ? Optional.of(file) : Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
