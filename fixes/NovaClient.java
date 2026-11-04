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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public final class NovaClient implements ClientModInitializer {
    public static final String MOD_ID="nova";
    public static final Logger LOGGER=LoggerFactory.getLogger(MOD_ID);
    private static KeyMapping openGuiKey, autoToolKey, entityRadarKey, minimapKey, targetHudKey;
    private static boolean previousAttackDown;
    private static final Deque<Long> ATTACK_CLICKS=new ArrayDeque<>();

    @Override public void onInitializeClient() {
        NovaConfig.load();
        KeyMapping.Category category=KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID,"main"));
        openGuiKey=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.nova.open_gui",
                InputConstants.Type.KEYBOARD,InputConstants.KEY_RALT,category));
        autoToolKey=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.nova.toggle_auto_tool",
                InputConstants.Type.KEYBOARD,InputConstants.KEY_R,category));
        entityRadarKey=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.nova.toggle_entity_radar",
                InputConstants.Type.KEYBOARD,InputConstants.KEY_J,category));
        minimapKey=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.nova.toggle_minimap",
                InputConstants.Type.KEYBOARD,InputConstants.KEY_K,category));
        targetHudKey=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.nova.toggle_target_hud",
                InputConstants.Type.KEYBOARD,InputConstants.KEY_T,category));
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            while(openGuiKey.consumeClick()) client.gui.setScreen(new NovaClickGuiScreen());
            while(autoToolKey.consumeClick()){NovaConfig.autoTool=!NovaConfig.autoTool;NovaConfig.save();}
            while(entityRadarKey.consumeClick()){NovaConfig.showEntityRadar=!NovaConfig.showEntityRadar;NovaConfig.save();}
            while(minimapKey.consumeClick()){NovaConfig.showMinimap=!NovaConfig.showMinimap;NovaConfig.save();}
            while(targetHudKey.consumeClick()){NovaConfig.showTargetHud=!NovaConfig.showTargetHud;NovaConfig.save();}
            if(client.player!=null && NovaConfig.toggleSprint && client.options.keyUp.isDown()) client.options.keySprint.setDown(true);
            boolean attackDown=client.options.keyAttack.isDown();
            if(attackDown && !previousAttackDown) ATTACK_CLICKS.addLast(System.currentTimeMillis());
            previousAttackDown=attackDown;
            long cutoff=System.currentTimeMillis()-1000L;
            while(!ATTACK_CLICKS.isEmpty() && ATTACK_CLICKS.peekFirst()<cutoff) ATTACK_CLICKS.removeFirst();
            NovaAutomation.tick(client);
        });
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID,"utility_hud"),NovaClient::renderHud);
        LOGGER.info("Nova Utility Client 0.8.0 initialized. Right Alt menu; R Auto Tool; J Radar; K Minimap; T Target HUD.");
    }

    private static void renderHud(GuiGraphicsExtractor graphics,DeltaTracker deltaTracker) {
        Minecraft client=Minecraft.getInstance();
        if(client.player==null || client.level==null) return;
        List<String> rows=new ArrayList<>();
        if(NovaConfig.showWatermark) rows.add("NOVA  /  UTILITY  0.8.0");
        if(NovaConfig.showCoordinates) rows.add(String.format(Locale.ROOT,"XYZ  %.1f  %.1f  %.1f",client.player.getX(),client.player.getY(),client.player.getZ()));
        if(NovaConfig.showFps) rows.add("FPS  "+client.getFps());
        if(NovaConfig.showDirection) rows.add("FACING  "+client.player.getDirection().getName().toUpperCase(Locale.ROOT));
        if(NovaConfig.showBiome) {
            String biome=client.level.getBiome(client.player.blockPosition()).unwrapKey()
                    .map(key->key.identifier().getPath().replace('_',' ')).orElse("unknown");
            rows.add("BIOME  "+biome);
        }
        if(NovaConfig.showKeystrokes) rows.add("KEYS  "+key(client.options.keyUp)+" "+key(client.options.keyLeft)
                +" "+key(client.options.keyDown)+" "+key(client.options.keyRight)+"   J "+key(client.options.keyJump));
        if(NovaConfig.showCps) rows.add("CLICK/s  "+ATTACK_CLICKS.size());
        if(NovaConfig.showArmor) {
            List<String> armor=new ArrayList<>();
            for(EquipmentSlot slot:List.of(EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET)) {
                var item=client.player.getItemBySlot(slot);
                if(!item.isEmpty()) armor.add(item.getHoverName().getString());
            }
            rows.add("ARMOR  "+(armor.isEmpty()?"none":String.join(", ",armor)));
        }
        if(NovaConfig.showEffects) {
            List<String> effects=new ArrayList<>();
            client.player.getActiveEffects().forEach(effect->effects.add(effect.getEffect().value().getDisplayName().getString()));
            rows.add("EFFECTS  "+(effects.isEmpty()?"none":String.join(", ",effects)));
        }
        if(NovaConfig.showTargetBlock && client.hitResult instanceof BlockHitResult hit) {
            String name=client.level.getBlockState(hit.getBlockPos()).getBlock().getName().getString();
            rows.add("TARGET  "+name+"  "+hit.getBlockPos().getX()+" "+hit.getBlockPos().getY()+" "+hit.getBlockPos().getZ());
        }
        if(NovaConfig.showChunkInfo) {
            int cx=client.player.blockPosition().getX()>>4, cz=client.player.blockPosition().getZ()>>4;
            rows.add("CHUNK  "+cx+"  "+cz);
        }
        if(NovaConfig.showWaypoint && NovaConfig.hasWaypoint) {
            double dx=client.player.getX()-NovaConfig.waypointX,dy=client.player.getY()-NovaConfig.waypointY,dz=client.player.getZ()-NovaConfig.waypointZ;
            double distance=Math.sqrt(dx*dx+dy*dy+dz*dz);
            double bearing=Math.toDegrees(Math.atan2(-dx,dz));
            rows.add(String.format(Locale.ROOT,"WAYPOINT  %.1fm  BEARING %.0f°",distance,bearing));
        }
        if(NovaConfig.showBuildGuide) rows.add("BUILD  "+NovaConfig.buildTemplate.toUpperCase(Locale.ROOT)
                +(NovaConfig.hasBuildAnchor?"  ANCHOR "+NovaConfig.buildX+" "+NovaConfig.buildY+" "+NovaConfig.buildZ:"  NO ANCHOR"));
        if(NovaConfig.showToolInfo) {
            var stack=client.player.getMainHandItem();
            if(stack.isEmpty()) rows.add("TOOL  HAND EMPTY");
            else if(stack.isDamageableItem()) {
                int max=stack.getMaxDamage(),left=max<=0?100:Math.max(0,(max-stack.getDamageValue())*100/max);
                rows.add("TOOL  "+stack.getHoverName().getString()+"  "+left+"%");
            } else rows.add("TOOL  "+stack.getHoverName().getString());
        }
        if(NovaConfig.showWorldTime) {
            long dayTime=Math.floorMod(client.level.getDefaultClockTime(),24000L);
            int hour=(int)(((dayTime+6000L)%24000L)*24L/24000L);
            int minute=(int)(((dayTime+6000L)%24000L)*60L/1000L)%60;
            rows.add(String.format(Locale.ROOT,"WORLD TIME  %02d:%02d",hour,minute));
        }
        if(NovaConfig.showWeather) rows.add("WEATHER  "+(client.level.isThundering()?"THUNDER":client.level.isRaining()?"RAIN":"CLEAR"));
        if(NovaConfig.showEntityCount) {
            int count=0; for(var ignored:client.level.entitiesForRendering()) count++;
            rows.add("VISIBLE ENTITIES  "+count);
        }
        if(NovaConfig.showEntityRadar) {
            List<String> nearby=new ArrayList<>();
            for(var entity:client.level.entitiesForRendering()) {
                if(entity==client.player || !entity.isAlive()) continue;
                double distance=entity.distanceTo(client.player);
                if(distance<=NovaConfig.entityRadarRange)
                    nearby.add(String.format(Locale.ROOT,"%s  %.1fm",entity.getName().getString(),distance));
            }
            rows.add("ENTITY RADAR  "+nearby.size()+" / "+NovaConfig.entityRadarRange+"m");
            for(int i=0;i<Math.min(5,nearby.size());i++) rows.add("  "+nearby.get(i));
            if(nearby.isEmpty()) rows.add("  No nearby entities");
        }
        if(NovaConfig.showPing && client.getConnection()!=null) {
            var info=client.getConnection().getPlayerInfo(client.player.getUUID());
            if(info!=null) rows.add("PING  "+info.getLatency()+" ms");
        }
        if(NovaConfig.maxBrightness) rows.add("MAX BRIGHTNESS  ON");
        if(NovaConfig.autoTool) rows.add("AUTO TOOL  ON");
        if(NovaConfig.autoMine) rows.add("AUTO MINE  ON");
        if(NovaConfig.autoFarm) rows.add("AUTO FARM  ON");
        if(NovaConfig.antiAfk) rows.add("ANTI-AFK  ON");
        if(NovaConfig.showArrayList) {
            List<String> active=new ArrayList<>();
            if(NovaConfig.showCoordinates) active.add("Coords");
            if(NovaConfig.showFps) active.add("FPS");
            if(NovaConfig.showBiome) active.add("Biome");
            if(NovaConfig.showKeystrokes) active.add("Keys");
            if(NovaConfig.autoMine) active.add("AutoMine");
            if(NovaConfig.autoFarm) active.add("AutoFarm");
            if(NovaConfig.antiAfk) active.add("AntiAFK");
            if(NovaConfig.toggleSprint) active.add("ToggleSprint");
            if(NovaConfig.showMinimap) active.add("Minimap");
            if(NovaConfig.showTargetHud) active.add("TargetHUD");
            if(NovaConfig.maxBrightness) active.add("Brightness");
            rows.add("ACTIVE  "+String.join(" / ",active));
        }
        if(!rows.isEmpty()) {
            int x=8,y=8,rowHeight=12,width=260,height=8+rows.size()*rowHeight;
            int panel=NovaConfig.lightTheme?0xB8E8ECF5:0xA8101420;
            int accent=NovaConfig.accentColor();
            int foreground=NovaConfig.lightTheme?0xFF202334:0xFFF1F3F8;
            int secondary=NovaConfig.lightTheme?0xFF535B70:0xFFB8C2D8;
            graphics.fill(x,y,x+width,y+height,panel);graphics.fill(x,y,x+2,y+height,accent);
            int textY=y+5;
            for(int i=0;i<rows.size();i++) {
                int color=i==0 && NovaConfig.showWatermark?accent:(i%2==0?foreground:secondary);
                graphics.text(client.font,rows.get(i),x+8,textY,color,true);textY+=rowHeight;
            }
        }
        if(NovaConfig.showMinimap) renderMinimap(graphics,client);
        if(NovaConfig.showTargetHud) renderTargetHud(graphics,client);
        if(NovaConfig.customCrosshair) {
            int cx=client.getWindow().getGuiScaledWidth()/2,cy=client.getWindow().getGuiScaledHeight()/2;
            int color=NovaConfig.accentColor();
            graphics.fill(cx-5,cy-1,cx+5,cy+1,color);graphics.fill(cx-1,cy-5,cx+1,cy+5,color);
        }
    }
    private static void renderMinimap(GuiGraphicsExtractor graphics,Minecraft client) {
        if(client.player==null || client.level==null) return;
        final int radius=5, tile=7, mapSize=(radius*2+1)*tile;
        int left=client.getWindow().getGuiScaledWidth()-mapSize-12, top=12;
        int px=client.player.blockPosition().getX(), py=client.player.blockPosition().getY(), pz=client.player.blockPosition().getZ();
        graphics.fill(left-4,top-4,left+mapSize+4,top+mapSize+4,0xB8101420);
        graphics.fill(left-4,top-4,left+mapSize+4,top-2,NovaConfig.accentColor());
        for(int dz=-radius;dz<=radius;dz++) for(int dx=-radius;dx<=radius;dx++) {
            int worldX=px+dx, worldZ=pz+dz;
            BlockPos sample=new BlockPos(worldX,py-1,worldZ);
            var state=client.level.getBlockState(sample);
            for(int depth=2;depth<=8 && state.isAir();depth++) {
                sample=new BlockPos(worldX,py-depth,worldZ);
                state=client.level.getBlockState(sample);
            }
            int color=mapBlockColor(state.getBlock().getName().getString());
            int x=left+(dx+radius)*tile, y=top+(dz+radius)*tile;
            graphics.fill(x,y,x+tile-1,y+tile-1,color);
            if(NovaConfig.showChunkBorders && (Math.floorMod(worldX,16)==0 || Math.floorMod(worldZ,16)==0)) {
                if(Math.floorMod(worldX,16)==0) graphics.fill(x,y,x+1,y+tile,NovaConfig.accentColor());
                if(Math.floorMod(worldZ,16)==0) graphics.fill(x,y,x+tile,y+1,NovaConfig.accentColor());
            }
        }
        int centerX=left+radius*tile,centerY=top+radius*tile;
        graphics.fill(centerX+1,centerY+1,centerX+tile-1,centerY+tile-1,0xFFFFFFFF);
        graphics.fill(centerX+2,centerY-2,centerX+tile-2,centerY+2,NovaConfig.accentColor());
        graphics.text(client.font,"N",left+mapSize/2-2,top-12,0xFFFFFFFF,true);
    }

    private static int mapBlockColor(String rawName) {
        String name=rawName.toLowerCase(Locale.ROOT);
        if(name.contains("water")) return 0xFF315FA8;
        if(name.contains("lava")) return 0xFFE07828;
        if(name.contains("grass") || name.contains("moss") || name.contains("leaves") || name.contains("azalea")) return 0xFF477B43;
        if(name.contains("sand") || name.contains("sandstone") || name.contains("terracotta")) return 0xFFD6BC78;
        if(name.contains("snow") || name.contains("ice") || name.contains("powder")) return 0xFFD5E5EE;
        if(name.contains("log") || name.contains("wood") || name.contains("plank")) return 0xFF87613E;
        if(name.contains("dirt") || name.contains("mud") || name.contains("root")) return 0xFF79543C;
        if(name.contains("stone") || name.contains("deepslate") || name.contains("ore")) return 0xFF777D86;
        if(name.contains("air")) return 0xFF253040;
        return 0xFF626B58;
    }

    private static void renderTargetHud(GuiGraphicsExtractor graphics,Minecraft client) {
        if(!(client.hitResult instanceof EntityHitResult hit) || !(hit.getEntity() instanceof LivingEntity target)) return;
        int x=8,y=client.getWindow().getGuiScaledHeight()-54,w=178,h=42;
        int panel=NovaConfig.lightTheme?0xDDE8ECF5:0xD9101420;
        graphics.fill(x,y,x+w,y+h,panel);
        graphics.fill(x,y,x+2,y+h,NovaConfig.accentColor());
        String name=target.getName().getString();
        if(name.length()>22) name=name.substring(0,19)+"...";
        graphics.text(client.font,name,x+9,y+6,0xFFFFFFFF,true);
        float max=Math.max(1.0F,target.getMaxHealth()),health=Math.max(0.0F,target.getHealth());
        int barWidth=(int)(150.0F*Math.min(1.0F,health/max));
        graphics.fill(x+9,y+24,x+159,y+31,0xFF46343A);
        graphics.fill(x+9,y+24,x+9+barWidth,y+31,0xFFE45B67);
        graphics.text(client.font,String.format(Locale.ROOT,"%.1f / %.1f HP",health,max),x+9,y+33,0xFFE2E7F0,false);
    }

    private static String key(KeyMapping mapping){return mapping.isDown()?"[■]":"[ ]";}
}