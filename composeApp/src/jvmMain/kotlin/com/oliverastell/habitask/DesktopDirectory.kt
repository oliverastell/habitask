package com.oliverastell.habitask

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem


data class DesktopDirectory(val path: Path = Path(".")) {
    companion object {
        val defaultDirectory = DesktopDirectory()
    }

    private val recentServersPath = Path(path, "recent_servers.txt")

    fun getRecentServers(): List<Path> {
        val recents = mutableListOf<Path>()

        SystemFileSystem.sink(recentServersPath).apply {
            var line = readlnOrNull()
            while (line != null) {
                recents += Path(line.trim())
                line = readlnOrNull()
            }
        }

        return recents
    }

}

