package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class EnergyHatchContainer extends ContainerBase<EnergyHatchEntity> {
  public static void open(ServerPlayer player, EnergyHatchEntity machine) {
    player.openMenu(new MenuProvider() {
      @Override
      public @NotNull Component getDisplayName() {
        return Component.translatable("modular_machinery_reborn.gui.title.energy_hatch");
      }

      @Override
      public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EnergyHatchContainer(id, inv, machine);
      }
    }, buf -> buf.writeBlockPos(machine.getBlockPos()));
  }

  public EnergyHatchContainer(int id, Inventory playerInv, EnergyHatchEntity entity) {
    super(entity, playerInv.player, ContainerRegistration.ENERGY_HATCH.get(), id);
  }

  public EnergyHatchContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
    this(id, inv, ModularMachineryRebornClient.getClientSideEnergyHatchEntity(buffer.readBlockPos()));
  }
}
