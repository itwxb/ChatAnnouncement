---
name: "mc-plugin-dev"
description: "Minecraft插件开发规范指南。当用户需要开发Minecraft插件、Bukkit/Spigot/Paper插件时调用此技能。"
---

# Minecraft 插件开发规范指南

## 技术选型标准

### 构建工具
- **推荐**: Gradle（Paper官方推荐，现代项目首选）
- **可选**: Maven（传统稳定，但逐渐被Gradle取代）

### 依赖管理
- 使用 `compileOnly` 依赖 Paper/Spigot API
- 可选依赖使用 `softdepend` 而非 `depend`
- 添加必要的 Maven 仓库

### Java 版本
- Minecraft 1.21+ 使用 Java 21
- 使用 `toolchain` 配置 Java 版本

## 项目结构规范

```
PluginName/
├── build.gradle.kts          # Gradle构建配置
├── settings.gradle.kts       # Gradle项目设置
├── .gitignore               # Git忽略文件
├── README.md                # 项目说明文档
├── 介绍.md                   # 论坛发布文档（可选）
├── gradle/
│   └── wrapper/             # Gradle Wrapper文件
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/plugin/
│       │       ├── PluginName.java      # 主类
│       │       ├── command/             # 命令处理
│       │       ├── listener/            # 事件监听器
│       │       ├── manager/             # 管理器类
│       │       └── util/                # 工具类
│       └── resources/
│           ├── plugin.yml               # 插件配置
│           └── config.yml               # 默认配置
```

## 核心代码规范

### 主类结构
```java
public class PluginName extends JavaPlugin {
    private static PluginName instance;
    private ManagerClass manager;
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        // 初始化管理器
        // 注册命令
        // 注册监听器
        getLogger().info("插件已启用!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("插件已禁用!");
    }
    
    public static PluginName getInstance() {
        return instance;
    }
}
```

### 命令处理规范
- 实现 `CommandExecutor` 和 `TabCompleter`
- 使用 switch-case 处理子命令
- 添加权限检查
- 提供友好的错误提示
- 支持命令补全

### 事件监听器规范
- 使用 `@EventHandler` 注解
- 指定事件优先级 `EventPriority`
- 添加必要的条件检查
- 异步操作使用 `Bukkit.getScheduler().runTaskLater()`

### 配置文件规范
- 使用 YAML 格式
- 添加中文注释说明
- 提供默认值
- 支持热重载
- 使用 `saveDefaultConfig()` 生成默认配置

## plugin.yml 规范

```yaml
name: PluginName
version: ${version}
main: com.example.plugin.PluginName
api-version: '1.21'
author: AuthorName
description: Plugin description
softdepend: [OptionalPlugin1, OptionalPlugin2]

commands:
  commandname:
    description: 命令描述
    usage: /commandname <args>
    aliases: [alias1, alias2]
    permission: plugin.use

permissions:
  plugin.use:
    description: 基础使用权限
    default: true
  plugin.admin:
    description: 管理员权限
    default: op
    children:
      plugin.use: true
```

## .gitignore 规范

```gitignore
# Gradle 构建工具
.gradle/
build/

# IDE 配置
.idea/
.vscode/
*.iml

# Java 编译产物
*.class
*.jar

# 操作系统文件
.DS_Store
Thumbs.db

# 日志文件
logs/
*.log
```

## 文档规范

### README.md 结构
1. 插件名称和简介
2. 技术规格表格
3. 安装指南（直接安装 + 源码构建）
4. 使用指南（命令、权限、配置）
5. 开发与贡献指南
6. 许可证信息
7. 联系方式
8. 更新日志（带日期）

### 论坛发布文档结构
1. 插件信息表格
2. 核心特性
3. 安装方法
4. 命令说明
5. 配置详解（包含示例）
6. 常见问题
7. 下载链接
8. 更新日志

## 开发流程

### 1. 需求确认
- 明确插件功能需求
- 确认目标 Minecraft 版本
- 确认依赖插件（如 AuthMe、Vault 等）
- 选择技术方案

### 2. 项目初始化
- 创建项目目录结构
- 配置 build.gradle.kts
- 创建 plugin.yml
- 创建默认 config.yml

### 3. 核心功能开发
- 编写主类
- 实现命令系统
- 实现事件监听
- 添加配置支持

### 4. 测试与调试
- 编译项目 `gradle build`
- 本地测试
- 修复问题

### 5. 文档编写
- 编写 README.md
- 编写论坛发布文档
- 更新版本号和更新日志

### 6. 发布准备
- 打包插件 jar 文件
- 准备下载链接
- 发布到论坛/GitHub

## 最佳实践

### 代码质量
- 遵循 Java 命名规范
- 包结构清晰合理
- 添加必要的注释
- 错误处理完善

### 性能优化
- 避免在主线程执行耗时操作
- 使用异步任务处理数据库/网络操作
- 合理使用缓存

### 兼容性
- 使用 Paper API（推荐）或 Spigot API
- 正确处理可选依赖
- 版本兼容性说明

### 安全性
- 权限检查
- 输入验证
- 防止命令注入

## 常用依赖

| 依赖 | 用途 | Maven 仓库 |
|------|------|-----------|
| Paper API | 服务端核心 | repo.papermc.io |
| AuthMe | 登录系统 | repo.codemc.io |
| Vault | 经济/权限 | jitpack.io |
| PlaceholderAPI | 变量替换 | repo.extendedclip.com |

## 版本号规范

使用语义化版本号：`主版本号.次版本号.修订号`

- **主版本号**: 不兼容的 API 修改
- **次版本号**: 向下兼容的功能性新增
- **修订号**: 向下兼容的问题修正

示例：
- v1.0.0 - 初始版本
- v1.1.0 - 新增功能
- v1.1.1 - Bug修复
- v2.0.0 - 重大更新
