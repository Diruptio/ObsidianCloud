plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 17
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
