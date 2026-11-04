package hu.peter.nova;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

/**
 * Procedural, real block-command blueprints. Commands are sent through the
 * normal server command channel and therefore still require server permission.
 */
public final class NovaBlueprints {
    public static final List<String> HOUSES = List.of(
        "starter_house","modern_house","medieval_house","wooden_survival_house",
        "stone_house","luxury_mansion","large_mansion","japanese_house",
        "mountain_house","underground_base","farmhouse","treehouse",
        "castle","watchtower","villager_house","storage_house"
    );
    public static final List<String> FARMS = List.of(
        "wheat_farm","carrot_farm","potato_farm","beetroot_farm","sugar_cane_farm",
        "bamboo_farm","cactus_farm","pumpkin_farm","melon_farm","tree_farm",
        "animal_farm","xp_farm","mob_farm","villager_breeder"
    );
    public static final List<String> STRUCTURES = List.of(
        "automatic_storage_room","villager_trading_hall","enchanting_room",
        "nether_portal_room","mine_entrance","bridge","road","wall",
        "tower","underground_storage","large_survival_base"
    );

    private static final Map<String,String> HU_NAMES = Map.ofEntries(
        Map.entry("starter_house","Kezdőház"),
        Map.entry("modern_house","Modern ház"),
        Map.entry("medieval_house","Középkori ház"),
        Map.entry("wooden_survival_house","Faház túléléshez"),
        Map.entry("stone_house","Kőház"),
        Map.entry("luxury_mansion","Luxusvilla"),
        Map.entry("large_mansion","Nagy kúria"),
        Map.entry("japanese_house","Japán ház"),
        Map.entry("mountain_house","Hegyi ház"),
        Map.entry("underground_base","Föld alatti bázis"),
        Map.entry("farmhouse","Tanya"),
        Map.entry("treehouse","Faház a fán"),
        Map.entry("castle","Kastély"),
        Map.entry("watchtower","Őrtorony"),
        Map.entry("villager_house","Falusi ház"),
        Map.entry("storage_house","Raktárház"),
        Map.entry("wheat_farm","Búzafarm"),
        Map.entry("carrot_farm","Répakert"),
        Map.entry("potato_farm","Burgonyafarm"),
        Map.entry("beetroot_farm","Céklafarm"),
        Map.entry("sugar_cane_farm","Cukornádfarm"),
        Map.entry("bamboo_farm","Bambuszfarm"),
        Map.entry("cactus_farm","Kaktuszfarm"),
        Map.entry("pumpkin_farm","Tökfarm"),
        Map.entry("melon_farm","Dinnyefarm"),
        Map.entry("tree_farm","Fafarm"),
        Map.entry("animal_farm","Állatfarm"),
        Map.entry("xp_farm","Tapasztalatfarm"),
        Map.entry("mob_farm","Mobfarm"),
        Map.entry("villager_breeder","Falusi szaporító"),
        Map.entry("automatic_storage_room","Automata raktár"),
        Map.entry("villager_trading_hall","Falusi kereskedőcsarnok"),
        Map.entry("enchanting_room","Bűvölőszoba"),
        Map.entry("nether_portal_room","Netherkapu-szoba"),
        Map.entry("mine_entrance","Bányabejárat"),
        Map.entry("bridge","Híd"),
        Map.entry("road","Út"),
        Map.entry("wall","Fal"),
        Map.entry("tower","Torony"),
        Map.entry("underground_storage","Föld alatti raktár"),
        Map.entry("large_survival_base","Nagy túlélőbázis")
    );

    private NovaBlueprints() {}

    public static List<String> commands(String id, int x, int y, int z) {
        String key = id == null ? "starter_house" : id.toLowerCase(Locale.ROOT);
        key = switch(key) {
            case "cabin" -> "wooden_survival_house";
            case "cottage" -> "farmhouse";
            case "modern" -> "modern_house";
            default -> key;
        };
        if (HOUSES.contains(key)) return house(key,x,y,z);
        if (FARMS.contains(key)) return farm(key,x,y,z);
        if (STRUCTURES.contains(key)) return structure(key,x,y,z);
        return house("starter_house",x,y,z);
    }

