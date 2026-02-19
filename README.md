# 📢 ChatAnnouncement - 高级聊天公告插件

> 支持点击事件的 Minecraft 聊天公告插件，适用于 1.21.x 版本

## 📋 项目简介

ChatAnnouncement 是一个功能强大的 Minecraft 聊天公告插件，支持丰富的点击事件和美观的消息格式，为服务器提供专业的公告系统。

### 🎯 核心功能

- **强大的点击事件**：支持填充命令到聊天框、直接执行命令、打开网页链接
- **美观的消息格式**：支持 MiniMessage 富文本，包括颜色、渐变、悬停提示
- **完整的指令系统**：支持向指定玩家或全体玩家发送公告
- **模板系统**：预设常用模板，方便快速发送
- **权限控制**：完善的权限节点，安全可靠

## 📦 技术规格

| 项目 | 详情 |
|------|------|
| **插件名称** | ChatAnnouncement |
| **版本** | 1.0.0 |
| **支持版本** | Minecraft 1.21.x |
| **构建工具** | Gradle |
| **依赖** | Paper/Spigot 1.21+ |
| **语言** | Java 21 |

## 🚀 安装指南

### 方法一：直接安装
1. 下载最新版本的 `ChatAnnouncement-1.0.0.jar`
2. 将插件放入服务器的 `plugins` 文件夹
3. 启动服务器，插件会自动加载

### 方法二：从源码构建
```bash
# 克隆仓库
git clone https://gitee.com/yourusername/chatannouncement.git

# 进入目录
cd chatannouncement

# 构建项目
./gradlew build

# 插件文件位于
build/libs/ChatAnnouncement-1.0.0.jar
```

## 🎮 使用指南

### 🔧 基础命令

| 命令 | 语法 | 描述 | 权限 |
|------|------|------|------|
| **发送公告** | `/announcement send <玩家\|all> <消息>` | 发送公告给指定玩家或所有人 | `announcement.send` |
| **广播公告** | `/announcement broadcast <消息>` | 广播公告给所有在线玩家 | `announcement.broadcast` |
| **使用模板** | `/announcement template <模板名> <玩家\|all>` | 使用预设模板发送公告 | `announcement.template` |
| **重载配置** | `/announcement reload` | 重新加载配置文件 | `announcement.reload` |
| **查看帮助** | `/announcement help` | 显示帮助信息 | 所有玩家 |

### 📱 快捷命令

- `/ann send <玩家\|all> <消息>`
- `/notice broadcast <消息>`

### 🖱️ 点击事件示例

#### 1. 填充命令到聊天框
```yaml
# 效果：点击后聊天框出现 /help 命令
<click:suggest_command:'/help'><hover:show_text:'点击获取帮助'><gold>[帮助]</gold></click>
```

#### 2. 直接执行命令
```yaml
# 效果：点击后立即执行 /spawn 命令
<click:run_command:'/spawn'><hover:show_text:'传送到主城'><gold>[传送]</gold></click>
```

#### 3. 打开网页链接
```yaml
# 效果：点击后打开百度
<click:open_url:'https://www.baidu.com'><hover:show_text:'点击打开百度'><gold>[打开网页]</gold></click>
```

### 🎨 消息格式

#### 基础颜色
```yaml
<red>红色文本</red>
<green>绿色文本</green>
<blue>蓝色文本</blue>
<yellow>黄色文本</yellow>
```

#### 渐变色
```yaml
<gradient:#FF0000:#FFFF00>红黄渐变</gradient>
<gradient:#00FF00:#00FFFF>绿蓝渐变</gradient>
```

#### 悬停提示
```yaml
<hover:show_text:'<green>这是悬停提示</green><white>可以多行显示</white>'>
  <click:run_command:'/help'><gold>[帮助]</gold></click>
</hover>
```

## ⚙️ 配置说明

### 配置文件
**位置**：`plugins/ChatAnnouncement/config.yml`

