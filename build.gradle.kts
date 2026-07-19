import java.math.BigDecimal

plugins {
    java
    jacoco
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.example"
version = "1.2.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.bstats.org/content/repositories/public/")
}

dependencies {
    // 运行时 API（仅在生产环境由服务器提供，不打包进 jar）
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    // 可选依赖
    compileOnly("fr.xephi:authme:5.6.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    // bStats：必须打进 jar（不是 compileOnly）
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // 测试：把 Bukkit API / AuthMe 也加进 testImplementation 以让测试运行时能找到
    // 仍然是 compileOnly（不进生产 jar），只是测试阶段需要
    testImplementation("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    testImplementation("fr.xephi:authme:5.6.0-SNAPSHOT")
    testImplementation("org.bstats:bstats-bukkit:3.0.2")

    // 测试框架
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
}

tasks.processResources {
    expand("version" to version)
}

tasks.shadowJar {
    archiveClassifier.set("")
    // bStats 改写包名到本插件私有命名空间，避免和其他插件冲突
    relocate("org.bstats", "com.example.announcement.libs.bstats")
    // 合并 META-INF/services（bStats 用 ServiceLoader）
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
    }
    finalizedBy(tasks.jacocoTestReport)
}

// JaCoCo 覆盖率报告
jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

// 校验覆盖率下限：核心 Service 层必须 >= 60%
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "PACKAGE"
            includes = listOf("com.example.announcement.service.*",
                              "com.example.announcement.config.*")
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"  // 校验覆盖率（不是覆盖率绝对值）
                minimum = BigDecimal("0.60")
            }
        }
    }
}
