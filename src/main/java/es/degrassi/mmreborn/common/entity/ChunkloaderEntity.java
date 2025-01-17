package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.machine.component.Chunkload;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.Chunkloader;
import es.degrassi.mmreborn.common.util.ChunkloaderList;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ChunkloaderEntity extends BlockEntityRestrictedTick implements MachineComponentEntity {
  private final Chunkloader chunkloader;
  public ChunkloaderEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.CHUNKLOADER.get(), pos, blockState);
    chunkloader = new Chunkloader(this);
  }

  @Override
  public @Nullable Chunkload provideComponent() {
    return new Chunkload() {
      @Override
      public Chunkloader getContainerProvider() {
        return chunkloader;
      }
    };
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    nbt.putString("chunkloader", chunkloader.toString());
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    chunkloader.deserializeNBT(pRegistries, nbt.getCompound("chunkloader"));
  }

  @Override
  public void setLevel(Level level) {
    super.setLevel(level);
    ChunkloaderList.add(this);
  }

  @Override
  public void doRestrictedTick() {
    chunkloader.serverTick();
  }

  @Override
  public void onLoad() {
    super.onLoad();
    chunkloader.init();
  }

  @Getter
  private boolean unloaded = false;

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    this.unloaded = true;
  }

  @Override
  public void setRemoved() {
    chunkloader.onRemoved();
    super.setRemoved();
  }
}
