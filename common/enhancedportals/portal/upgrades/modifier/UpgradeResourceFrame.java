package enhancedportals.portal.upgrades.modifier;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import enhancedportals.EnhancedPortals;
import enhancedportals.lib.Localization;
import enhancedportals.lib.Settings;
import enhancedportals.lib.Strings;
import enhancedportals.portal.upgrades.Upgrade;

public class UpgradeResourceFrame extends Upgrade
{
    public UpgradeResourceFrame()
    {
        super();
    }

    @Override
    public ItemStack getDisplayItemStack()
    {
        return new ItemStack(Block.blockDiamond);
    }

    @Override
    public ItemStack getItemStack()
    {
        return new ItemStack(EnhancedPortals.proxy.itemPortalModifierUpgrade, 1, getUpgradeID());
    }

    @Override
    public String getName()
    {
        return "resourceFrame";
    }

    @Override
    public List<String> getText(boolean includeTitle)
    {
        List<String> list = new ArrayList<String>();

        if (includeTitle)
        {
            list.add(EnumChatFormatting.AQUA + Localization.localizeString("item." + Localization.PortalModifierUpgrade_Name + "." + getName() + ".name"));
        }

        list.add(EnumChatFormatting.GRAY + Localization.localizeString("upgrade.blocks.text"));
        list.add(EnumChatFormatting.GRAY + Localization.localizeString("upgrade.blocks.textA"));

        if (Settings.ResourceFrameUpgrade.isEmpty())
        {
            list.add(EnumChatFormatting.DARK_AQUA + Localization.localizeString(Block.blockIron.getUnlocalizedName() + ".name"));
            list.add(EnumChatFormatting.DARK_AQUA + Localization.localizeString(Block.blockGold.getUnlocalizedName() + ".name"));
            list.add(EnumChatFormatting.DARK_AQUA + Localization.localizeString(Block.blockDiamond.getUnlocalizedName() + ".name"));
            list.add(EnumChatFormatting.DARK_AQUA + Localization.localizeString(Block.blockEmerald.getUnlocalizedName() + ".name"));
        }
        else
        {
            for (int id : Settings.ResourceFrameUpgrade)
            {
                list.add(EnumChatFormatting.DARK_AQUA + new ItemStack(id, 1, 0).getDisplayName());
            }
        }

        if (includeTitle)
        {
            list.add(Strings.RemoveUpgrade.toString());
        }

        return list;
    }

    @Override
    public boolean onActivated(net.minecraft.tileentity.TileEntity tileEntity)
    {
        return true;
    }

    @Override
    public boolean onDeactivated(TileEntity tileEntity)
    {
        return true;
    }
}
