package hu.peter.nova;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Imports Sponge Schematic v2/v3 files into ordinary setblock/fill commands.
 * Files are restricted to config/nova/schematics to avoid arbitrary path access.
 */
public final class NovaSchematicImporter {
    private static final Pattern SAFE_BLOCK_STATE=Pattern.compile(
            "[a-z0-9_.-]+:[a-z0-9_./-]+(?:\\[[a-zA-Z0-9_=,.-]+\\])?");
    private static final long MAX_FILE_BYTES=16L*1024L*1024L;
    private static final int MAX_BLOCKS=32768;
    private static final int MAX_COMMANDS=8192;

    private NovaSchematicImporter() {}

    public static Path schematicFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve("nova").resolve("schematics");
    }

    public static List<String> readSpongeSchematic(String fileName,BlockPos anchor) throws IOException {
        if(fileName==null || !(fileName.toLowerCase().endsWith(".schem"))) {
            throw new IllegalArgumentException("Only Sponge .schem files are supported by this importer.");
        }
        if(fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            throw new IllegalArgumentException("Use a file name inside config/nova/schematics.");
        }
        Path folder=schematicFolder().toAbsolutePath().normalize();
        Files.createDirectories(folder);
        Path file=folder.resolve(fileName).normalize();
        if(!file.startsWith(folder) || !Files.isRegularFile(file)) {
            throw new IOException("Schematic not found. Copy it to config/nova/schematics first.");
        }
        long bytes=Files.size(file);
        if(bytes<=0 || bytes>MAX_FILE_BYTES) throw new IOException("Schematic file must be between 1 byte and 16 MB.");

        CompoundTag root=NbtIo.readCompressed(file,NbtAccounter.unlimitedHeap());
        int width=root.getShortOr("Width",(short)0);
        int height=root.getShortOr("Height",(short)0);
        int length=root.getShortOr("Length",(short)0);
        long volume=(long)width*height*length;
        if(width<1 || height<1 || length<1 || volume>MAX_BLOCKS) {
            throw new IOException("Invalid schematic dimensions. Limit: 32768 blocks.");
        }
        int version=root.getIntOr("Version",2);
        if(version<2 || version>3) throw new IOException("Unsupported Sponge schematic version: "+version);

        CompoundTag palette=root.getCompoundOrEmpty("Palette");
        if(palette.keySet().isEmpty()) throw new IOException("Schematic palette is missing.");
        Map<Integer,String> states=new HashMap<>();
        for(String state:palette.keySet()) {
            int id=palette.getIntOr(state,-1);
            if(id<0 || !SAFE_BLOCK_STATE.matcher(state).matches()) {
                throw new IOException("Invalid block state in schematic palette: "+state);
            }
            states.put(id,state);
        }
        byte[] blockData=root.getByteArray("BlockData")
                .orElseThrow(()->new IOException("Schematic BlockData is missing."));
        int[] ids=decodeVarInts(blockData,(int)volume);
        List<String> commands=new ArrayList<>();
        for(int y=0;y<height;y++) {
            for(int z=0;z<length;z++) {
                int x=0;
                while(x<width) {
                    int id=ids[x+z*width+y*width*length];
                    String state=states.get(id);
                    if(state==null) throw new IOException("Palette ID "+id+" is not defined.");
                    int runEnd=x+1;
                    while(runEnd<width && ids[runEnd+z*width+y*width*length]==id) runEnd++;
                    int wx=anchor.getX()+x, wy=anchor.getY()+y, wz=anchor.getZ()+z;
                    if(runEnd-x>=2) {
                        commands.add("fill "+wx+" "+wy+" "+wz+" "
                                +(anchor.getX()+runEnd-1)+" "+wy+" "+wz+" "+state);
                    } else {
                        commands.add("setblock "+wx+" "+wy+" "+wz+" "+state);
                    }
                    x=runEnd;
                    if(commands.size()>MAX_COMMANDS) throw new IOException("Schematic expands to too many commands; simplify it first.");
                }
            }
        }
        return commands;
    }

    private static int[] decodeVarInts(byte[] data,int count) throws IOException {
        int[] values=new int[count];
        int cursor=0;
        for(int i=0;i<count;i++) {
            int value=0,shift=0;
            while(true) {
                if(cursor>=data.length || shift>28) throw new IOException("Invalid or truncated schematic BlockData.");
                int b=Byte.toUnsignedInt(data[cursor++]);
                value|=(b&0x7F)<<shift;
                if((b&0x80)==0) break;
                shift+=7;
            }
            values[i]=value;
        }
        return values;
    }
}
