package com.example.announcement.command;

import com.example.announcement.ChatAnnouncement;
import com.example.announcement.config.ConfigService;
import com.example.announcement.service.I18nService;
import com.example.announcement.service.MessageService;
import com.example.announcement.service.TargetResolver;
import com.example.announcement.service.TemplateService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 公告命令：仅做参数解析与子命令分发，所有业务逻辑委派给 Service 层。
 */
public final class AnnouncementCommand implements CommandExecutor, TabCompleter {

    private final ChatAnnouncement plugin;
    private final ConfigService config;
    private final MessageService messageService;
    private final TemplateService templateService;
    private final TargetResolver targetResolver;
    private final I18nService i18n;

    public AnnouncementCommand(@NotNull ChatAnnouncement plugin,
                               @NotNull ConfigService config,
                               @NotNull MessageService messageService,
                               @NotNull TemplateService templateService,
                               @NotNull TargetResolver targetResolver,
                               @NotNull I18nService i18n) {
        this.plugin = plugin;
        this.config = config;
        this.messageService = messageService;
        this.templateService = templateService;
        this.targetResolver = targetResolver;
        this.i18n = i18n;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        return switch (sub) {
            case "send" -> handleSend(sender, args);
            case "broadcast" -> handleBroadcast(sender, args);
            case "template" -> handleTemplate(sender, args);
            case "reload" -> handleReload(sender);
            case "help" -> {
                sendHelp(sender);
                yield true;
            }
            default -> {
                i18n.send(sender, "cmd.unknown");
                yield true;
            }
        };
    }

    // -- 子命令处理 ------------------------------------------------------------

    private boolean handleSend(CommandSender sender, String[] args) {
        if (!sender.hasPermission("announcement.send")) {
            i18n.send(sender, "cmd.no-permission");
            return true;
        }
        if (args.length < 3) {
            i18n.send(sender, "cmd.usage.send");
            return true;
        }
        String target = args[1];
        String message = joinArgs(args, 2);
        var result = targetResolver.resolve(target);
        if (result.isEmpty()) {
            i18n.send(sender, "cmd.player-not-found", target);
            return true;
        }
        for (Player p : result.players()) {
            p.sendMessage(messageService.parseWithPrefix(message, p));
        }
        if (result.type() == TargetResolver.Result.Type.ALL) {
            i18n.send(sender, "cmd.send.all.ok");
        } else {
            i18n.send(sender, "cmd.send.single.ok", target);
        }
        return true;
    }

    private boolean handleBroadcast(CommandSender sender, String[] args) {
        if (!sender.hasPermission("announcement.broadcast")) {
            i18n.send(sender, "cmd.no-permission");
            return true;
        }
        if (args.length < 2) {
            i18n.send(sender, "cmd.usage.broadcast");
            return true;
        }
        String message = joinArgs(args, 1);
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.sendMessage(messageService.parseWithPrefix(message, p));
        }
        i18n.send(sender, "cmd.broadcast.ok");
        return true;
    }

    private boolean handleTemplate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("announcement.template")) {
            i18n.send(sender, "cmd.no-permission");
            return true;
        }
        if (args.length < 3) {
            i18n.send(sender, "cmd.usage.template");
            return true;
        }
        String name = args[1];
        String target = args[2];
        String resolved = templateService.resolve(name, null);
        if (resolved == null) {
            i18n.send(sender, "cmd.template.not-found", name);
            return true;
        }
        var result = targetResolver.resolve(target);
        if (result.isEmpty()) {
            i18n.send(sender, "cmd.player-not-found", target);
            return true;
        }
        for (Player p : result.players()) {
            String personalized = templateService.resolve(name, p.getName());
            p.sendMessage(messageService.parseWithPrefix(
                    personalized == null ? resolved : personalized, p));
        }
        if (result.type() == TargetResolver.Result.Type.ALL) {
            i18n.send(sender, "cmd.template.all.ok", name);
        } else {
            i18n.send(sender, "cmd.template.single.ok", name, target);
        }
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("announcement.reload")) {
            i18n.send(sender, "cmd.no-permission");
            return true;
        }
        try {
            config.reload();
        } catch (Exception ex) {
            plugin.getLogger().warning("配置重载失败: " + ex.getMessage());
        }
        i18n.send(sender, "cmd.reload.ok");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        i18n.send(sender, "cmd.help.header");
        i18n.send(sender, "cmd.help.send");
        i18n.send(sender, "cmd.help.broadcast");
        i18n.send(sender, "cmd.help.template");
        i18n.send(sender, "cmd.help.reload");
        i18n.send(sender, "cmd.help.help");
        i18n.send(sender, "cmd.help.footer");
    }

    private static String joinArgs(String[] args, int from) {
        return String.join(" ", Arrays.copyOfRange(args, from, args.length));
    }

    // -- Tab 补全 --------------------------------------------------------------

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("send", "broadcast", "template", "reload", "help"));
        } else if (args.length == 2) {
            if ("send".equalsIgnoreCase(args[0])) {
                completions.add("all");
                plugin.getServer().getOnlinePlayers().forEach(p -> completions.add(p.getName()));
            } else if ("template".equalsIgnoreCase(args[0])) {
                completions.addAll(templateService.list());
            }
        } else if (args.length == 3 && "template".equalsIgnoreCase(args[0])) {
            completions.add("all");
            plugin.getServer().getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        }
        String last = args[args.length - 1].toLowerCase(Locale.ROOT);
        return completions.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(last))
                .distinct()
                .collect(Collectors.toList());
    }
}
