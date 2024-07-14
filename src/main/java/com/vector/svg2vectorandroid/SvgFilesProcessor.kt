package com.vector.svg2vectorandroid

import com.android.ide.common.vectordrawable.Svg2Vector
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.EnumSet
import kotlin.io.path.Path

class SvgFilesProcessor(private val config: SvgFilesProcessorConfig) {

  @Throws(IllegalStateException::class)
  fun process() {
    try {
      val options = EnumSet.of(FileVisitOption.FOLLOW_LINKS)
      // Check if source directory exists
      if (!Files.isDirectory(config.source)) {
        error("Source directory does not exist")
      }
      // Create destination directory if it does not yet exist
      if (!Files.isDirectory(config.destination)) {
        Files.createDirectory(config.destination)
      }

      Files.walkFileTree(config.source, options, Int.MAX_VALUE, object : SimpleFileVisitor<Path>() {
        @Throws(IOException::class)
        override fun visitFile(
          file: Path,
          attrs: BasicFileAttributes
        ): FileVisitResult {
          try {
            convertToVector(file, config.destination.resolve(config.source.relativize(file)))
          } catch (e: IOException) {
            System.err.println("Error reading file: ${e.message}")
          } catch (e: IllegalStateException) {
            System.err.println("Error parsing svg: ${e.message}")
          } catch (e: Exception) {
            System.err.println("Unknown error: ${e.message}")
          }
          return FileVisitResult.CONTINUE
        }
      })
    } catch (e: IOException) {
      error("Error reading from files: ${e.message}")
    } catch (e: Exception) {
      error("Unknown error: ${e.message}")
    }
  }

  @Throws(IOException::class, IllegalStateException::class)
  private fun convertToVector(source: Path, target: Path) {
    // convert only if it is .svg
    if (source.fileName.toString().endsWith(".svg")) {
      val targetFile = getFileWithXMlExtension(target)
      val fileOutputStream = FileOutputStream(targetFile)
      Svg2Vector.parseSvgToXml(source, fileOutputStream)
    } else if (config.logSkipped) {
      System.err.println("Skipping file: ${source.fileName}")
    }
  }

  private fun getFileWithXMlExtension(target: Path): File {
    val targetFile = target.toFile()
    val baseName = targetFile.nameWithoutExtension
    return Path(targetFile.parent, "$baseName.xml").toFile()
  }
}
