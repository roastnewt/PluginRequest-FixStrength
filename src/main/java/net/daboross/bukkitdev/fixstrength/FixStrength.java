/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.fixstrength;

import java.util.Map;
import java.util.Random;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author daboross
 */
public class FixStrength extends JavaPlugin implements Listener {

	private static final double CONSTANT_TIMES = 10;
	private static final double CONSTANT_DIVIDE = 13;
	private static final double CONSTANT_PLUS = 1.5;
	private static final double CONSTANT_PLUS_2 = 0.5;

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("FixStrength doesn't know about the command /" + cmd);
		return true;
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent evt) {
		if (!evt.isCancelled()) {
			if (evt.getDamager() instanceof Player) {
				double addition = 0;
				double multiplication = 1;
				Player player = (Player) evt.getDamager();
				for (PotionEffect potionEffect : player.getActivePotionEffects()) {
					if (potionEffect.getType().getName().equalsIgnoreCase("INCREASE_DAMAGE")) {
						multiplication *= CONSTANT_TIMES / (CONSTANT_DIVIDE * potionEffect.getAmplifier());
						addition += CONSTANT_PLUS + (CONSTANT_PLUS_2 * (potionEffect.getAmplifier() + 1));
					}
				}
				for (Map.Entry<Enchantment, Integer> enchant : player.getItemInHand().getEnchantments().entrySet()) {
					if (enchant.getKey() == Enchantment.DAMAGE_ALL) {
						multiplication *= 1 / (enchant.getValue());
						for (int i = 0; i < enchant.getValue(); i++) {
							addition += getRandomEnchant();
						}
					}
				}
				evt.setDamage((evt.getDamage() * multiplication) + addition);
			}
		}
	}

	private double getRandomEnchant() {
		return 0.5 + (Math.random() * 0.75);
	}
}
