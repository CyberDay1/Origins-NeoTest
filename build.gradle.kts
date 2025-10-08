plugins {
    java
}

val minecraftVersion: String by project
val neoForgeVersion: String by project

println("Building for Minecraft " + minecraftVersion + " with NeoForge " + neoForgeVersion)

allprojects {
    apply(plugin = "java")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    repositories {
        mavenCentral()
        maven("https://maven.neoforged.net/releases")
    }

    dependencies {
        implementation("net.neoforged:neoforge:${neoForgeVersion}:userdev")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        enabled = false
    }
}


tasks.register("printVersions") {
    doLast {
        println("Minecraft=" + project.findProperty("minecraftVersion"))
        println("NeoForge=" + project.findProperty("neoForgeVersion"))
    }
}
