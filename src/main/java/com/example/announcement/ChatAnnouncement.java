package com.example.announcement;

import com.example.announcement.command.AnnouncementCommand;
import com.example.announcement.listener.AuthMeListener;
import com.example.announcement.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatAnnouncement extends JavaPlugin {

    private static ChatAnnouncement instance;
    private MessageManager messageManager;
    private boolean authMeEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        this.messageManager = new MessageManager(this);
        
        AnnouncementCommand command = new AnnouncementCommand(this);
        getCommand("announcement").setExecutor(command);
        getCommand("announcement").setTabCompleter(command);
        
        if (Bukkit.getPluginManager().getPlugin("AuthMe") != null) {
            getServer().getPluginManager().registerEvents(new AuthMeListener(this), this);
            this.authMeEnabled = true;
            getLogger().info("已检测到 AuthMe 插件，注册监听器已启用!");
        } else {
            getLogger().info("未检测到 AuthMe 插件，注册监听器未启用。");
        }
         
        getLogger().info("ChatAnnouncement 插件已启用!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatAnnouncement 插件已禁用!");
    }

    public static ChatAnnouncement getInstance() {
        return instance;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public boolean isAuthMeEnabled() {
        return authMeEnabled;
    }
}
