package com.vector.svg2vectorandroid

object Runner {
  @JvmStatic
  fun main(args: Array<String>) {
    if (args.isEmpty()) {
      println("Provide source directory as first argument for svg files to be converted\n example: java -jar Svg2VectorAndroid-1.0.jar <SourceDirectoryPath> ")
      return
    }

    val sourceDirectory = args[0]
    val processor: SvgFilesProcessor
    if (args.size == 1) {
      processor = SvgFilesProcessor(sourceDirectory)
    } else {
      val destinationDirectory = args[1]
      processor = SvgFilesProcessor(sourceDirectory, destinationDirectory)
    }
    processor.process()
  }
}
