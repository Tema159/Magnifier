package me.tema159.magnifier;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Config {
    public static void setup() {
        Plugin plugin = Magnifier.getPlugin();
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        if (!config.isSet("item.craft.ingredients")) {
            config.addDefault("item.craft.ingredients.I", "IRON_INGOT");
            config.addDefault("item.craft.ingredients.G", "GRAY_STAINED_GLASS_PANE");
            config.addDefault("item.craft.ingredients.S", "STICK");
        }
        config.options().copyDefaults(true);
        Objects.requireNonNull(config.getDefaults());

        for (String path : Arrays.asList("radius", "item.custom_model_data"))
            if (!(config.get(path) instanceof Integer))
                config.set(path, config.getDefaults().get(path));

        String path = "item.name";
        Object object = config.get(path);
        config.set(path, translateColors(object));

        path = "item.lore";
        object = config.get(path);
        List<String> list;
        if (object instanceof List<?>) {
            list = new ArrayList<>();
            for (Object obj : (List<?>) object)
                list.add(translateColors(obj));
            list = Collections.unmodifiableList(list);
        } else list = Collections.singletonList(translateColors(object));
        config.set(path, list);

        path = "item.material";
        Magnifier.setMaterial(config);
        if (Magnifier.MATERIAL == null) {
            config.set(path, config.getDefaults().get(path));
            Magnifier.setMaterial(config);
        }

        try {
            Magnifier.addRecipe(config);
        } catch (Exception e) {
            for (String str : Arrays.asList("item.craft.shape", "item.craft.ingredients"))
                config.set(str, config.getDefaults().get(str));
            Magnifier.addRecipe(config);
        }

        Magnifier.getPlugin().saveConfig();
    }

    private static String translateColors(Object value) {
        return ChatColor.translateAlternateColorCodes('&', String.valueOf(value));
    }

    public static Object getValue(String path) {
        return Magnifier.getPlugin().getConfig().get(path);
    }
}
