package me.mrCookieSlime.QuestWorld.quests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Variable;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage.HoverAction;
import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.utils.Text;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Party {
	
	UUID leader;
	Set<UUID> members;
	QuestManager manager;
	Set<UUID> pending;

	public Party(UUID uuid) {
		this.leader = uuid;
		this.manager = QuestWorld.getInstance().getManager(Bukkit.getOfflinePlayer(uuid));
		members = new HashSet<UUID>();
		pending = new HashSet<UUID>();
		if (manager.toConfig().contains("party.members")) {
			for (String member: manager.toConfig().getStringList("party.members")) {
				members.add(UUID.fromString(member));
			}
		}
		if (manager.toConfig().contains("party.pending-requests")) {
			for (String request: manager.toConfig().getStringList("party.pending-requests")) {
				pending.add(UUID.fromString(request));
			}
		}
	}
	
	public static Party create(Player p) {
		QuestWorld.getInstance().getManager(p).toConfig().setValue("party.associated", p.getUniqueId().toString());
		return new Party(p.getUniqueId());
	}
	
	public void addPlayer(Player p) {
		for (UUID member: getPlayers()) {
			Player player = Bukkit.getPlayer(member);
			if (player != null) QuestWorld.getInstance().getLocalization().sendTranslation(player, "party.join", true, new Variable("%name%", p.getName()));
		}
		
		this.members.add(p.getUniqueId());
		QuestWorld.getInstance().getLocalization().sendTranslation(p, "party.joined", true, new Variable("%name%", Bukkit.getOfflinePlayer(leader).getName()));
		QuestWorld.getInstance().getManager(p).toConfig().setValue("party.associated", leader.toString());
		if (pending.contains(p.getUniqueId())) pending.remove(p.getUniqueId());
		save();
	}
	
	public void removePlayer(String name) {
		OfflinePlayer target = null;
		List<Player> existingParty = new ArrayList<Player>();
		
		for (UUID member: getPlayers()) {
			Player p = Bukkit.getPlayer(member);
			if (p != null) {
				existingParty.add(p);
				if(p.getName().equalsIgnoreCase(name))
					target = p;
			}
		}
		
		if(target != null) {
			for(Player p : existingParty) {
				QuestWorld.getInstance().getLocalization().sendTranslation(p, "party.kicked", true, new Variable("%name%", name));
			}
			
			members.remove(target.getUniqueId());
			QuestWorld.getInstance().getManager(target).toConfig().setValue("party.associated", null);
			save();
		}
	}
	
	public void abandon() {
		for (UUID member: members) {
			members.remove(member);
			QuestWorld.getInstance().getManager(Bukkit.getOfflinePlayer(member)).toConfig().setValue("party.associated", null);
		}
		manager.toConfig().setValue("party.associated", null);
		save();
	}

	public List<UUID> getPlayers() {
		List<UUID> players = new ArrayList<UUID>();
		players.add(leader);
		players.addAll(members);
		return players;
	}

	public int getSize() {
		return getPlayers().size();
	}
	
	public void save() {
		List<String> list = new ArrayList<String>();
		for (UUID member: members) {
			list.add(member.toString());
		}
		manager.toConfig().setValue("party.members", list);
		
		List<String> invitations = new ArrayList<String>();
		for (UUID p: pending) {
			invitations.add(p.toString());
		}
		manager.toConfig().setValue("party.pending-requests", invitations);
	}

	public boolean isLeader(OfflinePlayer player) {
		return player.getUniqueId().equals(leader);
	}

	public void invite(Player p) throws Exception {
		p.sendMessage("");
		QuestWorld.getInstance().getLocalization().sendTranslation(p, "party.invitation", false, new Variable("%name%", Bukkit.getOfflinePlayer(leader).getName()));
		
		new TellRawMessage()
		.addText(Text.colorize("&a&lACCEPT"))
		.addHoverEvent(HoverAction.SHOW_TEXT, Text.colorize("&7Click to accept this Invitation"))
		.addClickEvent(ClickAction.RUN_COMMAND, "/quests accept " + leader)
		.addText(Text.colorize(" &4&lDENY"))
		.addHoverEvent(HoverAction.SHOW_TEXT, Text.colorize("&7Click to deny this Invitation"))
		.send(p);
		
		p.sendMessage("");
		
		pending.add(p.getUniqueId());
		save();
	}
	
	public boolean hasInvited(Player p) {
		return pending.contains(p.getUniqueId());
	}

}
