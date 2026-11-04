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
    public static boolean showWatermark=true, showCoordinates=true, showFps=true, showDirection=true, showBiome=true;
    public static boolean showKeystrokes, showCps, showArmor, showEffects, showArrayList, customCrosshair;
    public static boolean showTargetBlock, showChunkInfo, showWaypoint=true, showBuildGuide, showToolInfo;
    public static boolean showWorldTime, showWeather, showEntityCount, showPing;
    public static boolean toggleSprint, lightTheme, autoMine, autoFarm, antiAfk, showEntityRadar, autoTool;
    public static boolean showMinimap, showTargetHud, showChunkBorders, maxBrightness;
    public static int entityRadarRange=24, accentColorIndex=0, minimapZoom=1;\n    public static boolean minimapRotate=true;
    public static boolean hasWaypoint, hasBuildAnchor;
    public static double waypointX, waypointY, waypointZ;
    public static int buildX, buildY, buildZ;
    public static String buildTemplate="cabin", profileName="Custom";

    private NovaConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        Properties p=new Properties();
        try (Reader reader=Files.newBufferedReader(FILE)) {
            p.load(reader);
            showWatermark=bool(p,"hud.watermark",showWatermark); showCoordinates=bool(p,"hud.coordinates",showCoordinates);
            showFps=bool(p,"hud.fps",showFps); showDirection=bool(p,"hud.direction",showDirection); showBiome=bool(p,"hud.biome",showBiome);
            showKeystrokes=bool(p,"hud.keystrokes",showKeystrokes); showCps=bool(p,"hud.cps",showCps); showArmor=bool(p,"hud.armor",showArmor);
            showEffects=bool(p,"hud.effects",showEffects); showArrayList=bool(p,"hud.arraylist",showArrayList); customCrosshair=bool(p,"hud.crosshair",customCrosshair);
            showTargetBlock=bool(p,"visuals.target_block",showTargetBlock); showChunkInfo=bool(p,"visuals.chunk_info",showChunkInfo);
            showWaypoint=bool(p,"visuals.waypoint",showWaypoint); showBuildGuide=bool(p,"building.guide",showBuildGuide);
            showToolInfo=bool(p,"mining.tool_info",showToolInfo); toggleSprint=bool(p,"movement.toggle_sprint",toggleSprint);
            showWorldTime=bool(p,"world.time",showWorldTime); showWeather=bool(p,"world.weather",showWeather);
            showEntityCount=bool(p,"world.entity_count",showEntityCount); showPing=bool(p,"hud.ping",showPing);
            autoMine=bool(p,"mining.auto",autoMine); autoFarm=bool(p,"farming.auto",autoFarm); antiAfk=bool(p,"misc.anti_afk",antiAfk);
            showEntityRadar=bool(p,"world.entity_radar",showEntityRadar); entityRadarRange=Math.max(8,Math.min(64,integer(p,"world.entity_radar_range",entityRadarRange)));
            autoTool=bool(p,"player.auto_tool",autoTool); accentColorIndex=Math.floorMod(integer(p,"settings.accent_color",accentColorIndex),5);
            showMinimap=bool(p,"visuals.minimap",showMinimap); showTargetHud=bool(p,"hud.target",showTargetHud);
            showChunkBorders=bool(p,"world.chunk_borders",showChunkBorders); maxBrightness=bool(p,"visuals.max_brightness",maxBrightness);\n            minimapZoom=Math.max(1,Math.min(4,integer(p,"visuals.minimap_zoom",minimapZoom)));\n            minimapRotate=bool(p,"visuals.minimap_rotate",minimapRotate);
            lightTheme=bool(p,"settings.light_theme",lightTheme); hasWaypoint=bool(p,"waypoint.saved",hasWaypoint);
            waypointX=decimal(p,"waypoint.x",0); waypointY=decimal(p,"waypoint.y",0); waypointZ=decimal(p,"waypoint.z",0);
            hasBuildAnchor=bool(p,"building.anchor_saved",hasBuildAnchor);
            buildX=integer(p,"building.x",0); buildY=integer(p,"building.y",0); buildZ=integer(p,"building.z",0);
            buildTemplate=p.getProperty("building.template",buildTemplate); profileName=p.getProperty("profile.name",profileName);
        } catch(IOException e) { NovaClient.LOGGER.error("Could not load Nova settings",e); }
    }

    public static void save() {
        Properties p=new Properties();
        p.setProperty("hud.watermark",Boolean.toString(showWatermark)); p.setProperty("hud.coordinates",Boolean.toString(showCoordinates));
        p.setProperty("hud.fps",Boolean.toString(showFps)); p.setProperty("hud.direction",Boolean.toString(showDirection)); p.setProperty("hud.biome",Boolean.toString(showBiome));
        p.setProperty("hud.keystrokes",Boolean.toString(showKeystrokes)); p.setProperty("hud.cps",Boolean.toString(showCps)); p.setProperty("hud.armor",Boolean.toString(showArmor));
        p.setProperty("hud.effects",Boolean.toString(showEffects)); p.setProperty("hud.arraylist",Boolean.toString(showArrayList)); p.setProperty("hud.crosshair",Boolean.toString(customCrosshair));
        p.setProperty("hud.ping",Boolean.toString(showPing)); p.setProperty("visuals.target_block",Boolean.toString(showTargetBlock));
        p.setProperty("visuals.chunk_info",Boolean.toString(showChunkInfo)); p.setProperty("visuals.waypoint",Boolean.toString(showWaypoint));
        p.setProperty("building.guide",Boolean.toString(showBuildGuide)); p.setProperty("mining.tool_info",Boolean.toString(showToolInfo));
        p.setProperty("movement.toggle_sprint",Boolean.toString(toggleSprint)); p.setProperty("world.time",Boolean.toString(showWorldTime));
        p.setProperty("world.weather",Boolean.toString(showWeather)); p.setProperty("world.entity_count",Boolean.toString(showEntityCount));
        p.setProperty("mining.auto",Boolean.toString(autoMine)); p.setProperty("farming.auto",Boolean.toString(autoFarm));
        p.setProperty("misc.anti_afk",Boolean.toString(antiAfk)); p.setProperty("settings.light_theme",Boolean.toString(lightTheme));
        p.setProperty("world.entity_radar",Boolean.toString(showEntityRadar)); p.setProperty("world.entity_radar_range",Integer.toString(entityRadarRange));
        p.setProperty("player.auto_tool",Boolean.toString(autoTool)); p.setProperty("settings.accent_color",Integer.toString(accentColorIndex));
        p.setProperty("visuals.minimap",Boolean.toString(showMinimap)); p.setProperty("hud.target",Boolean.toString(showTargetHud));
        p.setProperty("world.chunk_borders",Boolean.toString(showChunkBorders)); p.setProperty("visuals.max_brightness",Boolean.toString(maxBrightness));\n        p.setProperty("visuals.minimap_zoom",Integer.toString(minimapZoom));\n        p.setProperty("visuals.minimap_rotate",Boolean.toString(minimapRotate));
        p.setProperty("waypoint.saved",Boolean.toString(hasWaypoint)); p.setProperty("waypoint.x",Double.toString(waypointX));
        p.setProperty("waypoint.y",Double.toString(waypointY)); p.setProperty("waypoint.z",Double.toString(waypointZ));
        p.setProperty("building.anchor_saved",Boolean.toString(hasBuildAnchor)); p.setProperty("building.x",Integer.toString(buildX));
        p.setProperty("building.y",Integer.toString(buildY)); p.setProperty("building.z",Integer.toString(buildZ));
        p.setProperty("building.template",buildTemplate); p.setProperty("profile.name",profileName);
        try { Files.createDirectories(FILE.getParent()); try(Writer w=Files.newBufferedWriter(FILE)){p.store(w,"Nova Utility Client settings");} }
        catch(IOException e) { NovaClient.LOGGER.error("Could not save Nova settings",e); }
    }

    public static void applyProfile(String profile) {
        profileName=profile;
        showWatermark=true; showCoordinates=true; showFps=true; showDirection=true; showBiome=true;
        showKeystrokes=false; showCps=false; showArmor=false; showEffects=false; showArrayList=true;
        customCrosshair=false; showTargetBlock=false; showChunkInfo=false; showWaypoint=true; showBuildGuide=false;
        showToolInfo=false; showWorldTime=false; showWeather=false; showEntityCount=false; showPing=false;
        toggleSprint=false; autoMine=false; autoFarm=false; antiAfk=false; showEntityRadar=false; autoTool=false;
        showMinimap=false; showTargetHud=false; showChunkBorders=false; maxBrightness=false;
        switch(profile) {
            case "Survival" -> { showArmor=true; showEffects=true; showWorldTime=true; showWeather=true; showWaypoint=true; showToolInfo=true; showEntityRadar=true; autoTool=true; showMinimap=true; }
            case "Creative" -> { showChunkInfo=true; showChunkBorders=true; showMinimap=true; showBuildGuide=true; showEntityCount=true; }
            case "PvP" -> { showKeystrokes=true; showCps=true; showArmor=true; showPing=true; customCrosshair=true; }
            case "Visual / Utility" -> { showKeystrokes=true; showArmor=true; showEffects=true; showArrayList=true; showWorldTime=true; showWeather=true; showEntityCount=true; showPing=true; showMinimap=true; showTargetHud=true; }
            default -> profileName="Custom";
        }
        save();
    }

    public static int accentColor(){
        return switch(Math.floorMod(accentColorIndex,5)){
            case 1 -> 0xFF22C5A5;
            case 2 -> 0xFFFF9F43;
            case 3 -> 0xFF4D9CFF;
            case 4 -> 0xFFFF5C8A;
            default -> lightTheme?0xFF6550B7:0xFF9B83FF;
        };
    }
    public static String accentName(){return switch(Math.floorMod(accentColorIndex,5)){case 1->"Mint";case 2->"Amber";case 3->"Blue";case 4->"Rose";default->"Violet";};}
    public static void cycleAccent(){accentColorIndex=Math.floorMod(accentColorIndex+1,5);save();}

    private static boolean bool(Properties p,String k,boolean d){return Boolean.parseBoolean(p.getProperty(k,Boolean.toString(d)));}
    private static int integer(Properties p,String k,int d){try{return Integer.parseInt(p.getProperty(k,Integer.toString(d)));}catch(NumberFormatException e){return d;}}
    private static double decimal(Properties p,String k,double d){try{return Double.parseDouble(p.getProperty(k,Double.toString(d)));}catch(NumberFormatException e){return d;}}
}