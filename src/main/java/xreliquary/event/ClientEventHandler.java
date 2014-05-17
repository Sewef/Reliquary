package xreliquary.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.init.ContentHandler;
import xreliquary.items.ItemHandgun;
import xreliquary.lib.Names;

public class ClientEventHandler {

    private static int time;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        //handles the color shifting of the twilight cloak, until we can throw it on an animation
        if(event.phase != TickEvent.Phase.END)
            return;
        if (getTime() > 88) {
            time = 10;
        } else {
            time++;
        }

        //handles rendering the hud for the handgun, WIP
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemHandgun)) return;
        ItemStack handgunStack = player.getCurrentEquippedItem();
        ItemHandgun handgunItem = (ItemHandgun)handgunStack.getItem();
        ItemStack bulletStack = new ItemStack(ContentHandler.getItem(Names.bullet), handgunItem.getBulletCount(handgunStack), handgunItem.getBulletType(handgunStack));
        renderHandgunHUD(mc, player, handgunStack, bulletStack);
    }

    private static void renderHandgunHUD(Minecraft minecraft, EntityPlayer player, ItemStack handgunStack, ItemStack bulletStack)
    {

        float overlayScale = 2.5F;
        float overlayOpacity = 0.75F;

        GL11.glPushMatrix();
        ScaledResolution sr = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);

        int hudOverlayX = (int) (sr.getScaledWidth() - 16 * overlayScale);
        int hudOverlayY = (int) (sr.getScaledHeight() - 16 * overlayScale);

        renderItemIntoGUI(minecraft.fontRenderer, handgunStack, hudOverlayX, hudOverlayY, overlayOpacity, overlayScale);
        //renders the number of bullets onto the screen, I hope.
        for (int xOffset = 0; xOffset < bulletStack.stackSize; xOffset++) {
            //xOffset * 4 makes the bullets "overlap" one another, -16 moves them to the left by a bit
            renderItemIntoGUI(minecraft.fontRenderer, bulletStack, hudOverlayX + (xOffset * 4) - 16 , hudOverlayY, overlayOpacity, overlayScale / 2F);
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float opacity, float scale)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        for (int passes = 0; passes < itemStack.getItem().getRenderPasses(itemStack.getItemDamage()); passes++) {
            int overlayColour = itemStack.getItem().getColorFromItemStack(itemStack, passes);
            IIcon icon = itemStack.getItem().getIcon(itemStack, passes);
            float red = (overlayColour >> 16 & 255) / 255.0F;
            float green = (overlayColour >> 8 & 255) / 255.0F;
            float blue = (overlayColour & 255) / 255.0F;
            GL11.glColor4f(red, green, blue, opacity);
            drawTexturedQuad(x, y, icon, 16 * scale, 16 * scale, -90);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    public static void drawTexturedQuad(int x, int y, IIcon icon, float width, float height, double zLevel)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, zLevel, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y + height, zLevel, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y, zLevel, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y, zLevel, icon.getMinU(), icon.getMinV());
        tessellator.draw();
    }
    public static int getTime() {
        return time;
    }

}
