package hu.peter.nova;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Client-side automation helpers. Server commands remain subject to the
 * server's normal permission checks; this class does not bypass OP.
 */
public final class NovaAutomation {
    private static final Deque<String> BUILD_COMMANDS=new ArrayDeque<>();
    private static final Deque<String> ADMIN_COMMANDS=new ArrayDeque<>();
    private static int tickCounter;
    private static long lastAfkNudge=System.currentTimeMillis();
    private static float lastYaw;
    private static boolean wasHoldingAttack;
    private static int autoToolOriginalSlot=-1;
    private static boolean autoToolChangedSlot;
    private static String buildStatus="Idle";
    private static int buildTotal, buildSent;
    private static boolean buildPaused;
    private static String adminStatus="No admin command sent";
    private static Double originalGamma;

    private NovaAutomation() {}

    public static void tick(Minecraft mc) {
        tickCounter++;
        updateBrightness(mc);
        if(mc.player==null || mc.level==null || mc.getConnection()==null) {
            releaseAutomationKeys(mc);
            return;
        }

        if(mc.gui.screen()==null) {
            updateAutoTool(mc);
            boolean blockTarget=mc.hitResult instanceof BlockHitResult hit
                    && !mc.level.getBlockState(hit.getBlockPos()).isAir();
            boolean farmTarget=NovaConfig.autoFarm && isMatureCrop(mc);
            boolean autoAttack=(NovaConfig.autoMine && blockTarget) || farmTarget;
            if(NovaConfig.autoMine || NovaConfig.autoFarm) {
                mc.options.keyAttack.setDown(autoAttack);
                wasHoldingAttack=autoAttack;
            } else releaseAutomationKeys(mc);

            if(NovaConfig.antiAfk && System.currentTimeMillis()-lastAfkNudge>=55_000L) {
                lastYaw=mc.player.getYRot();
                mc.player.setYRot(lastYaw+2.0F);
                mc.player.setYHeadRot(lastYaw+2.0F);
                lastAfkNudge=System.currentTimeMillis();
            }
        } else {
            releaseAutomationKeys(mc);
            restoreAutoTool(mc);
        }

        if(!ADMIN_COMMANDS.isEmpty() && tickCounter%3==0) {
            String command=ADMIN_COMMANDS.pollFirst();
            try {
                mc.getConnection().sendCommand(command);
                adminStatus="OP command queue: "+ADMIN_COMMANDS.size()+" remaining";
            } catch(RuntimeException ex) {
                ADMIN_COMMANDS.clear();
                adminStatus="Command failed to send";
                NovaClient.LOGGER.error("Nova admin command failed",ex);
            }
            if(ADMIN_COMMANDS.isEmpty()) adminStatus="Commands sent; server feedback confirms permissions";
        }

        if(!BUILD_COMMANDS.isEmpty() && !buildPaused && tickCounter%5==0) {
            String command=BUILD_COMMANDS.pollFirst();
            if(command!=null) {
                try {
                    mc.getConnection().sendCommand(command);
                    buildSent++;
                    buildStatus="Építés: "+getBuildProgress()+"% · "+BUILD_COMMANDS.size()+" parancs hátra";
                } catch(RuntimeException ex) {
                    BUILD_COMMANDS.clear();
                    buildPaused=false;
                    buildStatus="Build failed while sending a command";
                    NovaClient.LOGGER.error("Nova build command failed",ex);
                }
            }
            if(BUILD_COMMANDS.isEmpty() && !buildPaused) buildStatus="Build command queue sent; server feedback determines success";
        }
    }

    private static void updateBrightness(Minecraft mc) {
        if(mc.options==null) return;
        if(NovaConfig.maxBrightness) {
            if(originalGamma==null) originalGamma=mc.options.gamma().get();
            if(mc.options.gamma().get()<1.0D) mc.options.gamma().set(1.0D);
        } else if(originalGamma!=null) {
            mc.options.gamma().set(originalGamma);
            originalGamma=null;
        }
    }

