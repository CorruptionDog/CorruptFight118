package net.corruptdog.cdm.world.item.items;

import net.corruptdog.cdm.main.CDmoveset;
import net.corruptdog.cdm.world.item.CorruptItemTier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.item.WeaponItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class GreatTachiItem extends WeaponItem {
    @OnlyIn(Dist.CLIENT)
    private List<Component> tooltipExpand;

    public GreatTachiItem(Item.Properties build) {
        super(CorruptItemTier.KATANA, 0, -2.0F, build);
        if (EpicFightMod.isPhysicalClient()) {
            this.tooltipExpand = new ArrayList<Component>();
            this.tooltipExpand.add(new TextComponent(""));
            this.tooltipExpand.add(new TranslatableComponent("item." + CDmoveset.MOD_ID + ".great_tachi.tooltip"));
        }
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return toRepair.getItem() == Items.IRON_BLOCK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        for (Component txtComp : tooltipExpand) {
            tooltip.add(txtComp);
        }
    }
}