plugins {
    id("java")
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":node:plugin-api"))
    compileOnly("org.jetbrains:annotations:25.0.0")
    implementation("net.lenni0451.classtransform:core:1.14.0")
    implementation("net.lenni0451.classtransform:additionalclassprovider:1.14.0")
    implementation("net.lenni0451.classtransform:mixinstranslator:1.14.0")
    implementation("net.lenni0451.classtransform:mixinsdummy:1.14.0")
}

val addJars =
    tasks.register<Copy>("addJars") {
        val nodeTask = project(":node").tasks.named("jar").get()
        dependsOn(nodeTask)
        from(nodeTask.outputs)
        into(layout.buildDirectory.dir("generated/sources/resources"))
    }
sourceSets.main.get().resources.srcDirs(addJars.map { it.outputs })

tasks {
    compileJava {
        dependsOn(addJars)
        options.encoding = "UTF-8"
        options.release = 17
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveBaseName = "OCNode"
        manifest.attributes["Implementation-Title"] = "ObsidianCloud"
        manifest.attributes["Implementation-Version"] = version
        manifest.attributes["Main-Class"] = "de.obsidiancloud.node.ObsidianCloudNodeLoader"
    }

    named<JavaExec>("run") {
        workingDir = rootProject.file("run")
        workingDir.mkdirs()
        standardOutput = System.out
        standardInput = System.`in`
    }
}

application {
    mainClass = "de.obsidiancloud.node.ObsidianCloudNodeLoader"
}
