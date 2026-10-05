package io.github.nistroy.carpetbots;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class CarpetBots implements ModInitializer {
	// 1 vérification par seconde suffit : boire prend 1,6 s et un raid dure plusieurs minutes.
	private static final int CHECK_INTERVAL_TICKS = 20;

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				OmenBotCommand.register(dispatcher));
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % CHECK_INTERVAL_TICKS == 0) {
				OmenBots.tick(server);
			}
		});
	}
}
