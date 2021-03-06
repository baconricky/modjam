package com.immibis.modjam3;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemLightningStaff extends Item {
	public ItemLightningStaff(int id) {
		super(id);
		
		setTextureName("immibis_modjam3:lstaff");
		setUnlocalizedName("immibis_modjam3.lstaff");
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
		setMaxDamage(3);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer ply) {
		
		final double RANGE = 100;
		
		Vec3 lookdir = ply.getLook(1);
		
		Vec3 src = par2World.getWorldVec3Pool().getVecFromPool(ply.posX+lookdir.xCoord, ply.posY+lookdir.yCoord+ply.getEyeHeight(), ply.posZ+lookdir.zCoord);
		Vec3 dst = src.addVector(lookdir.xCoord*RANGE, lookdir.yCoord*RANGE, lookdir.zCoord*RANGE);
		
		MovingObjectPosition rt = par2World.rayTraceBlocks_do_do(src, dst, true, true);
		if(rt == null)
			return par1ItemStack;
		
		if(!par2World.isRemote)
			par2World.addWeatherEffect(new EntityLightningBolt(par2World, rt.hitVec.xCoord, rt.hitVec.yCoord, rt.hitVec.zCoord));
		
		par1ItemStack.damageItem(1, ply);
		return par1ItemStack;
	}
}
