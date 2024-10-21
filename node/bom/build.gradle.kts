plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":node:loader"))
    implementation(project(":node"))
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
