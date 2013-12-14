package com.immibis.modjam3;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemChicken extends Item {
	public ItemChicken(int id) {
		super(id);
		
		setCreativeTab(CreativeTabs.tabMaterials);
		setUnlocalizedName("immibis_modjam3.chicken");
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		
		if(!par3World.isRemote) {
			par1ItemStack.stackSize--;
			EntityChicken ec = new EntityChicken(par3World);
			ec.setPosition(par4, par5, par6);
			par3World.spawnEntityInWorld(ec);
		}
		return true;
	}
}