    private static void updateAutoTool(Minecraft mc) {
        if(!NovaConfig.autoTool || !(mc.hitResult instanceof BlockHitResult hit)) {
            restoreAutoTool(mc);
            return;
        }
        BlockState state=mc.level.getBlockState(hit.getBlockPos());
        if(state.isAir()) { restoreAutoTool(mc); return; }
        int selected=mc.player.getInventory().getSelectedSlot();
        int best=selected;
        float bestScore=toolScore(mc.player.getInventory().getItem(selected),state);
        for(int slot=0;slot<9;slot++) {
            ItemStack stack=mc.player.getInventory().getItem(slot);
            float score=toolScore(stack,state);
            if(score>bestScore+0.01F) { bestScore=score; best=slot; }
        }
        if(best!=selected) {
            if(!autoToolChangedSlot) autoToolOriginalSlot=selected;
            autoToolChangedSlot=true;
            mc.player.getInventory().setSelectedSlot(best);
        }
    }

    private static float toolScore(ItemStack stack,BlockState state) {
        if(stack.isEmpty()) return 1.0F;
        float score=stack.getDestroySpeed(state);
        if(stack.isCorrectToolForDrops(state)) score+=1000.0F;
        return score;
    }

    private static void restoreAutoTool(Minecraft mc) {
        if(autoToolChangedSlot && mc.player!=null && autoToolOriginalSlot>=0)
            mc.player.getInventory().setSelectedSlot(autoToolOriginalSlot);
        autoToolOriginalSlot=-1;
        autoToolChangedSlot=false;
    }

    private static boolean isMatureCrop(Minecraft mc) {
        if(!(mc.hitResult instanceof BlockHitResult hit)) return false;
        BlockState state=mc.level.getBlockState(hit.getBlockPos());
        if(!(state.getBlock() instanceof CropBlock crop)) return false;
        return crop.isMaxAge(state);
    }

    private static void releaseAutomationKeys(Minecraft mc) {
        if(wasHoldingAttack) {
            mc.options.keyAttack.setDown(false);
            wasHoldingAttack=false;
        }
    }

    public static String getAdminStatus(){return adminStatus;}

    /** Sends only fixed, documented commands. Server permission checks still apply. */
    public static void runAdminAction(String action,Minecraft mc) {
        if(mc.player==null || mc.getConnection()==null) { adminStatus="Join a server first"; return; }
        if(!ADMIN_COMMANDS.isEmpty()) { adminStatus="Wait for the current admin command queue"; return; }
        String[] commands=switch(action) {
            case "creative" -> new String[]{"gamemode creative @s"};
            case "survival" -> new String[]{"gamemode survival @s"};
            case "day" -> new String[]{"time set day"};
            case "night" -> new String[]{"time set night"};
            case "clear_weather" -> new String[]{"weather clear"};
            case "rain" -> new String[]{"weather rain"};
            case "thunder" -> new String[]{"weather thunder"};
            case "peaceful" -> new String[]{"difficulty peaceful"};
            case "normal" -> new String[]{"difficulty normal"};
            case "hard" -> new String[]{"difficulty hard"};
            case "keep_on" -> new String[]{"gamerule keepInventory true"};
            case "keep_off" -> new String[]{"gamerule keepInventory false"};
            case "day_cycle_off" -> new String[]{"gamerule doDaylightCycle false"};
            case "day_cycle_on" -> new String[]{"gamerule doDaylightCycle true"};
            case "heal" -> new String[]{"effect give @s minecraft:instant_health 1 1 true","effect give @s minecraft:saturation 1 1 true"};
            case "speed" -> new String[]{"effect give @s minecraft:speed 60 1 true"};
            case "jump" -> new String[]{"effect give @s minecraft:jump_boost 60 1 true"};
            case "slow_falling" -> new String[]{"effect give @s minecraft:slow_falling 60 0 true"};
            case "fire_resistance" -> new String[]{"effect give @s minecraft:fire_resistance 300 0 true"};
            case "remove_blindness" -> new String[]{"effect clear @s minecraft:blindness"};
            case "spawn_here" -> new String[]{"setworldspawn "+mc.player.blockPosition().getX()+" "+mc.player.blockPosition().getY()+" "+mc.player.blockPosition().getZ()};
            case "clear_hostiles" -> new String[]{
                "kill @e[type=minecraft:zombie,distance=..24]","kill @e[type=minecraft:skeleton,distance=..24]",
                "kill @e[type=minecraft:creeper,distance=..24]","kill @e[type=minecraft:spider,distance=..24]",
                "kill @e[type=minecraft:cave_spider,distance=..24]","kill @e[type=minecraft:witch,distance=..24]",
                "kill @e[type=minecraft:enderman,distance=..24]","kill @e[type=minecraft:drowned,distance=..24]",
                "kill @e[type=minecraft:husk,distance=..24]","kill @e[type=minecraft:stray,distance=..24]",
                "kill @e[type=minecraft:phantom,distance=..24]","kill @e[type=minecraft:pillager,distance=..24]",
                "kill @e[type=minecraft:vindicator,distance=..24]","kill @e[type=minecraft:evoker,distance=..24]",
                "kill @e[type=minecraft:ravager,distance=..24]","kill @e[type=minecraft:blaze,distance=..24]",
                "kill @e[type=minecraft:ghast,distance=..24]","kill @e[type=minecraft:magma_cube,distance=..24]",
                "kill @e[type=minecraft:guardian,distance=..24]","kill @e[type=minecraft:shulker,distance=..24]",
                "kill @e[type=minecraft:silverfish,distance=..24]","kill @e[type=minecraft:endermite,distance=..24]"
            };
            default -> new String[0];
        };
        if(commands.length==0) { adminStatus="Unknown admin action"; return; }
        for(String command:commands) ADMIN_COMMANDS.addLast(command);
        adminStatus="Queued "+commands.length+" OP commands; server permission required";
    }

