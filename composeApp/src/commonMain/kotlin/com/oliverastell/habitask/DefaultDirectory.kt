package com.oliverastell.habitask

import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class DefaultDirectory(val path: Path = Path(".")) {
    companion object {
        val defaultDirectory = DefaultDirectory()
    }



    init {



    }

}