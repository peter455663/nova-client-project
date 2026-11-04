package hu.peter.nova;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Independent module windows launched from the Nova ClickGUI. */
public final class NovaCategoryScreen extends Screen {
    public enum Category { HUD,VISUALS,MOVEMENT,PLAYER,WORLD,BUILDING,FARMING,MINING,MISC,PROFILES,SETTINGS,OP_ONLY,STRUCTURES }
    private static final int PANEL_WIDTH=470,DEFAULT_PANEL_HEIGHT=520,OP_PANEL_HEIGHT=660;
    private final Category category;
    private final int panelHeight;
    private final List<Control> controls=new ArrayList<>();
    private final List<Button> buttons=new ArrayList<>();

    public NovaCategoryScreen(Category category){super(Component.literal("Nova "+category.name()));this.category=category;this.panelHeight=category==Category.OP_ONLY?OP_PANEL_HEIGHT:DEFAULT_PANEL_HEIGHT;}

    @Override protected void init(){
        controls.clear();buttons.clear();buildControls();
        int left=(width-PANEL_WIDTH)/2,top=(height-panelHeight)/2;
        int x1=left+24,x2=left+242,w=204,h=25,gapY=34;
        for(int i=0;i<controls.size();i++){
            Control c=controls.get(i);int x=(i%2==0)?x1:x2,y=top+82+(i/2)*gapY;
            Button b=Button.builder(label(c),button->{c.action.run();NovaConfig.save();refreshLabels();})
                    .bounds(x,y,w,h).build();
            buttons.add(b);addRenderableWidget(b);
        }
        addRenderableWidget(Button.builder(Component.literal("Vissza"),b->this.minecraft.gui.setScreen(new NovaClickGuiScreen()))
                .bounds(left+24,top+panelHeight-44,86,25).build());
        addRenderableWidget(Button.builder(Component.literal("Bezárás"),b->onClose())
                .bounds(left+PANEL_WIDTH-110,top+panelHeight-44,86,25).build());
    }

