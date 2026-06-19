package com.example.announcement;

import com.example.announcement.command.AnnouncementCommand;
import com.example.announcement.config.ConfigService;
import com.example.announcement.listener.AuthMeListener;
import com.example.announcement.metrics.PluginMetrics;
import com.example.announcement.service.I18nService;
import com.example.announcement.service.MessageService;
import com.example.announcement.service.TargetResolver;
import com.example.announcement.service.TemplateService;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * 插件入口：负责装配（Wire-up）所有 Service。
 * <p>
 * 整个插件的对象图都在这里构建，业务对象通过构造函数注入依赖，
 * 没有任何单例/Spring 容器之类的 magic。
 */
public final class ChatAnnouncement extends JavaPlugin {

    private ConfigService config;
    private MessageService messageService;
    private TemplateService templateService;
    private TargetResolver targetResolver;
    private I18nService i18n;
    private PluginMetrics metrics;
    private boolean authMeEnabled = false;

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        saveDefaultConfig();

        // 装配服务对象图
        this.config = new ConfigService(this);
        this.messageService = new MessageService(config, logger);
        this.templateService = new TemplateService(config);
        this.targetResolver = new TargetResolver();
        this.i18n = new I18nService(logger);

        // 注册命令
        PluginCommand cmd = getCommand("announcement");
        if (cmd == null) {
            logger.severe("未能在 plugin.yml 中找到 'announcement' 命令，请检查插件描述符！");
            setEnabled(false);
            return;
        }
        AnnouncementCommand executor = new AnnouncementCommand(
                this, config, messageService, templateService, targetResolver, i18n);
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);

        // 注册可选监听器
        if (Bukkit.getPluginManager().getPlugin("AuthMe") != null) {
            getServer().getPluginManager().registerEvents(
                    new AuthMeListener(this, config, messageService), this);
            this.authMeEnabled = true;
            logger.info("已检测到 AuthMe，注册监听器已启用");
        } else {
            logger.info("未检测到 AuthMe，注册监听器未启用");
        }

        // 启动 bStats 指标采集
        try {
            this.metrics = new PluginMetrics(this);
        } catch (Throwable t) {
            // bStats 初始化失败不应阻断插件启动
            logger.warning("bStats 初始化失败，已禁用: " + t.getMessage());
        }

        logger.info(() -> "ChatAnnouncement v" + getDescription().getVersion() + " 已启用");
    }

    @Override
    public void onDisable() {
        if (metrics != null) {
            metrics.shutdown();
        }
        getLogger().info(() -> "ChatAnnouncement v" + getDescription().getVersion() + " 已禁用");
    }

    // -- 受限的 getter（不暴露具体实现类，避免外部强耦合） --------------------

    @NotNull
    public MessageService getMessageService() {
        return messageService;
    }

    @NotNull
    public TemplateService getTemplateService() {
        return templateService;
    }

    @NotNull
    public I18nService getI18n() {
        return i18n;
    }

    @Nullable
    public PluginMetrics getMetrics() {
        return metrics;
    }

    public boolean isAuthMeEnabled() {
        return authMeEnabled;
    }
}
