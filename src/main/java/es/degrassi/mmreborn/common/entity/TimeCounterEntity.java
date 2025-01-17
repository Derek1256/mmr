package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementWeather;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.IntRange;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TimeCounterEntity extends ColorableMachineComponentEntity implements MachineComponentEntity {
  public TimeCounterEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.TIME_COUNTER.get(), pos, blockState);
  }

  @Override
  public @Nullable MachineComponent provideComponent() {
    return new MachineComponent<>(IOType.INPUT) {
      @Override
      public ComponentType getComponentType() {
        return ComponentRegistration.COMPONENT_TIME.get();
      }

      @Override
      public IntRange getContainerProvider() {
        return null;
      }
    };
  }
}
