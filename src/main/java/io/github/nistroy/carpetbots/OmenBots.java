package io.github.nistroy.carpetbots;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Bots activés par /omenbot. En mémoire seulement : un bot déconnecté (tué, /kill, arrêt du serveur) est oublié, il
 * faut le réactiver après l'avoir refait apparaître.
 */
public final class OmenBots {
	private static final Set<UUID> ENABLED = new HashSet<>();

	private OmenBots() {
	}

	public static void enable(UUID bot) {
		ENABLED.add(bot);
	}

	public static void disable(UUID bot) {
		ENABLED.remove(bot);
	}

	public static boolean isEnabled(UUID bot) {
		return ENABLED.contains(bot);
	}

	public static void tick(MinecraftServer server) {
		Iterator<UUID> bots = ENABLED.iterator();
		while (bots.hasNext()) {
			ServerPlayer bot = server.getPlayerList().getPlayer(bots.next());
			if (bot == null) {
				bots.remove();
			} else {
				OmenDrinker.tryDrink(bot);
			}
		}
	}
}
