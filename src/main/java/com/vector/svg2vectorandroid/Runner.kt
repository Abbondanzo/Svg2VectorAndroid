package com.vector.svg2vectorandroid

import kotlin.system.exitProcess

object Runner {
  @JvmStatic
  fun main(args: Array<String>) {
    if (args.isEmpty()) {
      println("Provide source directory as first argument for svg files to be converted\n example: java -jar Svg2VectorAndroid.jar <SourceDirectoryPath>")
      exitProcess(1)
    }

    val sourceDirectory = args[0]
    val destinationDirectory = args.getOrNull(1)
    val config = SvgFilesProcessorConfig(sourceDirectory, destinationDirectory)
    val processor = SvgFilesProcessor(config)

    try {
      processor.process()
    } catch (e: Throwable) {
      System.err.println(e.message)
      exitProcess(1)
    }
  }
}
