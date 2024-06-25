package io.github.karmasmp.karma

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import java.io.File
import java.nio.file.Files

object PluginRebuilder {
    private const val REPO_URL = "https://github.com/KarmaSMP/Karma.git"
    private const val SUCCESS_CODE = 0

    private val INFO_STYLE = Style.style(NamedTextColor.BLUE, TextDecoration.BOLD)

    fun rebuild(branch: String) {
        val tempDir = Files.createTempDirectory("karma-rebuild").toFile()
        tempDir.deleteOnExit()

        cloneRepo(tempDir)
        checkoutBranch(branch, tempDir)
        gradleBuild(tempDir)
        copyJarToPluginsDirectory(tempDir)
        stopServer()
    }

    private fun cloneRepo(tempDir: File) {
        log("Cloning repo into ${tempDir.path}")
        val pb = ProcessBuilder("git", "clone", REPO_URL, ".")
            .directory(tempDir)

        val exitCode = pb.start().waitFor()
        if (exitCode != SUCCESS_CODE) failedOnStep("Clone")
    }

    private fun checkoutBranch(branch: String, tempDir: File) {
        log("Checking out $branch")

        val pb = ProcessBuilder("git", "checkout", branch)
            .directory(tempDir)

        val exitCode = pb.start().waitFor()
        if (exitCode != SUCCESS_CODE) failedOnStep("Checkout")
    }

    private fun gradleBuild(tempDir: File) {
        log("Running gradlew shadowJar task")

        val pb = ProcessBuilder("bash", "-c","./gradlew", "shadowJar")
            .directory(tempDir)
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        pb.redirectError(ProcessBuilder.Redirect.INHERIT)

        val exitCode = pb.start().waitFor()
        if (exitCode != SUCCESS_CODE) failedOnStep("Gradle shadowJar")
    }

    private fun copyJarToPluginsDirectory(tempDir: File) {
        log("Copying jar to plugins dir")
        val pluginDir = plugin.dataFolder.parentFile
        log(pluginDir.absolutePath)
        val oldJarFile = pluginDir.listFiles()!!.find { it.isFile && it.name.startsWith("Karma") && it.name.endsWith(".jar") }
        oldJarFile?.deleteOnExit()

        val libsDir = tempDir.toPath().resolve("build/libs").toFile()

        val newJarFile = libsDir.listFiles()!!.find { it.name.startsWith("Karma") && it.name.endsWith(".jar") }!!
        Files.copy(newJarFile.toPath(), pluginDir.resolve(newJarFile.name).toPath())
    }

    private fun stopServer() {
        Bukkit.broadcast(Component.text("Plugin rebuild complete, stopping server in 10 seconds").style(INFO_STYLE))
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            Bukkit.shutdown()
        }, 10 * 20)
    }

    private fun log(message: String) {
        Bukkit.broadcast(Component.text(message).style(INFO_STYLE))
    }

    private fun failedOnStep(step: String) {
        Bukkit.broadcast(Component.text("FAILED ON STEP: $step", NamedTextColor.RED, TextDecoration.BOLD))
        throw RuntimeException("Plugin rebuilder failed: $step")
    }
}