    private void buildControls(){
        switch(category){
            case HUD->{
                toggle("Vízjel",()->NovaConfig.showWatermark,()->NovaConfig.showWatermark=!NovaConfig.showWatermark);
                toggle("Koordináták",()->NovaConfig.showCoordinates,()->NovaConfig.showCoordinates=!NovaConfig.showCoordinates);
                toggle("FPS számláló",()->NovaConfig.showFps,()->NovaConfig.showFps=!NovaConfig.showFps);
                toggle("Irány",()->NovaConfig.showDirection,()->NovaConfig.showDirection=!NovaConfig.showDirection);
                toggle("Biom",()->NovaConfig.showBiome,()->NovaConfig.showBiome=!NovaConfig.showBiome);
                toggle("Billentyűk",()->NovaConfig.showKeystrokes,()->NovaConfig.showKeystrokes=!NovaConfig.showKeystrokes);
                toggle("CPS számláló",()->NovaConfig.showCps,()->NovaConfig.showCps=!NovaConfig.showCps);
                toggle("Páncél adatok",()->NovaConfig.showArmor,()->NovaConfig.showArmor=!NovaConfig.showArmor);
                toggle("Bájitalhatások",()->NovaConfig.showEffects,()->NovaConfig.showEffects=!NovaConfig.showEffects);
                toggle("Aktív modulok",()->NovaConfig.showArrayList,()->NovaConfig.showArrayList=!NovaConfig.showArrayList);
                toggle("Egyedi célkereszt",()->NovaConfig.customCrosshair,()->NovaConfig.customCrosshair=!NovaConfig.customCrosshair);
                toggle("Ping",()->NovaConfig.showPing,()->NovaConfig.showPing=!NovaConfig.showPing);
                toggle("Célpont HUD",()->NovaConfig.showTargetHud,()->NovaConfig.showTargetHud=!NovaConfig.showTargetHud);
            }
            case VISUALS->{
                toggle("Célzott blokk adatai",()->NovaConfig.showTargetBlock,()->NovaConfig.showTargetBlock=!NovaConfig.showTargetBlock);
                toggle("Chunk koordináták",()->NovaConfig.showChunkInfo,()->NovaConfig.showChunkInfo=!NovaConfig.showChunkInfo);
                toggle("Waypoint távolság",()->NovaConfig.showWaypoint,()->NovaConfig.showWaypoint=!NovaConfig.showWaypoint);
                action("Waypoint mentése itt",()->NovaConfig.hasWaypoint?"SAVED":"NOT SET",NovaCategoryScreen::saveWaypoint);
                action("Waypoint törlése",()->NovaConfig.hasWaypoint?"SAVED":"EMPTY",()->NovaConfig.hasWaypoint=false);
                toggle("Közeli entitás radar",()->NovaConfig.showEntityRadar,()->NovaConfig.showEntityRadar=!NovaConfig.showEntityRadar);
                action("Radar hatótáv",()->NovaConfig.entityRadarRange+" blocks",()->NovaConfig.entityRadarRange=NovaConfig.entityRadarRange>=64?8:NovaConfig.entityRadarRange+8);
                toggle("Blokk minimap",()->NovaConfig.showMinimap,()->NovaConfig.showMinimap=!NovaConfig.showMinimap);
                action("Minimap forgatás",()->NovaConfig.minimapRotate?"ROTÁLÓ":"ÉSZAK FELÉ",()->NovaConfig.minimapRotate=!NovaConfig.minimapRotate);
                action("Minimap nagyítás",()->"x"+NovaConfig.minimapZoom,()->NovaConfig.minimapZoom=NovaConfig.minimapZoom>=4?1:NovaConfig.minimapZoom+1);
                action("Minimap méret",()->Integer.toString(NovaConfig.minimapRadius),()->NovaConfig.minimapRadius=NovaConfig.minimapRadius>=9?4:NovaConfig.minimapRadius+1);
                action("Minimap átlátszóság",()->NovaConfig.minimapOpacity+"%",()->NovaConfig.minimapOpacity=NovaConfig.minimapOpacity>=100?25:NovaConfig.minimapOpacity+15);
                toggle("Kör alakú minimap",()->NovaConfig.minimapCircle,()->NovaConfig.minimapCircle=!NovaConfig.minimapCircle);
                action("Minimap képernyősarok",()->switch(NovaConfig.minimapCorner){case 0->"BAL FENT";case 2->"BAL LENT";case 3->"JOBB LENT";default->"JOBB FENT";},()->NovaConfig.minimapCorner=(NovaConfig.minimapCorner+1)%4);
            }
            case MOVEMENT->toggle("Futás kapcsoló",()->NovaConfig.toggleSprint,()->NovaConfig.toggleSprint=!NovaConfig.toggleSprint);
            case PLAYER->{
                toggle("Automatikus eszközválasztás",()->NovaConfig.autoTool,()->NovaConfig.autoTool=!NovaConfig.autoTool);
                toggle("Maximális fényerő",()->NovaConfig.maxBrightness,()->NovaConfig.maxBrightness=!NovaConfig.maxBrightness);
            }
            case WORLD->{
                toggle("Világidő",()->NovaConfig.showWorldTime,()->NovaConfig.showWorldTime=!NovaConfig.showWorldTime);
                toggle("Időjárás",()->NovaConfig.showWeather,()->NovaConfig.showWeather=!NovaConfig.showWeather);
                toggle("Látható entitások",()->NovaConfig.showEntityCount,()->NovaConfig.showEntityCount=!NovaConfig.showEntityCount);
                toggle("Ping",()->NovaConfig.showPing,()->NovaConfig.showPing=!NovaConfig.showPing);
                action("Waypoint mentése itt",()->NovaConfig.hasWaypoint?"SAVED":"NOT SET",NovaCategoryScreen::saveWaypoint);
                toggle("Minimap",()->NovaConfig.showMinimap,()->NovaConfig.showMinimap=!NovaConfig.showMinimap);
                toggle("Chunkrács a minimapon",()->NovaConfig.showChunkBorders,()->NovaConfig.showChunkBorders=!NovaConfig.showChunkBorders);
            }
            case BUILDING->{
                toggle("Építési segéd HUD",()->NovaConfig.showBuildGuide,()->NovaConfig.showBuildGuide=!NovaConfig.showBuildGuide);
                action("Építési pont mentése itt",()->NovaConfig.hasBuildAnchor?"MENTVE":"NINCS",NovaCategoryScreen::saveBuildAnchor);
                action("Építés szüneteltetése",NovaAutomation::getBuildStatus,NovaAutomation::pauseBuild);
                action("Építés folytatása",NovaAutomation::getBuildStatus,NovaAutomation::resumeBuild);
                action("Építés megszakítása",NovaAutomation::getBuildStatus,NovaAutomation::cancelBuild);
                for(String blueprint:NovaBlueprints.HOUSES)
                    action(NovaBlueprints.displayName(blueprint),NovaAutomation::getBuildStatus,
                            ()->NovaAutomation.startBuild(blueprint,Minecraft.getInstance()));
            }
            case FARMING->{
                toggle("Érett növények automatikus aratása",()->NovaConfig.autoFarm,()->NovaConfig.autoFarm=!NovaConfig.autoFarm);
                for(String blueprint:NovaBlueprints.FARMS)
                    action(NovaBlueprints.displayName(blueprint),NovaAutomation::getBuildStatus,
                            ()->NovaAutomation.startBuild(blueprint,Minecraft.getInstance()));
            }
            case STRUCTURES->{
                for(String blueprint:NovaBlueprints.STRUCTURES)
                    action(NovaBlueprints.displayName(blueprint),NovaAutomation::getBuildStatus,
                            ()->NovaAutomation.startBuild(blueprint,Minecraft.getInstance()));
                action("Építés szüneteltetése",NovaAutomation::getBuildStatus,NovaAutomation::pauseBuild);
                action("Építés folytatása",NovaAutomation::getBuildStatus,NovaAutomation::resumeBuild);
                action("Építés megszakítása",NovaAutomation::getBuildStatus,NovaAutomation::cancelBuild);
            }
            case MINING->{
                toggle("Célzott blokk automatikus bányászata",()->NovaConfig.autoMine,()->NovaConfig.autoMine=!NovaConfig.autoMine);
                toggle("Kézben tartott eszköz tartóssága",()->NovaConfig.showToolInfo,()->NovaConfig.showToolInfo=!NovaConfig.showToolInfo);
            }
            case MISC->{
                toggle("Anti-AFK kamera mozdítás",()->NovaConfig.antiAfk,()->NovaConfig.antiAfk=!NovaConfig.antiAfk);
            }
            case PROFILES->{
                for(String profile:List.of("Survival","Creative","PvP","Visual / Utility","Custom"))
                    action(profile,()->NovaConfig.profileName.equals(profile)?"ACTIVE":"APPLY",()->NovaConfig.applyProfile(profile));
            }
            case SETTINGS->{
                toggle("Világos téma",()->NovaConfig.lightTheme,()->NovaConfig.lightTheme=!NovaConfig.lightTheme);
                action("Kiemelőszín",NovaConfig::accentName,NovaConfig::cycleAccent);
                action("Nyelv / Language",()->NovaLanguage.code().equals("hu")?"Magyar":"English",NovaLanguage::toggle);
            }
            case OP_ONLY->{
                action("Kreatív mód",()->"OP",()->NovaAutomation.runAdminAction("creative",Minecraft.getInstance()));
                action("Túlélő mód",()->"OP",()->NovaAutomation.runAdminAction("survival",Minecraft.getInstance()));
                action("Nappal beállítása",()->"OP",()->NovaAutomation.runAdminAction("day",Minecraft.getInstance()));
                action("Éjszaka beállítása",()->"OP",()->NovaAutomation.runAdminAction("night",Minecraft.getInstance()));
                action("Időjárás tisztítása",()->"OP",()->NovaAutomation.runAdminAction("clear_weather",Minecraft.getInstance()));
                action("Rain",()->"OP",()->NovaAutomation.runAdminAction("rain",Minecraft.getInstance()));
                action("Vihar",()->"OP",()->NovaAutomation.runAdminAction("thunder",Minecraft.getInstance()));
                action("Nehézség: békés",()->"OP",()->NovaAutomation.runAdminAction("peaceful",Minecraft.getInstance()));
                action("Nehézség: normál",()->"OP",()->NovaAutomation.runAdminAction("normal",Minecraft.getInstance()));
                action("Nehézség: nehéz",()->"OP",()->NovaAutomation.runAdminAction("hard",Minecraft.getInstance()));
                action("Tárgyak megtartása BE",()->"OP",()->NovaAutomation.runAdminAction("keep_on",Minecraft.getInstance()));
                action("Tárgyak megtartása KI",()->"OP",()->NovaAutomation.runAdminAction("keep_off",Minecraft.getInstance()));
                action("Nappal-éjszaka ciklus leállítása",()->"OP",()->NovaAutomation.runAdminAction("day_cycle_off",Minecraft.getInstance()));
                action("Nappal-éjszaka ciklus folytatása",()->"OP",()->NovaAutomation.runAdminAction("day_cycle_on",Minecraft.getInstance()));
                action("Gyógyítás + telítettség",()->"OP",()->NovaAutomation.runAdminAction("heal",Minecraft.getInstance()));
                action("Tűzállóság (5 perc)",()->"OP",()->NovaAutomation.runAdminAction("fire_resistance",Minecraft.getInstance()));
                action("Vakság eltávolítása",()->"OP",()->NovaAutomation.runAdminAction("remove_blindness",Minecraft.getInstance()));
                action("Világspawn beállítása itt",()->"OP",()->NovaAutomation.runAdminAction("spawn_here",Minecraft.getInstance()));
                action("Ellenséges mobok törlése (24 blokk)",()->"OP",()->NovaAutomation.runAdminAction("clear_hostiles",Minecraft.getInstance()));
                action("Gyorsaság II",()->"60 sec",()->NovaAutomation.runAdminAction("speed",Minecraft.getInstance()));
                action("Ugrásnövelés II",()->"60 sec",()->NovaAutomation.runAdminAction("jump",Minecraft.getInstance()));
                action("Lassú esés",()->"60 sec",()->NovaAutomation.runAdminAction("slow_falling",Minecraft.getInstance()));
                action("Kezdő kunyhó építése",()->"OP",()->NovaAutomation.startBuild("cabin",Minecraft.getInstance()));
                action("Házikó építése",()->"OP",()->NovaAutomation.startBuild("cottage",Minecraft.getInstance()));
                action("Modern ház építése",()->"OP",()->NovaAutomation.startBuild("modern",Minecraft.getInstance()));
                action("Őrtorony építése",()->"OP",()->NovaAutomation.startBuild("watchtower",Minecraft.getInstance()));
                action("Építés megszakítása",NovaAutomation::getBuildStatus,NovaAutomation::cancelBuild);
            }
        }
    }

