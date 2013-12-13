package com.immibis.modjam3;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="ChickenBones", name="The Chicken Bones Mod", version="1.0")
//@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class Modjam3Mod {
	
	public static BlockIChest blockIChest; 
	
	private Configuration cfg;
	private int blockid_ichest = -1;
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent evt) {
		cfg = new Configuration(evt.getSuggestedConfigurationFile());
		cfg.load();
		
		if(cfg.getCategory(Configuration.CATEGORY_BLOCK).keySet().contains("ichest"))
			blockid_ichest = cfg.getBlock("ichest", 2345).getInt(2345);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		if(blockid_ichest == -1)
			blockid_ichest = cfg.getBlock("ichest", 2345).getInt(2345);
			
		blockIChest = new BlockIChest(blockid_ichest);
		GameRegistry.registerBlock(blockIChest, "ichest");
		
		if(cfg.hasChanged())
			cfg.save();
		
		
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void onLivingDrops(LivingDropsEvent evt) {
		if(evt.entity instanceof EntityChicken) {
			evt.drops.add(new EntityItem(evt.entity.worldObj, evt.entity.posX, evt.entity.posY, evt.entity.posZ, new ItemStack(Item.bone)));
		}
	}
}
