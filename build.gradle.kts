plugins {   
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}
dependencies {  
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.test {
  useJUnitPlatform()
}

application {   
    // Define the main class for the application.
    mainClass = "nanollama.Main"
}
