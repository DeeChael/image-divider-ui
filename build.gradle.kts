import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import proguard.gradle.ProGuardTask

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.0")
    }
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "net.deechael"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        buildTypes.release.proguard {
            isEnabled.set(false)
        }

        nativeDistributions {
            modules("jdk.unsupported")

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "image-divider-ui"
            packageVersion = "1.0.0"

            windows {
                iconFile.set(project.file("src/main/resources/icon/image-divider-icon.ico"))
            }
        }
    }
}