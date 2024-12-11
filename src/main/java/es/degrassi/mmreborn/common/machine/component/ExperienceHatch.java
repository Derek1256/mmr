package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;

public abstract class ExperienceHatch extends MachineComponent<IExperienceHandler> {
  public ExperienceHatch(IOType ioType) {
    super(ioType);
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_EXPERIENCE.get();
  }
}