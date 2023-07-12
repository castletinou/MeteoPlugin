package fr.castletinou.meteoplugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandBoussole implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage("§3[MeteoPlugin]§r Gave §1§l[1]§r §4§lMeteo Compass§r to player.");

			ItemStack compass = new ItemStack(Material.COMPASS);
			ItemMeta cm = compass.getItemMeta();

			cm.setDisplayName("§4§lMeteo Compass");
			cm.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
			cm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			List<String> lore = new ArrayList<>();
			lore.add("§dl'item doit avoir un lore, et des couleurs.");
			cm.setLore(lore);
			compass.setItemMeta(cm);

			player.getInventory().addItem(compass);
			player.updateInventory();

			return true;
		}

		return false;
	}

}
