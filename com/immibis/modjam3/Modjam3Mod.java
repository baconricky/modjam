package com.immibis.modjam3;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.immibis.modjam3.ItemEggBomb.EntityEggBomb;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.registry.TickRegistry;


@Mod(modid="ChickenBones", name="The Chicken Bones Mod: You can pick up chicks!", version="1.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class Modjam3Mod implements IGuiHandler, ICraftingHandler, ITickHandler, IWorldGenerator {
	
	@Instance("ChickenBones")
	public static Modjam3Mod instance;
	
	public static int GUI_ICHEST = 0;
	
	/** Disables some things that are not allowed for modjam */
	public static final boolean MODJAM = true;
	
	public static final boolean CHICKENS_RIDE_STUFF = false;
	
	public static final double EGG_BOMB_CHANCE = 0.1;
	
	public static BlockIChest blockIChest;
	public static BlockChickenOre blockChickenOre;
	public static Block blockChickenBlock;
	public static Block blockChickenBlockBlock;
	public static Block blockChickenPipe;
	public static Block blockChickNT;
	public static Item itemChickenBone;
	public static Item itemEggStaff;
	public static ItemChicken itemChicken;
	public static Item itemChickenCore;
	public static Item itemChickenIngot;
	public static ItemChickenStaff itemChickenStaff;
	public static Item itemChickaxe;
	public static ItemFood itemChickenNugget;
	public static ItemChickenWing itemChickenWing;
	public static Item[] itemRecords;
	public static Item itemWRCBE;
	public static Item itemChickenBeak;
	public static Item itemLightningStaff;
	public static Item itemEggBomb;
	
	public static EnumToolMaterial toolMaterialChicken = EnumHelper.addToolMaterial("IMMIBIS_MJ3", 1, 500, 16.0f, 0.0f, 35);
	
	@SidedProxy(clientSide="com.immibis.modjam3.ClientProxy", serverSide="com.immibis.modjam3.Proxy")
	public static Proxy proxy;
	
	private Configuration cfg;
	private int blockid_ichest = -1;
	private int blockid_chore = -1;
	private int blockid_cblock = -1;
	private int blockid_cblockblock = -1;
	private int blockid_chickenpipe = -1;
	private int blockid_chicknt = -1;
	private int itemid_chickenBone = -1;
	private int itemid_eggstaff = -1;
	private int itemid_chicken = -1;
	private int itemid_chickencore = -1;
	private int itemid_chickenstaff = -1;
	private int itemid_chickeningot = -1;
	private int itemid_chickaxe = -1;
	private int itemid_cnugget = -1;
	private int itemid_cwing = -1;
	private int itemid_record1 = -1;
	private int itemid_record2 = -1;
	private int itemid_record3 = -1;
	private int itemid_wrcbe = -1;
	private int itemid_chomper = -1;
	private int itemid_lstaff = -1;
	private int itemid_eggbomb = -1;
	
	private int preinit_block(String name) {
		if(cfg.getCategory(Configuration.CATEGORY_BLOCK).keySet().contains(name))
			return cfg.getBlock(name, 2345).getInt(2345);
		else
			return -1;
	}
	
	private int preinit_item(String name) {
		if(cfg.getCategory(Configuration.CATEGORY_ITEM).keySet().contains(name))
			return cfg.getItem(name, 23456).getInt(23456);
		else
			return -1;
	}
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent evt) {
		cfg = new Configuration(evt.getSuggestedConfigurationFile());
		cfg.load();
		
		blockid_ichest = preinit_block("ichest");
		blockid_chore = preinit_block("chickenOre");
		blockid_cblock = preinit_block("chickenBlock");
		blockid_cblockblock = preinit_block("chickenBlockBlock");
		blockid_chickenpipe = preinit_block("chickenPipe");
		blockid_chicknt = preinit_block("chicknt");
		itemid_chickenBone = preinit_item("chickenbone");
		itemid_eggstaff = preinit_item("eggstaff");
		itemid_chicken = preinit_item("chicken");
		itemid_chickencore = preinit_item("chickencore");
		itemid_chickenstaff = preinit_item("chickenstaff");
		itemid_chickeningot = preinit_item("chickeningot");
		itemid_chickaxe = preinit_item("chickaxe");
		itemid_cnugget = preinit_item("chickenNugget");
		itemid_cwing = preinit_item("chickenWing");
		itemid_record1 = preinit_item("record1");
		itemid_record2 = preinit_item("record2");
		itemid_record3 = preinit_item("record3");
		itemid_wrcbe = preinit_item("wrcbe");
		itemid_chomper = preinit_item("chomper");
		itemid_lstaff = preinit_item("lstaff");
		itemid_eggbomb = preinit_item("eggbomb");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		if(blockid_ichest == -1)
			blockid_ichest = cfg.getBlock("ichest", 2345).getInt(2345);
		if(blockid_chore == -1)
			blockid_chore = cfg.getBlock("chickenOre", 2345).getInt(2345);
		if(blockid_cblock == -1)
			blockid_cblock = cfg.getBlock("chickenBlock", 2345).getInt(2345);
		if(blockid_cblockblock == -1)
			blockid_cblockblock = cfg.getBlock("chickenBlockBlock", 2345).getInt(2345);
		if(blockid_chickenpipe == -1)
			blockid_chickenpipe = cfg.getBlock("chickenPipe", 2345).getInt(2345);
		if(blockid_chicknt == -1)
			blockid_chicknt = cfg.getBlock("chicknt", 2345).getInt(2345);
		if(itemid_chickenBone == -1)
			itemid_chickenBone = cfg.getItem("chickenbone", 23456).getInt(23456);
		if(itemid_eggstaff == -1)
			itemid_eggstaff = cfg.getItem("eggstaff", 23457).getInt(23457);
		if(itemid_chicken == -1)
			itemid_chicken = cfg.getItem("chicken", 23457).getInt(23457);
		if(itemid_chickencore == -1)
			itemid_chickencore = cfg.getItem("chickencore", 23457).getInt(23457);
		if(itemid_chickenstaff == -1)
			itemid_chickenstaff = cfg.getItem("chickenstaff", 23457).getInt(23457);
		if(itemid_chickeningot == -1)
			itemid_chickeningot = cfg.getItem("chickeningot", 23457).getInt(23457);
		if(itemid_chickaxe == -1)
			itemid_chickaxe = cfg.getItem("chickaxe", 23456).getInt(23456);
		if(itemid_cnugget == -1)
			itemid_cnugget = cfg.getItem("chickenNugget", 23456).getInt(23456);
		if(itemid_cwing == -1)
			itemid_cwing = cfg.getItem("chickenWing", 23456).getInt(23456);
		if(itemid_record1 == -1)
			itemid_record1 = cfg.getItem("record1", 23456).getInt(23456);
		if(itemid_record2 == -1)
			itemid_record2 = cfg.getItem("record2", 23456).getInt(23456);
		if(itemid_record3 == -1)
			itemid_record3 = cfg.getItem("record3", 23456).getInt(23456);
		if(itemid_wrcbe == -1)
			itemid_wrcbe = cfg.getItem("wrcbe", 23456).getInt(23456);
		if(itemid_chomper == -1)
			itemid_chomper = cfg.getItem("chomper", 23456).getInt(23456);
		if(itemid_lstaff == -1)
			itemid_lstaff = cfg.getItem("lstaff", 23456).getInt(23456);
		if(itemid_eggbomb == -1)
			itemid_eggbomb = cfg.getItem("eggbomb", 23456).getInt(23456);
			
		if(cfg.hasChanged())
			cfg.save();
		
		
		
		
		blockIChest = new BlockIChest(blockid_ichest);
		blockChickenOre = new BlockChickenOre(blockid_chore);
		blockChickenBlock = new BlockChickenBlock(blockid_cblock);
		blockChickenBlockBlock = new BlockChickenBlock(blockid_cblockblock).setUnlocalizedName("immibis_modjam3.chickenblockblock").setTextureName("immibis_modjam3:compressed_chicken_block");
		blockChickenPipe = new BlockChickenPipe(blockid_chickenpipe);
		blockChickNT = new BlockChickNT(blockid_chicknt);
		itemEggStaff = new ItemEggStaff(itemid_eggstaff);
		itemChicken = new ItemChicken(itemid_chicken);
		itemChickenStaff = new ItemChickenStaff(itemid_chickenstaff);
		itemChickaxe = new ItemChickaxe(itemid_chickaxe);
		itemChickenWing = new ItemChickenWing(itemid_cwing);
		itemWRCBE = new ItemWirelessRedstone(itemid_wrcbe);
		itemChickenBeak = new ItemChickenBeak(itemid_chomper);
		itemLightningStaff = new ItemLightningStaff(itemid_lstaff);
		itemEggBomb = new ItemEggBomb(itemid_eggbomb);
		
		if(!MODJAM) {
			itemRecords = new Item[] {
				new ItemRecord(itemid_record1, "immibis_modjam3:oli_chang_chicken_techno") {
					public String getRecordTitle() {return "Oli Chang - Chicken Techno";}
					public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
						super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
						if(par1ItemStack.getItemDamage() == 3)
							par3List.add("Crafts a random record.");
					}
				}.setUnlocalizedName("record").setTextureName("immibis_modjam3:record1"),
				
				new ItemRecord(itemid_record2, "immibis_modjam3:dj_bewan_chicken_song_full") {
					public String getRecordTitle() {return "DJ Bewan - Chicken Song";}
				}.setUnlocalizedName("record").setTextureName("immibis_modjam3:record2"),
				
				new ItemRecord(itemid_record3, "immibis_modjam3:dj_bewan_chicken_song_short") {
					public String getRecordTitle() {return "DJ Bewan - Chicken Song (Short version)";}
				}.setUnlocalizedName("record").setTextureName("immibis_modjam3:record3"),
			};
		}
		
		itemChickenCore = new Item(itemid_chickencore).setCreativeTab(CreativeTabs.tabMaterials).setTextureName("immibis_modjam3:chickencore").setUnlocalizedName("immibis_modjam3.chickencore");
		itemChickenIngot = new Item(itemid_chickeningot).setCreativeTab(CreativeTabs.tabMaterials).setTextureName("immibis_modjam3:chickeningot").setUnlocalizedName("immibis_modjam3.chickeningot");
		itemChickenNugget = (ItemFood)new ItemFood(itemid_cnugget, 1, 0, false).setCreativeTab(CreativeTabs.tabMaterials).setTextureName("immibis_modjam3:chickennugget").setUnlocalizedName("immibis_modjam3.chickennugget");
		
		itemChickenBone = new Item(itemid_chickenBone);
		itemChickenBone.setCreativeTab(CreativeTabs.tabMaterials);
		itemChickenBone.setTextureName("immibis_modjam3:chickenbone");
		itemChickenBone.setUnlocalizedName("immibis_modjam3.chickenbone");
		
		GameRegistry.registerItem(itemChickenBone, "chickenbone");
		GameRegistry.registerItem(itemEggStaff, "eggstaff");
		GameRegistry.registerItem(itemChicken, "chicken");
		GameRegistry.registerItem(itemChickenCore, "chickencore");
		GameRegistry.registerItem(itemChickenIngot, "chickeningot");
		GameRegistry.registerItem(itemChickenNugget, "chickennugget");
		GameRegistry.registerItem(itemChickenStaff, "chickenstaff");
		GameRegistry.registerItem(itemChickaxe, "chickaxe");
		GameRegistry.registerItem(itemChickenWing, "chickenWing");
		GameRegistry.registerItem(itemLightningStaff, "lstaff");
		GameRegistry.registerItem(itemEggBomb, "eggbomb");
		if(!MODJAM) {
			GameRegistry.registerItem(itemRecords[0], "record1");
			GameRegistry.registerItem(itemRecords[1], "record2");
			GameRegistry.registerItem(itemRecords[2], "record3");
		}
		GameRegistry.registerItem(itemWRCBE, "wrcbe");
		GameRegistry.registerItem(itemChickenBeak, "chickenbeak");
		GameRegistry.registerBlock(blockIChest, "ichest");
		GameRegistry.registerBlock(blockChickenOre, "chickenore");
		GameRegistry.registerBlock(blockChickenBlock, "chickenblock");
		GameRegistry.registerBlock(blockChickenBlockBlock, "chickenblockblock");
		GameRegistry.registerBlock(blockChickenPipe, "chickenpipe");
		GameRegistry.registerBlock(blockChickNT, "chicknt");
		
		GameRegistry.registerTileEntity(TileEntityIChest.class, "immibis_modjam3.ichest");
		
		GameRegistry.addRecipe(new ItemStack(blockIChest), "###", "#C#", "###", 'C', Block.enderChest, '#', itemChickenBone);
		GameRegistry.addRecipe(new ItemStack(itemEggStaff), "  #", " / ", "/  ", '#', Item.egg, '/', itemChickenBone);
		GameRegistry.addRecipe(new ItemStack(itemChickenBone), "#", '#', itemChicken);
		GameRegistry.addRecipe(new ItemStack(itemChickenCore), "###", "#O#", "###", '#', itemChickenBone, 'O', Item.chickenRaw);
		GameRegistry.addRecipe(new ItemStack(itemChickenStaff), "  #", " / ", "/  ", '#', itemChickenCore, '/', itemChickenBone);
		GameRegistry.addRecipe(new ItemStack(itemChickaxe), "###", " | ", " | ", '#', itemChickenIngot, '|', itemChickenBone);
		GameRegistry.addRecipe(new ItemStack(itemChickenNugget, 9), "#", '#', itemChickenIngot);
		GameRegistry.addRecipe(new ItemStack(itemChickenIngot), "###", "###", "###", '#', itemChickenNugget);
		GameRegistry.addRecipe(new ItemStack(itemChickenIngot, 9), "#", '#', blockChickenBlock);
		GameRegistry.addRecipe(new ItemStack(blockChickenBlock), "###", "###", "###", '#', itemChickenIngot);
		GameRegistry.addRecipe(new ItemStack(blockChickenBlock, 9), "#", '#', blockChickenBlockBlock);
		GameRegistry.addRecipe(new ItemStack(blockChickenBlockBlock), "###", "###", "###", '#', blockChickenBlock);
		GameRegistry.addShapelessRecipe(new ItemStack(blockChickenOre), itemChicken, Block.stone);
		GameRegistry.addRecipe(new ItemStack(itemChickenWing), "/#.", "/#.", "/  ", '.', itemChickenNugget, '#', blockChickenBlockBlock, '/', itemChickenBone);
		if(!MODJAM)
			GameRegistry.addRecipe(new ItemStack(itemRecords[0], 1, 3), "###", "#O#", "###", 'O', itemChickenCore, '#', Block.music); // XXX for modjam
		GameRegistry.addShapelessRecipe(new ItemStack(itemWRCBE), itemChickenBone, Item.redstone);
		GameRegistry.addRecipe(new ItemStack(itemChickenBeak), "X#X", "#O#", "X#X", '#', itemChickenNugget, 'X', itemChickenCore, 'O', blockChickenBlockBlock);
		GameRegistry.addRecipe(new ItemStack(itemLightningStaff), "RR#", "R/R", "/RR", '#', itemChicken, '/', itemChickenBone, 'R', Block.blockRedstone);
		GameRegistry.addRecipe(new ItemStack(blockChickNT), "#O#", "O#O", "#O#", 'O', Item.gunpowder, '#', itemChicken);
		FurnaceRecipes.smelting().addSmelting(itemChicken.itemID, new ItemStack(itemChickenIngot), 1.5f);
		
		EntityRegistry.registerModEntity(EntityAngryChicken.class, "angryChicken", 0, this, 100, 5, true);
		EntityRegistry.registerModEntity(EntityPipedItem.class, "pipedItem", 1, this, 30, 1, true); // TODO change to 20 or so
		EntityRegistry.registerModEntity(EntityBossChicken.class, "bossChicken", 2, this, 200, 5, true);
		EntityRegistry.registerModEntity(ItemEggBomb.EntityEggBomb.class, "eggbomb", 3, this, 50, 3, true);
		EntityRegistry.registerModEntity(BlockChickNT.EntityChickNTPrimed.class, "chicknt", 4, this, 50, 20, false);
		
		GameRegistry.registerCraftingHandler(this);
		NetworkRegistry.instance().registerGuiHandler(this, this);
		TickRegistry.registerTickHandler(this, Side.SERVER);
		TickRegistry.registerTickHandler(new PlayerTickHandler(), Side.SERVER);
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerWorldGenerator(this);
		
		chickenOreGen = new WorldGenMinable(blockChickenOre.blockID, 8);
		
		proxy.init();
	}
	
	@ForgeSubscribe
	public void onLivingDrops(LivingDropsEvent evt) {
		if(evt.entity instanceof EntityChicken) {
			if(evt.entity instanceof EntityAngryChicken) {
				evt.drops.clear();
				return;
			}
			
			if(evt.entity.worldObj.rand.nextInt(3) == 0)
				evt.drops.add(new EntityItem(evt.entity.worldObj, evt.entity.posX, evt.entity.posY, evt.entity.posZ, new ItemStack(itemChickenBone, 1)));
			
			Entity source = evt.source.getSourceOfDamage();
			
			if(source instanceof EntityPlayer && evt.entity.worldObj.rand.nextInt(7) == 0 && !evt.source.getDamageType().equals("explosion.player"))
				for(int k = 0; k < 30; k++)
					evt.entity.worldObj.spawnEntityInWorld(new EntityAngryChicken(evt.entity.worldObj, (EntityPlayer)source));
		}
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == GUI_ICHEST)
			return new GuiChest(player.inventory, ((TileEntityIChest)world.getBlockTileEntity(x, y, z)));
		return null;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == GUI_ICHEST)
			return new ContainerChest(player.inventory, ((TileEntityIChest)world.getBlockTileEntity(x, y, z)));
		return null;
	}
	
	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {
		if(item.itemID == blockIChest.blockID && !player.worldObj.isRemote)
			player.worldObj.playSoundAtEntity(player, "immibis_modjam3:ichest.doppler", 1, 1);
		if(!MODJAM && item.itemID == itemRecords[0].itemID && !player.worldObj.isRemote) {
			item.itemID = itemRecords[player.worldObj.rand.nextInt(itemRecords.length)].itemID;
			item.setItemDamage(0);
		}
	}
	
	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
		//if(item.itemID == itemChickenIngot.itemID && !player.worldObj.isRemote)
			//player.worldObj.playSoundAtEntity(player, "immibis_modjam3:ichest.doppler", 1, 1);
	}
	
	@ForgeSubscribe
	public void onToolBreak(PlayerDestroyItemEvent evt) {
		if(evt.original.getItem() == itemChickaxe && !evt.entity.worldObj.isRemote) {
			EntityChicken ch = new EntityChicken(evt.entity.worldObj);
			ch.setPosition(evt.entity.posX, evt.entity.posY, evt.entity.posZ);
			evt.entity.worldObj.spawnEntityInWorld(ch);
			evt.entity.worldObj.playSoundAtEntity(evt.entity, "immibis_modjam3:ichest.close", 0.5F, 1.0f);
		}
	}
	
	private Queue<Chunk> toGenerate = new LinkedList<Chunk>();
	
	private WorldGenMinable chickenOreGen;
	@ForgeSubscribe
	public void onChunkLoad(ChunkDataEvent.Load evt) {
		if(!evt.getData().getBoolean("ImmibisMJ3Gen")) {
			toGenerate.add(evt.getChunk());
		}
	}
	
	@ForgeSubscribe
	public void onChunkSave(ChunkDataEvent.Save evt) {
		evt.getData().setBoolean("ImmibisMJ3Gen", true);
	}
	
	@Override
	public String getLabel() {
		return "Chicken Bones";
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		Chunk c;
		while((c = toGenerate.poll()) != null) {
			Random r = new Random(c.worldObj.getSeed() ^ 0xC1C2C3C4C5C6C7C8L ^ ((long)c.xPosition << 16) ^ (long)c.zPosition);
			generate(r, c.xPosition, c.zPosition, c.worldObj, c.worldObj.getChunkProvider(), c.worldObj.getChunkProvider());
		}
	}
	
	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}
	
	@Override
	public void generate(Random r, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		for(int k = 0; k < 6; k++) {
			int x = r.nextInt(16) + (chunkX << 4), y = r.nextInt(128), z = r.nextInt(16) + (chunkZ << 4);
			//System.out.println(x+" "+y+" "+z);
			chickenOreGen.generate(world, r, x, y, z);
		}
	}
	
	@ForgeSubscribe
	public void makeChickensRideStuff(LivingSpawnEvent.SpecialSpawn evt) {
		if(CHICKENS_RIDE_STUFF && evt.world.rand.nextInt(8) == 0) {
			EntityChicken ec = new EntityChicken(evt.world);
			ec.setPosition(evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ);
			evt.world.spawnEntityInWorld(ec);
			ec.mountEntity(evt.entityLiving);
		}
	}
	
	@ForgeSubscribe
	public void chickenLightning(EntityStruckByLightningEvent evt) {
		if(evt.entity instanceof EntityChicken && !(evt.entity instanceof EntityAngryChicken) && !(evt.entity instanceof EntityBossChicken) && !evt.entity.worldObj.isRemote) {
			evt.entity.setDead();
			
			EntityBossChicken b = new EntityBossChicken(evt.entity.worldObj);
			b.setPosition(evt.entity.posX, evt.entity.posY, evt.entity.posZ);
			b.rotationPitch = b.prevRotationPitch = evt.entity.rotationPitch;
			b.rotationYawHead = b.prevRotationYawHead = ((EntityChicken)evt.entity).rotationYawHead;
			b.renderYawOffset = b.prevRenderYawOffset = ((EntityChicken)evt.entity).renderYawOffset;
			b.rotationYaw = b.prevRotationYaw = evt.entity.rotationYaw;
			evt.entity.worldObj.spawnEntityInWorld(b);
		}
	}
	
	private boolean droppingEgg;
	@ForgeSubscribe
	public void onEggSoundPlay(PlaySoundAtEntityEvent evt) {
		if(!evt.entity.worldObj.isRemote && evt.entity instanceof EntityChicken && evt.name.equals("mob.chicken.plop"))
			droppingEgg = true;
	}
	
	@ForgeSubscribe
	public void onEggDrop(EntityJoinWorldEvent evt) {
		if(evt.world.isRemote)
			return;
		
		if(evt.entity instanceof EntityItem && ((EntityItem)evt.entity).getEntityItem().itemID == Item.egg.itemID && droppingEgg && Math.random() < EGG_BOMB_CHANCE) {
			((EntityItem)evt.entity).setEntityItemStack(new ItemStack(itemEggBomb));
		}
		droppingEgg = false;
	}
	
	@ForgeSubscribe
	public void onPickUpEggBomb(EntityItemPickupEvent evt) {
		if(!evt.entity.worldObj.isRemote && evt.item.getEntityItem().getItem() == itemEggBomb) {
			evt.item.setDead();
			evt.setCanceled(true);
			ItemEggBomb.explode(evt.entity);
		}
	}
}
