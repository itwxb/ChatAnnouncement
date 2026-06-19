package com.example.announcement.listener;

import com.example.announcement.ChatAnnouncement;
import com.example.announcement.config.ConfigService;
import com.example.announcement.service.MessageService;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.RegisterEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 监听 AuthMe 事件，对未注册玩家发送注册提示；注册成功时全服广播欢迎消息。
 * <p>
 * 改用 {@link ConfigService} 与 {@link MessageService}，避免裸字符串拼接配置路径。
 */
public final class AuthMeListener implements Listener {

    private final ChatAnnouncement plugin;
    private final ConfigService config;
    private final MessageService messageService;
    private final Logger logger;
    private final boolean authMePresent;

    public AuthMeListener(@NotNull ChatAnnouncement plugin,
                          @NotNull ConfigService config,
                          @NotNull MessageService messageService) {
        this.plugin = plugin;
        this.config = config;
        this.messageService = messageService;
        this.logger = plugin.getLogger();
        this.authMePresent = Bukkit.getPluginManager().getPlugin("AuthMe") != null;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!authMePresent || !config.isAuthMeEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        AuthMeApi api = AuthMeApi.getInstance();
        if (api == null || api.isRegistered(player.getName())) {
            return;
        }
        String raw = config.getAuthMeUnregisteredMessage();
        if (raw.isEmpty()) {
            return;
        }
        String personalized = raw.replace("%player%", player.getName());
        Component component = messageService.parseWithPrefix(personalized, player);
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> player.sendMessage(component), 20L);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRegister(RegisterEvent event) {
        if (!authMePresent || !config.isAuthMeEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        String raw = config.getAuthMeRegisterBroadcast();
        if (raw.isEmpty()) {
            return;
        }
        String base = raw.replace("%player%", player.getName());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                String personalized = base.replace("%sender%", online.getName());
                try {
                    online.sendMessage(messageService.parseWithPrefix(personalized, online));
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "Failed to send welcome broadcast", ex);
                }
            }
        }, 20L);
    }
}
