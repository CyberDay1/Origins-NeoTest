plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
}

rootProject.name = "Origins-NeoTest"

stonecutter {
    // Uses stonecutter.json in project root by default.
    create(rootProject, file("stonecutter.json"))
}

