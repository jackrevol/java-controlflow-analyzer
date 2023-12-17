plugins {
    id("java")
}

group = "com.jackrevol"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.36.0")

    implementation("org.projectlombok:lombok:1.18.20")
    implementation("com.google.guava:guava:30.1-jre")
    annotationProcessor("org.projectlombok:lombok:1.18.20")


    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}