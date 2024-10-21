plugins {
    id("com.diffplug.spotless") version "6.25.0"
}

group = "diruptio"
version = "0.3.0"

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
        palantirJavaFormat("2.48.0").formatJavadoc(true)
        removeUnusedImports()
        indentWithSpaces()
        endWithNewline()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}
