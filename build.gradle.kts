import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.Locale
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    java
}

val minecraftVersion: String by project
val neoForgeVersion: String by project

println("Building for Minecraft $minecraftVersion with NeoForge $neoForgeVersion")

abstract class ModrinthDownloadTask : DefaultTask() {
    @get:Input
    abstract val downloadUrl: Property<String>

    @get:Input
    @get:Optional
    abstract val expectedSha1: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun download() {
        val target = outputFile.get().asFile
        val expected = expectedSha1.orNull?.lowercase(Locale.ROOT)

        if (target.exists() && expected != null && target.sha1() == expected) {
            logger.info("Using cached {}", target.name)
            return
        }

        target.parentFile.mkdirs()
        val url = URL(downloadUrl.get())
        logger.lifecycle("Downloading {}", url)
        url.openStream().use { input ->
            Files.copy(input, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }

        if (expected != null) {
            val actual = target.sha1()
            check(actual == expected) {
                "Checksum mismatch for ${target.name}: expected $expected but was $actual"
            }
        }
    }

    private fun File.sha1(): String {
        val digest = MessageDigest.getInstance("SHA-1")
        inputStream().use { stream ->
            val buffer = ByteArray(8 * 1024)
            while (true) {
                val read = stream.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

data class OptionalDependency(
    val key: String,
    val version: String,
    val projectId: String,
    val versionId: String,
    val filename: String,
    val sha1: String
)

fun Project.registerOptionalDependency(
    dep: OptionalDependency,
    downloadDir: Provider<Directory>
): ConfigurableFileCollection {
    val capitalized = dep.key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    val downloadTask = tasks.register<ModrinthDownloadTask>("download$capitalized") {
        group = "modrinth"
        description = "Downloads ${dep.key} ${dep.version} from Modrinth"
        downloadUrl.set("https://cdn.modrinth.com/data/${dep.projectId}/versions/${dep.versionId}/${dep.filename}")
        expectedSha1.set(dep.sha1)
        outputFile.set(downloadDir.map { it.file(dep.filename) })
    }

    return objects.fileCollection().from(downloadTask.flatMap { it.outputFile }).builtBy(downloadTask)
}

fun Project.createOptionalDependencies(): List<OptionalDependency> {
    val root = rootProject
    fun property(name: String) = root.providers.gradleProperty(name).get()
    return listOf(
        OptionalDependency(
            key = "sodium",
            version = property("sodium.version"),
            projectId = property("sodium.projectId"),
            versionId = property("sodium.versionId"),
            filename = property("sodium.filename"),
            sha1 = property("sodium.sha1")
        ),
        OptionalDependency(
            key = "iris",
            version = property("iris.version"),
            projectId = property("iris.projectId"),
            versionId = property("iris.versionId"),
            filename = property("iris.filename"),
            sha1 = property("iris.sha1")
        ),
        OptionalDependency(
            key = "yacl",
            version = property("yacl.version"),
            projectId = property("yacl.projectId"),
            versionId = property("yacl.versionId"),
            filename = property("yacl.filename"),
            sha1 = property("yacl.sha1")
        ),
        OptionalDependency(
            key = "clothConfig",
            version = property("shedaniel.cloth.versionNumber"),
            projectId = property("shedaniel.cloth.project"),
            versionId = property("shedaniel.cloth.version"),
            filename = property("shedaniel.cloth.filename"),
            sha1 = property("shedaniel.cloth.sha1")
        )
    )
}

fun Project.ensureOptionalDependencyFiles(): Map<String, ConfigurableFileCollection> {
    val root = rootProject
    val extras = root.extensions.extraProperties
    if (extras.has("optionalDependencyFiles")) {
        @Suppress("UNCHECKED_CAST")
        return extras["optionalDependencyFiles"] as Map<String, ConfigurableFileCollection>
    }

    val downloadDir = root.layout.buildDirectory.dir("modrinth")
    val files = root.createOptionalDependencies().associate { dep ->
        dep.key to root.registerOptionalDependency(dep, downloadDir)
    }
    extras["optionalDependencyFiles"] = files
    return files
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

if (project == rootProject) {
    ensureOptionalDependencyFiles()

    val rootCompile = tasks.register("validateCompileMatrix") {
        group = "verification"
        description = "Compile every configured Stonecutter variant to ensure optional dependencies resolve."
    }
    val rootRuntime = tasks.register("validateRuntimeMatrix") {
        group = "verification"
        description = "Process resources for every configured Stonecutter variant."
    }
    val rootPortal = tasks.register("validatePortalFunctionMatrix") {
        group = "verification"
        description = "Confirm runtime classpaths for every configured Stonecutter variant."
    }

    gradle.projectsEvaluated {
        rootCompile.configure {
            dependsOn(rootProject.subprojects.map { it.tasks.named("validateCompileMatrix") })
        }
        rootRuntime.configure {
            dependsOn(rootProject.subprojects.map { it.tasks.named("validateRuntimeMatrix") })
        }
        rootPortal.configure {
            dependsOn(rootProject.subprojects.map { it.tasks.named("validatePortalFunctionMatrix") })
        }
    }
} else {
    val optionalDependencyFiles = ensureOptionalDependencyFiles()

    dependencies {
        implementation("net.neoforged:neoforge:${project.property("neoForgeVersion")}:userdev")
        optionalDependencyFiles.values.forEach { files ->
            add("compileOnly", files)
            if (project.name != "1.21.1-neoforge") {
                add("runtimeOnly", files)
            }
        }
    }

    tasks.register("printVersions") {
        doLast {
            println("Minecraft=" + project.findProperty("minecraftVersion"))
            println("NeoForge=" + project.findProperty("neoForgeVersion"))
        }
    }

    val classPathCheck = tasks.register("classPathCheck") {
        group = "verification"
        description = "Dump the runtime classpath for ${project.path}"
        doLast {
            val runtimeClasspath = configurations.getByName("runtimeClasspath").resolve()
            println(runtimeClasspath.joinToString(File.pathSeparator) { it.absolutePath })
        }
    }

    val compileClasspathCheck = tasks.register("compileClasspathCheck") {
        group = "verification"
        description = "Resolve the compile classpath for ${project.path}"
        doLast {
            val compileClasspath = configurations.getByName("compileClasspath").resolve()
            println(compileClasspath.joinToString(File.pathSeparator) { it.absolutePath })
        }
    }

    tasks.register("validateCompileMatrix") {
        group = "verification"
        description = "Compile ${project.path} to ensure optional dependencies resolve."
        dependsOn(compileClasspathCheck)
    }

    tasks.register("validateRuntimeMatrix") {
        group = "verification"
        description = "Process resources for ${project.path}."
        dependsOn(tasks.named("processResources"))
    }

    tasks.register("validatePortalFunctionMatrix") {
        group = "verification"
        description = "Confirm runtime classpath for ${project.path}."
        dependsOn(classPathCheck)
    }
}
