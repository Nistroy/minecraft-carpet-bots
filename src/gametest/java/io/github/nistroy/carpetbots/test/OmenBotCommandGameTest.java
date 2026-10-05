package io.github.nistroy.carpetbots.test;

import io.github.nistroy.carpetbots.OmenBots;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class OmenBotCommandGameTest implements FabricGameTest {
	@GameTest(template = EMPTY_STRUCTURE)
	public void helpShowsUsageAndEnabledBots(GameTestHelper helper) {
		ServerPlayer bot = helper.makeMockServerPlayerInLevel();
		OmenBots.enable(bot.getUUID());

		String output;
		try {
			output = run(helper.getLevel().getServer(), "omenbot");
		} finally {
			OmenBots.disable(bot.getUUID());
		}

		helper.assertTrue(output.contains("/omenbot <bot> on|off"), "l'aide donne la syntaxe : " + output);
		helper.assertTrue(output.contains(bot.getGameProfile().getName()), "l'aide liste les bots activés : " + output);
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void helpSaysWhenNoBotIsEnabled(GameTestHelper helper) {
		String output = run(helper.getLevel().getServer(), "omenbot");

		helper.assertTrue(output.contains("Aucun bot activé"), "l'aide dit qu'aucun bot n'est activé : " + output);
		helper.succeed();
	}

	private static String run(MinecraftServer server, String command) {
		List<String> messages = new ArrayList<>();
		CommandSource capture = new CommandSource() {
			@Override
			public void sendSystemMessage(Component message) {
				messages.add(message.getString());
			}

			@Override
			public boolean acceptsSuccess() {
				return true;
			}

			@Override
			public boolean acceptsFailure() {
				return true;
			}

			@Override
			public boolean shouldInformAdmins() {
				return false;
			}
		};
		CommandSourceStack source = server.createCommandSourceStack().withSource(capture).withPermission(4);
		server.getCommands().performPrefixedCommand(source, command);
		return String.join("\n", messages);
	}
}
