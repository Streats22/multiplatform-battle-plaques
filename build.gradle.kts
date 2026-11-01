plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("dev.architectury.loom") version "1.11-SNAPSHOT" apply false
    // Lets Loom remap the Kotlin metadata inside Cobblemon so its dev client can start
    kotlin("jvm") version "2.2.20" apply false
}

subprojects {
    apply(plugin = "java")

    group = property("mod_group") as String
    version = property("mod_version") as String

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    repositories {
        maven("https://maven.parchmentmc.org")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.impactdev.net/repository/development/")
        maven("https://maven.terraformersmc.com/releases/")
        exclusiveContent {
            forRepository { maven("https://api.modrinth.com/maven") }
            filter { includeGroup("maven.modrinth") }
        }
        mavenCentral()
    }
}
