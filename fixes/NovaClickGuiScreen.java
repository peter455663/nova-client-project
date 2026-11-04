package hu.peter.nova;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Small dark ClickGUI. All switches affect only this player's HUD. */
public final class NovaClickGuiScreen extends Screen {
    private static final int PANEL_WIDTH = 370;
    private static final int PANEL_HEIGHT = 310;

    private Button watermarkButton;
    private Button coordinatesButton;
    private Button fpsButton;
    private Button directionButton;
    private Button biomeButton;

    public NovaClickGuiScreen() {
        super(Component.literal("Nova Utility Client"));
    }

    @Override
    protected void init() {
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;
        int buttonX = left + 25;
        int buttonWidth = PANEL_WIDTH - 50;

        watermarkButton = Button.builder(watermarkLabel(), button -> {
            NovaConfig.showWatermark = !NovaConfig.showWatermark;
            button.setMessage(watermarkLabel());
            NovaConfig.save();
        }).bounds(buttonX, top + 82, buttonWidth, 24).build();
        this.addRenderableWidget(watermarkButton);

        coordinatesButton = Button.builder(coordinatesLabel(), button -> {
            NovaConfig.showCoordinates = !NovaConfig.showCoordinates;
            button.setMessage(coordinatesLabel());
            NovaConfig.save();
        }).bounds(buttonX, top + 114, buttonWidth, 24).build();
        this.addRenderableWidget(coordinatesButton);

        fpsButton = Button.builder(fpsLabel(), button -> {
            NovaConfig.showFps = !NovaConfig.showFps;
            button.setMessage(fpsLabel());
            NovaConfig.save();
        }).bounds(buttonX, top + 146, buttonWidth, 24).build();
        this.addRenderableWidget(fpsButton);

        directionButton = Button.builder(directionLabel(), button -> {
            NovaConfig.showDirection = !NovaConfig.showDirection;
            button.setMessage(directionLabel());
            NovaConfig.save();
        }).bounds(buttonX, top + 178, buttonWidth, 24).build();
        this.addRenderableWidget(directionButton);

        biomeButton = Button.builder(biomeLabel(), button -> {
            NovaConfig.showBiome = !NovaConfig.showBiome;
            button.setMessage(biomeLabel());
            NovaConfig.save();
        }).bounds(buttonX, top + 210, buttonWidth, 24).build();
        this.addRenderableWidget(biomeButton);

        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.onClose())
                .bounds(left + PANEL_WIDTH - 100, top + PANEL_HEIGHT - 38, 75, 22)
                .build());
    }

    private static Component watermarkLabel() {
        return Component.literal("Watermark HUD: " + onOff(NovaConfig.showWatermark));
    }

    private static Component coordinatesLabel() {
        return Component.literal("Coordinates HUD: " + onOff(NovaConfig.showCoordinates));
    }

    private static Component fpsLabel() {
        return Component.literal("FPS HUD: " + onOff(NovaConfig.showFps));
    }

    private static Component directionLabel() {
        return Component.literal("Direction HUD: " + onOff(NovaConfig.showDirection));
    }

    private static Component biomeLabel() {
        return Component.literal("Biome HUD: " + onOff(NovaConfig.showBiome));
    }

    private static String onOff(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;

        graphics.fill(0, 0, this.width, this.height, 0x88000000);
        graphics.fill(left - 1, top - 1, left + PANEL_WIDTH + 1, top + PANEL_HEIGHT + 1,
                0xFF353047);
        graphics.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xF0121520);
        graphics.fill(left, top, left + PANEL_WIDTH, top + 40, 0xFF1C2030);
        graphics.fill(left, top, left + 3, top + PANEL_HEIGHT, 0xFF8B5CF6);

        graphics.text(this.font, "NOVA", left + 20, top + 13, 0xFFB9A0FF, true);
        graphics.text(this.font, "UTILITY CLIENT  /  26.3", left + 79, top + 13,
                0xFFE6E8F0, true);
        graphics.text(this.font, "HUD MODULES", left + 25, top + 59,
                0xFF9AA4B8, true);
        graphics.text(this.font, "Right Alt opens this panel  /  client-side HUD",
                left + 25, top + 252, 0xFF9AA4B8, false);

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(null);
    }
}
