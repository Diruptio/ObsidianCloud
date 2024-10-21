plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.0.1")
}

val generateSources =
    tasks.register<Copy>("generateSources") {
        doFirst { delete(layout.buildDirectory.dir("generated/sources/templates").get()) }
        from(file("src/main/templates"))
        into(layout.buildDirectory.dir("generated/sources/templates"))
        expand(mapOf("version" to version))
    }
sourceSets.main.get().java.srcDir(generateSources.map { it.outputs })

tasks {
    compileJava {
        dependsOn(generateSources)
        options.encoding = "UTF-8"
        options.release = 17
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveBaseName = "OCVelocity"
    }
}
