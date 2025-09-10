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
    id("org.javamodularity.moduleplugin") version "1.8.15"
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
    implementation("com.azure:azure-ai-translation-text:1.1.5") {
        // sine 2.0.66.Final, the Automatic-Module-Name is not set correctly in MANIFEST.MF
        exclude(group = "io.netty", module = "netty-tcnative-boringssl-static")
    }
//    implementation(platform("io.netty:netty-parent:4.1.127.Final"))
    implementation("io.netty:netty-tcnative-boringssl-static:2.0.61.Final")
    implementation("io.projectreactor.tools:blockhound:1.0.9.RELEASE")
    implementation("io.micrometer:context-propagation:1.1.1")

//    testImplementation("org.testfx:testfx-core:4.0.18")
//    testImplementation("org.testfx:testfx-junit:4.0.18")
//    testImplementation("junit:junit:4.13.1")
}



