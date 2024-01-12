package com.ultreon.bubbles.platform.minecraft;

import com.badlogic.gdx.math.MathUtils;
import com.google.common.collect.Lists;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.render.gui.widget.Container;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ModListScreen extends Screen {
    private static final int GAP = 2;
    private static final int ENTRY_HEIGHT = 110;
    private final List<Mod> entries = Lists.newArrayList(Platform.getMods());
    private ObjectList<Mod> modList;
    private GuiComponent detailsPane;

    public ModListScreen() {
        super();

        this.entries.sort(Comparator.comparing(Mod::getName));
    }

    @Override
    public void init() {
        this.clearWidgets();

        var calcWidth = this.calculateWidth();
        this.modList = this.add(new ObjectList<>(this.entries, ENTRY_HEIGHT, GAP, 0, 0, calcWidth, this.height));
        this.modList.setSelectable(true);
        this.modList.setEntryRenderer(this::renderEntry);

        var entryAt = this.modList.getEntryAt(0, 0);
        if (entryAt != null) {
            this.modList.setSelected(entryAt);
        }

        this.detailsPane = this.add(new InfoContainer(calcWidth));
    }

    private int calculateWidth() {
        return Math.min(this.width - 50, 500);
    }

    @Override
    public boolean mouseWheel(int x, int y, float rotation) {
        return super.mouseWheel(x, y, rotation);
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.modList.setHeight(this.height);
        this.detailsPane.setX(this.modList.getWidth());
        this.detailsPane.setWidth(this.width - this.modList.getWidth());
        this.detailsPane.setHeight(this.height);

        super.render(renderer, mouseX, mouseY, deltaTime);
    }

    private void renderEntry(Renderer renderer, float width, float height, float y, Mod entry, boolean selected, boolean hovered) {
        renderer.fill(0, y, width, height, Color.argb(hovered ? 0x40ffffff : 0x20ffffff));
        if (selected)
            renderer.drawEffectBox(this.modList.getX(), (int) y, width, height, new Insets(1, 1, 4, 1));
        else if (hovered)
            renderer.drawEffectBox(this.modList.getX(), (int) y, width, height, new Insets(1, 1, 1, 1));

        renderer.scissored(this.modList.getX(), y, width, height, () -> {
            var iconSize = ENTRY_HEIGHT - 40;
            var tex = ModDataManager.getIcon(entry);
            renderer.blit(tex, this.modList.getX() + 20, y + 20, iconSize, iconSize);

            var textX = this.modList.getX() + 20 + iconSize + 20;
            renderer.drawText(Fonts.MONOSPACED_BOLD.get(), entry.getModId(), textX, y + 20, Color.WHITE.withAlpha(0x80));
            renderer.drawText(Fonts.SANS_HEADER_1.get(), entry.getName(), textX, y + 36, Color.WHITE);
            renderer.drawText(Fonts.SANS_ITALIC.get(), entry.getDescription(), textX, y + height - 25, Color.WHITE.withAlpha(0x80));
        });
    }

    private class InfoContainer extends Container {
        public InfoContainer(int calcWidth) {
            super(calcWidth, 0, ModListScreen.this.width - calcWidth, ModListScreen.this.height);
        }

        @Override
        public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
            this.renderComponent(renderer);
            super.render(renderer, mouseX, mouseY, deltaTime);
        }

        @Override
        public void renderComponent(Renderer renderer) {
            var selected = ModListScreen.this.modList.getSelected();
            if (selected == null) return;
            var metadata = selected.value;

            var textX = new AtomicInteger(this.x + 20);
            var textY = this.y + 20;

            this.drawIcon(renderer, selected, textX, textY);
            this.drawModDetails(renderer, metadata, textX, textY);
        }

        private void drawIcon(Renderer renderer, ObjectList.ListEntry<Mod, ? extends Mod> selected, AtomicInteger textX, int textY) {
            try {
                var tex = ModDataManager.getIcon(selected.value);
                renderer.blit(tex, textX.get(), textY, 64, 64);
                textX.addAndGet(80);
            } catch (RuntimeException e) {
                BubbleBlaster.LOGGER.warn("Can't load and draw mod icon for '" + selected.value.getModId() + "': " + e);
            }
        }

        private void drawModDetails(Renderer renderer, Mod metadata, AtomicInteger textX, int textY) {
            this.layout.setText(Fonts.SANS_TITLE.get(), metadata.getName() + "  ");

            renderer.drawText(Fonts.SANS_TITLE.get(), metadata.getName(), textX.get(), textY, Color.WHITE);
            renderer.drawText(Fonts.MONOSPACED_HEADING_2.get(), metadata.getVersion(), textX.get() + this.layout.width, textY + Fonts.SANS_BETA_LEVEL_UP.get().getLineHeight() / 2 - Fonts.MONOSPACED_BOLD.get().getLineHeight() + 1, Color.argb(0x80ffffff));
            renderer.drawText(Fonts.MONOSPACED_BOLD.get(), metadata.getModId(), textX.get(), textY + Fonts.SANS_TITLE.get().getLineHeight() - Fonts.MONOSPACED_BOLD.get().getLineHeight(), Color.argb(0x80ffffff));
            renderer.drawText(Fonts.MONOSPACED_BOLD.get(), metadata.getModId(), textX.get(), textY + Fonts.SANS_TITLE.get().getLineHeight() - Fonts.MONOSPACED_BOLD.get().getLineHeight(), Color.argb(0x80ffffff));
            var description = metadata.getDescription();
            var i = new AtomicInteger();
            description.lines().forEachOrdered(line -> renderer.drawText(this.font, line, textX.get(), this.y + 90 + i.getAndUpdate(this::addFontHeight) * (this.font.getLineHeight() + 1), Color.argb(0x60ffffff)));
        }

        private int addFontHeight(int i1) {
            return i1 + MathUtils.ceilPositive(this.font.getLineHeight() + 1);
        }
    }
}