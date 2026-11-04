package hu.peter.nova;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NovaClient implements ClientModInitializer {
    public static final String MOD_ID = "nova";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyMapping openGuiKey;

    @Override
    public void onInitializeClient() {
        NovaConfig.load();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main")
        );

        openGuiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.nova.open_gui",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_RALT,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.consumeClick()) {
                client.gui.setScreen(new NovaClickGuiScreen());
            }
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "utility_hud"),
                NovaClient::renderHud
        );

        LOGGER.info("Nova Utility Client initialized. Press Right Alt to open the menu.");
    }

    private static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) {
            return;
        }

        int enabledRows = 0;
        if (NovaConfig.showWatermark) enabledRows++;
        if (NovaConfig.showCoordinates) enabledRows++;
        if (NovaConfig.showFps) enabledRows++;
        if (NovaConfig.showDirection) enabledRows++;
        if (NovaConfig.showBiome) enabledRows++;
        if (enabledRows == 0) return;

        int x = 8;
        int y = 8;
        int rowHeight = 12;
        int width = 190;
        int height = 8 + enabledRows * rowHeight;

        graphics.fill(x, y, x + width, y + height, 0xA8101420);
        graphics.fill(x, y, x + 2, y + height, 0xFF8B5CF6);

        int textY = y + 5;
        if (NovaConfig.showWatermark) {
            graphics.text(client.font, "NOVA  /  UTILITY", x + 8, textY,
                    0xFFB9A0FF, true);
            textY += rowHeight;
        }

        if (NovaConfig.showCoordinates) {
            String coordinates = String.format(java.util.Locale.ROOT,
                    "XYZ  %.1f  %.1f  %.1f",
                    client.player.getX(), client.player.getY(), client.player.getZ());
            graphics.text(client.font, coordinates, x + 8, textY,
                    0xFFF1F3F8, true);
            textY += rowHeight;
        }

        if (NovaConfig.showFps) {
            graphics.text(client.font, "FPS  " + client.getFps(), x + 8, textY,
                    0xFFB8C2D8, true);
            textY += rowHeight;
        }

        if (NovaConfig.showDirection) {
            String direction = client.player.getDirection().getName().toUpperCase(java.util.Locale.ROOT);
            graphics.text(client.font, "FACING  " + direction, x + 8, textY,
                    0xFFF1F3F8, true);
            textY += rowHeight;
        }

        if (NovaConfig.showBiome) {
            String biome = client.level.getBiome(client.player.blockPosition())
                    .unwrapKey()
                    .map(key -> key.identifier().getPath().replace('_', ' '))
                    .orElse("unknown");
            graphics.text(client.font, "BIOME  " + biome, x + 8, textY,
                    0xFFB8C2D8, true);
        }
    }
}
