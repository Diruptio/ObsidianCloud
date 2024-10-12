plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":node"))
    compileOnly("org.jetbrains:annotations:25.0.0")
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
        archiveBaseName = "ExamplePlugin"
        doLast {
            copy {
                from(archiveFile)
                into(rootProject.file("run/plugins"))
            }
        }
    }
}
