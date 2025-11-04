package com.oliverastell.habitask

import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine
import me.sujanpoudel.utils.paths.appDataDirectory

//import me.sujanpoudel.utils.paths.appDataDirectory

data class DesktopDirectory(val path: Path = appDataDirectory("habitask")) {
    companion object {
        val defaultDirectory = DesktopDirectory()
    }

    private val recentServersPath = Path(path, "recent_servers.txt")
    private val defaultServersDirectory = Path(path, "server")

    init {
        if (!SystemFileSystem.exists(recentServersPath))
            SystemFileSystem.sink(recentServersPath)

        if (!SystemFileSystem.exists(defaultServersDirectory))
            SystemFileSystem.createDirectories(defaultServersDirectory)
    }

    fun getRecentServers(): List<Path> {
        val recents = mutableListOf<Path>()

        SystemFileSystem.source(recentServersPath).buffered().apply {
            var line = readLine()
            while (line != null) {
                recents.add(Path(line.trim()))
                line = readLine()
            }
        }

        return recents
    }

}

