plugins {
    id("java")
}

group = "diruptio"
version = "0.0.1-alpha"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-all:4.1.110.Final")
    implementation("com.google.guava:guava:31.1-jre")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks.test {
    useJUnitPlatform()
}