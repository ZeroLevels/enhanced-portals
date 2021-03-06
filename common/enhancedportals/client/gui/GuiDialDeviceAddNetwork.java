package enhancedportals.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.network.PacketDispatcher;
import enhancedcore.gui.GuiItemStackButton;
import enhancedportals.EnhancedPortals;
import enhancedportals.container.ContainerDialDeviceAddNetwork;
import enhancedportals.lib.GuiIds;
import enhancedportals.lib.Localization;
import enhancedportals.lib.Reference;
import enhancedportals.lib.Strings;
import enhancedportals.lib.Textures;
import enhancedportals.network.packet.PacketDialEntry;
import enhancedportals.network.packet.PacketEnhancedPortals;
import enhancedportals.network.packet.PacketGui;
import enhancedportals.portal.PortalTexture;
import enhancedportals.tileentity.TileEntityDialDevice;

public class GuiDialDeviceAddNetwork extends GuiEnhancedPortalsScreen
{
    TileEntityDialDevice dialDevice;
    GuiTextField nameField;

    byte thickness;
    boolean popUpState;
    String texture;
    String network, name;

    List<GuiGlyphElement> elementList = new ArrayList<GuiGlyphElement>();
    List<GuiGlyphElement> stackList = new ArrayList<GuiGlyphElement>();
    int elementCount = 0;
    String elementString = "";

    public GuiDialDeviceAddNetwork(InventoryPlayer inventory, TileEntityDialDevice dialdevice)
    {
        super(new ContainerDialDeviceAddNetwork(inventory), null);

        dialDevice = dialdevice;
        texture = "";
        name = "Name";
        thickness = 0;
        popUpState = false;

        for (int i = 0; i < 9; i++)
        {
            elementList.add(new GuiGlyphElement(guiLeft + 8 + i * 18, guiTop + 15, Reference.glyphItems.get(i).getItemName().replace("item.", ""), Reference.glyphItems.get(i), this));
        }

        for (int i = 9; i < 18; i++)
        {
            elementList.add(new GuiGlyphElement(guiLeft + 8 + (i - 9) * 18, guiTop + 33, Reference.glyphItems.get(i).getItemName().replace("item.", ""), Reference.glyphItems.get(i), this));
        }

        for (int i = 18; i < 27; i++)
        {
            elementList.add(new GuiGlyphElement(guiLeft + 8 + (i - 18) * 18, guiTop + 51, Reference.glyphItems.get(i).getItemName().replace("item.", ""), Reference.glyphItems.get(i), this));
        }

        extendedSlots.add(new GuiTextureSlot(xSize - 24, 40, Textures.getItemStackFromTexture(texture), this));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 1)
        {
            if (network == null || network.equals(""))
            {
                return;
            }

            PacketDispatcher.sendPacketToServer(PacketEnhancedPortals.makePacket(new PacketDialEntry(dialDevice, (byte) 0, name, texture, network, thickness)));
            PacketDispatcher.sendPacketToServer(PacketEnhancedPortals.makePacket(new PacketGui(dialDevice, GuiIds.DialDevice)));
        }
        else if (button.id == 2)
        {
            PacketDispatcher.sendPacketToServer(PacketEnhancedPortals.makePacket(new PacketGui(dialDevice, GuiIds.DialDevice)));
        }
        else if (button.id == 13)
        {
            int num = 0;

            if (button.displayString != null && button.displayString != "")
            {
                num = Integer.parseInt(button.displayString);
            }

            if (num + 1 < 5)
            {
                button.displayString = "" + (num + 1);
                num++;
            }
            else
            {
                button.displayString = "1";
                num = 1;
            }

            thickness = (byte) (num - 1);
            ((GuiItemStackButton) button).hoverText.set(1, EnumChatFormatting.GRAY + (thickness == 0 ? Strings.Normal.toString() : thickness == 1 ? Strings.Thick.toString() : thickness == 2 ? Strings.Thicker.toString() : Strings.FullBlock.toString()));
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        drawBackground(0);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(Reference.GUI_LOCATION + "dialDeviceInventory.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        if (stackList != null)
        {
            for (int i2 = 0; i2 < stackList.size(); i2++)
            {
                int x2 = guiLeft + 8, y2 = guiTop + 62;

                if (stackList.get(i2).itemStack == null)
                {
                    continue;
                }

                itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, stackList.get(i2).itemStack, x2 + i2 * 18, y2);
            }
        }

        drawExtendedSlots(i, j);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRenderer.drawString(Localization.localizeString("tile.dialDevice.name"), xSize / 2 - fontRenderer.getStringWidth(Localization.localizeString("tile.dialDevice.name")) / 2, -20, 0xFFCCCCCC);

        if (!popUpState)
        {
            if (stackList.isEmpty())
            {
                drawRect(7, 61, xSize - 7, 79, 0x55000000);
                fontRenderer.drawStringWithShadow(Strings.ClickToSetIdentifier.toString(), xSize / 2 - fontRenderer.getStringWidth(Strings.ClickToSetIdentifier.toString()) / 2, 66, 0x00FF00);
            }

            fontRenderer.drawString(Strings.Modifications.toString(), xSize / 2 - fontRenderer.getStringWidth(Strings.Modifications.toString()) / 2, 44, 0xFF444444);
            drawExtendedSlotsForeground(par1, par2);
        }
    }

