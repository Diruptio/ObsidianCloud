plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform"))
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.0.1")
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
        archiveBaseName = "OCPaper"
    }

    reobfJar {
        outputJar = file("build/libs/${jar.get().archiveBaseName.get()}-${jar.get().archiveVersion.get()}.jar")
    }

    assemble {
        dependsOn(reobfJar)
    }
}
