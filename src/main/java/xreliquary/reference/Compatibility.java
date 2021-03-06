package xreliquary.reference;

import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;

public class Compatibility {
	
	private static Map<String, Boolean> loadedMods = new HashMap<>();

	public static final String DEPENDENCIES = "after:" + MOD_ID.BAUBLES + ";after:" + MOD_ID.ENDERIO + ";after:" + MOD_ID.TINKERS_CONSTRUCT;

	public static class MOD_ID {
		public static final String JEI = "jei";
		public static final String JER = "jeresources";
		public static final String BAUBLES = "baubles";
		public static final String BOTANIA = "botania";
		//public static final String THAUMCRAFT = "Thaumcraft";
		public static final String WAILA = "waila";
		public static final String HWYLA = "hwyla";
		public static final String TINKERS_CONSTRUCT = "tconstruct";
		public static final String ENDERIO = "enderio";
	}
	
	public static boolean isLoaded(String modName) {
		if (!loadedMods.containsKey(modName)) {
			loadedMods.put(modName, Loader.isModLoaded(modName));
		}
		return loadedMods.get(modName);
	}
}
