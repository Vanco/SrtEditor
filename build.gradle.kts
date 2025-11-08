buildscript {
    repositories {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        gradlePluginPortal()
    }
}

plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.javamodularity.moduleplugin") version "2.0.0"
    id("org.beryx.jlink") version "3.1.3"
}

group = "io.vanstudio"
version = "1.1.2"

repositories {
    maven("https://mirrors.huaweicloud.com/repository/maven/")
    mavenCentral()
}

tasks.withType(JavaCompile::class.java).configureEach {
    options.encoding = "UTF-8"
}

application {
    mainModule = "io.vanstudio.srt"
    mainClass = "io.vanstudio.srt.Main"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
//    modularity.inferModulePath.set(false)
}

javafx {
    version = "23.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.base")
}

jlink {
    addOptions(
        "--strip-debug",
        "--compress=zip-6",
        "--no-header-files",
        "--no-man-pages",
        "--ignore-signing-information"
    )
    launcher {
        name = "SrtEditor"
        jvmArgs = listOf(
            "-Dorg.slf4j.simpleLogger.logFile=System.out",
            "-Djava.util.logging.config.file=./logging.properties",
            "-Djava.locale.providers=HOST,CLDR,COMPAT",
            "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug",
            "-Dreactor.netty.native=false",
        )
    }

    val os = org.gradle.internal.os.OperatingSystem.current()
    jpackage {
        icon = "src/main/resources/icons/srte.icns"

        // 正确的 jpackage 参数配置
        if (os.isMacOsX) {
            // macOS 特定的配置
            imageOptions = listOf(
                "--verbose",
                "--resource-dir", "src/main/resources"
            )
            appVersion = version.toString()
            vendor = "VanStudio"
            description = "Simple Srt Subtitle editor"
        }
    }

    addExtraDependencies("javafx")

    forceMerge(
        "netty-tcnative",
        "slf4j"
    )

    jarExclude("netty", "**/license/")
}

dependencies {
    implementation("com.azure:azure-ai-translation-text:1.1.7")
    implementation("io.projectreactor.tools:blockhound:1.0.15.RELEASE")
    implementation("io.micrometer:context-propagation:1.1.3")
    implementation("org.slf4j:slf4j-simple:2.0.17")
}




