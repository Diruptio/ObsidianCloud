plugins {
    id("com.diffplug.spotless") version "6.25.0"
}

group = "diruptio"
version = "0.1.0"

repositories {
    mavenCentral()
}

spotless {
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
        endWithNewline()
    }
    java {
        target("**/src/**/*.java")
        googleJavaFormat().aosp()
        removeUnusedImports()
        indentWithSpaces()
        endWithNewline()
    }
}

tasks {
    register<Zip>("createReleaseBundle") {
        group = "distribution"
        dependsOn(build)

        val dir = layout.buildDirectory.dir("generated/releaseBundle").get().asFile
        dir.deleteRecursively()
        dir.mkdirs()
        dir.resolve("start.bat").writeText("java -Xmx256M -jar OCNode.jar")
        dir.resolve("start.sh").writeText("java -Xmx256M -jar OCNode.jar")
        val nodeJar = project(":node").tasks.named("jar").get().outputs.files.first()
        nodeJar.copyTo(dir.resolve("OCNode.jar"))
        from(dir)
        archiveFileName = "ObsidianCloud.zip"
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}
