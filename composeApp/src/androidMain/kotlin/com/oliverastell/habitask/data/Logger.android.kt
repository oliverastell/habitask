package com.oliverastell.habitask.data

import android.util.Log


actual fun loggerStaticBindings(logger: Logger) {
    logger.info.bind("INFO", protocol = { Log.i("habitask", "$it") })
    logger.debug.bind("DEBUG", protocol = { Log.i("habitask", "$it") })
    logger.warning.bind("WARNING", protocol = { Log.i("habitask", "$it") })
    logger.error.bind("ERROR", protocol = { Log.i("habitask", "$it") })
}