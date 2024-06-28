package com.vector.svg2vectorandroid;

import com.android.ide.common.vectordrawable.Svg2Vector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by ravi on 18/12/17.
 */

public class SvgFilesProcessor {

    private final Path sourceSvgPath;
    private final Path destinationVectorPath;
    private final String extension;
    private final String extensionSuffix;

    public SvgFilesProcessor(String sourceSvgDirectory) {
        this(sourceSvgDirectory, sourceSvgDirectory + File.pathSeparator + "ProcessedSVG", "xml", "");
    }

    public SvgFilesProcessor(String sourceSvgDirectory, String destinationVectorDirectory) {
        this(sourceSvgDirectory, destinationVectorDirectory, "xml", "");
    }

    public SvgFilesProcessor(String sourceSvgDirectory, String destinationVectorDirectory, String extension,
                             String extensionSuffix) {
        this.sourceSvgPath = Paths.get(sourceSvgDirectory);
        this.destinationVectorPath = Paths.get(destinationVectorDirectory);
        this.extension = extension;
        this.extensionSuffix = extensionSuffix;
    }

    public void process() {
        try {
            EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            // Check if source directory exists
            if (!Files.isDirectory(sourceSvgPath)) {
                System.out.println("Source directory does not exist");
                return;
            }
            // Create destination directory if it does not yet exist
            if (!Files.isDirectory(destinationVectorPath)) {
                Files.createDirectory(destinationVectorPath);
            }

            Files.walkFileTree(sourceSvgPath, options, Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {
                    try {
                        convertToVector(file, destinationVectorPath.resolve(sourceSvgPath.relativize(file)));
                    } catch (IOException e) {
                        System.out.println("Error reading file: " + e.getMessage());
                    } catch (IllegalStateException e) {
                        System.out.println("Error parsing svg: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unknown error: " + e.getMessage());
                    }
                    return CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("Error reading from files: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unknown error: " + e.getMessage());
        }
    }

    private void convertToVector(Path source, Path target) throws IOException, IllegalStateException {
        // convert only if it is .svg
        if (source.getFileName().toString().endsWith(".svg")) {
            File targetFile = getFileWithXMlExtension(target, extension, extensionSuffix);
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            Svg2Vector.parseSvgToXml(source, fileOutputStream);
        } else {
            System.out.println("Skipping file as its not svg " + source.getFileName());
        }
    }

    private File getFileWithXMlExtension(Path target, String extension, String extensionSuffix) {
        String svgFilePath = target.toFile().getAbsolutePath();
        StringBuilder svgBaseFile = new StringBuilder();
        int index = svgFilePath.lastIndexOf(".");
        if (index != -1) {
            String subStr = svgFilePath.substring(0, index);
            svgBaseFile.append(subStr);
        }
        svgBaseFile.append(null != extensionSuffix ? extensionSuffix : "");
        svgBaseFile.append(".");
        svgBaseFile.append(extension);
        return new File(svgBaseFile.toString());
    }
}