    public static String displayName(String id) {
        if (!NovaLanguage.isEnglish()) return HU_NAMES.getOrDefault(id, titleCase(id));
        return titleCase(id);
    }

    private static String titleCase(String id) {
        StringBuilder out=new StringBuilder();
        for(String part:id.replace('_',' ').split(" ")) {
            if(part.isEmpty()) continue;
            if(out.length()>0) out.append(' ');
            out.append(part.substring(0,1).toUpperCase(Locale.ROOT));
            if(part.length()>1) out.append(part.substring(1));
        }
        return out.toString();
    }

    private static List<String> house(String id,int x,int y,int z) {
        List<String> c=new ArrayList<>();
        int size=9,height=5,baseY=y;
        String wall="oak_planks",floor="spruce_planks",roof="spruce_stairs",glass="glass_pane",log="oak_log";
        switch(id) {
            case "modern_house" -> { size=11; height=5; wall="quartz_block"; floor="smooth_quartz"; roof="smooth_quartz_slab"; glass="black_stained_glass"; log="quartz_pillar"; }
            case "medieval_house" -> { size=11; height=6; wall="cobblestone"; floor="spruce_planks"; roof="dark_oak_planks"; glass="glass_pane"; log="dark_oak_log"; }
            case "wooden_survival_house" -> { size=7; height=4; wall="spruce_planks"; floor="spruce_planks"; roof="spruce_planks"; glass="glass_pane"; log="spruce_log"; }
            case "stone_house" -> { size=9; height=5; wall="stone_bricks"; floor="oak_planks"; roof="stone_bricks"; glass="glass_pane"; log="stone_bricks"; }
            case "luxury_mansion" -> { size=15; height=7; wall="white_concrete"; floor="polished_andesite"; roof="dark_oak_planks"; glass="tinted_glass"; log="quartz_pillar"; }
            case "large_mansion" -> { size=19; height=8; wall="calcite"; floor="polished_diorite"; roof="dark_oak_planks"; glass="glass_pane"; log="quartz_pillar"; }
            case "japanese_house" -> { size=11; height=5; wall="white_concrete"; floor="bamboo_mosaic"; roof="dark_prismarine"; glass="white_stained_glass_pane"; log="stripped_spruce_log"; }
            case "mountain_house" -> { size=9; height=5; wall="stone_bricks"; floor="spruce_planks"; roof="stone"; glass="glass_pane"; log="spruce_log"; }
            case "underground_base" -> { size=13; height=5; wall="stone_bricks"; floor="polished_deepslate"; roof="polished_deepslate"; glass="glass_pane"; log="deepslate_bricks"; }
            case "farmhouse" -> { size=11; height=5; wall="oak_planks"; floor="oak_planks"; roof="hay_block"; glass="glass_pane"; log="oak_log"; }
            case "treehouse" -> { size=9; height=5; wall="oak_planks"; floor="spruce_planks"; roof="oak_leaves"; glass="glass_pane"; log="oak_log"; y+=6; }
            case "castle" -> { size=19; height=9; wall="stone_bricks"; floor="polished_diorite"; roof="stone_bricks"; glass="iron_bars"; log="stone_bricks"; }
            case "watchtower" -> { size=7; height=9; wall="spruce_planks"; floor="stone_bricks"; roof="spruce_planks"; glass="glass_pane"; log="spruce_log"; }
            case "villager_house" -> { size=7; height=4; wall="oak_planks"; floor="oak_planks"; roof="oak_stairs"; glass="glass_pane"; log="oak_log"; }
            case "storage_house" -> { size=11; height=5; wall="spruce_planks"; floor="stone_bricks"; roof="spruce_planks"; glass="glass_pane"; log="spruce_log"; }
            default -> { }
        }
        if(id.equals("underground_base")) {
            c.add(fill(x,y,z,x+size-1,y+height+2,z+size-1,"air"));
            c.add(fill(x,y,z,x+size-1,y,z+size-1,"polished_deepslate"));
            c.add(fill(x,y+1,z,x+size-1,y+height,z+size-1,wall+" hollow"));
            c.add(fill(x+1,y+height,z+1,x+size-2,y+height,z+size-2,"glass"));
            c.add(fill(x+size/2-1,y+1,z,x+size/2,y+2,z,"air"));
            c.add("setblock "+(x+size/2-1)+" "+(y+1)+" "+z+" iron_door[facing=south,half=lower]");
            c.add("setblock "+(x+size/2-1)+" "+(y+2)+" "+z+" iron_door[facing=south,half=upper]");
            return c;
        }
        c.add(fill(x,y,z,x+size-1,y,z+size-1,floor));
        c.add(fill(x,y+1,z,x+size-1,y+height,z+size-1,wall+" hollow"));
        c.add(fill(x-1,y+height+1,z-1,x+size,y+height+1,z+size,roof));
        c.add(fill(x,y+1,z,x,y+height,z,log));
        c.add(fill(x+size-1,y+1,z,x+size-1,y+height,z,log));
        c.add(fill(x,y+1,z+size-1,x,y+height,z+size-1,log));
        c.add(fill(x+size-1,y+1,z+size-1,x+size-1,y+height,z+size-1,log));
        int mid=size/2;
        c.add(fill(x+mid-1,y+1,z,x+mid,y+2,z,"air"));
        c.add("setblock "+(x+mid-1)+" "+(y+1)+" "+z+" oak_door[facing=south,half=lower]");
        c.add("setblock "+(x+mid-1)+" "+(y+2)+" "+z+" oak_door[facing=south,half=upper]");
        c.add(fill(x+1,y+2,z,x+2,y+3,z,glass));
        c.add(fill(x+size-3,y+2,z,x+size-2,y+3,z,glass));
        c.add(fill(x+1,y+2,z+size-1,x+2,y+3,z+size-1,glass));
        c.add(fill(x+size-3,y+2,z+size-1,x+size-2,y+3,z+size-1,glass));
        c.add(fill(x,y+2,z+2,x,y+3,z+3,glass));
        c.add(fill(x+size-1,y+2,z+2,x+size-1,y+3,z+3,glass));
        c.add("setblock "+(x+2)+" "+(y+1)+" "+(z+2)+" lantern");
        c.add("setblock "+(x+size-3)+" "+(y+1)+" "+(z+2)+" lantern");
        if(id.equals("castle")) {
            for(int[] p:new int[][]{{0,0},{size-4,0},{0,size-4},{size-4,size-4}}) {
                c.add(fill(x+p[0],y+1,z+p[1],x+p[0]+3,y+height+5,z+p[1]+3,"stone_bricks hollow"));
                c.add(fill(x+p[0],y+height+6,z+p[1],x+p[0]+3,y+height+6,z+p[1]+3,"stone_brick_slab"));
            }
        }
        if(id.equals("watchtower")) {
            c.add(fill(x+1,y+height-1,z+1,x+size-2,y+height-1,z+size-2,"air"));
            c.add(fill(x-1,y+height+2,z-1,x+size,y+height+2,z+size,"spruce_planks"));
        }
        if(id.equals("treehouse")) c.add(fill(x+mid,y-6,z+mid,x+mid,y,z+mid,"oak_log"));
        if(id.equals("storage_house")) {
            for(int i=2;i<size-2;i+=2) c.add("setblock "+(x+i)+" "+(y+1)+" "+(z+2)+" chest[facing=south]");
        }
        return c;
    }

