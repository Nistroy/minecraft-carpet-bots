package io.github.nistroy.carpetbots.test;

import io.github.nistroy.carpetbots.OmenDrinker;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class OmenDrinkerGameTest implements FabricGameTest {
	private static final int BOTTLE_SLOT = 5;

	@GameTest(template = EMPTY_STRUCTURE)
	public void drinksBottleFromInventoryAndKeepsSword(GameTestHelper helper) {
		ServerPlayer bot = botWithSwordAndBottle(helper);

		helper.assertTrue(OmenDrinker.tryDrink(bot), "le bot devrait commencer à boire");
		helper.assertTrue(bot.isUsingItem() && bot.getUsedItemHand() == InteractionHand.OFF_HAND,
				"la fiole se boit de la main gauche");
		finishDrinking(bot);

		helper.assertTrue(bot.hasEffect(MobEffects.BAD_OMEN), "la fiole bue donne le Mauvais présage");
		helper.assertTrue(bot.getMainHandItem().is(Items.IRON_SWORD), "l'épée reste en main droite");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void skipsWithBadOmen(GameTestHelper helper) {
		ServerPlayer bot = botWithSwordAndBottle(helper);
		bot.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 1200));

		assertDoesNotDrink(helper, bot);
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void skipsWithRaidOmen(GameTestHelper helper) {
		ServerPlayer bot = botWithSwordAndBottle(helper);
		bot.addEffect(new MobEffectInstance(MobEffects.RAID_OMEN, 600));

		assertDoesNotDrink(helper, bot);
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE, batch = "raid")
	public void skipsDuringNearbyRaid(GameTestHelper helper) {
		ServerPlayer bot = botWithSwordAndBottle(helper);
		Raid raid = bot.serverLevel().getRaids().createOrExtendRaid(bot, bot.blockPosition());
		helper.assertTrue(raid != null && raid.isActive(), "raid de test non créé");

		try {
			assertDoesNotDrink(helper, bot);
		} finally {
			raid.stop();
		}
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void keepsOffhandItem(GameTestHelper helper) {
		ServerPlayer bot = botWithSwordAndBottle(helper);
		bot.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD));

		assertDoesNotDrink(helper, bot);
		helper.assertTrue(bot.getOffhandItem().is(Items.SHIELD), "le bouclier reste en main gauche");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void doesNothingWithoutBottle(GameTestHelper helper) {
		ServerPlayer bot = helper.makeMockServerPlayerInLevel();

		assertDoesNotDrink(helper, bot);
		helper.succeed();
	}

	static ServerPlayer botWithSwordAndBottle(GameTestHelper helper) {
		ServerPlayer bot = helper.makeMockServerPlayerInLevel();
		bot.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
		bot.getInventory().setItem(BOTTLE_SLOT, new ItemStack(Items.OMINOUS_BOTTLE, 3));
		return bot;
	}

	static void finishDrinking(ServerPlayer bot) {
		for (int tick = 0; tick < 100 && bot.isUsingItem(); tick++) {
			bot.doTick();
		}
	}

	private static void assertDoesNotDrink(GameTestHelper helper, ServerPlayer bot) {
		helper.assertFalse(OmenDrinker.tryDrink(bot), "le bot ne devrait pas boire");
		helper.assertFalse(bot.isUsingItem(), "le bot ne devrait rien utiliser");
	}
}
