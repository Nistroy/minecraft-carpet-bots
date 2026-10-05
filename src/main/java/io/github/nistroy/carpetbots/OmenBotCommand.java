package io.github.nistroy.carpetbots;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** /omenbot (aide + bots activés) et /omenbot &lt;bot&gt; on|off — niveau 2, comme /player de Carpet par défaut. */
public final class OmenBotCommand {
	private static final String USAGE = "/omenbot <bot> on|off : le bot Carpet boit une fiole sinistre de son inventaire"
			+ " dès qu'aucun raid n'est actif à moins de 96 blocs (main gauche libre, épée gardée en main droite)."
			+ " À refaire après chaque /player <bot> spawn.";

	private OmenBotCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("omenbot")
				.requires(source -> source.hasPermission(2))
				.executes(OmenBotCommand::help)
				.then(Commands.argument("bot", EntityArgument.player())
						.then(Commands.literal("on").executes(context -> set(context, true)))
						.then(Commands.literal("off").executes(context -> set(context, false)))));
	}

	private static int help(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		List<String> names = OmenBots.enabled().stream()
				.map(bot -> source.getServer().getPlayerList().getPlayer(bot))
				.filter(Objects::nonNull)
				.map(bot -> bot.getGameProfile().getName())
				.sorted()
				.toList();
		source.sendSuccess(() -> Component.literal(USAGE), false);
		String enabled = names.isEmpty() ? "Aucun bot activé" : "Bots activés : " + String.join(", ", names);
		source.sendSuccess(() -> Component.literal(enabled), false);
		return names.size();
	}

	private static int set(CommandContext<CommandSourceStack> context, boolean enabled) throws CommandSyntaxException {
		ServerPlayer bot = EntityArgument.getPlayer(context, "bot");
		String name = bot.getGameProfile().getName();
		// Un vrai joueur ne doit jamais boire une fiole à son insu.
		if (!(bot instanceof EntityPlayerMPFake)) {
			context.getSource().sendFailure(Component.literal(name + " n'est pas un bot Carpet"));
			return 0;
		}
		if (enabled) {
			OmenBots.enable(bot.getUUID());
		} else {
			OmenBots.disable(bot.getUUID());
		}
		String state = enabled ? "boira une fiole sinistre dès qu'aucun raid n'est actif autour de lui"
				: "ne boit plus de fiole sinistre";
		context.getSource().sendSuccess(() -> Component.literal(name + " " + state), false);
		return 1;
	}
}
