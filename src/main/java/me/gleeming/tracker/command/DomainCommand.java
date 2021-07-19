package me.gleeming.tracker.command;

import me.gleeming.tracker.file.FileManager;
import me.gleeming.tracker.util.TimeUtility;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.atomic.AtomicLong;

public class DomainCommand extends Command {
    public DomainCommand() { super("domain"); }
    public void execute(CommandSender sender, String[] args) {
        // Check the players permission
        if(!sender.hasPermission("domain.command.use")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to use this command."));
            return;
        }

        if(args.length != 1 && args.length != 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /domain <player/domain> [timeframe]"));
            return;
        }

        if(!args[0].contains(".")) {
            ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
            if(target == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "A player by the name of '" + args[0] + "' cannot be found."));
                return;
            }

            sender.sendMessage(new TextComponent(ChatColor.GREEN + "The player " + target.getName() + " is connected using '" + FileManager.getInstance().getDomain(target.getUniqueId()) + "'."));
            return;
        }

        AtomicLong timeFrame = new AtomicLong(-1);
        if(args.length == 2) {
            long duration = TimeUtility.parseDuration(args[1]);
            if(duration == -1) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "You have entered an invalid time frame."));
                return;
            } else {
                timeFrame.set(duration);
            }
        }

        FileManager.getInstance().getJoins(args[0].toLowerCase(), timeFrame.get(), joins -> {
            if(joins == 0) sender.sendMessage(new TextComponent(ChatColor.RED + "Nobody has joined using '" + args[0].toLowerCase() + "'" + (timeFrame.get() == -1 ? "." : " in the past " + TimeUtility.toLongFrame(timeFrame.get() / 1000) + ".")));
            else sender.sendMessage(new TextComponent(ChatColor.GREEN + Integer.toString(joins) + " player(s) have joined using '" + args[0].toLowerCase() + "'" + (timeFrame.get() == -1 ? "." : " in the past " + TimeUtility.toLongFrame(timeFrame.get() / 1000) + ".")));
        });
    }
}