    private static List<String> farm(String id,int x,int y,int z) {
        List<String> c=new ArrayList<>();
        c.add(fill(x,y,z,x+10,y,z+10,"dirt"));
        c.add(fill(x,y+1,z,x+10,y+1,z+10,"air"));
        String crop=switch(id) {
            case "carrot_farm" -> "carrots[age=7]";
            case "potato_farm" -> "potatoes[age=7]";
            case "beetroot_farm" -> "beetroots[age=3]";
            case "sugar_cane_farm" -> "sugar_cane";
            case "bamboo_farm" -> "bamboo";
            case "cactus_farm" -> "cactus";
            case "pumpkin_farm" -> "pumpkin";
            case "melon_farm" -> "melon";
            case "tree_farm" -> "oak_sapling";
            default -> "wheat[age=7]";
        };
        if(id.equals("animal_farm")) {
            c.add(fill(x,y+1,z,x+10,y+1,z+10,"grass_block"));
            c.add(fill(x,y+2,z,x+10,y+3,z,"oak_fence"));
            c.add(fill(x,y+2,z+10,x+10,y+3,z+10,"oak_fence"));
            c.add(fill(x,y+2,z,x,y+3,z+10,"oak_fence"));
            c.add(fill(x+10,y+2,z,x+10,y+3,z+10,"oak_fence"));
            c.add("setblock "+(x+5)+" "+(y+2)+" "+z+" oak_fence_gate");
            return c;
        }
        if(id.equals("mob_farm")||id.equals("xp_farm")) {
            c.add(fill(x,y+1,z,x+10,y+1,z+10,"stone_bricks"));
            c.add(fill(x+1,y+2,z+1,x+9,y+9,z+9,"cobblestone hollow"));
            c.add(fill(x+2,y+3,z+2,x+8,y+3,z+8,"air"));
            c.add(fill(x+2,y+8,z+2,x+8,y+8,z+8,"air"));
            c.add(fill(x+1,y+10,z+1,x+9,y+10,z+9,"stone_bricks"));
            c.add(fill(x+5,y+2,z+1,x+5,y+5,z+1,"air"));
            return c;
        }
        if(id.equals("villager_breeder")) {
            c.add(fill(x,y+1,z,x+10,y+1,z+10,"oak_planks"));
            c.add(fill(x,y+2,z,x+10,y+2,z,"glass"));
            c.add(fill(x,y+2,z+10,x+10,y+2,z+10,"glass"));
            c.add(fill(x,y+2,z,x,y+2,z+10,"glass"));
            c.add(fill(x+10,y+2,z,x+10,y+2,z+10,"glass"));
            c.add("setblock "+(x+5)+" "+(y+2)+" "+(z+5)+" composter");
            return c;
        }
        if(id.equals("sugar_cane_farm")||id.equals("bamboo_farm")||id.equals("cactus_farm")) {
            c.add(fill(x+1,y+1,z+1,x+9,y+1,z+9,id.equals("cactus_farm")?"sand":"sand"));
            for(int i=1;i<=9;i+=2) {
                c.add("setblock "+(x+i)+" "+(y+2)+" "+(z+2)+" "+crop);
                c.add("setblock "+(x+i)+" "+(y+2)+" "+(z+8)+" "+crop);
            }
            c.add(fill(x+1,y+1,z+5,x+9,y+1,z+5,"water"));
            return c;
        }
        c.add(fill(x+1,y+1,z+1,x+9,y+1,z+9,"farmland[moisture=7]"));
        c.add(fill(x+5,y+1,z+1,x+5,y+1,z+9,"water"));
        for(int dx=1;dx<=9;dx++) for(int dz=1;dz<=9;dz++) {
            if(dx==5) continue;
            if((id.equals("pumpkin_farm")||id.equals("melon_farm")) && (dx%2==0 || dz%2==0)) continue;
            c.add("setblock "+(x+dx)+" "+(y+2)+" "+(z+dz)+" "+crop);
        }
        c.add(fill(x,y+1,z,x+10,y+1,z,"oak_planks"));
        c.add(fill(x,y+1,z+10,x+10,y+1,z+10,"oak_planks"));
        return c;
    }

