package com.oliverastell.habitask.filemanagers

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine
import kotlinx.io.writeString
import me.sujanpoudel.utils.paths.appDataDirectory

data class DesktopPaths(
    val recentServers: Path,
    val defaultServerDirectory: Path
)

class DesktopFileManager(
    val root: Path
) {
    companion object {
        val defaultManager = DesktopFileManager(appDataDirectory("habitask"))
    }

    val paths = DesktopPaths(
        recentServers = Path(root, "recent_servers.txt"),
        defaultServerDirectory = Path(root, "servers")
    )

    init {
        SystemFileSystem.createDirectories(paths.defaultServerDirectory, true)
    }

    fun addRecentServer(recentServerPath: Path) {
        val subPaths = mutableListOf(recentServerPath)

        SystemFileSystem.source(this.paths.recentServers).buffered().use {
            var line = it.readLine()

            while (line != null) {
                val strippedLine = line.trim()
                if (strippedLine.isNotEmpty()) {
                    val path = Path(strippedLine)

                    if (!subPaths.contains(path))
                        subPaths.add(path)
                }

                line = it.readLine()
            }
        }

        SystemFileSystem.sink(this.paths.recentServers).buffered().use {
            for (path in subPaths) {
                it.writeString("$path\n")
            }
        }
    }

    fun getRecentServers(): List<Path> {
        val recents = mutableListOf<Path>()

        SystemFileSystem.source(this.paths.recentServers).buffered().use {
            var line = it.readLine()

            while (line != null) {
                val strippedLine = line.trim()
                if (strippedLine.isNotEmpty()) {
                    recents.add(Path(strippedLine))
                }

                line = it.readLine()
            }
        }

        return recents
    }


}