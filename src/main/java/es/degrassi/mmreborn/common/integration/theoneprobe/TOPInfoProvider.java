package es.degrassi.mmreborn.common.integration.theoneprobe;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.util.Utils;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class TOPInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {
  @Override
  public Void apply(ITheOneProbe probe) {
    probe.registerProvider(this);
    return null;
  }

  @Override
  public ResourceLocation getID() {
    return ModularMachineryReborn.rl("controller_info_provider");
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData data) {
    BlockEntity tile = level.getBlockEntity(data.getPos());
    if (tile instanceof MachineControllerEntity controller) {
      showCraftingInfo(controller, info);
    }
  }

  private void showCraftingInfo(MachineControllerEntity tile, IProbeInfo info) {
    MachineControllerEntity.CraftingStatus status = tile.getCraftingStatus();
    MutableComponent message = Component.translatable(status.getUnlocMessage());
    switch (status.getStatus()) {
      case CRAFTING -> message.withStyle(ChatFormatting.GREEN);
      case NO_RECIPE -> message.withStyle(ChatFormatting.GOLD);
      case MISSING_STRUCTURE, FAILURE -> message.withStyle(ChatFormatting.RED);
    }
    ;
    info.mcText(message);
    if (tile.hasActiveRecipe()) {
      int ticks = tile.getRecipeTicks();
      int total = tile.getActiveRecipe().getRecipe().getRecipeTotalTickTime();
      float progress = (float) ticks / total;
      String ticksTotal = ticks + " / " + total;
      boolean seconds = false;
      if (total >= 20) {
        ticksTotal = Utils.decimalFormat(ticks / 20) + " / " + Utils.decimalFormat(total / 20) + "s";
        ticks /= 20;
        total /= 20;
        seconds = true;
      }
      info.progress(
          ticks,
          total,
          info.defaultProgressStyle()
              .suffix("/"
                  + total
                  + (seconds ? "s" : "")
                  + "("
                  + Utils.decimalFormatWithPercentage(progress * 100)
                  + ")"
              )
      );
    }
  }
}
