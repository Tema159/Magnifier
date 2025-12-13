package me.tema159.magnifier;

import me.tema159.magnifier.Commands.GiveItem;
import net.coreprotect.command.LookupCommand;
import net.coreprotect.config.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Listeners extends LookupCommand implements Listener {
    static final String PERM_LOOKUP = "coreprotect.lookup";
    static final String PERM_INSPECT = "coreprotect.inspect";
    static final String PERM_UNLOCK = "magnifier.commands.unlock";

    private static final String[] args = {"l",
            "radius:" + Config.getValue("radius"),
            "time:" + Config.getValue("time")};

    private static final Command command = new Command("co") {
        @Override
        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            return true;
        }
    };

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        JavaPlugin plugin = Magnifier.getPlugin();
        PluginManager manager = Bukkit.getPluginManager();

        if (!manager.isPluginEnabled("CoreProtect")) {
            plugin.getLogger().severe("CoreProtect plugin not installed!");
            manager.disablePlugin(plugin);
        }

        Config.setup();

        PluginCommand command = plugin.getCommand("magnifier-give");
        if (command != null)
            command.setExecutor(new GiveItem());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerInspect(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();

        boolean isMagnifier = item != null && item.getItemMeta() != null &&
                item.getItemMeta().getPersistentDataContainer().has(Magnifier.key, PersistentDataType.STRING);

        if (!event.hasBlock() && isMagnifier)
            LookupCommand.runCommand(p, command, p.hasPermission(PERM_LOOKUP), args);
        else if (event.hasBlock() && p.hasPermission(PERM_INSPECT) &&
                (isMagnifier || !p.hasPermission(PERM_UNLOCK) || Boolean.TRUE.equals(Magnifier.cache.get(p)))) {
            ConfigHandler.inspecting.put(p.getName(), isMagnifier);
            Magnifier.cache.put(p, isMagnifier);
        }
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        Magnifier.cache.remove(event.getPlayer());
    }

    @EventHandler
    void onPlayerCrafts(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();

        for (ItemStack item : inv.getMatrix()) {
            if (item == null) continue;
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(Magnifier.key, PersistentDataType.STRING)) {
                inv.setResult(new ItemStack(Material.AIR));
                break;
            }
        }
    }

    @EventHandler
    void onCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        if ((msg.startsWith("/co") || msg.startsWith("/coreprotect:co"))
                && !event.getPlayer().hasPermission(PERM_UNLOCK))
            event.setMessage("/co");
    }
}