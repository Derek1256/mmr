package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.entity.EnergyOutputHatchEntity;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.util.Mods;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockEnergyOutputHatch extends BlockEnergyHatch {

//  public static final EnumProperty<EnergyHatchSize> BUS_TYPE = EnumProperty.create("size", EnergyHatchSize.class);
  public BlockEnergyOutputHatch(EnergyHatchSize type) {
    super(type);
  }

  @Override
  protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    switch (type) {
      case TINY ->        drops.add(ItemRegistration.ENERGY_OUTPUT_HATCH_TINY.get().getDefaultInstance());
      case SMALL ->       drops.add(ItemRegistration.ENERGY_OUTPUT_HATCH_SMALL.get().getDefaultInstance());
      case NORMAL ->      drops.add(ItemRegistration.ENERGY_OUTPUT_HATCH_NORMAL.get().getDefaultInstance());
      case REINFORCED ->  drops.add(ItemRegistration.ENERGY_OUTPUT_HATCH_REINFORCED.get().getDefaultInstance());
      case BIG ->         drops.add(ItemRegistration.ENERGY_OUTPUT_HATCH_BIG.get().getDefaultInstance());
      case HUGE ->        drops.add(ItemRegistration.ENERGY_OUTPUT_HATCH_HUGE.get().getDefaultInstance());
      case LUDICROUS ->   drops.add(ItemRegistration.ENERGY_OUTPUT_HATCH_LUDICROUS.get().getDefaultInstance());
      case ULTIMATE ->    drops.add(ItemRegistration.ENERGY_OUTPUT_HATCH_ULTIMATE.get().getDefaultInstance());
    }
    return drops;
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext pContext, List<Component> tooltip, TooltipFlag flag) {
      if (EnergyDisplayUtil.displayFETooltip) {
        tooltip.add(Component.translatable("tooltip.energyhatch.storage", type.maxEnergy).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.energyhatch.out.transfer", type.transferLimit).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
      }
      if (Mods.IC2.isPresent() && EnergyDisplayUtil.displayIC2EUTooltip) {
        tooltip.add(Component.translatable(
          "tooltip.energyhatch.ic2.out.voltage",
          Component.translatable(type.getUnlocalizedEnergyDescriptor()).withStyle(ChatFormatting.BLUE)
        ));
        tooltip.add(Component.translatable(
          "tooltip.energyhatch.ic2.out.transfer",
          Component.literal(String.valueOf(type.getIC2EnergyTransmission())).withStyle(ChatFormatting.BLUE),
          Component.translatable("tooltip.energyhatch.ic2.powerrate").withStyle(ChatFormatting.BLUE)
        ));
        tooltip.add(Component.empty());
      }
//            if (Mods.GREGTECH.isPresent() && EnergyDisplayUtil.displayGTEUTooltip) {
//                addGTTooltip(tooltip, size);
//                tooltip.add("");
//            }
  }


  @Override
  public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    if(level.isClientSide()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    BlockEntity te = level.getBlockEntity(pos);
    if(te instanceof EnergyHatchEntity) {
//                playerIn.openGui(ModularMachinery.MODID, CommonProxy.GuiType.ENERGY_INVENTORY.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
      return ItemInteractionResult.SUCCESS;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new EnergyOutputHatchEntity(pos, state, type);
  }

}