package com.vector.svg2vectorandroid

import java.nio.file.Path
import java.nio.file.Paths

data class SvgFilesProcessorConfig(
  private val sourceDirectory: String,
  private val destinationDirectory: String?,
  val logSkipped: Boolean = false,
) {
  val source: Path = Paths.get(sourceDirectory).normalize()
  val destination: Path = destinationDirectory?.let(Paths::get)?.normalize() ?: source.resolve("generated")
}
