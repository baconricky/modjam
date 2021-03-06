package com.immibis.modjam3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemChickenRender implements IItemRenderer {
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch(type) {
		case ENTITY:
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
		case INVENTORY:
			return true;
		case FIRST_PERSON_MAP:
			return false;
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		switch(helper) {
		case BLOCK_3D:
		case ENTITY_BOBBING:
		case ENTITY_ROTATION:
		case EQUIPPED_BLOCK:
		case INVENTORY_BLOCK:
			return true;
		}
		return false;
	}
	
	public static RenderChicken rc = new RenderChicken(new ModelChicken(), 0.3f);
	public static EntityChicken chicken = new EntityChicken(null) {
		public float getBrightness(float par1) {return 1;}
	};

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch(type) {
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			GL11.glScalef(1.7f, 1.7f, 1.7f);
			break;
		case INVENTORY:
			GL11.glTranslatef(-0f, -0.5f, -0f);
			GL11.glScalef(1.5f, 1.5f, 1.5f);
			break;
		case ENTITY:
			GL11.glTranslatef(-0f, -0.5f, -0f);
			//GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			GL11.glScalef(1.2f, 1.2f, 1.2f);
			break;
		default: return;
		}
		RenderManager.instance.renderEngine = Minecraft.getMinecraft().getTextureManager();
		rc.setRenderManager(RenderManager.instance);
		setYaw(0);
		rc.doRender(chicken, 0, 0, 0, 0, 0);
		
		GL11.glColor3f(1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.enableStandardItemLighting();
	}

	public static void setYaw(double yaw) {
		chicken.rotationYaw = chicken.rotationYawHead =
				chicken.prevRotationYaw = chicken.prevRotationYawHead =
				chicken.renderYawOffset = chicken.prevRenderYawOffset = (float) yaw;
	}

	public static void renderChicken(double x, double y, double z) {
		rc.setRenderManager(RenderManager.instance);
		rc.doRender(chicken, x, y, z, 0, 0);
	}

}
