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
    public enum Category { HUD,VISUALS,MOVEMENT,PLAYER,WORLD,BUILDING,FARMING,MINING,MISC,PROFILES,SETTINGS }
    private static final int PANEL_WIDTH=470,PANEL_HEIGHT=410;
    private final Category category;
    private final List<Control> controls=new ArrayList<>();
    private final List<Button> buttons=new ArrayList<>();

    public NovaCategoryScreen(Category category){super(Component.literal("Nova "+category.name()));this.category=category;}

    @Override protected void init(){
        controls.clear();buttons.clear();buildControls();
        int left=(width-PANEL_WIDTH)/2,top=(height-PANEL_HEIGHT)/2;
        int x1=left+24,x2=left+242,w=204,h=25,gapY=34;
        for(int i=0;i<controls.size();i++){
            Control c=controls.get(i);int x=(i%2==0)?x1:x2,y=top+82+(i/2)*gapY;
            Button b=Button.builder(label(c),button->{c.action.run();NovaConfig.save();refreshLabels();})
                    .bounds(x,y,w,h).build();
            buttons.add(b);addRenderableWidget(b);
        }
        addRenderableWidget(Button.builder(Component.literal("Back"),b->this.minecraft.gui.setScreen(new NovaClickGuiScreen()))
                .bounds(left+24,top+PANEL_HEIGHT-44,86,25).build());
        addRenderableWidget(Button.builder(Component.literal("Close"),b->onClose())
                .bounds(left+PANEL_WIDTH-110,top+PANEL_HEIGHT-44,86,25).build());
    }

    private void buildControls(){
        switch(category){
            case HUD->{
                toggle("Watermark",()->NovaConfig.showWatermark,()->NovaConfig.showWatermark=!NovaConfig.showWatermark);
                toggle("Coordinates",()->NovaConfig.showCoordinates,()->NovaConfig.showCoordinates=!NovaConfig.showCoordinates);
                toggle("FPS counter",()->NovaConfig.showFps,()->NovaConfig.showFps=!NovaConfig.showFps);
                toggle("Direction",()->NovaConfig.showDirection,()->NovaConfig.showDirection=!NovaConfig.showDirection);
                toggle("Biome",()->NovaConfig.showBiome,()->NovaConfig.showBiome=!NovaConfig.showBiome);
                toggle("Keystrokes",()->NovaConfig.showKeystrokes,()->NovaConfig.showKeystrokes=!NovaConfig.showKeystrokes);
                toggle("CPS counter",()->NovaConfig.showCps,()->NovaConfig.showCps=!NovaConfig.showCps);
                toggle("Armor details",()->NovaConfig.showArmor,()->NovaConfig.showArmor=!NovaConfig.showArmor);
                toggle("Potion effect names",()->NovaConfig.showEffects,()->NovaConfig.showEffects=!NovaConfig.showEffects);
                toggle("Active module list",()->NovaConfig.showArrayList,()->NovaConfig.showArrayList=!NovaConfig.showArrayList);
                toggle("Custom crosshair",()->NovaConfig.customCrosshair,()->NovaConfig.customCrosshair=!NovaConfig.customCrosshair);
                toggle("Ping",()->NovaConfig.showPing,()->NovaConfig.showPing=!NovaConfig.showPing);
            }
            case VISUALS->{
                toggle("Target block information",()->NovaConfig.showTargetBlock,()->NovaConfig.showTargetBlock=!NovaConfig.showTargetBlock);
                toggle("Chunk coordinates",()->NovaConfig.showChunkInfo,()->NovaConfig.showChunkInfo=!NovaConfig.showChunkInfo);
                toggle("Waypoint distance HUD",()->NovaConfig.showWaypoint,()->NovaConfig.showWaypoint=!NovaConfig.showWaypoint);
                action("Save waypoint here",()->NovaConfig.hasWaypoint?"SAVED":"NOT SET",NovaCategoryScreen::saveWaypoint);
                action("Clear waypoint",()->NovaConfig.hasWaypoint?"SAVED":"EMPTY",()->NovaConfig.hasWaypoint=false);
            }
            case MOVEMENT->toggle("Toggle sprint",()->NovaConfig.toggleSprint,()->NovaConfig.toggleSprint=!NovaConfig.toggleSprint);
            case PLAYER->{
                action("Player utilities",()->"CLIENT-SIDE",()->{});
                action("Status",()->"No combat or anti-cheat bypass",()->{});
            }
            case WORLD->{
                toggle("World time",()->NovaConfig.showWorldTime,()->NovaConfig.showWorldTime=!NovaConfig.showWorldTime);
                toggle("Weather",()->NovaConfig.showWeather,()->NovaConfig.showWeather=!NovaConfig.showWeather);
                toggle("Visible entity count",()->NovaConfig.showEntityCount,()->NovaConfig.showEntityCount=!NovaConfig.showEntityCount);
                toggle("Ping",()->NovaConfig.showPing,()->NovaConfig.showPing=!NovaConfig.showPing);
                action("Save waypoint here",()->NovaConfig.hasWaypoint?"SAVED":"NOT SET",NovaCategoryScreen::saveWaypoint);
            }
            case BUILDING->{
                toggle("Build guide HUD",()->NovaConfig.showBuildGuide,()->NovaConfig.showBuildGuide=!NovaConfig.showBuildGuide);
                action("Build starter cabin  (OP)",()->"PLACE",()->NovaAutomation.startBuild("cabin",Minecraft.getInstance()));
                action("Build cottage  (OP)",()->"PLACE",()->NovaAutomation.startBuild("cottage",Minecraft.getInstance()));
                action("Build modern house  (OP)",()->"PLACE",()->NovaAutomation.startBuild("modern",Minecraft.getInstance()));
                action("Build watchtower  (OP)",()->"PLACE",()->NovaAutomation.startBuild("watchtower",Minecraft.getInstance()));
                action("Cancel queued build",()->NovaAutomation.getBuildStatus(),NovaAutomation::cancelBuild);
                action("Set build anchor here",()->NovaConfig.hasBuildAnchor?"SAVED":"NOT SET",NovaCategoryScreen::saveBuildAnchor);
                action("Selected template",()->NovaConfig.buildTemplate.toUpperCase(),()->{});
            }
            case FARMING->{
                toggle("Auto harvest mature crops",()->NovaConfig.autoFarm,()->NovaConfig.autoFarm=!NovaConfig.autoFarm);
                action("How it works",()->"AIM AT RIPE CROP",()->{});
            }
            case MINING->{
                toggle("Auto mine targeted block",()->NovaConfig.autoMine,()->NovaConfig.autoMine=!NovaConfig.autoMine);
                toggle("Held tool durability HUD",()->NovaConfig.showToolInfo,()->NovaConfig.showToolInfo=!NovaConfig.showToolInfo);
                action("How it works",()->"VANILLA BREAK SPEED",()->{});
            }
            case MISC->{
                toggle("Anti-AFK view nudge",()->NovaConfig.antiAfk,()->NovaConfig.antiAfk=!NovaConfig.antiAfk);
                action("Status",()->"Small look movement every 55s",()->{});
            }
            case PROFILES->{
                for(String profile:List.of("Survival","Creative","PvP","Visual / Utility","Custom"))
                    action(profile,()->NovaConfig.profileName.equals(profile)?"ACTIVE":"APPLY",()->NovaConfig.applyProfile(profile));
            }
            case SETTINGS->toggle("Light theme",()->NovaConfig.lightTheme,()->NovaConfig.lightTheme=!NovaConfig.lightTheme);
        }
    }

    private void toggle(String name,Supplier<Boolean> get,Runnable action){controls.add(new Control(name,()->get.get()?"ON":"OFF",action));}
    private void action(String name,Supplier<String> state,Runnable action){controls.add(new Control(name,state,action));}
    private Component label(Control c){String state=c.state.get();if(state.length()>21)state=state.substring(0,18)+"...";return Component.literal(c.name+"  ·  "+state);}
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
        int left=(width-PANEL_WIDTH)/2,top=(height-PANEL_HEIGHT)/2;
        int bg=NovaConfig.lightTheme?0xF0F4F5FA:0xF0121520,header=NovaConfig.lightTheme?0xFFE0E4F0:0xFF1C2030;
        int text=NovaConfig.lightTheme?0xFF202334:0xFFE6E8F0,accent=NovaConfig.lightTheme?0xFF6550B7:0xFF8B5CF6;
        g.fill(0,0,width,height,0x88000000);g.fill(left-1,top-1,left+PANEL_WIDTH+1,top+PANEL_HEIGHT+1,0xFF353047);
        g.fill(left,top,left+PANEL_WIDTH,top+PANEL_HEIGHT,bg);g.fill(left,top,left+PANEL_WIDTH,top+48,header);
        g.fill(left,top,left+3,top+PANEL_HEIGHT,accent);g.text(font,"NOVA  /  "+category.name(),left+24,top+16,accent,true);
        g.text(font,description(),left+24,top+60,text,false);
        if(category==Category.BUILDING) g.text(font,NovaAutomation.getBuildStatus(),left+24,top+PANEL_HEIGHT-70,text,false);
        super.extractRenderState(g,mx,my,delta);
    }
    private String description(){
        return switch(category){
            case HUD->"Toggle the information widgets shown on your screen.";
            case VISUALS->"Waypoints and information about blocks you are looking at.";
            case MOVEMENT->"Movement preferences; no server protection bypass.";
            case PLAYER->"Basic client-side utilities; more modules can be added.";
            case WORLD->"Information about local world time, weather and entities.";
            case BUILDING->"Prebuilt structures use server commands and require OP permission.";
            case FARMING->"Auto harvest works when you aim at a fully grown vanilla crop.";
            case MINING->"Auto mine holds the normal attack key on the block you target.";
            case MISC->"Anti-AFK performs a small view adjustment every 55 seconds.";
            case PROFILES->"Apply a preset set of HUD and utility options.";
            case SETTINGS->"Change the menu appearance and save preferences.";
        };
    }
    @Override public void onClose(){this.minecraft.gui.setScreen(new NovaClickGuiScreen());}
    private record Control(String name,Supplier<String> state,Runnable action){}
}