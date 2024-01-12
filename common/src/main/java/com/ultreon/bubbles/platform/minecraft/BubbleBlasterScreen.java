package com.ultreon.bubbles.platform.minecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GameLibGDXWrapper;
import com.ultreon.libs.crash.v0.CrashLog;
import io.github.ultreon.gdxminecraft.api.GdxScreen;
import net.minecraft.network.chat.Component;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class BubbleBlasterScreen extends GdxScreen {
    private GameLibGDXWrapper gameWrapper;

    protected BubbleBlasterScreen(Component component) {
        super(component);
    }

    @Override
    protected void init() {
        super.init();

        try {
            gameWrapper = BubbleBlasterMC.getGameWrapper();
            gameWrapper.create();
            BubbleBlaster.getInstance().windowLoaded();
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Game crashed :(", t).createCrash());
        }
    }

    @Override
    public void render(ShapeDrawer shapeDrawer, Batch batch, int i, int j, float f) {
        gameWrapper.render();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        Gdx.app.exit();
        return true;
    }
}
