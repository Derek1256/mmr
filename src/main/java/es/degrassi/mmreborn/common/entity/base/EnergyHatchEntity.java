package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.entity.EnergyInputHatchEntity;
import es.degrassi.mmreborn.common.entity.EnergyOutputHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.network.server.component.SUpdateEnergyComponentPacket;
import es.degrassi.mmreborn.common.network.server.component.SUpdateFluidComponentPacket;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import es.degrassi.mmreborn.common.util.MiscUtils;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class EnergyHatchEntity extends ColorableMachineComponentEntity implements IEnergyStorage, IEnergyHandler, MachineComponentEntity {

  protected long energy = 0;
  protected EnergyHatchSize size;

//    private GTEnergyContainer energyContainer;

  public EnergyHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public EnergyHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, EnergyHatchSize size, IOType ioType) {
    super(type, pos, state);
    this.size = size;
//    this.energyContainer = new GTEnergyContainer(this, ioType);
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (!canReceive()) {
      return 0;
    }
    int insertable = this.energy + maxReceive > this.size.maxEnergy ? convertDownEnergy(this.size.maxEnergy - this.energy) : maxReceive;
    insertable = Math.min(insertable, convertDownEnergy(size.transferLimit));
    if (!simulate) {
      this.energy = MiscUtils.clamp(this.energy + insertable, 0, this.size.maxEnergy);
      markForUpdate();
      if (getLevel() instanceof ServerLevel l)
        PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SUpdateEnergyComponentPacket(this.energy, getBlockPos()));
    }
    return insertable;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (!canExtract()) {
      return 0;
    }
    int extractable = this.energy - maxExtract < 0 ? convertDownEnergy(this.energy) : maxExtract;
    extractable = Math.min(extractable, convertDownEnergy(size.transferLimit));
    if (!simulate) {
      this.energy = MiscUtils.clamp(this.energy - extractable, 0, this.size.maxEnergy);
      markForUpdate();
      if (getLevel() instanceof ServerLevel l)
        PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SUpdateEnergyComponentPacket(this.energy, getBlockPos()));
    }
    return extractable;
  }

  @Override
  public int getEnergyStored() {
    return convertDownEnergy(this.energy);
  }

  @Override
  public int getMaxEnergyStored() {
    return convertDownEnergy(this.size.maxEnergy);
  }

  @Override
  public boolean canExtract() {
    return this instanceof EnergyOutputHatchEntity;
  }

  @Override
  public boolean canReceive() {
    return this instanceof EnergyInputHatchEntity;
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);

    Tag energyTag = compound.get("energy");
    if (energyTag instanceof NumericTag) {
      this.energy = ((NumericTag) energyTag).getAsLong();
    }
    this.size = EnergyHatchSize.value(compound.getString("hatchSize").toUpperCase(Locale.ROOT));
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);

    compound.putLong("energy", this.energy);
    compound.putString("hatchSize", this.size.getSerializedName());
  }

  protected int convertDownEnergy(long energy) {
    return energy >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy;
  }

  //MM stuff

  public EnergyHatchSize getTier() {
    return size;
  }

  @Override
  public long getCurrentEnergy() {
    return this.energy;
  }

  @Override
  public void setCurrentEnergy(long energy) {
    this.energy = MiscUtils.clamp(energy, 0, getMaxEnergy());

    if (getLevel() instanceof ServerLevel l)
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SUpdateEnergyComponentPacket(this.energy, getBlockPos()));
    markForUpdate();
  }

  @Override
  public long getMaxEnergy() {
    return this.size.maxEnergy;
  }

}
