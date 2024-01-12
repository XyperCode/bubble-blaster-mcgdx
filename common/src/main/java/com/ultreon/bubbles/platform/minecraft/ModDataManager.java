package com.ultreon.bubbles.platform.minecraft;

import com.badlogic.gdx.graphics.Texture;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.architectury.platform.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;

public class ModDataManager {
    private static final Map<String, Texture> ICONS = Maps.newHashMap();

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public static Texture setIcon(Mod entry, Texture pixmap) {
        return ICONS.put(entry.getModId(), pixmap);
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public static Texture setIcon(String id, Texture read) {
        System.out.println("id = " + id + ", read = " + read);
        return ICONS.put(id, read);
    }

    @CanIgnoreReturnValue
    public static Texture getIcon(Mod mod) {
        return Objects.requireNonNull(ICONS.get(mod.getModId()), "Icon not found for mod: " + mod.getModId());
    }
}
