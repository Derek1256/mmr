package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.api.FluidIngredient;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireFluid(FluidStack stack, int x, int y) {
    return addRequirement(new RequirementFluid(IOType.INPUT, new FluidIngredient(stack.getFluid()), stack.getAmount(), new PositionedRequirement(x, y)));
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack, int x, int y) {
    return addRequirement(new RequirementFluid(IOType.OUTPUT, new FluidIngredient(stack.getFluid()), stack.getAmount(), new PositionedRequirement(x, y)));
  }
  default MachineRecipeBuilderJS requireFluid(FluidStack stack) {
    return requireFluid(stack, 0, 0);
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack) {
    return produceFluid(stack, 0, 0);
  }
}
