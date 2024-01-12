package io.github.xypercode.bubbleblastermcgdx.forge;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.platform.minecraft.BubbleBlasterMC;
import net.minecraftforge.fml.common.Mod;

@Mod(BubbleBlaster.NAMESPACE)
public class BubbleBlasterMCForge {
    public BubbleBlasterMCForge() {
        BubbleBlasterMC.init();
    }
}