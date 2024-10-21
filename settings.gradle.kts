pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "ObsidianCloud"

include("common")
include("node")
include("node:bom")
include("node:loader")
include("node:plugin-api")
include("platform")
include("platform:paper")
include("platform:velocity")
include("plugins:example-plugin")
