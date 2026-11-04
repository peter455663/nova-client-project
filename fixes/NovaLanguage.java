package hu.peter.nova;

import java.util.Map;

/** Small runtime language table for all visible Nova category and module labels. */
public final class NovaLanguage {
    private static boolean english;

    private static final Map<String,String> EN = Map.ofEntries(
        Map.entry("HUD / KÉPERNYŐ","HUD / DISPLAY"),
        Map.entry("VIZUÁLIS","VISUALS"), Map.entry("MOZGÁS","MOVEMENT"),
        Map.entry("JÁTÉKOS","PLAYER"), Map.entry("VILÁG","WORLD"),
        Map.entry("ÉPÍTÉS","BUILDING"), Map.entry("FARMOLÁS","FARMING"),
        Map.entry("BÁNYÁSZAT","MINING"), Map.entry("ESZKÖZÖK","TOOLS"),
        Map.entry("PROFILOK","PROFILES"), Map.entry("BEÁLLÍTÁSOK","SETTINGS"),
        Map.entry("OP PARANCSOK","OP COMMANDS"), Map.entry("ÉPÍTMÉNYEK","STRUCTURES"),
        Map.entry("Kijelzők és információs elemek","HUD elements and information"),
        Map.entry("Radar, waypoint és minimap","Radar, waypoints and minimap"),
        Map.entry("Sprint és mozgási beállítások","Sprint and movement settings"),
        Map.entry("Eszközök és játékossegédek","Player utilities"),
        Map.entry("Világadatok és környezet","World information"),
        Map.entry("Építési segéd és házsablonok","Build assistant and house blueprints"),
        Map.entry("Növénytermesztési automatizmusok","Farming automation"),
        Map.entry("Bányászati segédek","Mining utilities"),
        Map.entry("Kényelmi funkciók","Convenience features"),
        Map.entry("Mentett konfigurációk","Saved configurations"),
        Map.entry("Megjelenés és Nova beállítások","Appearance and Nova settings"),
        Map.entry("Szerver által engedélyezett parancsok","Server-authorized commands"),
        Map.entry("Házak és bázisok","Houses and bases"),
        Map.entry("Külső építmények és infrastruktúra","Structures and infrastructure"),
        Map.entry("BEZÁRÁS  ×","CLOSE  ×"), Map.entry("VISSZA","BACK"),
        Map.entry("MINIMAP forgatás","MINIMAP ROTATION")
    );

    private NovaLanguage() {}

    public static void load(String language) {
        english = "en".equalsIgnoreCase(language);
    }

    public static boolean isEnglish() { return english; }
    public static String code() { return english ? "en" : "hu"; }
    public static String toggle() {
        english = !english;
        NovaConfig.language = code();
        NovaConfig.save();
        return english ? "English" : "Magyar";
    }
    public static String tr(String text) {
        if (!english || text == null) return text;
        return EN.getOrDefault(text, text);
    }
}