    @SuppressWarnings("rawtypes")
    private void drawHText(List par1List, int par2, int par3, FontRenderer font)
    {
        if (!par1List.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = par1List.iterator();

            while (iterator.hasNext())
            {
                String s = (String) iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;

            if (par1List.size() > 1)
            {
                k1 += 2 + (par1List.size() - 1) * 10;
            }

            if (i1 + k > width)
            {
                i1 -= 28 + k;
            }

            if (j1 + k1 + 6 > height)
            {
                j1 = height - k1 - 6;
            }

            zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            int l1 = -267386864;
            drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < par1List.size(); ++k2)
            {
                String s1 = (String) par1List.get(k2);
                font.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0)
                {
                    j1 += 2;
                }

                j1 += 10;
            }

            zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void drawItemTooltip(ItemStack itemStack, int par2, int par3)
    {
        List list = itemStack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k)
        {
            if (k == 0)
            {
                list.set(k, "\u00a7" + Integer.toHexString(itemStack.getRarity().rarityColor) + (String) list.get(k));
            }
            else
            {
                list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
            }
        }

        FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
        drawHText(list, par2, par3, font == null ? fontRenderer : font);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        ((GuiButton) buttonList.get(2)).drawButton = popUpState;
        ((GuiButton) buttonList.get(3)).drawButton = popUpState;

        if (popUpState)
        {
            drawBackground(0);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(Reference.GUI_LOCATION + "portalModifierNetwork.png");
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

            fontRenderer.drawString(Localization.localizeString("tile.dialDevice.name"), guiLeft + xSize / 2 - fontRenderer.getStringWidth(Localization.localizeString("tile.dialDevice.name")) / 2, 20, 0xFFCCCCCC);
            fontRenderer.drawString(Strings.Glyphs.toString(), guiLeft + 7, guiTop + 6, 0xFF444444);
            fontRenderer.drawString(Strings.UniqueIdentifier.toString(), guiLeft + 7, guiTop + 74, 0xFF444444);

            ((GuiButton) buttonList.get(2)).drawButton(mc, par1, par2);
            ((GuiButton) buttonList.get(3)).drawButton(mc, par1, par2);

            x = par1;
            y = par2;

            for (int i = stackList.size() - 1; i >= 0; i--)
            {
                stackList.get(i).drawElement(guiLeft + 8 + i * 18, guiTop + 86, x, y, fontRenderer, itemRenderer, mc.renderEngine);
            }

            for (int i = 8; i >= 0; i--)
            {
                elementList.get(i).drawElement(guiLeft, guiTop + 3, x, y, fontRenderer, itemRenderer, mc.renderEngine);
            }

            for (int i = 17; i >= 9; i--)
            {
                elementList.get(i).drawElement(guiLeft, guiTop + 3, x, y, fontRenderer, itemRenderer, mc.renderEngine);
            }

            for (int i = elementList.size() - 1; i >= 18; i--)
            {
                elementList.get(i).drawElement(guiLeft, guiTop + 3, x, y, fontRenderer, itemRenderer, mc.renderEngine);
            }
        }
        else
        {
            super.drawScreen(par1, par2, par3);
            nameField.drawTextBox();
        }
    }

    public void elementClicked(GuiGlyphElement element, int button)
    {
        if (!element.isStack)
        {
            if (button == 0)
            {
                if (elementCount < 9)
                {
                    stackList.add(new GuiGlyphElement(0, 0, element.value, element.itemStack, this, true));
                    element.stackSize++;

                    elementCount++;
                }
            }
            else if (button == 1)
            {
                if (elementCount > 0)
                {
                    for (int i = stackList.size() - 1; i >= 0; i--)
                    {
                        if (stackList.get(i).itemStack.isItemEqual(element.itemStack))
                        {
                            stackList.remove(i);

                            if (element.stackSize >= 1)
                            {
                                element.stackSize--;
                            }

                            elementCount--;
                            return;
                        }
                    }
                }
            }
        }
        else
        {
            if (button == 1)
            {
                stackList.remove(element);

                for (int i = elementList.size() - 1; i >= 0; i--)
                {
                    if (elementList.get(i).itemStack.isItemEqual(element.itemStack))
                    {
                        if (elementList.get(i).stackSize >= 1)
                        {
                            elementList.get(i).stackSize--;
                        }

                        elementCount--;
                        return;
                    }
                }

                elementCount--;
                return;
            }
        }
    }

