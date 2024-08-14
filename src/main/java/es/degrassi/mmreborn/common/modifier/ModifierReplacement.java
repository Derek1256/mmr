package es.degrassi.mmreborn.common.modifier;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import es.degrassi.mmreborn.common.machine.MachineLoader;
import es.degrassi.mmreborn.common.util.BlockArray;
import es.degrassi.mmreborn.common.util.MiscUtils;
import es.degrassi.mmreborn.common.util.nbt.NBTJsonDeserializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

public class ModifierReplacement {

  private final BlockArray.BlockInformation info;
  private final List<RecipeModifier> modifier;
  private final List<String> description;

  public ModifierReplacement(BlockArray.BlockInformation info, List<RecipeModifier> modifier, String description) {
    this.info = info;
    this.modifier = modifier;
    this.description = description.isEmpty() ? Lists.newArrayList() : MiscUtils.splitStringBy(description, "\n");
  }

  public BlockArray.BlockInformation getBlockInformation() {
    return info;
  }

  public List<RecipeModifier> getModifiers() {
    return Collections.unmodifiableList(modifier);
  }

  public List<String> getDescriptionLines() {
    return description;
  }

  public static class Deserializer implements JsonDeserializer<ModifierReplacement> {

    @Override
    public ModifierReplacement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      CompoundTag match = null;
      JsonObject part = json.getAsJsonObject();
      if(part.has("nbt")) {
        JsonElement je = part.get("nbt");
        if(!je.isJsonObject()) {
          throw new JsonParseException("The ComponentType 'nbt' expects a json compound that defines the NBT tag to match the tileentity's nbt against!");
        }
        String jsonStr = je.toString();
        try {
          match = NBTJsonDeserializer.deserialize(jsonStr);
        } catch (NBTJsonDeserializer.NBTException exc) {
          throw new JsonParseException("Error trying to parse NBTTag! Rethrowing exception...", exc);
        }
      }
      if(!part.has("elements")) {
        throw new JsonParseException("Modifier-tag contained no element!");
      }
      BlockArray.BlockInformation blockInfo;
      JsonElement partElement = part.get("elements");
      if(partElement.isJsonPrimitive() && partElement.getAsJsonPrimitive().isString()) {
        String strDesc = partElement.getAsString();
        blockInfo = MachineLoader.variableContext.get(strDesc);
        if(blockInfo == null) {
          blockInfo = new BlockArray.BlockInformation(Lists.newArrayList(BlockArray.BlockInformation.getDescriptor(partElement.getAsString())));
        } else {
          blockInfo = blockInfo.copy(); //Avoid NBT-definitions bleed into variable context
        }
        if(match != null) {
          blockInfo.setMatchingTag(match);
        }
      } else if(partElement.isJsonArray()) {
        JsonArray elementArray = partElement.getAsJsonArray();
        List<BlockArray.BlockStateDescriptor> descriptors = Lists.newArrayList();
        for (int xx = 0; xx < elementArray.size(); xx++) {
          JsonElement p = elementArray.get(xx);
          if(!p.isJsonPrimitive() || !p.getAsJsonPrimitive().isString()) {
            throw new JsonParseException("Part elements of 'elements' have to be blockstate descriptions!");
          }
          String prim = p.getAsString();
          BlockArray.BlockInformation descr = MachineLoader.variableContext.get(prim);
          if(descr != null) {
            descriptors.addAll(descr.copy().matchingStates);
          } else {
            descriptors.add(BlockArray.BlockInformation.getDescriptor(prim));
          }
        }
        if(descriptors.isEmpty()) {
          throw new JsonParseException("'elements' array didn't contain any blockstate descriptors!");
        }
        blockInfo = new BlockArray.BlockInformation(descriptors);
        if(match != null) {
          blockInfo.setMatchingTag(match);
        }
      } else {
        throw new JsonParseException("'elements' has to either be a blockstate description, variable or array of blockstate descriptions!");
      }

      if (!part.has("modifier") && !part.has("modifiers")) {
        throw new JsonParseException("'modifiers' tag not found!");
      }
      JsonElement elementModifiers = part.has("modifier") ? part.get("modifier") : part.get("modifiers");
      List<RecipeModifier> modifiers = Lists.newArrayList();
      if (elementModifiers.isJsonObject()) {
        modifiers.add(context.deserialize(elementModifiers, RecipeModifier.class));

      } else if (elementModifiers.isJsonArray()) {
        JsonArray modifierArray = elementModifiers.getAsJsonArray();
        for (JsonElement modifierElement : modifierArray) {
          if (!modifierElement.isJsonObject()) {
            throw new JsonParseException("'modifiers' array needs to consist of json objects, each a modifier object!");
          }
          modifiers.add(context.deserialize(modifierElement, RecipeModifier.class));
        }
      } else {
        throw new JsonParseException("'modifiers' tag needs to be either a single modifier object or an array of modifier objects!");
      }


      String description = part.has("description") ? part.getAsJsonPrimitive("description").getAsString() : "";
      return new ModifierReplacement(blockInfo, modifiers, description);
    }

  }
}
