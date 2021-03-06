package me.mrCookieSlime.QuestWorld.quests.missions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.mrCookieSlime.QuestWorld.quests.MissionType;
import me.mrCookieSlime.QuestWorld.quests.QuestMission;
import me.mrCookieSlime.QuestWorld.utils.SubmissionItemResolver;

public class KillNamedMission extends MissionType {
	public KillNamedMission() {
		super("KILL_NAMED_MOB", true, true, false, SubmissionType.ENTITY, "Kill %s", new MaterialData(Material.GOLD_SWORD));
	}
	
	@Override
	public ItemStack getQuestItem(QuestMission qm) {
		return SubmissionItemResolver.mobEgg(qm.getEntity());
	}
}
