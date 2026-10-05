package io.github.nistroy.carpetbots.test;

import io.github.nistroy.carpetbots.OmenBots;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;

public class OmenBotsGameTest implements FabricGameTest {
	@GameTest(template = EMPTY_STRUCTURE)
	public void enabledBotDrinksOnTick(GameTestHelper helper) {
		ServerPlayer bot = OmenDrinkerGameTest.botWithSwordAndBottle(helper);
		OmenBots.enable(bot.getUUID());

		try {
			OmenBots.tick(helper.getLevel().getServer());
			helper.assertTrue(bot.isUsingItem(), "un bot activé boit au tick");
		} finally {
			OmenBots.disable(bot.getUUID());
		}
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void disabledBotDoesNotDrink(GameTestHelper helper) {
		ServerPlayer bot = OmenDrinkerGameTest.botWithSwordAndBottle(helper);

		OmenBots.tick(helper.getLevel().getServer());

		helper.assertFalse(bot.isUsingItem(), "un bot non activé ne boit pas");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void forgetsDisconnectedBot(GameTestHelper helper) {
		ServerPlayer bot = OmenDrinkerGameTest.botWithSwordAndBottle(helper);
		OmenBots.enable(bot.getUUID());
		helper.getLevel().getServer().getPlayerList().remove(bot);

		OmenBots.tick(helper.getLevel().getServer());

		helper.assertFalse(OmenBots.isEnabled(bot.getUUID()), "un bot déconnecté est oublié");
		helper.succeed();
	}
}
