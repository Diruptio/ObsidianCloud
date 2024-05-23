plugins {
    id("java")
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    compileOnly("org.jetbrains:annotations:24.1.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("net.kyori:adventure-api:4.15.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.15.0")
    implementation("org.springframework:spring-core:6.1.7")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 17
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveBaseName = "OCNode"
        manifest.attributes["Implementation-Title"] = "ObsidianCloud"
        manifest.attributes["Implementation-Version"] = version
        manifest.attributes["Main-Class"] = "de.obsidiancloud.node.Node"
    }
}

application {
    mainClass = "de.obsidiancloud.node.Node"
    tasks.getByName("run", JavaExec::class) {
        workingDir = file("run")
        standardOutput = System.out
        standardInput = System.`in`
    }
}
