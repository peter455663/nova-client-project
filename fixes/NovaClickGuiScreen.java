package hu.peter.nova;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Main launcher for Nova's separately opened module category panels. */
public final class NovaClickGuiScreen extends Screen {
    private static final int PANEL_WIDTH=470,PANEL_HEIGHT=410;
    public NovaClickGuiScreen(){super(Component.literal("Nova Utility Client"));}
    @Override protected void init(){
        int left=(width-PANEL_WIDTH)/2,top=(height-PANEL_HEIGHT)/2;
        String[] labels={"HUD","VISUALS","MOVEMENT","PLAYER","WORLD","BUILDING","FARMING","MINING","MISC","PROFILES","SETTINGS","ADMIN"};
        NovaCategoryScreen.Category[] categories=NovaCategoryScreen.Category.values();
        int x1=left+24,x2=left+242,w=204,h=27;
        for(int i=0;i<labels.length;i++){
            int x=(i%2==0)?x1:x2,y=top+82+(i/2)*43;
            NovaCategoryScreen.Category category=categories[i];
            addRenderableWidget(Button.builder(Component.literal(labels[i]),b->this.minecraft.gui.setScreen(new NovaCategoryScreen(category)))
                    .bounds(x,y,w,h).build());
        }
        addRenderableWidget(Button.builder(Component.literal("Close"),b->onClose())
                .bounds(left+PANEL_WIDTH-110,top+PANEL_HEIGHT-44,86,25).build());
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float delta){
        int left=(width-PANEL_WIDTH)/2,top=(height-PANEL_HEIGHT)/2;
        int bg=NovaConfig.lightTheme?0xF0F4F5FA:0xF0121520,header=NovaConfig.lightTheme?0xFFE0E4F0:0xFF1C2030;
        int text=NovaConfig.lightTheme?0xFF202334:0xFFE6E8F0,accent=NovaConfig.accentColor();
        g.fill(0,0,width,height,0x88000000);g.fill(left-1,top-1,left+PANEL_WIDTH+1,top+PANEL_HEIGHT+1,0xFF353047);
        g.fill(left,top,left+PANEL_WIDTH,top+PANEL_HEIGHT,bg);g.fill(left,top,left+PANEL_WIDTH,top+48,header);
        g.fill(left,top,left+3,top+PANEL_HEIGHT,accent);g.text(font,"NOVA",left+24,top+16,accent,true);
        g.text(font,"UTILITY CLIENT  /  0.7.0",left+94,top+16,text,true);
        g.text(font,"Each category opens in its own module window.",left+24,top+60,text,false);
        g.text(font,"Right Alt opens this menu  ·  settings save automatically",left+24,top+PANEL_HEIGHT-68,text,false);
        super.extractRenderState(g,mx,my,delta);
    }
    @Override public void onClose(){this.minecraft.gui.setScreen(null);}
}