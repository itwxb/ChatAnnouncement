package com.example.announcement.listener;

import com.example.announcement.ChatAnnouncement;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.RegisterEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AuthMeListener implements Listener {

    private final ChatAnnouncement plugin;
    private final MiniMessage miniMessage;

    public AuthMeListener(ChatAnnouncement plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("authme.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        AuthMeApi authMeApi = AuthMeApi.getInstance();

        if (authMeApi == null) {
            return;
        }

        boolean isRegistered = authMeApi.isRegistered(player.getName());

        if (!isRegistered) {
            String message = plugin.getConfig().getString("authme.unregistered-message", 
                "<yellow>欢迎来到服务器！请点击 <click:suggest_command:'/register 密码 确认密码'><hover:show_text:'<green>点击注册账号'><gold>[注册]</gold></click> 完成注册</yellow>");
            
            message = message.replace("%player%", player.getName());
            Component component = miniMessage.deserialize(message);
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(component);
            }, 20L);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRegister(RegisterEvent event) {
        if (!plugin.getConfig().getBoolean("authme.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        String playerName = player.getName();

        String message = plugin.getConfig().getString("authme.register-broadcast", 
            "<gradient:#FFD700:#FFA500>[欢迎]</gradient> <white>欢迎 <gold>%player%</gold> 加入梦想之都服务器！点击 <click:suggest_command:'欢迎 %player%，我是 %sender%，一起游玩吧！'><hover:show_text:'<green>点击发送欢迎消息'><gold>[欢迎]</gold></click> 欢迎新玩家！</white>");
        
        message = message.replace("%player%", playerName);
        
        String finalMessage = message;
        Component component = miniMessage.deserialize(finalMessage);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String personalizedMessage = finalMessage.replace("%sender%", onlinePlayer.getName());
                Component personalizedComponent = miniMessage.deserialize(personalizedMessage);
                onlinePlayer.sendMessage(personalizedComponent);
            }
        }, 20L);
    }
}
