plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":platform"))
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 17
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(mapOf("version" to version))
        }
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    reobfJar {
        outputJar = file("build/libs/OCSyncPaper.jar")
    }

    assemble {
        dependsOn(reobfJar)
    }
}
