package es.degrassi.mmreborn.client.screen;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.container.EnergyHatchContainer;
import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class EnergyHatchScreen extends AbstractContainerScreen<EnergyHatchContainer> {
  private final EnergyHatchEntity entity;

  public static final ResourceLocation TEXTURES_ENERGY_HATCH = ResourceLocation.fromNamespaceAndPath(ModularMachineryReborn.MODID, "textures/gui/guibar.png");

  public EnergyHatchScreen(EnergyHatchContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle);
    this.entity = pMenu.getEntity();
  }

  @Override
  public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
    // Neo: replicate the super method's implementation to insert the event between background and widgets
    super.render(guiGraphics, mouseX, mouseY, pPartialTick);
    renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    // render image background:
    guiGraphics.pose().pushPose();
    guiGraphics.setColor(1f, 1f, 1f, 1f);
    int i = (this.width - this.imageWidth) / 2;
    int j = (this.height - this.imageHeight) / 2;
    guiGraphics.blit(TEXTURES_ENERGY_HATCH, i, j, 0, 0, imageWidth, imageHeight);
    guiGraphics.pose().popPose();
    guiGraphics.pose().pushPose();
    float percFilled = ((float) entity.getCurrentEnergy()) / ((float) entity.getMaxEnergy());
    int pxFilled = Mth.ceil(percFilled * 61F);
    guiGraphics.blit(TEXTURES_ENERGY_HATCH, i + 15,  j + 10 + 61 - pxFilled, 196, 61 - pxFilled, 20, pxFilled);
    guiGraphics.pose().popPose();
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    super.renderTooltip(guiGraphics, x, y);

    int offsetX = (this.width - this.getXSize()) / 2;
    int offsetZ = (this.height - this.getYSize()) / 2;

    if(x >= 15 + offsetX && x <= 35 + offsetX && y >= 10 + offsetZ && y <= 71 + offsetZ) {
      long currentEnergy = EnergyDisplayUtil.type.formatEnergyForDisplay(entity.getCurrentEnergy());
      long maxEnergy = EnergyDisplayUtil.type.formatEnergyForDisplay(entity.getMaxEnergy());

      Component text = Component.translatable("tooltip.energyhatch.charge",
          String.valueOf(currentEnergy),
          String.valueOf(maxEnergy),
          Component.translatable(EnergyDisplayUtil.type.getUnlocalizedFormat()));

      Font font = Minecraft.getInstance().font;
      guiGraphics.renderTooltip(font, text, x, y);
    }
  }
}
