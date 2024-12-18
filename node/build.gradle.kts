plugins {
    id("java")
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":node:plugin-api"))
    compileOnly("org.jetbrains:annotations:26.0.1")
    implementation("commons-io:commons-io:2.17.0")
}

val addPlatformJars =
    tasks.register<Copy>("addPlatformJars") {
        val paperTask = project(":platform:paper").tasks.named("reobfJar").get()
        dependsOn(paperTask)
        val velocityTask = project(":platform:velocity").tasks.named("jar").get()
        dependsOn(velocityTask)
        doFirst { delete(layout.buildDirectory.dir("generated/sources/resources").get()) }
        from(paperTask.outputs, velocityTask.outputs)
        into(layout.buildDirectory.dir("generated/sources/resources"))
    }
sourceSets.main.get().resources.srcDir(addPlatformJars.map { it.outputs })

val generateSources =
    tasks.register<Copy>("generateSources") {
        val paperTask = project(":platform:paper").tasks.named("reobfJar").get()
        dependsOn(paperTask)
        val velocityTask = project(":platform:velocity").tasks.named("jar").get()
        dependsOn(velocityTask)
        doFirst { delete(layout.buildDirectory.dir("generated/sources/java").get()) }
        from(file("src/main/templates"))
        into(layout.buildDirectory.dir("generated/sources/java"))
        expand(
            mapOf(
                "version" to version,
                "paper_platform_file" to paperTask.outputs.files.first().name,
                "velocity_platform_file" to velocityTask.outputs.files.first().name,
            ),
        )
    }
sourceSets.main.get().java.srcDir(generateSources.map { it.outputs })

java {
    withSourcesJar()
}

tasks {
    compileJava {
        dependsOn(addPlatformJars)
        dependsOn(generateSources)
        options.encoding = "UTF-8"
        options.release = 17
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveFileName = "OCNode.jar"
        manifest.attributes["Implementation-Title"] = "ObsidianCloud"
        manifest.attributes["Implementation-Version"] = version
        manifest.attributes["Main-Class"] = "de.obsidiancloud.node.ObsidianCloudNode"
    }

    named<JavaExec>("run") {
        workingDir = rootProject.file("run")
        workingDir.mkdirs()
        standardOutput = System.out
        standardInput = System.`in`
    }
}

application {
    mainClass = "de.obsidiancloud.node.ObsidianCloudNode"
}
