pluginManagement {
    repositories {
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        gradlePluginPortal()
    }
}

rootProject.name = "cobblemon-battleplacks"

include("common", "fabric")
// NeoForge build is not working yet (macOS natives fail to resolve); enable with -PwithNeoForge to debug it
if (providers.gradleProperty("withNeoForge").isPresent) include("neoforge")
