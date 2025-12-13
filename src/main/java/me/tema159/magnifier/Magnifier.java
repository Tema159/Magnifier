package me.tema159.magnifier;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Magnifier extends JavaPlugin implements Listener {
    public static final Map<Player, Boolean> cache = new HashMap<>();
    public static NamespacedKey key;
    public static Material MATERIAL;

    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        key = new NamespacedKey(this, "magnifier");
        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    public static void setMaterial(Configuration cfg) {
        Magnifier.MATERIAL = Material.matchMaterial(String.valueOf(cfg.get("item.material")));
    }

    static void addRecipe(Configuration cfg) {
        ConfigurationSection section = cfg.getConfigurationSection("item.craft.ingredients");
        if (section == null)
            throw new NullPointerException();

        ShapedRecipe recipe = new ShapedRecipe(key, getItem());
        
        List<String> shape = cfg.getStringList("item.craft.shape");
        shape.replaceAll(s -> s.replace("_", " "));
        recipe.shape(shape.get(0), shape.get(1), shape.get(2));

        for (String key : section.getKeys(false)) {
            String value = String.valueOf(section.get(key));
            Material mat = Material.matchMaterial(value);
            if (mat != null)
                recipe.setIngredient(key.charAt(0), mat);
        }

        Bukkit.addRecipe(recipe);
    }

    @SuppressWarnings("unchecked")
    public static ItemStack getItem() {
        ItemStack item = new ItemStack(MATERIAL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");
            meta.setDisplayName(String.valueOf(Config.getValue("item.name")));
            meta.setLore((List<String>) Config.getValue("item.lore"));
            meta.setCustomModelData((Integer) Config.getValue("item.custom_model_data"));
            item.setItemMeta(meta);
        }

        return item;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
