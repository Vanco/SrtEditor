buildscript {
    repositories {
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
    }
}

plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.beryx.jlink") version "3.0.1"
}

group = "io.vanstudio"
version = "1.1.2"

repositories {
    maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    mavenCentral()
    maven("https://maven.aliyun.com/repository/gradle-plugin")
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
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

jlink {
    options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages", "--ignore-signing-information")
    launcher{
        name = "SrtEdit"
        jvmArgs = listOf(/*"-Dlog4j.configurationFile=./log4j2.xml",*/"-Djava.locale.providers=HOST,CLDR,COMPAT")
    }
    jpackage {
        icon = "src/main/resources/icons/srte.icns"
    }
}

dependencies {
    implementation("com.azure:azure-ai-translation-text:1.0.0")
    implementation("io.projectreactor.tools:blockhound:1.0.9.RELEASE")
    implementation("io.micrometer:context-propagation:1.1.1")

//    testImplementation("org.testfx:testfx-core:4.0.18")
//    testImplementation("org.testfx:testfx-junit:4.0.18")
//    testImplementation("junit:junit:4.13.1")
}



