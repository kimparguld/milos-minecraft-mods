package se.guldbransen.milos.playerhighlight.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.uku3lig.ukulib.config.option.widget.ColorInputWidget;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import se.guldbransen.milos.playerhighlight.PlayerHighlightMod;

import java.util.ArrayList;
import java.util.List;

public class PlayerHighlightConfigScreen extends CloseableScreen {
    private static final int ROW_HEIGHT = 24;
    private static final int TOP = 32;
    private static final int LEFT = 20;
    private static final int NAME_WIDTH = 200;
    private static final int COLOR_WIDTH = 90;
    private static final int REMOVE_WIDTH = 20;
    private static final int GAP = 8;
    private static final int FOOTER_BUTTON_HEIGHT = 20;

    private final PlayerHighlightConfig config;
    private final List<Row> rows = new ArrayList<>();

    private int scrollOffset = 0;
    private int listBottom;

    public PlayerHighlightConfigScreen(Screen parent) {
        super("playerhighlight.config", parent);
        this.config = PlayerHighlightMod.config();
    }

    @Override
    protected void init() {
        rows.clear();
        scrollOffset = 0;

        List<TrackedPlayer> entries = config.getEntries();

        for (int i = 0; i < entries.size(); i++) {
            addRow(entries.get(i), i);
        }

        // "Done" stays pinned to the bottom of the screen, and "+ Add player" is
        // pinned right above it, so neither moves with the (scrollable) row list.
        int doneY = this.height - 28;
        int addY = doneY - FOOTER_BUTTON_HEIGHT - 4;
        listBottom = addY - GAP;

        this.addRenderableWidget(Button.builder(Component.literal("+ Add player"), button -> {
            entries.add(new TrackedPlayer());
            this.minecraft.setScreen(new PlayerHighlightConfigScreen(this.parent));
        }).bounds(LEFT, addY, NAME_WIDTH, FOOTER_BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose())
                .bounds(this.width / 2 - 100, doneY, 200, 20).build());

        repositionRows();
    }

    private void addRow(TrackedPlayer entry, int index) {
        int colorX = LEFT + NAME_WIDTH + GAP;
        int removeX = colorX + COLOR_WIDTH + GAP;

        EditBox nameBox = new EditBox(this.font, LEFT, TOP, NAME_WIDTH, 20, Component.literal("player name"));
        nameBox.setMaxLength(16);
        nameBox.setValue(entry.getName());
        nameBox.setResponder(entry::setName);
        this.addRenderableWidget(nameBox);

        ColorInputWidget colorWidget = new ColorInputWidget(colorX, TOP, COLOR_WIDTH, 20,
                entry.getColor(), entry::setColor, "#RRGGBB", false);
        this.addRenderableWidget(colorWidget);

        Button removeButton = this.addRenderableWidget(Button.builder(Component.literal("X"), button -> {
            config.getEntries().remove(index);
            this.minecraft.setScreen(new PlayerHighlightConfigScreen(this.parent));
        }).bounds(removeX, TOP, REMOVE_WIDTH, 20).build());

        rows.add(new Row(index, List.of(nameBox, colorWidget, removeButton)));
    }

    /**
     * Repositions every row's widgets according to the current scroll offset,
     * hiding (and disabling) rows that fall outside the visible list area
     * between the header and the pinned footer buttons.
     */
    private void repositionRows() {
        for (Row row : rows) {
            int y = TOP + row.index() * ROW_HEIGHT - scrollOffset;
            boolean visible = y >= TOP && y + ROW_HEIGHT <= listBottom;
            for (AbstractWidget widget : row.widgets()) {
                widget.setY(y);
                widget.visible = visible;
            }
        }
    }

    private int maxScroll() {
        int contentHeight = config.getEntries().size() * ROW_HEIGHT;
        int viewportHeight = listBottom - TOP;
        return Math.max(0, contentHeight - viewportHeight);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = maxScroll();
        if (maxScroll <= 0) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        scrollOffset -= (int) Math.round(scrollY) * ROW_HEIGHT;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        repositionRows();
        return true;
    }

    @Override
    public void onClose() {
        PlayerHighlightConfigIO.save(config);
        PlayerHighlightMod.reloadConfig();
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
    }

    private record Row(int index, List<AbstractWidget> widgets) {
    }
}