    @Override
    public void extendedSlotChanged(GuiExtendedItemSlot slot)
    {
        ItemStack stack = slot.getItemStack();

        if (stack.itemID == EnhancedPortals.proxy.blockDummyPortal.blockID)
        {
            stack = new ItemStack(Item.dyePowder, 1, stack.getItemDamage());
        }

        PortalTexture text = Textures.getTextureFromItemStack(stack);

        if (text != null)
        {
            texture = text.getID();
        }
        else
        {
            texture = "C:5";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();

        buttonList.add(new GuiButton(1, guiLeft + xSize - 82, guiTop + ySize + 5, 75, 20, Strings.Save.toString()));
        buttonList.add(new GuiButton(2, guiLeft + 7, guiTop + ySize + 5, 75, 20, Strings.Cancel.toString()));

        buttonList.add(new GuiButton(3, guiLeft + 7 + 3, guiTop + ySize - 55, 75, 20, Strings.Cancel.toString()));
        buttonList.add(new GuiButton(4, guiLeft + 87 + 3, guiTop + ySize - 55, 75, 20, Strings.Save.toString()));

        nameField = new GuiTextField(fontRenderer, guiLeft, guiTop, xSize, 16);

        if (name == null)
        {
            nameField.setText("Name");
        }
        else
        {
            nameField.setText(name);
        }

        List<String> strList = new ArrayList<String>();
        strList.add(Strings.Thickness.toString());
        strList.add("");
        buttonList.add(new GuiItemStackButton(13, guiLeft + xSize - 42, guiTop + 40, new ItemStack(Block.portal), true, strList, true));
        ((GuiItemStackButton) buttonList.get(4)).displayString = "" + (thickness + 1);
        ((GuiItemStackButton) buttonList.get(4)).hoverText.set(1, EnumChatFormatting.GRAY + (thickness == 0 ? Strings.Normal.toString() : thickness == 1 ? Strings.Thick.toString() : thickness == 2 ? Strings.Thicker.toString() : Strings.FullBlock.toString()));
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        if (!popUpState)
        {
            if (par1 != ';')
            {
                if (nameField.textboxKeyTyped(par1, par2))
                {
                    name = nameField.getText();
                    return;
                }
            }
        }
        else
        {
            if (par2 == 1 || par2 == mc.gameSettings.keyBindInventory.keyCode)
            {
                popUpState = false;
                return;
            }
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (!popUpState)
        {
            if (isShiftKeyDown() && getSlotAtPosition(par1, par2) != null)
            {
                extendedSlots.get(0).setSlot(getSlotAtPosition(par1, par2).getStack());
            }

            super.mouseClicked(par1, par2, par3);
            nameField.mouseClicked(par1, par2, par3);

            if (isPointInRegion(7, 40 + 21, xSize - 15, 17, par1, par2))
            {
                popUpState = true;
            }
        }
        else
        {
            if (isPointInRegion(10, ySize - 55, 75, ySize - 35, par1, par2))
            {
                popUpState = false;
            }
            else if (isPointInRegion(10 + 80, ySize - 55, 75, ySize - 35, par1, par2))
            {
                String str = "";

                for (int i = 0; i < stackList.size(); i++)
                {
                    str = str + Reference.glyphSeperator + stackList.get(i).value;
                }

                if (str.length() > 0)
                {
                    str = str.substring(Reference.glyphSeperator.length());
                }

                network = str;
                popUpState = false;
            }

            for (int i = 0; i < elementList.size(); i++)
            {
                elementList.get(i).handleMouseClick(guiLeft, guiTop, par1, par2, par3);
            }

            for (int i = 0; i < stackList.size(); i++)
            {
                stackList.get(i).handleMouseClick(guiLeft + 8 + i * 18, guiTop + 83, par1, par2, par3);
            }
        }
    }

    @Override
    public void updateScreen()
    {
        if (!popUpState)
        {
            super.updateScreen();

            nameField.updateCursorCounter();
        }
    }
}
