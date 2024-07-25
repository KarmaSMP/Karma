import java.io.BufferedReader

val commitHash = Runtime
    .getRuntime()
    .exec(arrayOf("git", "rev-parse", "--short", "HEAD"))
    .let { process ->
        process.waitFor()
        val output = process.inputStream.use {
            it.bufferedReader().use(BufferedReader::readText)
        }
        process.destroy()
        output.trim()
    }

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("kapt") version "2.0.0"
    id("io.github.goooler.shadow") version "8.1.7"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "io.karmasmp"
version = "1.0-$commitHash"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    implementation("org.incendo:cloud-core:2.0.0-rc.2")
    implementation("org.incendo:cloud-paper:2.0.0-beta.9")
    implementation("org.incendo:cloud-annotations:2.0.0-rc.2")
    implementation("org.incendo:cloud-kotlin-coroutines-annotations:2.0.0-rc.2")
    kapt("org.incendo:cloud-kotlin-coroutines-annotations:2.0.0-rc.2")
    implementation("org.incendo:cloud-kotlin-extensions:2.0.0-rc.2")
    implementation("org.incendo:cloud-processors-confirmation:1.0.0-beta.3")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        javaParameters = true
    }
}

tasks {
    shadowJar {
        isEnableRelocation = true
        relocationPrefix = "io.github.karmasmp.shade"
    }
    runServer {
        minecraftVersion("1.21")
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}