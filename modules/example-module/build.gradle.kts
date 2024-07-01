plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":node"))
}

tasks {
    compileJava {
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
        archiveBaseName = "example-module"
    }
}
