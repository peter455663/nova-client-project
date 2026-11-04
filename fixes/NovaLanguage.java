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
        Map.entry("MINIMAP forgatás","MINIMAP ROTATION"),
        Map.entry("Vízjel","Watermark"), Map.entry("Koordináták","Coordinates"),
        Map.entry("FPS számláló","FPS counter"), Map.entry("Irány","Direction"),
        Map.entry("Biom","Biome"), Map.entry("Billentyűk","Keystrokes"),
        Map.entry("CPS számláló","CPS counter"), Map.entry("Páncél adatok","Armor status"),
        Map.entry("Bájitalhatások","Potion effects"), Map.entry("Aktív modulok","Active modules"),
        Map.entry("Egyedi célkereszt","Custom crosshair"), Map.entry("Célpont HUD","Target HUD"),
        Map.entry("Célzott blokk adatai","Target block data"), Map.entry("Chunk koordináták","Chunk coordinates"),
        Map.entry("Waypoint távolság HUD","Waypoint distance HUD"), Map.entry("Waypoint mentése itt","Save waypoint here"),
        Map.entry("Waypoint törlése","Clear waypoint"), Map.entry("Közeli entitás radar","Nearby entity radar"),
        Map.entry("Radar hatótáv","Radar range"), Map.entry("Blokk minimap","Block minimap"),
        Map.entry("Minimap forgatás","Minimap rotation"), Map.entry("Minimap nagyítás","Minimap zoom"),
        Map.entry("Futás kapcsoló","Toggle sprint"), Map.entry("Automatikus eszközválasztás","Auto Tool"),
        Map.entry("Maximális fényerő","Full brightness"), Map.entry("Világidő","World time"),
        Map.entry("Időjárás","Weather"), Map.entry("Látható entitások","Visible entities"),
        Map.entry("Chunkrács a minimapon","Chunk grid on minimap"), Map.entry("Építési segéd HUD","Build guide HUD"),
        Map.entry("Építési pont mentése itt","Save build anchor here"),
        Map.entry("Érett növények automatikus aratása","Auto harvest mature crops"),
        Map.entry("Célzott blokk automatikus bányászata","Auto mine targeted block"),
        Map.entry("Kézben tartott eszköz tartóssága","Held tool durability"),
        Map.entry("Anti-AFK kamera mozdítás","Anti-AFK camera nudge"),
        Map.entry("Világos téma","Light theme"), Map.entry("Kiemelőszín","Accent color"),
        Map.entry("Nyelv / Language","Language"), Map.entry("Magyar","Hungarian"),
        Map.entry("BE","ON"), Map.entry("KI","OFF"),
        Map.entry("Építés szüneteltetése","Pause building"),
        Map.entry("Építés folytatása","Resume building"),
        Map.entry("Építés megszakítása","Cancel building"),
        Map.entry("Építési segéd és házsablonok","Auto Build and house blueprints"),
        Map.entry("Növénytermesztési automatizmusok","Farming automation"),
        Map.entry("Külső építmények és infrastruktúra","Structures and infrastructure"),
        Map.entry("Építmények","Structures"), Map.entry("VEZÉRLŐPULT","DASHBOARD"),
        Map.entry("Válassz kategóriát a modulok és beállítások kezeléséhez.","Choose a category to manage modules and settings"),
        Map.entry("GYORS ÁLLAPOT","QUICK STATUS"), Map.entry("BEZÁRÁS  ×","CLOSE  ×"),
        Map.entry("Vissza","Back"), Map.entry("Bezárás","Close"),
        Map.entry("HUD","HUD"), Map.entry("VISUALS","VISUALS"),
        Map.entry("MOVEMENT","MOVEMENT"), Map.entry("PLAYER","PLAYER"),
        Map.entry("WORLD","WORLD"), Map.entry("BUILDING","BUILDING"),
        Map.entry("FARMING","FARMING"), Map.entry("MINING","MINING"),
        Map.entry("MISC","MISC"), Map.entry("PROFILES","PROFILES"),
        Map.entry("SETTINGS","SETTINGS"), Map.entry("OP_ONLY","OP COMMANDS"),
        Map.entry("STRUCTURES","STRUCTURES")
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
