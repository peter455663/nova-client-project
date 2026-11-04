package hu.peter.nova;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
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
    private static int tickCounter;
    private static long lastAfkNudge=System.currentTimeMillis();
    private static float lastYaw;
    private static boolean wasHoldingAttack;
    private static String buildStatus="Idle";

    private NovaAutomation() {}

    public static void tick(Minecraft mc) {
        tickCounter++;
        if(mc.player==null || mc.level==null || mc.getConnection()==null) {
            releaseAutomationKeys(mc);
            return;
        }

        if(mc.screen==null) {
            boolean blockTarget=mc.hitResult instanceof BlockHitResult hit
                    && !mc.level.getBlockState(hit.getBlockPos()).isAir();
            boolean farmTarget=NovaConfig.autoFarm && isMatureCrop(mc);
            boolean autoAttack=(NovaConfig.autoMine && blockTarget) || farmTarget;
            if(NovaConfig.autoMine || NovaConfig.autoFarm) {
                mc.options.keyAttack.setDown(autoAttack || mc.options.keyAttack.isDown());
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
        }

        if(!BUILD_COMMANDS.isEmpty() && tickCounter%5==0) {
            String command=BUILD_COMMANDS.pollFirst();
            if(command!=null) {
                try {
                    mc.getConnection().sendCommand(command);
                    buildStatus="Building: "+BUILD_COMMANDS.size()+" commands left";
                } catch(RuntimeException ex) {
                    BUILD_COMMANDS.clear();
                    buildStatus="Could not send build command";
                    NovaClient.LOGGER.error("Nova build command failed",ex);
                }
            }
            if(BUILD_COMMANDS.isEmpty() && buildStatus.startsWith("Building:")) buildStatus="Build commands sent; check server feedback";
        }
    }

    private static boolean isMatureCrop(Minecraft mc) {
        if(!(mc.hitResult instanceof BlockHitResult hit)) return false;
        BlockState state=mc.level.getBlockState(hit.getBlockPos());
        if(!(state.getBlock() instanceof CropBlock crop)) return false;
        return state.getValue(crop.getAgeProperty())>=crop.getMaxAge();
    }

    private static void releaseAutomationKeys(Minecraft mc) {
        if(wasHoldingAttack) {
            mc.options.keyAttack.setDown(false);
            wasHoldingAttack=false;
        }
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
            case "cottage" -> "cottage";
            case "modern" -> "modern";
            case "watchtower" -> "watchtower";
            default -> "cabin";
        };
        BlockPos p=mc.player.blockPosition();
        int x=p.getX(), y=p.getY(), z=p.getZ()+2;
        BUILD_COMMANDS.addAll(makeHouseCommands(selected,x,y,z));
        NovaConfig.buildX=x; NovaConfig.buildY=y; NovaConfig.buildZ=z;
        NovaConfig.hasBuildAnchor=true; NovaConfig.buildTemplate=selected; NovaConfig.showBuildGuide=true; NovaConfig.save();
        buildStatus="Queued "+BUILD_COMMANDS.size()+" commands; requires OP";
    }

    public static void cancelBuild() {
        BUILD_COMMANDS.clear();
        buildStatus="Build cancelled";
    }

    public static String getBuildStatus() { return buildStatus; }
    public static int getQueuedBuildCommands() { return BUILD_COMMANDS.size(); }

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