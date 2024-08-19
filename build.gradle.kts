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

subprojects {
    group = rootProject.group
    version = rootProject.version
}
