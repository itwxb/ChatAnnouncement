# 📢 ChatAnnouncement - 高级聊天公告插件

> 支持点击事件、i18n、PlaceholderAPI 的 Minecraft 聊天公告插件，适用于 1.21.x 版本

![Java](https://img.shields.io/badge/Java-21-orange)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-green)
![License](https://img.shields.io/badge/License-MIT-blue)
![Tests](https://img.shields.io/badge/Tests-26_passing-brightgreen)
![Coverage](https://img.shields.io/badge/Coverage-60%25+-yellow)
[![Build](https://github.com/yourname/ChatAnnouncement/actions/workflows/build.yml/badge.svg)](https://github.com/yourname/ChatAnnouncement/actions/workflows/build.yml)

## 📋 项目简介

ChatAnnouncement 是一个企业级架构设计的 Minecraft 聊天公告插件，支持丰富的点击事件、PAPI 占位符、双语切换和模板系统。代码按 Service 层拆分，配套 CI/CD、覆盖率门禁、bStats 遥测和完整工程文档。

### 🎯 核心功能

- **强大的点击事件**：支持填充命令到聊天框、直接执行命令、打开网页链接
- **美观的消息格式**：支持 MiniMessage 富文本，包括颜色、渐变、悬停提示
- **完整的指令系统**：支持向指定玩家或全体玩家发送公告
- **模板系统**：预设常用模板，方便快速发送
- **权限控制**：完善的权限节点，安全可靠
- **i18n 国际化**：自动根据玩家客户端语言切换（zh_CN / en_US）
- **AuthMe 集成**：支持未注册玩家提示、注册成功公告
- **PlaceholderAPI 集成**：消息内可使用 PAPI 占位符（可选）
- **bStats 遥测**：匿名统计使用情况，便于改进插件

## 📦 技术规格

| 项目 | 详情 |
|------|------|
| **插件名称** | ChatAnnouncement |
| **版本** | 1.2.0 |
| **支持版本** | Minecraft 1.21.x |
| **构建工具** | Gradle 8.8+ |
| **依赖** | Paper/Spigot 1.21+ |
| **可选依赖** | AuthMe, PlaceholderAPI |
| **遥测** | bStats（可在 bStats 配置中关闭） |
| **语言** | Java 21 |
| **测试** | JUnit 5 + Mockito（26 个测试用例） |
| **覆盖率** | JaCoCo 0.8.11（Service/Config 包 ≥ 60%） |
| **许可证** | MIT |

## 🏗️ 项目架构

```
src/main/java/com/example/announcement/
├── ChatAnnouncement.java          # 入口：装配所有 Service + bStats
├── config/
│   └── ConfigService.java         # 类型安全配置访问
├── service/
│   ├── MessageService.java        # MiniMessage 解析（try/catch + PAPI）
│   ├── TargetResolver.java        # 解析 all/玩家名为玩家集合
│   ├── TemplateService.java       # 模板加载与占位符替换
│   └── I18nService.java           # 双语支持（zh_CN / en_US）
├── command/
│   └── AnnouncementCommand.java   # 命令：参数解析 + 委派
├── listener/
│   └── AuthMeListener.java        # AuthMe 事件监听
└── metrics/
    └── PluginMetrics.java         # bStats 指标采集

src/main/resources/
├── plugin.yml                     # 插件描述
├── config.yml                     # 业务配置
└── com/example/announcement/lang/ # i18n 资源
    ├── messages.properties        # 默认（en_US）
    ├── messages_zh_CN.properties
    └── messages_en_US.properties
```

### 设计原则

- **依赖注入**：所有 Service 通过构造函数注入，无单例/全局状态
- **错误隔离**：MiniMessage 解析失败降级到纯文本，绝不抛
- **资源抽象**：`ConfigService` 把 `plugin.getConfig().getString("...")` 集中管理
- **可测试性**：Service 与 Bukkit 解耦（`TargetResolver.PlayerProvider`），单测不依赖 MockBukkit
- **i18n 优先**：所有面向用户的消息都走资源包，硬编码字符串为零
- **遥测透明**：bStats 默认开启，但配置中可关闭

## 🛡️ 工程质量

本项目按照企业级开源项目标准进行工程化：

| 维度 | 工具 / 实践 |
|------|------------|
| **CI/CD** | GitHub Actions（构建 + 测试 + 覆盖率 + 制品归档） |
| **覆盖率** | JaCoCo 0.8.11，Service/Config 包强制 ≥ 60% |
| **覆盖率上报** | Codecov，PR 自动展示 diff |
| **遥测** | bStats 3.x，可选匿名统计 |
| **版本管理** | 语义化版本（SemVer） |
| **变更日志** | [CHANGELOG.md](CHANGELOG.md)（Keep a Changelog 规范） |
| **许可证** | [LICENSE](LICENSE)（MIT） |
| **依赖声明** | `compileOnly` / `implementation` 严格分离 |
| **i18n** | UTF-8 ResourceBundle，自动按客户端语言切换 |
| **可观测性** | 上下文感知日志（`Supplier<String>` + `Level`） |

### CI/CD 流程

每次 push / PR 都会自动执行：

```
checkout → JDK 21 setup → ./gradlew test
                          ↓
                  ./gradlew jacocoTestReport
                          ↓
                   ┌──────┴──────┐
                   ↓             ↓
           上传 Codecov      上传 HTML 报告
                   ↓
          ./gradlew jacocoTestCoverageVerification（覆盖率门禁）
```

## 🚀 安装指南

### 直接安装
1. 下载 [releases](https://gitee.com/yourusername/chatannouncement/releases) 中的 `ChatAnnouncement-1.2.0.jar`
2. 放入服务器的 `plugins` 文件夹
3. 启动服务器

### 从源码构建
```bash
./gradlew build              # 编译 + 测试 + 覆盖率 + 打包
./gradlew test               # 仅跑测试
./gradlew jacocoTestReport   # 生成覆盖率报告
./gradlew shadowJar          # 仅生成可分发 jar
```

构建产物位于 `build/libs/ChatAnnouncement-1.2.0.jar`。  
覆盖率报告位于 `build/reports/jacoco/test/html/index.html`。

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

### 🖱️ 点击事件详解

本插件支持三种点击事件类型：

| 类型 | 触发方式 | 是否需要确认 | 适用场景 |
|------|---------|-------------|---------|
| **suggest_command** | 填充到聊天框 | ✅ 需要按回车 | 需要玩家确认的操作 |
| **run_command** | 立即执行 | ❌ 无需确认 | 安全的快速操作 |
| **open_url** | 打开网页 | ❌ 无需确认 | 外部链接跳转 |

#### 1. 填充命令到聊天框（suggest_command）
```yaml
# 点击后聊天框出现 /help 命令，玩家按回车执行
<click:suggest_command:'/help'><hover:show_text:'点击获取帮助'><gold>[帮助]</gold></click>
```

#### 2. 直接执行命令（run_command）
```yaml
# 点击后立即执行 /spawn 命令，传送到主城
<click:run_command:'/spawn'><hover:show_text:'传送到主城'><gold>[传送]</gold></click>
```

#### 3. 打开网页链接（open_url）
```yaml
# 点击后打开百度
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
# 公告前缀，会自动添加到每条公告消息前面
prefix: "<gradient:#FF0000:#FF5555><bold>[公告]</bold></gradient> "

# 预设模板，可以通过 /announcement template <模板名> <玩家|all> 使用
templates:
  welcome:
    text: "<yellow>欢迎来到服务器！<click:suggest_command:'/register'><hover:show_text:'<green>点击注册账号'><gold>[点击注册]</gold></click></yellow>"
  welcome_new:
    text: "<light_purple><bold><click:suggest_command:'欢迎新的小伙伴来到梦想之都！'><hover:show_text:'<green>点击发送欢迎语'><yellow>欢迎新的小伙伴来到梦想之都！</yellow><gold>[发送欢迎语]</gold></click></bold></light_purple>"
  spawn:
    text: "<green>点击 <click:run_command:'/spawn'><hover:show_text:'<aqua>传送到主城'><gold>[传送]</gold></click> 返回主城</green>"
  help:
    text: "<white>需要帮助？<click:suggest_command:'/help'><hover:show_text:'<yellow>点击获取帮助'><gold>[帮助]</gold></click> <click:run_command:'/rules'><hover:show_text:'<yellow>查看服务器规则'><gold>[规则]</gold></click></white>"
  vote:
    text: "<light_purple>支持服务器！<click:open_url:'https://example.com/vote'><hover:show_text:'<green>点击前往投票'><gold>[投票]</gold></click></light_purple>"
  donation:
    text: "<gradient:#FFD700:#FFA500>[赞助]</gradient> <white>感谢 <gold>大佬</gold> 赞助服务器！点击 <click:suggest_command:'感谢大佬赞助服务器！'><hover:show_text:'<green>点击发送感谢语'><gold>[感谢大佬]</gold></click> 送上祝福！</white>"

# 三种点击事件的参考示例
example:
  suggest_command: "<white>点击 <click:suggest_command:'/help'><hover:show_text:'点击后聊天框会出现 /help'><gold>[填充命令]</gold></click> 测试</white>"
  run_command: "<white>点击 <click:run_command:'/spawn'><hover:show_text:'点击后执行 /spawn'><gold>[执行命令]</gold></click> 传送到主城</white>"
  open_url: "<white>点击 <click:open_url:'https://www.baidu.com'><hover:show_text:'点击打开百度'><gold>[打开网页]</gold></click> 访问官网</white>"

# AuthMe 集成（需同时安装 AuthMe 插件）
authme:
  enabled: true
  unregistered-message: "<yellow>欢迎来到服务器！请点击 <click:suggest_command:'/register 密码 在输入一次密码'><hover:show_text:'<green>点击注册账号'><gold>[注册]</gold></click> 完成注册</yellow>"
  register-broadcast: "<green>新成员</green> <white><gold>%player%</gold> 加入了梦想之都服务器！点击 <click:suggest_command:'欢迎 %player%，我是 %sender%，一起游玩吧！'><hover:show_text:'<green>点击发送欢迎消息'><gold>[打招呼]</gold></click> 欢迎新玩家！</white>"
```

### 如何新增模板
1. 打开配置文件 `plugins/ChatAnnouncement/config.yml`
2. 在 `templates:` 部分添加新模板
3. 保存配置文件
4. 执行 `/announcement reload` 重载配置
5. 使用模板 `/announcement template <模板名> <玩家|all>`

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

> ⚠️ **CI 门禁**：PR 必须通过 GitHub Actions + 覆盖率 ≥ 60% 才能合入。

### 构建项目
```bash
./gradlew build                    # 编译 + 测试 + 覆盖率 + 打包（全跑）
./gradlew test                     # 仅跑测试
./gradlew jacocoTestReport         # 生成覆盖率报告
./gradlew jacocoTestCoverageVerification  # 验证覆盖率门槛
./gradlew shadowJar                # 仅生成可分发 jar
```

### 🚀 发布流程（SOP）

```bash
# 1. 确认所有改动已提交
git status

# 2. 改版本号（build.gradle.kts）
#    version = "1.3.0"  ← 按 SemVer：MAJOR.MINOR.PATCH

# 3. 同步 CHANGELOG.md
#    在 [Unreleased] 写新版本，列出 Added / Changed / Fixed

# 4. 跑完整构建（含测试 + 覆盖率）
./gradlew clean build

# 5. 确认 jar 产物
ls -lh build/libs/ChatAnnouncement-*.jar

# 6. 提交 + 打 tag
git add .
git commit -m "release: v1.3.0"
git tag v1.3.0
git push origin main --tags

# 7. GitHub Actions 自动构建并上传制品
#    → 在 Release 页面 Draft a new release → 选择 v1.3.0 tag → 发布
```

#### 快速打包（跳过测试）

如果你已经在本地测过、只想快速出包：

```bash
./gradlew shadowJar -x test -x jacocoTestCoverageVerification
```

> ⚠️ 不要在 CI 流水线里用这个命令 —— CI 必须跑完整测试。

#### SemVer 版本号规则

| 改动类型 | 版本号变化 | 例子 |
|---------|----------|------|
| 不兼容 API 变更 | MAJOR++ | 1.2.0 → 2.0.0 |
| 向后兼容的新功能 | MINOR++ | 1.2.0 → 1.3.0 |
| 向后兼容的 bug 修复 | PATCH++ | 1.2.0 → 1.2.1 |
| 候选版 | `-RC<N>` 后缀 | 1.3.0-RC1 |
| 公测版 | `-beta.<N>` 后缀 | 1.3.0-beta.1 |

### 测试结构

| 测试类 | 用例数 | 覆盖范围 |
|--------|--------|---------|
| `ConfigServiceTest` | 7 | 类型安全配置、节点缺失、AuthMe 开关 |
| `MessageServiceTest` | 4 | MiniMessage 解析、PAPI 快速路径、降级 |
| `TemplateServiceTest` | 5 | 模板加载、`%player%` 替换、存在性 |
| `TargetResolverTest` | 7 | `all` / `@a` / 单玩家 / null / 大小写 |
| `I18nServiceTest` | 5 | 双语切换、缺失键、占位符 |
| **合计** | **26** | **Service + Config 全覆盖** |

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

> 详细版本变更请参见 [CHANGELOG.md](CHANGELOG.md)。

### v1.2.0 (2026-06-19) — 企业级重构 + 工程化

**业务层**
- ✅ 架构重构：拆出 Service 层（Config/Message/Target/Template/I18n），构造函数注入
- ✅ 错误隔离：MiniMessage 解析 try/catch，畸形消息降级到纯文本
- ✅ i18n 国际化：自动按 `player.locale()` 切换 zh_CN / en_US，UTF-8 资源控制
- ✅ PlaceholderAPI 集成：消息内支持 PAPI 占位符（可选依赖，反射调用）
- ✅ 类型安全配置：`ConfigService` 集中管理配置键路径，附带基础校验
- ✅ 可测试性：`TargetResolver.PlayerProvider` 抽象 Bukkit 依赖
- ✅ 26 个单元测试（JUnit 5 + Mockito）：覆盖 Service 层核心逻辑

**工程层**
- ✅ JaCoCo 0.8.11 集成：Service/Config 包强制 ≥ 60% 覆盖率
- ✅ GitHub Actions CI/CD：构建 + 测试 + Codecov + 制品自动归档
- ✅ MIT 许可证 + 规范化 CHANGELOG.md
- ✅ bStats 3.x 遥测：可选匿名统计
- ✅ 公共 API 收窄：移除 `getMessageManager()` getter
- ✅ 包结构清理：旧 `manager/MessageManager` 删除
- ✅ 依赖解耦：`compileOnly` / `implementation` 严格分离

### v1.1.0 (2026-02-20)
- ✅ 新增 AuthMe 集成功能
- ✅ 未注册玩家加入时自动发送注册提示
- ✅ 注册成功后全服广播欢迎消息
- ✅ 支持个性化欢迎消息（%player%、%sender% 变量）

### v1.0.0 (2026-02-20)
- ✅ 初始版本发布
- ✅ 支持点击事件（填充命令、执行命令、打开网页）
- ✅ 支持 MiniMessage 富文本格式
- ✅ 完整的指令系统和权限控制
- ✅ 模板系统，方便快速发送
- ✅ 配置文件热重载

## 🎯 项目状态

![Build](https://img.shields.io/badge/build-success-brightgreen.svg)
![Version](https://img.shields.io/badge/version-1.2.0-blue.svg)
![Minecraft Version](https://img.shields.io/badge/minecraft-1.21.x-green.svg)
![Tests](https://img.shields.io/badge/tests-26_passing-brightgreen.svg)
![Coverage](https://img.shields.io/badge/coverage-60%25+-yellow.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

---

**🎉 感谢使用 ChatAnnouncement 插件！**

如果觉得插件好用，欢迎给个 Star ⭐ 支持一下！
