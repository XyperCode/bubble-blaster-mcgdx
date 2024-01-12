package com.ultreon.bubbles.platform.minecraft;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.Constants;
import com.ultreon.bubbles.GameLibGDXWrapper;
import com.ultreon.libs.crash.v0.CrashLog;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
 */
public class BubbleBlasterMC {
    private static final int[] SIZES = {
            16, 24, 32, 48, 64, 72, 96, 108, 128, 256
    };
    private static final KeyMapping ACTIVATE_KEY = new KeyMapping("key.bubbleblaster.activate", InputConstants.KEY_NUMPADENTER, "key.categories.misc");
    private static GameLibGDXWrapper gameWrapper;

    public static void init() {
        KeyMappingRegistry.register(ACTIVATE_KEY);

        var platform = new MinecraftPlatform();

        try {
            BubbleBlasterMC.gameWrapper = new GameLibGDXWrapper(platform);
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Launch failure", t).createCrash());
            return;
        }

        ClientTickEvent.CLIENT_PRE.register(instance -> {
            final Minecraft mcClient = Minecraft.getInstance();
            if (ACTIVATE_KEY.isDown() && !(mcClient.screen instanceof BubbleBlasterScreen)) {
                mcClient.setScreen(new BubbleBlasterScreen(Component.literal("Bubble Blaster")));
            }
        });
    }

    public static GameLibGDXWrapper getGameWrapper() {
        return gameWrapper;
    }

    /**
     * @deprecated Unsupported on Minecraft backend.
     */
    @NotNull
    @Deprecated(since = "0.1.0", forRemoval = true)
    private static Lwjgl3ApplicationConfiguration createConfig() {
        List<String> icons = new ArrayList<>();
        for (var size : SIZES) {
            icons.add("assets/bubbleblaster/icons/icon" + size + ".png");
        }

        var config = new Lwjgl3ApplicationConfiguration();
        config.setBackBufferConfig(8, 8, 8, 8, 32, 0, 16);
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setIdleFPS(5);
        config.setInitialVisible(false);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2);
        config.setWindowedMode(Constants.DEFAULT_SIZE.x, Constants.DEFAULT_SIZE.y);
        config.setTitle("Bubble Blaster 2");
        config.setWindowIcon(icons.toArray(new String[]{}));
        return config;
    }
}
