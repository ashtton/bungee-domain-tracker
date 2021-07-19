package me.gleeming.tracker.command;

import me.gleeming.tracker.DomainTracker;
import me.gleeming.tracker.file.FileManager;
import me.gleeming.tracker.util.TimeUtility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DomainsCommand extends Command {
    public DomainsCommand() { super("domains"); }
    public void execute(CommandSender sender, String[] args) {
        // Check the players permission
        if(!sender.hasPermission("domain.command.use")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to use this command."));
            return;
        }

        AtomicLong timeFrame = new AtomicLong(-1);
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("current")) {
                DomainTracker.getInstance().async(() -> {
                    HashMap<String, Integer> players = new HashMap<>();
                    FileManager.getInstance().getDomains().forEach((uuid, server) -> {
                        if(!players.containsKey(server)) players.put(server, 0);
                        players.put(server, players.get(server) + 1);
                    });

                    sendJoins(sender, players, "Current");
                });

                return;
            }

            long duration = TimeUtility.parseDuration(args[0]);
            if(duration == -1) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "You have entered an invalid time frame."));
                return;
            } else {
                timeFrame.set(duration);
            }
        }

        FileManager.getInstance().getJoins(timeFrame.get(), joins -> {
            if(joins.size() == 0) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Nobody has joined" + (timeFrame.get() == -1 ? "." : " in the past " + TimeUtility.toLongFrame(timeFrame.get() / 1000) + ".")));
                return;
            }

            sendJoins(sender, joins, (timeFrame.get() == -1 ? "All Time" : TimeUtility.toLongFrame(timeFrame.get() / 1000)));
        });
    }

    /**
     * Sends a message of the joins to the player
     *
     * @param sender Sender
     * @param joins Joins
     */
    public void sendJoins(CommandSender sender, HashMap<String, Integer> joins, String duration) {
        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7&m------------------------------------")));
        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&f\u2756 &6&lDomains &7(" + duration + ")")));
        joins.forEach((domain, amount) -> {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7" + domain + " &8\u00BB &f" + amount)));
        });
        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7&m------------------------------------")));
    }
}
