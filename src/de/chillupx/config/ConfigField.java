package de.chillupx.config;

public enum ConfigField {

	MAX_HEIGHT("bounds.max-height"),
	RADIUS("bounds.radius"),
	ENABLE_EFFECTS("enable-effects"),
	CYCLE_TIME("cycle-time"),
	
	WORLDGUARD_REGIONS("worldguard-regions"),
	
	BIG_TREE_GETTER("bounds.big-tree-getter.enabled"),
	BIG_TREE_GETTER_WIDTH("bounds.big-tree-getter.extra-width"),
	
	TOOL_USE_DETAILED("tools.use-detailed"),
	TOOL_DAMAGE_ALL_LEAVES("tools.general.damage-cut-leaves"),
	TOOL_DAMAGE_ALL_LOG("tools.general.damage-cut-log"),
	TOOL_DAMAGE_DIAMOND_LEAVES("tools.detailed.diamond-axe.damage-cut-leaves"),
	TOOL_DAMAGE_DIAMOND_LOG("tools.detailed.diamond-axe.damage-cut-log"),
	TOOL_DAMAGE_GOLD_LEAVES("tools.detailed.gold-axe.damage-cut-leaves"),
	TOOL_DAMAGE_GOLD_LOG("tools.detailed.gold-axe.damage-cut-log"),
	TOOL_DAMAGE_IRON_LEAVES("tools.detailed.iron-axe.damage-cut-leaves"),
	TOOL_DAMAGE_IRON_LOG("tools.detailed.iron-axe.damage-cut-log"),
	TOOL_DAMAGE_STONE_LEAVES("tools.detailed.stone-axe.damage-cut-leaves"),
	TOOL_DAMAGE_STONE_LOG("tools.detailed.stone-axe.damage-cut-log"),
	TOOL_DAMAGE_WOOD_LEAVES("tools.detailed.wood-axe.damage-cut-leaves"),
	TOOL_DAMAGE_WOOD_LOG("tools.detailed.woodd-axe.damage-cut-log"),
	
	ECON_ENABLED("economy.enabled"),
	ECON_CREATION_PRICE("economy.creation-price"),
	ECON_REFUND_AMOUNT("economy.destroy-refund"),
	ECON_PRICE_PER_BLOCK_LOG("economy.price-per-block.log"),
	ECON_PRICE_PER_BLOCK_LEAVES("economy.price-per-block.leaves");
	
	private String path;
	ConfigField(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path; 
	}
}