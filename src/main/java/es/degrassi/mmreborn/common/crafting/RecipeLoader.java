package es.degrassi.mmreborn.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecipeLoader {

  private static final Gson GSON = new GsonBuilder()
//            .registerTypeHierarchyAdapter(MachineRecipe.MachineRecipeContainer.class, new MachineRecipe.Deserializer())
//            .registerTypeHierarchyAdapter(ComponentRequirement.class, new MachineRecipe.ComponentDeserializer())
//            .registerTypeHierarchyAdapter(RecipeAdapterAccessor.class, new RecipeAdapterAccessor.Deserializer())
//            .registerTypeHierarchyAdapter(RecipeModifier.class, new RecipeModifier.Deserializer())
//            .registerTypeHierarchyAdapter(RecipeRunnableCommand.class, new RecipeRunnableCommand.Deserializer())
    .create();

  //    public static List<RecipeAdapterAccessor> recipeAdapters = new LinkedList<>();
  private static Map<String, Exception> failedAttempts = new HashMap<>();
  public static String currentlyReadingPath = null;

  public static Map<FileType, List<File>> discoverDirectory(File directory) {
    Map<FileType, List<File>> candidates = new HashMap<>();
    for (FileType type : FileType.values()) {
      candidates.put(type, Lists.newLinkedList());
    }
    LinkedList<File> directories = Lists.newLinkedList();
    directories.add(directory);
    while (!directories.isEmpty()) {
      File dir = directories.remove(0);
      for (File f : dir.listFiles()) {
        if (f.isDirectory()) {
          directories.addLast(f);
        } else {
          if (FileType.ADAPTER.accepts(f.getName())) {
            candidates.get(FileType.ADAPTER).add(f);
          } else if (FileType.RECIPE.accepts(f.getName())) {
            candidates.get(FileType.RECIPE).add(f);
          }
        }
      }
    }
    return candidates;
  }

  public static List<MachineRecipe> loadRecipes(List<File> recipeCandidates, List<PreparedRecipe> preparedRecipes) {
//        recipeAdapters.clear();

    List<MachineRecipe> loadedRecipes = Lists.newArrayList();
    for (File f : recipeCandidates) {
      currentlyReadingPath = f.getPath();
      try (InputStreamReader isr = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
//                MachineRecipe.MachineRecipeContainer container = JsonUtils.fromJson(GSON, isr, MachineRecipe.MachineRecipeContainer.class);
//                loadedRecipes.addAll(container.getRecipes());
      } catch (Exception exc) {
        failedAttempts.put(f.getPath(), exc);
      } finally {
        currentlyReadingPath = null;
      }
    }
    for (PreparedRecipe recipe : preparedRecipes) {
      loadedRecipes.add(convertPreparedRecipe(recipe));
    }
    return loadedRecipes;
  }

  public static List<MachineRecipe> loadAdapterRecipes(List<File> adapterCandidates) {
    List<MachineRecipe> loadedRecipes = Lists.newArrayList();
    for (File f : adapterCandidates) {
      try (InputStreamReader isr = new InputStreamReader(new FileInputStream(f))) {
//                RecipeAdapterAccessor accessor = JsonUtils.fromJson(GSON, isr, RecipeAdapterAccessor.class);
//                Collection<MachineRecipe> recipes = accessor.loadRecipesForAdapter();
//                if(recipes.isEmpty()) {
//                    ModularMachinery.log.warn("Adapter with name " + accessor.getAdapterKey().toString() + " didn't provide have any recipes!");
//                } else {
//                    loadedRecipes.addAll(recipes);
//                }
//                recipeAdapters.add(accessor);
      } catch (Exception exc) {
        failedAttempts.put(f.getPath(), exc);
      }
    }
    return loadedRecipes;
  }

  private static MachineRecipe convertPreparedRecipe(PreparedRecipe recipe) {
    MachineRecipe mr = new MachineRecipe(recipe.getId(), recipe.getAssociatedMachineName(),
      recipe.getTotalProcessingTickTime(), recipe.getPriority(), recipe.voidPerTickFailure());
    recipe.getComponents().forEach(mr::addRequirement);
    return mr;
  }

  public static Map<String, Exception> captureFailedAttempts() {
    Map<String, Exception> failed = failedAttempts;
    failedAttempts = new HashMap<>();
    return failed;
  }

  public enum FileType {

    ADAPTER,
    RECIPE;

    public boolean accepts(String fileName) {
      switch (this) {
        case ADAPTER:
          return fileName.endsWith(".adapter.json");
        case RECIPE:
        default:
          return fileName.endsWith(".json");
      }
    }

  }

}
