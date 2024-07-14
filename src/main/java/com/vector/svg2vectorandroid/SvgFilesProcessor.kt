package com.vector.svg2vectorandroid

import com.android.ide.common.vectordrawable.Svg2Vector
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.EnumSet

class SvgFilesProcessor(
  sourceSvgDirectory: String,
  destinationVectorDirectory: String? = null,
  private val extension: String = "xml",
  private val extensionSuffix: String = ""
) {
  private val sourceSvgPath: Path = Paths.get(sourceSvgDirectory).normalize()
  private val destinationVectorPath: Path =
    Paths.get(destinationVectorDirectory ?: "$sourceSvgDirectory${File.pathSeparator}ProcessedSVG").normalize()

  fun process() {
    try {
      val options = EnumSet.of(FileVisitOption.FOLLOW_LINKS)
      // Check if source directory exists
      if (!Files.isDirectory(sourceSvgPath)) {
        println("Source directory does not exist")
        return
      }
      // Create destination directory if it does not yet exist
      if (!Files.isDirectory(destinationVectorPath)) {
        Files.createDirectory(destinationVectorPath)
      }

      Files.walkFileTree(sourceSvgPath, options, Int.MAX_VALUE, object : SimpleFileVisitor<Path>() {
        @Throws(IOException::class)
        override fun visitFile(
          file: Path,
          attrs: BasicFileAttributes
        ): FileVisitResult {
          try {
            convertToVector(file, destinationVectorPath.resolve(sourceSvgPath.relativize(file)))
          } catch (e: IOException) {
            println("Error reading file: " + e.message)
          } catch (e: IllegalStateException) {
            println("Error parsing svg: " + e.message)
          } catch (e: Exception) {
            println("Unknown error: " + e.message)
          }
          return FileVisitResult.CONTINUE
        }

        @Throws(IOException::class)
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
          if (dir.normalize() == destinationVectorPath) {
            return FileVisitResult.SKIP_SUBTREE
          }
          return FileVisitResult.CONTINUE
        }
      })
    } catch (e: IOException) {
      println("Error reading from files: " + e.message)
    } catch (e: Exception) {
      println("Unknown error: " + e.message)
    }
  }

  @Throws(IOException::class, IllegalStateException::class)
  private fun convertToVector(source: Path, target: Path) {
    // convert only if it is .svg
    if (source.fileName.toString().endsWith(".svg")) {
      val targetFile = getFileWithXMlExtension(target, extension, extensionSuffix)
      val fileOutputStream = FileOutputStream(targetFile)
      Svg2Vector.parseSvgToXml(source, fileOutputStream)
    } else {
      println("Skipping file as its not svg " + source.fileName)
    }
  }

  private fun getFileWithXMlExtension(target: Path, extension: String, extensionSuffix: String?): File {
    val svgFilePath = target.toFile().absolutePath
    val svgBaseFile = StringBuilder()
    val index = svgFilePath.lastIndexOf(".")
    if (index != -1) {
      val subStr = svgFilePath.substring(0, index)
      svgBaseFile.append(subStr)
    }
    svgBaseFile.append(extensionSuffix ?: "")
    svgBaseFile.append(".")
    svgBaseFile.append(extension)
    return File(svgBaseFile.toString())
  }
}
