plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":node"))
    compileOnly("org.jetbrains:annotations:24.1.0")
}

val addPlatformJars =
    tasks.register<Copy>("addPlatformJars") {
        val paperTask = parent?.project("sync-paper")?.tasks?.named("reobfJar")?.get()
        dependsOn(paperTask)
        doFirst { delete(layout.buildDirectory.dir("generated/sources/resources").get()) }
        from(paperTask?.outputs)
        into(layout.buildDirectory.dir("generated/sources/resources"))
    }
sourceSets.main.get().resources.srcDir(addPlatformJars.map { it.outputs })

tasks {
    compileJava {
        dependsOn(addPlatformJars)
        options.encoding = "UTF-8"
        options.release = 17
    }

    processResources {
        filesMatching("module.yml") {
            expand(mapOf("version" to version))
        }
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveBaseName = "OCSync"
    }
}
