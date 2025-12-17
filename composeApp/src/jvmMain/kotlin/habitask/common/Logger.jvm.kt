package habitask.common

actual fun loggerStaticBindings(logger: Logger) {
    logger.info.bind("INFO", protocol = ::println)
    logger.debug.bind("DEBUG", protocol = ::println)
    logger.warning.bind("WARNING", protocol = ::println)
    logger.error.bind("ERROR", protocol = ::println)
}