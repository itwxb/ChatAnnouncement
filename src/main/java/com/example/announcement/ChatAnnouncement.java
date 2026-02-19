package com.example.announcement;

import com.example.announcement.command.AnnouncementCommand;
import com.example.announcement.manager.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatAnnouncement extends JavaPlugin {

    private static ChatAnnouncement instance;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        this.messageManager = new MessageManager(this);
        
        AnnouncementCommand command = new AnnouncementCommand(this);
        getCommand("announcement").setExecutor(command);
        getCommand("announcement").setTabCompleter(command);
        
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
}
