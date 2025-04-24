plugins {
    id("buildlogic.java-application-conventions")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation("org.apache.pulsar:pulsar-client-admin:4.0.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("info.picocli:picocli:4.7.7")
    implementation("org.apache.pulsar:pulsar-client:2.11.0")

    annotationProcessor("info.picocli:picocli-codegen:4.7.7")

    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

application {
    // Define the main class for the application.
    mainClass = "com.luxepricing.pulsarstats.App"
}

tasks {
    // Configure the ShadowJar task to create an executable fat jar
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("fat")
        mergeServiceFiles()
    }

    named("nativeBuild") {
        dependsOn("build")
    }

    test {
        exclude("**/org/example/app/**")
    }
}
