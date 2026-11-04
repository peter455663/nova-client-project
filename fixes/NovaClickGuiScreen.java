package hu.peter.nova;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Nova's modern dashboard. Category screens remain separate so the existing
 * module controls and saved configuration keep working.
 */
public final class NovaClickGuiScreen extends Screen {
    private static final int PANEL_WIDTH = 720;
    private static final int PANEL_HEIGHT = 452;
    private static final String[] LABELS = {
            "HUD / KÉPERNYŐ", "VIZUÁLIS", "MOZGÁS", "JÁTÉKOS",
            "VILÁG", "ÉPÍTÉS", "FARMOLÁS", "BÁNYÁSZAT",
            "ESZKÖZÖK", "PROFILOK", "BEÁLLÍTÁSOK", "OP PARANCSOK"
    };
    private static final String[] SUBTITLES = {
            "Kijelzők és információs elemek", "Radar, waypoint és minimap",
            "Sprint és mozgási beállítások", "Eszközök és játékossegédek",
            "Világadatok és környezet", "Építési segéd és sablonok",
            "Növénytermesztési automatizmusok", "Bányászati segédek",
            "Kényelmi funkciók", "Mentett konfigurációk",
            "Megjelenés és Nova beállítások", "Szerver által engedélyezett parancsok"
    };
    private static final NovaCategoryScreen.Category[] CATEGORIES =
            NovaCategoryScreen.Category.values();

    public NovaClickGuiScreen() {
        super(Component.literal("Nova Utility Client"));
    }

    @Override
    protected void init() {
        clearWidgets();
        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;
        int gridX = left + 26;
        int gridY = top + 112;
        int cardW = 205;
        int cardH = 43;
        int gapX = 12;
        int gapY = 10;

        for (int i = 0; i < LABELS.length; i++) {
            final NovaCategoryScreen.Category category = CATEGORIES[i];
            int col = i % 3;
            int row = i / 3;
            int x = gridX + col * (cardW + gapX);
            int y = gridY + row * (cardH + gapY);
            addRenderableWidget(Button.builder(Component.literal(LABELS[i] + "   ›"),
                    button -> this.minecraft.gui.setScreen(new NovaCategoryScreen(category)))
                    .bounds(x, y, cardW, cardH).build());
        }
        addRenderableWidget(Button.builder(Component.literal("BEZÁRÁS  ×"), button -> onClose())
                .bounds(left + PANEL_WIDTH - 132, top + PANEL_HEIGHT - 43, 106, 27).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;
        int right = left + PANEL_WIDTH;
        int bottom = top + PANEL_HEIGHT;

        int background = NovaConfig.lightTheme ? 0xF4F5F7FC : 0xF20D111B;
        int surface = NovaConfig.lightTheme ? 0xFFFFFFFF : 0xFF151B28;
        int surfaceAlt = NovaConfig.lightTheme ? 0xFFF0F1F8 : 0xFF1B2333;
        int primary = NovaConfig.lightTheme ? 0xFF202638 : 0xFFF0F3FA;
        int secondary = NovaConfig.lightTheme ? 0xFF69738A : 0xFF9AA7BE;
        int accent = NovaConfig.accentColor();

        g.fill(0, 0, width, height, 0x99070A12);
        g.fill(left - 1, top - 1, right + 1, bottom + 1, 0xFF343D52);
        g.fill(left, top, right, bottom, background);

        // Header and accent rail
        g.fill(left, top, right, top + 76, surface);
        g.fill(left, top, left + 4, bottom, accent);
        g.fill(left + 22, top + 19, left + 58, top + 55, accent);
        g.text(font, "N", left + 34, top + 31, 0xFFFFFFFF, true);
        g.text(font, "NOVA", left + 72, top + 20, primary, true);
        g.text(font, "UTILITY CLIENT  /  MODERN INTERFACE", left + 72, top + 39, secondary, false);
        g.fill(right - 184, top + 25, right - 26, top + 52, surfaceAlt);
        g.text(font, "PROFILE  " + NovaConfig.profileName.toUpperCase(), right - 174, top + 34, accent, true);

        // Section heading
        g.text(font, "VEZÉRLŐPULT", left + 26, top + 91, primary, true);
        g.text(font, "Válassz kategóriát a modulok és beállítások kezeléséhez.",
                left + 143, top + 91, secondary, false);

        // Category card backgrounds, aligned with the actual buttons
        for (int i = 0; i < LABELS.length; i++) {
            int col = i % 3;
            int row = i / 3;
            int x = left + 26 + col * 217;
            int y = top + 112 + row * 53;
            boolean hovered = mouseX >= x && mouseX <= x + 205 && mouseY >= y && mouseY <= y + 43;
            g.fill(x, y, x + 205, y + 43, hovered ? surfaceAlt : surface);
            g.fill(x, y, x + 3, y + 43, i == 0 ? accent : (hovered ? accent : 0xFF343D50));
            g.text(font, LABELS[i], x + 13, y + 8, primary, true);
            g.text(font, SUBTITLES[i], x + 13, y + 24, secondary, false);
        }

        // Bottom information strip
        int stripY = top + 335;
        g.fill(left + 26, stripY, right - 26, stripY + 66, surface);
        g.text(font, "GYORS ÁLLAPOT", left + 40, stripY + 12, accent, true);
        g.text(font, "Minimap  " + state(NovaConfig.showMinimap)
                + "     Entity radar  " + state(NovaConfig.showEntityRadar)
                + "     Auto Tool  " + state(NovaConfig.autoTool),
                left + 40, stripY + 30, primary, false);
        g.text(font, "FPS HUD  " + state(NovaConfig.showFps)
                + "     Koordináták  " + state(NovaConfig.showCoordinates)
                + "     Waypoint  " + state(NovaConfig.hasWaypoint),
                left + 40, stripY + 46, secondary, false);

        g.text(font, "RIGHT ALT  ·  MENÜ", left + 27, bottom - 30, secondary, false);
        g.text(font, "K  MINIMAP     J  RADAR     R  AUTO TOOL     T  TARGET HUD",
                left + 190, bottom - 30, secondary, false);
        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    private static String state(boolean enabled) {
        return enabled ? "BE" : "KI";
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(null);
    }
}