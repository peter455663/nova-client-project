package hu.peter.nova;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Persistent client-side preferences for Nova Utility Client. */
public final class NovaConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nova.properties");
    public static boolean showWatermark = true, showCoordinates = true, showFps = true, showDirection = true, showBiome = true;
    public static boolean showKeystrokes, showCps, showArmor, showEffects, showArrayList, customCrosshair;
    public static boolean showTargetBlock, showChunkInfo, showWaypoint = true, showBuildGuide, showToolInfo;
    public static boolean toggleSprint, lightTheme;
    public static boolean hasWaypoint, hasBuildAnchor;
    public static double waypointX, waypointY, waypointZ;
    public static int buildX, buildY, buildZ;
    public static String buildTemplate = "cabin";

    private NovaConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(FILE)) {
            p.load(reader);
            showWatermark = bool(p,"hud.watermark",showWatermark); showCoordinates = bool(p,"hud.coordinates",showCoordinates);
            showFps = bool(p,"hud.fps",showFps); showDirection = bool(p,"hud.direction",showDirection); showBiome = bool(p,"hud.biome",showBiome);
            showKeystrokes = bool(p,"hud.keystrokes",showKeystrokes); showCps = bool(p,"hud.cps",showCps); showArmor = bool(p,"hud.armor",showArmor);
            showEffects = bool(p,"hud.effects",showEffects); showArrayList = bool(p,"hud.arraylist",showArrayList); customCrosshair = bool(p,"hud.crosshair",customCrosshair);
            showTargetBlock = bool(p,"visuals.target_block",showTargetBlock); showChunkInfo = bool(p,"visuals.chunk_info",showChunkInfo);
            showWaypoint = bool(p,"visuals.waypoint",showWaypoint); showBuildGuide = bool(p,"building.guide",showBuildGuide);
            showToolInfo = bool(p,"mining.tool_info",showToolInfo); toggleSprint = bool(p,"movement.toggle_sprint",toggleSprint);
            lightTheme = bool(p,"settings.light_theme",lightTheme); hasWaypoint = bool(p,"waypoint.saved",hasWaypoint);
            waypointX = decimal(p,"waypoint.x",0); waypointY = decimal(p,"waypoint.y",0); waypointZ = decimal(p,"waypoint.z",0);
            hasBuildAnchor = bool(p,"building.anchor_saved",hasBuildAnchor);
            buildX = integer(p,"building.x",0); buildY = integer(p,"building.y",0); buildZ = integer(p,"building.z",0);
            buildTemplate = p.getProperty("building.template","cabin");
        } catch (IOException e) { NovaClient.LOGGER.error("Could not load Nova settings", e); }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("hud.watermark",Boolean.toString(showWatermark)); p.setProperty("hud.coordinates",Boolean.toString(showCoordinates));
        p.setProperty("hud.fps",Boolean.toString(showFps)); p.setProperty("hud.direction",Boolean.toString(showDirection)); p.setProperty("hud.biome",Boolean.toString(showBiome));
        p.setProperty("hud.keystrokes",Boolean.toString(showKeystrokes)); p.setProperty("hud.cps",Boolean.toString(showCps)); p.setProperty("hud.armor",Boolean.toString(showArmor));
        p.setProperty("hud.effects",Boolean.toString(showEffects)); p.setProperty("hud.arraylist",Boolean.toString(showArrayList)); p.setProperty("hud.crosshair",Boolean.toString(customCrosshair));
        p.setProperty("visuals.target_block",Boolean.toString(showTargetBlock)); p.setProperty("visuals.chunk_info",Boolean.toString(showChunkInfo));
        p.setProperty("visuals.waypoint",Boolean.toString(showWaypoint)); p.setProperty("building.guide",Boolean.toString(showBuildGuide));
        p.setProperty("mining.tool_info",Boolean.toString(showToolInfo)); p.setProperty("movement.toggle_sprint",Boolean.toString(toggleSprint));
        p.setProperty("settings.light_theme",Boolean.toString(lightTheme)); p.setProperty("waypoint.saved",Boolean.toString(hasWaypoint));
        p.setProperty("waypoint.x",Double.toString(waypointX)); p.setProperty("waypoint.y",Double.toString(waypointY)); p.setProperty("waypoint.z",Double.toString(waypointZ));
        p.setProperty("building.anchor_saved",Boolean.toString(hasBuildAnchor)); p.setProperty("building.x",Integer.toString(buildX));
        p.setProperty("building.y",Integer.toString(buildY)); p.setProperty("building.z",Integer.toString(buildZ));
        p.setProperty("building.template",buildTemplate);
        try { Files.createDirectories(FILE.getParent()); try (Writer w = Files.newBufferedWriter(FILE)) { p.store(w,"Nova Utility Client settings"); } }
        catch (IOException e) { NovaClient.LOGGER.error("Could not save Nova settings",e); }
    }
    private static boolean bool(Properties p,String k,boolean d) { return Boolean.parseBoolean(p.getProperty(k,Boolean.toString(d))); }
    private static int integer(Properties p,String k,int d) { try { return Integer.parseInt(p.getProperty(k,Integer.toString(d))); } catch (NumberFormatException e) { return d; } }
    private static double decimal(Properties p,String k,double d) { try { return Double.parseDouble(p.getProperty(k,Double.toString(d))); } catch (NumberFormatException e) { return d; } }
}