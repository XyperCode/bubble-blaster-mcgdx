package com.ultreon.bubbles.platform.minecraft;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.DesktopGameWindow;
import com.ultreon.bubbles.GamePlatform;
import com.ultreon.bubbles.GameWindow;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.util.FileHandles;
import com.ultreon.commons.os.OperatingSystem;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.commons.v0.ProgressMessenger;
import com.ultreon.libs.crash.v0.CrashLog;
import com.ultreon.libs.datetime.v0.Duration;
import dev.architectury.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.ultreon.bubbles.BubbleBlaster.LOGGER;
import static com.ultreon.bubbles.BubbleBlaster.NAMESPACE;

public class MinecraftPlatform extends GamePlatform {
    private final FileHandle dataDir;
    private final URL gameFile;
    private boolean unknownResources = false;
    private final Notification notify = Notification.builder("Missing Resources Detected!", "Check the log for more information.")
            .subText("Resource Manager")
            .duration(Duration.ofSeconds(5))
            .build();

    public MinecraftPlatform() {
        Path path;
        if (SharedLibraryLoader.isWindows) path = Paths.get(System.getenv("APPDATA"), "BubbleBlaster");
        else if (SharedLibraryLoader.isMac) path = Paths.get(System.getProperty("user.home"), "Library/Application Support/BubbleBlaster");
        else if (SharedLibraryLoader.isLinux) path = Paths.get(System.getProperty("user.home"), ".config/BubbleBlaster");
        else throw new UnsupportedOperationException("Unsupported platform " + System.getProperty("os.name"));

        this.dataDir = new FileHandle(path.toFile());
        this.gameFile = BubbleBlaster.class.getProtectionDomain().getCodeSource().getLocation();
        this.data("game-crashes").mkdirs();
        this.data("logs").mkdirs();
        this.data("screenshots").mkdirs();
        this.data("saves").mkdirs();
        this.data("config").mkdirs();
        this.data("mods").mkdirs();
    }

    @Override
    public GameWindow createWindow(GameWindow.Properties properties) {
        return new DesktopGameWindow(properties);
    }

    @Override
    public FileHandle data(String path) {
        return this.dataDir.child(path);
    }

    @Override
    public Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    @Override
    public OperatingSystem getOperatingSystem() {
        if (SharedLibraryLoader.isWindows) return OperatingSystem.Windows;
        if (SharedLibraryLoader.isLinux) return OperatingSystem.Linux;
        if (SharedLibraryLoader.isMac) return OperatingSystem.MacOS;
        else throw new UnsupportedOperationException("Unsupported operating system");
    }

    @Override
    public boolean isDebug() {
        return Platform.isModLoaded("bubbleblaster_debug");
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return Platform.isDevelopmentEnvironment();
    }

    @Override
    public Screen openModListScreen() {
        return new ModListScreen();
    }

    @Override
    public void setupMods() {
        // Not supported for Minecraft backend.
    }

    @Override
    public void setCustomIcon(String modId, Identifier location) {
        GameEvents.RESOURCES_LOADED.listen(resourceManager -> {
            final var resource = resourceManager.getResource(location);
            if (resource == null) {
                this.logUnknownResource("Custom mod icon for " + modId + " wasn't found!");
                return;
            }
            ModDataManager.setIcon(modId, BubbleBlaster.invokeAndWait(() -> new Texture(this.setFilter(new Pixmap(FileHandles.imageBytes(resource.loadOrGet()))))));
        });
    }

    private Pixmap setFilter(Pixmap pixmap) {
        pixmap.setFilter(Pixmap.Filter.BiLinear);
        return pixmap;
    }

    private void logUnknownResource(String message) {
        if (!this.unknownResources) {
            this.unknownResources = true;
            BubbleBlaster.whenLoaded(UUID.fromString("f8000df9-f94b-4106-bd26-c7ba48338a23"), () -> BubbleBlaster.getInstance().notifications.notify(this.notify));
        }
        LOGGER.error("Unknown resource: " + message);
    }

    @Override
    public void loadModResources(AtomicReference<ProgressMessenger> progressAlt, Messenger msgAlt) {
        // Not supported for Minecraft backend.
        progressAlt.set(null);
    }

    @Override
    public void loadGameResources(AtomicReference<ProgressMessenger> progressAlt, Messenger msgAlt) {
        try {
            this.game().getResourceManager().importPackage(this.getGameFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL getGameFile() {
        return this.gameFile;
    }

    @Override
    public FileHandle getDataDirectory() {
        return this.dataDir;
    }

    public String getArg(String key) {
        return null;
    }

    public String getArgOrDefault(String key, String value) {
        return value;
    }

    public List<String> getExtraArgs() {
        return List.of();
    }

    public boolean hasArg(String key) {
        return false;
    }

    @Override
    public boolean allowsMods() {
        return true;
    }

    @Override
    public int getModsCount() {
        return Platform.getMods().size();
    }

    @Override
    public void initImGui() {
        // Not supported for Minecraft backend.
    }

    @Override
    public void renderImGui(Renderer renderer) {
        // Not supported for Minecraft backend.
    }

    @Override
    public void dispose() {
        // Not supported for Minecraft backend.
    }

    @Override
    public void initMods() {
        // Not supported for Minecraft backend.
    }

    @Override
    public String getFabricLoaderVersion() {
        return "0.0.0";
    }

    @Override
    public String getGameVersion() {
        return Platform.getMod(NAMESPACE).getVersion();
    }

    @Override
    public void toggleDebugGui() {
        // Not supported for Minecraft backend.
    }

    @Override
    public boolean isDebugGuiOpen() {
        return false;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return Platform.isModLoaded(modId);
    }

    @Override
    public boolean isDesktop() {
        return true;
    }

    @Override
    public void showError(@NotNull String title, @Nullable String description) {
        JOptionPane.showMessageDialog(null, description, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public int getRecommendedFPS() {
        return 120;
    }

    @Override
    public void handleCrash(CrashLog crashLog) {
        crashLog.writeToFile(new File(GamePlatform.get().data("game-crashes").file(), crashLog.getDefaultFileName()));
    }
}
