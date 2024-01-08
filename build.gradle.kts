plugins {
    id("java")
    id("maven-publish")
}

group = "com.jackrevol"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.36.0")

    implementation("org.projectlombok:lombok:1.18.20")
    implementation("com.google.guava:guava:30.1-jre")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
    implementation("org.slf4j:slf4j-api:2.0.9")


    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")


    testImplementation("org.graphstream:gs-core:2.0")
    testImplementation("org.graphstream:gs-ui-swing:2.0")
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}