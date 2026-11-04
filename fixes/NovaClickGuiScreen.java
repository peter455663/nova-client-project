package hu.peter.nova;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Main launcher for the separate Nova category panels. */
public final class NovaClickGuiScreen extends Screen {
    private static final int PANEL_WIDTH=430, PANEL_HEIGHT=330;
    public NovaClickGuiScreen() { super(Component.literal("Nova Utility Client")); }

    @Override protected void init() {
        int left=(this.width-PANEL_WIDTH)/2, top=(this.height-PANEL_HEIGHT)/2;
        int col=178, gap=12, x1=left+25, x2=x1+col+gap, y=top+82, h=28;
        addCategory("HUD",NovaCategoryScreen.Category.HUD,x1,y,col,h);
        addCategory("VISUALS",NovaCategoryScreen.Category.VISUALS,x2,y,col,h);
        addCategory("MOVEMENT",NovaCategoryScreen.Category.MOVEMENT,x1,y+42,col,h);
        addCategory("BUILDING",NovaCategoryScreen.Category.BUILDING,x2,y+42,col,h);
        addCategory("MINING",NovaCategoryScreen.Category.MINING,x1,y+84,col,h);
        addCategory("SETTINGS",NovaCategoryScreen.Category.SETTINGS,x2,y+84,col,h);
        addRenderableWidget(Button.builder(Component.literal("Close"),b->onClose())
                .bounds(left+PANEL_WIDTH-105,top+PANEL_HEIGHT-40,80,24).build());
    }
    private void addCategory(String label,NovaCategoryScreen.Category category,int x,int y,int w,int h) {
        addRenderableWidget(Button.builder(Component.literal(label),b->this.minecraft.gui.setScreen(new NovaCategoryScreen(category)))
                .bounds(x,y,w,h).build());
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float delta) {
        int left=(width-PANEL_WIDTH)/2, top=(height-PANEL_HEIGHT)/2;
        int bg=NovaConfig.lightTheme?0xF0F4F5FA:0xF0121520;
        int header=NovaConfig.lightTheme?0xFFE0E4F0:0xFF1C2030;
        int text=NovaConfig.lightTheme?0xFF202334:0xFFE6E8F0;
        int accent=NovaConfig.lightTheme?0xFF6550B7:0xFF8B5CF6;
        g.fill(0,0,width,height,0x88000000);
        g.fill(left-1,top-1,left+PANEL_WIDTH+1,top+PANEL_HEIGHT+1,0xFF353047);
        g.fill(left,top,left+PANEL_WIDTH,top+PANEL_HEIGHT,bg);
        g.fill(left,top,left+PANEL_WIDTH,top+48,header);
        g.fill(left,top,left+3,top+PANEL_HEIGHT,accent);
        g.text(font,"NOVA",left+24,top+16,accent,true);
        g.text(font,"UTILITY CLIENT  /  0.3.0",left+94,top+16,text,true);
        g.text(font,"Choose a separate module window",left+25,top+60,text,false);
        g.text(font,"Right Alt opens this menu  •  Settings save automatically",left+25,top+PANEL_HEIGHT-65,text,false);
        super.extractRenderState(g,mx,my,delta);
    }
    @Override public void onClose() { this.minecraft.gui.setScreen(null); }
}