    private static List<String> structure(String id,int x,int y,int z) {
        List<String> c=new ArrayList<>();
        switch(id) {
            case "bridge" -> {
                c.add(fill(x,y,z,x+4,y,z+24,"spruce_planks"));
                c.add(fill(x,y+1,z,x,y+2,z+24,"oak_fence"));
                c.add(fill(x+4,y+1,z,x+4,y+2,z+24,"oak_fence"));
                c.add(fill(x+2,y-1,z,x+2,y-1,z+24,"stone_bricks"));
            }
            case "road" -> {
                c.add(fill(x,y,z,x+8,y,z+28,"stone"));
                c.add(fill(x+3,y,z,x+5,y,z+28,"gray_concrete"));
                for(int i=1;i<28;i+=4) c.add(fill(x+4,y+1,z+i,x+4,y+1,z+i+1,"yellow_concrete"));
            }
            case "wall" -> {
                c.add(fill(x,y,z,x+20,y+5,z+2,"stone_bricks"));
                c.add(fill(x,y+6,z,x+20,y+6,z+2,"stone_brick_slab"));
                for(int i=0;i<=20;i+=2) c.add(fill(x+i,y+7,z,x+i,y+7,z+2,"stone_brick_wall"));
            }
            case "tower" -> {
                c.add(fill(x,y,z,x+7,y,z+7,"stone_bricks"));
                c.add(fill(x,y+1,z,x+7,y+16,z+7,"stone_bricks hollow"));
                c.add(fill(x-1,y+17,z-1,x+8,y+17,z+8,"stone_brick_slab"));
            }
            case "mine_entrance" -> {
                c.add(fill(x,y,z,x+5,y,z+7,"stone_bricks"));
                c.add(fill(x+1,y+1,z+1,x+4,y+4,z+7,"air"));
                c.add(fill(x+1,y+1,z+1,x+4,y+1,z+7,"spruce_planks"));
                c.add(fill(x+1,y+4,z+1,x+4,y+4,z+7,"oak_log"));
                c.add("setblock "+(x+2)+" "+(y+2)+" "+z+" lantern");
            }
            case "nether_portal_room" -> {
                c.addAll(house("stone_house",x,y,z));
                int px=x+4,py=y+1,pz=z;
                c.add(fill(px,py,pz,px+3,py+4,pz,"obsidian hollow"));
                c.add(fill(px+1,py+1,pz,px+2,py+3,pz,"air"));
            }
            case "enchanting_room" -> {
                c.addAll(house("japanese_house",x,y,z));
                c.add("setblock "+(x+4)+" "+(y+1)+" "+(z+4)+" enchanting_table");
                for(int[] p:new int[][]{{2,2},{2,6},{6,2},{6,6}})
                    c.add("setblock "+(x+p[0])+" "+(y+1)+" "+(z+p[1])+" bookshelf");
            }
            case "automatic_storage_room","underground_storage" -> {
                c.addAll(house("underground_base",x,y,z));
                for(int row=0;row<3;row++) for(int col=0;col<4;col++)
                    c.add("setblock "+(x+2+col*2)+" "+(y+1+row)+" "+(z+2)+" chest[facing=south]");
            }
            case "villager_trading_hall" -> {
                c.add(fill(x,y,z,x+16,y,z+8,"polished_andesite"));
                c.add(fill(x,y+1,z,x+16,y+4,z+8,"oak_planks hollow"));
                for(int i=1;i<16;i+=2) c.add("setblock "+(x+i)+" "+(y+1)+" "+(z+4)+" lectern");
            }
            case "large_survival_base" -> {
                c.addAll(house("large_mansion",x,y,z));
                c.add(fill(x-5,y,z+5,x-1,y+8,z+9,"stone_bricks hollow"));
                c.add(fill(x+19,y,z+5,x+23,y+8,z+9,"stone_bricks hollow"));
            }
            default -> c.addAll(house("starter_house",x,y,z));
        }
        return c;
    }

    private static String fill(int x1,int y1,int z1,int x2,int y2,int z2,String block) {
        return "fill "+x1+" "+y1+" "+z1+" "+x2+" "+y2+" "+z2+" "+block;
    }
}
