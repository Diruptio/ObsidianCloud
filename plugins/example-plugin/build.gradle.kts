plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":node"))
}

java {
    withSourcesJar()
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 17
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveFileName = "ExamplePlugin.jar"
        doLast {
            copy {
                from(archiveFile)
                into(rootProject.file("run/plugins"))
            }
        }
    }
}