    public static void startBuild(String template,Minecraft mc) {
        if(mc.player==null || mc.getConnection()==null) {
            buildStatus="Join a world before building";
            return;
        }
        if(!BUILD_COMMANDS.isEmpty()) {
            buildStatus="A build is already running";
            return;
        }
        String selected=switch(template) {
            case "cabin" -> "wooden_survival_house";
            case "cottage" -> "farmhouse";
            case "modern" -> "modern_house";
            default -> template;
        };
        if(!NovaBlueprints.HOUSES.contains(selected)
                && !NovaBlueprints.FARMS.contains(selected)
                && !NovaBlueprints.STRUCTURES.contains(selected)) {
            buildStatus="Unknown blueprint: "+selected;
            return;
        }
        int x,y,z;
        if(NovaConfig.hasBuildAnchor) {
            x=NovaConfig.buildX; y=NovaConfig.buildY; z=NovaConfig.buildZ;
        } else {
            BlockPos p=mc.player.blockPosition();
            x=p.getX(); y=p.getY(); z=p.getZ()+2;
        }
        List<String> commands=NovaBlueprints.commands(selected,x,y,z);
        BUILD_COMMANDS.addAll(commands);
        buildTotal=commands.size();
        buildSent=0;
        buildPaused=false;
        NovaConfig.buildX=x; NovaConfig.buildY=y; NovaConfig.buildZ=z;
        NovaConfig.hasBuildAnchor=true; NovaConfig.buildTemplate=selected; NovaConfig.showBuildGuide=true; NovaConfig.save();
        buildStatus="Queued "+buildTotal+" commands; requires operator permission";
    }

    public static void startImportedBuild(String name,List<String> commands,Minecraft mc) {
        if(mc.player==null || mc.getConnection()==null) {
            buildStatus="Join a world before importing a schematic";
            return;
        }
        if(!BUILD_COMMANDS.isEmpty()) {
            buildStatus="A build is already running";
            return;
        }
        if(commands==null || commands.isEmpty() || commands.size()>8192) {
            buildStatus="Schematic is empty or exceeds the safe 8192-command limit";
            return;
        }
        BUILD_COMMANDS.addAll(commands);
        buildTotal=commands.size();
        buildSent=0;
        buildPaused=false;
        NovaConfig.buildTemplate=name;
        NovaConfig.showBuildGuide=true;
        NovaConfig.save();
        buildStatus="Imported "+name+" · "+buildTotal+" placement commands queued; operator permission required";
    }

    public static void pauseBuild() {
        if(!BUILD_COMMANDS.isEmpty()) {
            buildPaused=true;
            buildStatus="Build paused at "+getBuildProgress()+"%";
        }
    }

    public static void resumeBuild() {
        if(!BUILD_COMMANDS.isEmpty()) {
            buildPaused=false;
            buildStatus="Build resumed at "+getBuildProgress()+"%";
        }
    }

