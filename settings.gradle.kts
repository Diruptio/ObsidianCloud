pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "ObsidianCloud"

include("common")
include("node")
include("platform")
include("platform:paper")
include("platform:velocity")
include("modules:example-module")
