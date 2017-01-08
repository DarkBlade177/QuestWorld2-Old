package me.mrCookieSlime.QuestWorld.quests.missions;

import me.mrCookieSlime.QuestWorld.quests.MissionType;
import me.mrCookieSlime.QuestWorld.quests.QuestMission;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class CommandMission extends MissionType {
    public CommandMission() {
        super("COMMAND", false, false, false, SubmissionType.COMMAND,
                "Use %s", new MaterialData(Material.COMMAND));
    }

    @Override
    public ItemStack getQuestItem(QuestMission qm) {
        return qm.getRawItem().clone();
    }
}
