package io.github.nistroy.carpetbots;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Fait boire une fiole sinistre à un joueur quand rien ne s'y oppose. Boire pendant un raid ou avec un présage déjà
 * actif ferait monter le niveau du raid en cours ou gâcherait la fiole.
 */
public final class OmenDrinker {
	private OmenDrinker() {
	}

	/** @return vrai si le joueur a commencé à boire. */
	public static boolean tryDrink(ServerPlayer player) {
		if (!player.isAlive() || player.isUsingItem()) {
			return false;
		}
		if (player.hasEffect(MobEffects.BAD_OMEN) || player.hasEffect(MobEffects.RAID_OMEN)) {
			return false;
		}
		// Rayon vanilla de getRaidAt : 96 blocs autour du centre d'un raid actif.
		if (player.serverLevel().getRaidAt(player.blockPosition()) != null) {
			return false;
		}
		if (!moveBottleToOffhand(player)) {
			return false;
		}
		ItemStack bottle = player.getOffhandItem();
		player.gameMode.useItem(player, player.serverLevel(), bottle, InteractionHand.OFF_HAND);
		return player.isUsingItem();
	}

	// Main gauche : l'épée reste en main droite pour les attaques Carpet. Un objet déjà en main gauche (bouclier,
	// torche) n'est jamais déplacé.
	private static boolean moveBottleToOffhand(ServerPlayer player) {
		ItemStack offhand = player.getOffhandItem();
		if (offhand.is(Items.OMINOUS_BOTTLE)) {
			return true;
		}
		if (!offhand.isEmpty()) {
			return false;
		}
		Inventory inventory = player.getInventory();
		for (int slot = 0; slot < inventory.items.size(); slot++) {
			if (inventory.items.get(slot).is(Items.OMINOUS_BOTTLE)) {
				player.setItemInHand(InteractionHand.OFF_HAND, inventory.removeItemNoUpdate(slot));
				return true;
			}
		}
		return false;
	}
}
