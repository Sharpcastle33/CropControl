package com.programmerdan.minecraft.cropcontrol.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.programmerdan.minecraft.cropcontrol.CropControl;

/**
 * This configures a specific drop
 * 
 * @author ProgrammerDan
 *
 */
public class DropConfig {
	private static ConcurrentHashMap<String, DropConfig> configs = new ConcurrentHashMap<String, DropConfig>();
	private static DropConfig nonce = new DropConfig();
	
	private ConfigurationSection dropSection = null;
	
	private List<ItemStack> template = null;
	
	private double chance = 0.0d;
	private int lowMult = 1;
	private int highMult = 1;
	
	@SuppressWarnings("unchecked")
	public static DropConfig byIdent(String ident) {
		DropConfig config = configs.get(ident);
		if (config == null) {
			config = new DropConfig();
			
			config.dropSection = CropControl.getPlugin().getConfig().getConfigurationSection("drops." + ident);
			if (config.dropSection == null) {
				CropControl.getPlugin().warning("Request for drop called {0}, drop is not configured.", ident);
				return nonce;
			}
			
			try {
				config.template = (List<ItemStack>) config.dropSection.getList("template");
			} catch (Exception e) {
				CropControl.getPlugin().warning("Drop attempts to configure a template but template is broken.", e);
				return nonce;
			}
			
			config.chance = config.dropSection.getDouble("base.chance", 0.0d);
			config.lowMult = config.dropSection.getInt("base.min", 1);
			config.highMult = config.dropSection.getInt("base.max", 1);
			
			configs.put(ident, config);
		}
		return config;
	}
	
	public static void reload() {
		configs.clear();
	}
	
	public double getChance() {
		return chance;
	}
	
	public int getMultiplierMin() {
		return lowMult;
	}
	
	public int getMutliplierMax() {
		return highMult;
	}
	
	public List<ItemStack> getDrops(int multiplier) {
		if (template == null || template.size() == 0) {
			return new ArrayList<ItemStack>();
		}
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>(template.size());
		for (ItemStack i : template) {
			int newSize = i.getAmount() * multiplier;
			if (newSize <= 0) { 
				continue;
			}
			ItemStack clone = null; 
			while (newSize > i.getMaxStackSize()) {
				clone = i.clone();
				clone.setAmount(i.getMaxStackSize());
				newSize -= i.getMaxStackSize();
				drops.add(clone);
			}
			clone = i.clone();
			clone.setAmount(newSize);
			drops.add(clone);
		}
		return drops;
	}
}
