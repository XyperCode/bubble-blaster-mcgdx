package io.github.xypercode.bubbleblastermcgdx.fabric;

import com.ultreon.bubbles.platform.minecraft.BubbleBlasterMC;
import net.fabricmc.api.ModInitializer;

public class BubbleBlasterMCFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BubbleBlasterMC.init();
    }
}