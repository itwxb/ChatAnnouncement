package com.example.announcement.manager;

import com.example.announcement.ChatAnnouncement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageManager {

    private final ChatAnnouncement plugin;
    private final MiniMessage miniMessage;

    public MessageManager(ChatAnnouncement plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    public Component parseMessage(String message) {
        String prefix = plugin.getConfig().getString("prefix", "");
        String fullMessage = prefix + message;
        return miniMessage.deserialize(fullMessage);
    }

    public Component parseMessageWithoutPrefix(String message) {
        return miniMessage.deserialize(message);
    }
}
