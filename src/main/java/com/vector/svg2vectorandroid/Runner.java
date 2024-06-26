package com.vector.svg2vectorandroid;

/**
 * Created by ravi on 19/12/17.
 */
public class Runner {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Provide source directory as first argument for svg files to be converted\n example: java -jar Svg2VectorAndroid-1.0.jar <SourceDirectoryPath> ");
            return;
        }

        String sourceDirectory = args[0];
        SvgFilesProcessor processor;
        if (args.length == 1) {
            processor = new SvgFilesProcessor(sourceDirectory);
        } else {
            String destinationDirectory = args[1];
            processor = new SvgFilesProcessor(sourceDirectory, destinationDirectory);
        }
        processor.process();
    }
}
