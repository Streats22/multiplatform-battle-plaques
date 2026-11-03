plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
    minecraft = property("minecraft_version") as String
}

// :common is compiled against (common, not bundled as-is); shadowBundle is the copy of :common that Architectury
// has transformed for NeoForge (mixin refmap and @ExpectPlatform), which goes inside the final jar.
val common: Configuration by configurations.creating
val shadowBundle: Configuration by configurations.creating
configurations {
    compileClasspath { extendsFrom(common) }
    runtimeClasspath { extendsFrom(common) }
    named("developmentNeoForge") { extendsFrom(common) }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    "neoForge"("net.neoforged:neoforge:${property("neoforge_version")}")

    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":common", configuration = "transformProductionNeoForge"))
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("META-INF/neoforge.mods.toml") { expand("version" to project.version) }
    // NeoForge runs on Mojang names, the same names the mixins are written in, so it needs no refmap.
    // The refmap from :common maps to Fabric's intermediary names and would point the mixins at classes that don't exist.
    from(project(":common").file("src/main/resources/battleplacks.mixins.json")) {
        filter { line -> if (line.contains("\"refmap\"")) null else line }
        rename { "battleplacks-neoforge.mixins.json" }
    }
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier.set("dev-shadow")
    // NeoForge gets its own copy of the mixin config from processResources (see there); the refmap is not needed at all
    exclude("battleplacks.refmap.json", "battleplacks.mixins.json")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}
