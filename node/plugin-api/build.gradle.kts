plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":common"))
    compileOnly("org.jetbrains:annotations:26.0.1")
    compileOnly("net.lenni0451.classtransform:core:1.14.0")
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