### 核心配置
```yaml
# 公告前缀，支持渐变颜色
prefix: "<gradient:#FFD700:#FFA500>[公告]</gradient> "

# 预设模板
templates:
  welcome:
    text: "<yellow>欢迎来到服务器！<click:suggest_command:'/register'><hover:show_text:'<green>点击注册账号'><gold>[点击注册]</gold></click></yellow>"
  spawn:
    text: "<green>点击 <click:run_command:'/spawn'><hover:show_text:'<aqua>传送到主城'><gold>[传送]</gold></click> 返回主城</green>"
  help:
    text: "<white>需要帮助？<click:suggest_command:'/help'><hover:show_text:'<yellow>点击获取帮助'><gold>[帮助]</gold></click> <click:run_command:'/rules'><hover:show_text:'<yellow>查看服务器规则'><gold>[规则]</gold></click></white>"
  vote:
    text: "<light_purple>支持服务器！<click:open_url:'https://example.com/vote'><hover:show_text:'<green>点击前往投票'><gold>[投票]</gold></click></light_purple>"
```

### 权限配置

| 权限节点 | 描述 | 默认值 |
|---------|------|--------|
| `announcement.use` | 基础使用权限 | 所有玩家 |
| `announcement.send` | 发送公告权限 | OP |
| `announcement.broadcast` | 广播公告权限 | OP |
| `announcement.template` | 使用模板权限 | OP |
| `announcement.reload` | 重载配置权限 | OP |
| `announcement.admin` | 管理员权限（包含所有） | OP |

## 📝 使用示例

### 发送公告
```bash
# 发送给特定玩家
/ann send Steve <yellow>欢迎回来！</yellow>

# 发送给所有人
/ann broadcast <red>服务器将于10分钟后重启！</red>
```

### 使用模板
```bash
# 发送欢迎模板给所有人
/ann template welcome all

# 发送传送模板给特定玩家
/ann template spawn Alex
```

### 高级示例
```bash
# 带点击事件的公告
/ann broadcast <gradient:#FFD700:#FFA500>服务器活动</gradient> <white>周末双倍经验！<click:run_command:'/event'><hover:show_text:'查看活动详情'><gold>[查看详情]</gold></click></white>

# 带链接的公告
/ann broadcast <blue>服务器官网</blue> <white>点击 <click:open_url:'https://example.com'><hover:show_text:'访问官网'><gold>[官网]</gold></click> 了解更多</white>
```

## 🔧 开发与贡献

### 环境要求
- JDK 21+
- Gradle 8.8+
- Git

### 开发流程
1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

### 构建项目
```bash
# 编译项目
./gradlew build

# 运行测试
./gradlew test

# 生成 javadoc
./gradlew javadoc
```

## 📄 许可证

本项目采用 **MIT 许可证** - 详情请参阅 [LICENSE](LICENSE) 文件

## 🤝 贡献者

感谢以下贡献者对本项目的支持：

<!-- 贡献者列表 -->

## 📞 联系方式

- **作者**：Example
- **Gitee 地址**：[https://gitee.com/yourusername/chatannouncement](https://gitee.com/yourusername/chatannouncement)
- **邮箱**：example@example.com
- **QQ群**：123456789（可选）

## 📋 更新日志

### v1.0.0 (2026-02-20)
- ✅ 初始版本发布
- ✅ 支持点击事件（填充命令、执行命令、打开网页）
- ✅ 支持MiniMessage富文本格式
- ✅ 完整的指令系统和权限控制
- ✅ 模板系统，方便快速发送
- ✅ 配置文件热重载

## 🎯 项目状态

![Build Status](https://img.shields.io/badge/build-success-brightgreen.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Minecraft Version](https://img.shields.io/badge/minecraft-1.21.x-green.svg)

---

**🎉 感谢使用 ChatAnnouncement 插件！**

如果觉得插件好用，欢迎给个 Star ⭐ 支持一下！
