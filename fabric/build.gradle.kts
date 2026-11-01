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

    // Dev client runs next to the real Cobblemon (its pom brings Fabric Language Kotlin and friends)
    modLocalRuntime("com.cobblemon:fabric:${property("cobblemon_version")}")
}

// Package the shared code (and the mixin config) from :common into this jar; remapJar then maps it to production names.
tasks.jar {
    from(project(":common").sourceSets.main.get().output)
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") { expand("version" to project.version) }
}