    public static void cancelBuild() {
        BUILD_COMMANDS.clear();
        buildPaused=false;
        buildTotal=0;
        buildSent=0;
        buildStatus="Build cancelled";
    }

    public static String getBuildStatus() { return buildPaused ? "Paused · "+getBuildProgress()+"%" : buildStatus; }
    public static int getQueuedBuildCommands() { return BUILD_COMMANDS.size(); }
    public static int getBuildProgress() {
        if(buildTotal<=0) return 0;
        return Math.max(0,Math.min(100,(int)((buildSent*100L)/buildTotal)));
    }

    private static List<String> makeHouseCommands(String template,int x,int y,int z) {
        List<String> c=new ArrayList<>();
        if(template.equals("watchtower")) {
            c.add(fill(x,y,z,x+4,y,z+4,"stone_bricks"));
            c.add(fill(x,y+1,z,x+4,y+7,z+4,"spruce_planks hollow"));
            c.add(fill(x+1,y+1,z+1,x+3,y+6,z+3,"air"));
            c.add(fill(x,y+8,z,x+4,y+8,z+4,"spruce_planks"));
            c.add(fill(x+1,y+3,z,x+3,y+4,z,"glass_pane"));
            c.add(fill(x+1,y+3,z+4,x+3,y+4,z+4,"glass_pane"));
            c.add(fill(x,y+3,z+1,x,y+4,z+3,"glass_pane"));
            c.add(fill(x+4,y+3,z+1,x+4,y+4,z+3,"glass_pane"));
            c.add(fill(x+2,y+1,z,x+2,y+2,z,"air"));
            c.add("setblock "+(x+2)+" "+(y+1)+" "+z+" spruce_door[facing=south,half=lower]");
            c.add("setblock "+(x+2)+" "+(y+2)+" "+z+" spruce_door[facing=south,half=upper]");
            return c;
        }

        int size=template.equals("cottage")?9:(template.equals("modern")?9:7);
        int height=template.equals("cabin")?4:5;
        String wall=template.equals("modern")?"quartz_block":(template.equals("cottage")?"oak_planks":"spruce_planks");
        String floor=template.equals("modern")?"smooth_quartz":"spruce_planks";
        String roof=template.equals("modern")?"stone_slab":"spruce_planks";
        String glass=template.equals("modern")?"black_stained_glass_pane":"glass_pane";
        int mid=size/2;
        c.add(fill(x,y,z,x+size-1,y,z+size-1,floor));
        c.add(fill(x,y+1,z,x+size-1,y+height,z+size-1,wall+" hollow"));
        c.add(fill(x-1,y+height+1,z-1,x+size,y+height+1,z+size,roof));
        c.add(fill(x+mid,y+1,z,x+mid,y+2,z,"air"));
        c.add("setblock "+(x+mid)+" "+(y+1)+" "+z+" oak_door[facing=south,half=lower]");
        c.add("setblock "+(x+mid)+" "+(y+2)+" "+z+" oak_door[facing=south,half=upper]");
        c.add(fill(x+1,y+2,z,x+2,y+3,z,glass));
        c.add(fill(x+size-3,y+2,z,x+size-2,y+3,z,glass));
        c.add(fill(x+1,y+2,z+size-1,x+2,y+3,z+size-1,glass));
        c.add(fill(x+size-3,y+2,z+size-1,x+size-2,y+3,z+size-1,glass));
        c.add(fill(x,y+2,z+2,x,y+3,z+3,glass));
        c.add(fill(x+size-1,y+2,z+2,x+size-1,y+3,z+3,glass));
        c.add("setblock "+(x+1)+" "+(y+1)+" "+(z+1)+" lantern");
        c.add("setblock "+(x+size-2)+" "+(y+1)+" "+(z+1)+" lantern");
        if(template.equals("modern")) {
            c.add(fill(x+1,y+1,z+1,x+size-2,y+1,z+size-2,"smooth_quartz"));
        }
        return c;
    }

    private static String fill(int x1,int y1,int z1,int x2,int y2,int z2,String block) {
        return "fill "+x1+" "+y1+" "+z1+" "+x2+" "+y2+" "+z2+" "+block;
    }
}