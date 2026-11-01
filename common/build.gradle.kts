plugins {
    kotlin("jvm")
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("fabric", "neoforge")
    minecraft = property("minecraft_version") as String
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    // Fabric API must be here too: Cobblemon is remapped once and cached, and it needs Fabric API's class hierarchy to map correctly
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    modCompileOnly("com.cobblemon:fabric:${property("cobblemon_version")}")
    // In-game config screen (optional at runtime: only used when Cloth Config is installed)
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${property("cloth_config_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
    compileOnly("com.google.code.gson:gson:2.11.0")
    compileOnly("net.fabricmc:sponge-mixin:0.15.4+mixin.0.8.7")
}
