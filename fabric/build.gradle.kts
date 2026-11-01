plugins {
    kotlin("jvm")
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    platformSetupLoomIde()
    fabric()
    minecraft = property("minecraft_version") as String
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    implementation(project(":common", configuration = "namedElements"))

    // Mod Menu adds the config button; Cloth Config draws the screen (both from Maven)
    modCompileOnly("com.terraformersmc:modmenu:${property("modmenu_version")}")
    modLocalRuntime("com.terraformersmc:modmenu:${property("modmenu_version")}")
    modLocalRuntime("me.shedaniel.cloth:cloth-config-fabric:${property("cloth_config_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    // Dev client runs next to the real Cobblemon (its pom brings Fabric Language Kotlin and friends)
    modLocalRuntime("com.cobblemon:fabric:${property("cobblemon_version")}")

    // RCT API needs Architectury API 13.0.2+ and Fabric Loader 0.18+
    modLocalRuntime("dev.architectury:architectury-fabric:${property("architectury_api_version")}")

    // Radical Cobblemon Trainers, so RCT trainer battles can be tested in the dev client. The jars are not in the
    // repo (libs/ is gitignored), so they are only added when they exist; the build works without them.
    listOf("rctapi-fabric-1.21.1-0.16.2-beta.jar", "rctmod-fabric-1.21.1-0.19.2-beta.jar")
        .map { rootProject.file("libs/$it") }
        .filter { it.exists() }
        .forEach { modLocalRuntime(files(it)) }
    modLocalRuntime("maven.modrinth:forge-config-api-port:v21.1.6-1.21.1-Fabric")
    // RCT reads its config with Night Config, which Forge Config API Port does not bring along in a dev environment
    runtimeOnly("com.electronwill.night-config:core:3.8.1")
    runtimeOnly("com.electronwill.night-config:toml:3.8.1")
}

// Package the shared code (and the mixin config) from :common into this jar; remapJar then maps it to production names.
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(project(":common").sourceSets.main.get().output)
}

// Dev client only: LWJGL 3.3.3 (Minecraft 1.21.1's version) crashes in GLFW on new macOS, so run on 3.3.6.
// lwjgl-freetype stays on Mojang's version because its macOS patch jar only exists for 3.3.3.
configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.lwjgl" && requested.name != "lwjgl-freetype") useVersion("3.3.6")
    }
}

tasks.processResources {
    // Shared assets (textures, mixin config) live in :common; Fabric only finds a mod's assets in its own resources
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(project(":common").file("src/main/resources"))
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") { expand("version" to project.version) }
}
