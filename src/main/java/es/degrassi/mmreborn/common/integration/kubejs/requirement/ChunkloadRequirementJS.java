package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.common.crafting.requirement.RequirementChunkload;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;

public interface ChunkloadRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS chunkload(Integer radius) {
    if (radius < 1)
      return error("Chunkload radius can no be less than 1: \"{}\"", radius);
    return this.addRequirement(new RequirementChunkload(radius));
  }
}
