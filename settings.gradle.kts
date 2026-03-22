rootProject.name = "nanollama"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("junit", "5.10.0")
            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
        }
    }
}
