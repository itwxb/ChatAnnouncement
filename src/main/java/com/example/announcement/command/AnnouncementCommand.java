package com.example.announcement.command;

import com.example.announcement.ChatAnnouncement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementCommand implements CommandExecutor, TabCompleter {

    private final ChatAnnouncement plugin;
    private final MiniMessage miniMessage;

    public AnnouncementCommand(ChatAnnouncement plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "send":
                return handleSend(sender, args);
            case "broadcast":
                return handleBroadcast(sender, args);
            case "template":
                return handleTemplate(sender, args);
            case "reload":
                return handleReload(sender);
            case "help":
                sendHelp(sender);
                return true;
            default:
                sender.sendMessage(miniMessage.deserialize("<red>未知命令! 使用 /announcement help 查看帮助"));
                return true;
        }
    }

    private boolean handleSend(CommandSender sender, String[] args) {
        if (!sender.hasPermission("announcement.send")) {
            sender.sendMessage(miniMessage.deserialize("<red>你没有权限执行此命令!"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(miniMessage.deserialize("<red>用法: /announcement send <玩家|all> <消息>"));
            return true;
        }

        String target = args[1];
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (target.equalsIgnoreCase("all")) {
            Component component = plugin.getMessageManager().parseMessage(message);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(component);
            }
            sender.sendMessage(miniMessage.deserialize("<green>公告已发送给所有玩家!"));
        } else {
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
                sender.sendMessage(miniMessage.deserialize("<red>玩家 " + target + " 不在线!"));
                return true;
            }
            Component component = plugin.getMessageManager().parseMessage(message);
            player.sendMessage(component);
            sender.sendMessage(miniMessage.deserialize("<green>公告已发送给 " + target + "!"));
        }

        return true;
    }

    private boolean handleBroadcast(CommandSender sender, String[] args) {
        if (!sender.hasPermission("announcement.broadcast")) {
            sender.sendMessage(miniMessage.deserialize("<red>你没有权限执行此命令!"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize("<red>用法: /announcement broadcast <消息>"));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Component component = plugin.getMessageManager().parseMessage(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(component);
        }

        sender.sendMessage(miniMessage.deserialize("<green>公告已广播给所有玩家!"));
        return true;
    }

    private boolean handleTemplate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("announcement.template")) {
            sender.sendMessage(miniMessage.deserialize("<red>你没有权限执行此命令!"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(miniMessage.deserialize("<red>用法: /announcement template <模板名> <玩家|all>"));
            return true;
        }

        String templateName = args[1];
        String target = args[2];

        String template = plugin.getConfig().getString("templates." + templateName + ".text");
        if (template == null) {
            sender.sendMessage(miniMessage.deserialize("<red>模板 " + templateName + " 不存在!"));
            return true;
        }

        Component component = plugin.getMessageManager().parseMessage(template);

        if (target.equalsIgnoreCase("all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(component);
            }
            sender.sendMessage(miniMessage.deserialize("<green>模板公告已发送给所有玩家!"));
        } else {
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
                sender.sendMessage(miniMessage.deserialize("<red>玩家 " + target + " 不在线!"));
                return true;
            }
            player.sendMessage(component);
            sender.sendMessage(miniMessage.deserialize("<green>模板公告已发送给 " + target + "!"));
        }

        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("announcement.reload")) {
            sender.sendMessage(miniMessage.deserialize("<red>你没有权限执行此命令!"));
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(miniMessage.deserialize("<green>配置文件已重新加载!"));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize("<gradient:#FFD700:#FFA500>========== ChatAnnouncement 帮助 ==========</gradient>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/announcement send <玩家|all> <消息></yellow> <gray>- 发送公告给指定玩家或所有人</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/announcement broadcast <消息></yellow> <gray>- 广播公告给所有玩家</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/announcement template <模板名> <玩家|all></yellow> <gray>- 使用模板发送公告</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/announcement reload</yellow> <gray>- 重载配置文件</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/announcement help</yellow> <gray>- 显示此帮助信息</gray>"));
        sender.sendMessage(miniMessage.deserialize("<gradient:#FFD700:#FFA500>========================================</gradient>"));
        sender.sendMessage(miniMessage.deserialize("<green>点击事件示例:"));
        sender.sendMessage(miniMessage.deserialize("<white>  <click:suggest_command:'/help'><hover:show_text:'点击获取帮助'><gold>[点击填充命令]</gold></click>"));
        sender.sendMessage(miniMessage.deserialize("<white>  <click:run_command:'/spawn'><hover:show_text:'传送到主城'><gold>[点击执行命令]</gold></click>"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("send", "broadcast", "template", "reload", "help"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("send")) {
                completions.add("all");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            } else if (args[0].equalsIgnoreCase("template")) {
                completions.addAll(plugin.getConfig().getConfigurationSection("templates").getKeys(false));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("template")) {
                completions.add("all");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lastArg))
                .collect(Collectors.toList());
    }
}
