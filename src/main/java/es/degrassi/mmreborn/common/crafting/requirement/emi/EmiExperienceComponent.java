package es.degrassi.mmreborn.common.crafting.requirement.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.widget.WidgetHolder;
import es.degrassi.experiencelib.util.ExperienceUtils;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementExperience;
import es.degrassi.mmreborn.common.integration.emi.recipe.MMREmiRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EmiExperienceComponent extends EmiComponent<Long, RequirementExperience> {
  public EmiExperienceComponent(RequirementExperience requirement) {
    super(requirement, 0, 0);
  }

  @Override
  public int getWidth() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public List<Long> ingredients() {
    return Lists.newArrayList(requirement.getRequired());
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.render(guiGraphics, mouseX, mouseY);
  }

  @Override
  public List<Component> getTooltip() {
    return super.getTooltip();
  }

  @Override
  public void addWidgets(WidgetHolder widgets, MMREmiRecipe recipe) {
    super.addWidgets(widgets, recipe);
    String literal = String.format("%s XP", ExperienceUtils.format(requirement.getRequired()));
    String level =  ExperienceUtils.format(ExperienceUtils.getLevelFromXp(requirement.getRequired()));
    recipe.textsToRender.add(
        Component.translatable("mmr.gui.element.experience.tooltip." + requirement.getActionType().getSerializedName(),
            literal,
            Component.translatable("mmr.gui.element.experience.level", level)
        )
    );
  }
}