    private void toggle(String name,Supplier<Boolean> get,Runnable action){controls.add(new Control(name,()->get.get()?"ON":"OFF",action));}
    private void action(String name,Supplier<String> state,Runnable action){controls.add(new Control(name,state,action));}
    private Component label(Control c){String state=c.state.get();if(state.length()>21)state=state.substring(0,18)+"...";return Component.literal(NovaLanguage.tr(c.name)+"  ·  "+NovaLanguage.tr(state));}
    private void refreshLabels(){for(int i=0;i<controls.size();i++)buttons.get(i).setMessage(label(controls.get(i)));}

    private static void saveWaypoint(){
        Minecraft mc=Minecraft.getInstance();if(mc.player==null)return;
        NovaConfig.waypointX=mc.player.getX();NovaConfig.waypointY=mc.player.getY();NovaConfig.waypointZ=mc.player.getZ();NovaConfig.hasWaypoint=true;
    }
    private static void saveBuildAnchor(){
        Minecraft mc=Minecraft.getInstance();if(mc.player==null)return;
        NovaConfig.buildX=mc.player.blockPosition().getX();NovaConfig.buildY=mc.player.blockPosition().getY();NovaConfig.buildZ=mc.player.blockPosition().getZ()+2;
        NovaConfig.hasBuildAnchor=true;NovaConfig.showBuildGuide=true;
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float delta){
        int left=(width-PANEL_WIDTH)/2,top=(height-panelHeight)/2;
        int bg=NovaConfig.lightTheme?0xF0F4F5FA:0xF0121520,header=NovaConfig.lightTheme?0xFFE0E4F0:0xFF1C2030;
        int text=NovaConfig.lightTheme?0xFF202334:0xFFE6E8F0,accent=NovaConfig.accentColor();
        g.fill(0,0,width,height,0x88000000);g.fill(left-1,top-1,left+PANEL_WIDTH+1,top+panelHeight+1,0xFF353047);
        g.fill(left,top,left+PANEL_WIDTH,top+panelHeight,bg);g.fill(left,top,left+PANEL_WIDTH,top+48,header);
        g.fill(left,top,left+3,top+panelHeight,accent);g.text(font,"NOVA  /  "+category.name(),left+24,top+16,accent,true);
        g.text(font,description(),left+24,top+60,text,false);
        if(category==Category.BUILDING) g.text(font,NovaAutomation.getBuildStatus(),left+24,top+panelHeight-70,text,false);
        if(category==Category.OP_ONLY) {
            g.text(font,"OP ONLY  ·  Requires operator permission and server command support.",left+24,top+panelHeight-70,text,false);
            g.text(font,NovaAutomation.getAdminStatus(),left+24,top+panelHeight-54,text,false);
            g.text(font,NovaAutomation.getBuildStatus(),left+24,top+panelHeight-38,text,false);
        }
        super.extractRenderState(g,mx,my,delta);
    }
    private String description(){
        return switch(category){
            case HUD->"Toggle the information widgets shown on your screen.";
            case VISUALS->"Waypoints, a local block minimap, chunk grid and a nearby entity radar (J toggles radar).";
            case MOVEMENT->"Movement preferences; no server protection bypass.";
            case PLAYER->"Auto Tool selects a suitable hotbar tool; max brightness changes only your client setting.";
            case WORLD->"World time, weather, entities, minimap and chunk grid.";
            case BUILDING->"Prebuilt structures use server commands and require OP permission.";
            case FARMING->"Auto harvest works when you aim at a fully grown vanilla crop.";
            case MINING->"Auto mine holds the normal attack key on the block you target.";
            case MISC->"Anti-AFK performs a small view adjustment every 55 seconds.";
            case PROFILES->"Apply a preset set of HUD and utility options.";
            case SETTINGS->"Change the menu appearance, accent color and save preferences.";
            case OP_ONLY->"OP ONLY: commands and structure builds need operator permission; servers can reject them.";
            case STRUCTURES->"Előre elkészített építmények és infrastruktúra-sablonok.";
        };
    }
    @Override public void onClose(){this.minecraft.gui.setScreen(new NovaClickGuiScreen());}
    private record Control(String name,Supplier<String> state,Runnable action){}
}