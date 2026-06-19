package com.example.announcement.metrics;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * bStats 指标采集封装。
 * <p>
 * 在 <a href="https://bstats.org">bstats.org</a> 注册插件后替换 {@link #PLUGIN_ID}，
 * 即可在面板查看：
 * <ul>
 *     <li>服务器数量 / 玩家总数</li>
 *     <li>Java / Bukkit / Paper 版本分布</li>
 *     <li>玩家位置 / 地理位置</li>
 * </ul>
 * <p>
 * 隐私：bStats 不会收集玩家名/IP 等敏感信息，仅统计聚合数据。
 * 用户可在 {@code plugins/bStats/config.yml} 中关闭。
 */
public final class PluginMetrics {

    /** bStats 平台分配的插件 ID（占位，请在 bstats.org 替换为真实 ID）。 */
    public static final int PLUGIN_ID = 00000;

    private final JavaPlugin plugin;
    private final Metrics metrics;

    public PluginMetrics(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.metrics = new Metrics(plugin, PLUGIN_ID);
        registerCustomCharts();
    }

    /** 关闭采集（保留接口，便于测试或用户主动关闭）。 */
    public void shutdown() {
        if (metrics != null) {
            // bStats 3.x 自动随插件生命周期关闭
        }
    }

    private void registerCustomCharts() {
        // 这里可以加自定义 chart，例如：
        // metrics.addCustomChart(new Metrics.SingleLineChart("active_servers", () -> 1));
        plugin.getLogger().info(() -> "bStats 指标采集已启用（Plugin ID: " + PLUGIN_ID + "）");
    }
}
