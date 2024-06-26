# Svg2VectorAndroid

Batch conversion of SVG files into Vector Drawable XML files using the same conversion tool Android Studio provides. Under the hood, it uses Android Studio's Svg2Vector implementation ([reference](https://android.googlesource.com/platform/tools/base/+/master/sdk-common/src/main/java/com/android/ide/common/vectordrawable/Svg2Vector.java)).


## Java Usage

Simply pass source directory path to SvgFilesProcessor and call process.

```java
SvgFilesProcessor processor = new SvgFilesProcessor("/Volumes/Development/Features/MySvgs");
processor.process();
```

If no destination directory is provided, this will create a new folder "ProcessedSvgs" inside source folder.


## CLI Usage

If you directly want to use the jar , use as below:

```bash
java -jar bin/Svg2VectorAndroid-1.0.2.jar "/Path/to/my/svgs"
```

## Build from source

To build the .jar file from source, you can simply run the following and your JAR will be created in `bin/`.

```bash
gradlew :fatJar
```

## Changelog

### `1.0.2` (2024-06-26)

- Upgraded Android Studio tools to 31.5.0. This requires Java 11+
- Upgraded Gradle to 8.8
- Added support for optionally providing an output directory via CLI
- Added support for creating the output directory if it does not already exist

### `1.0.1` (2018-12-26)

- Upgraded Android Studio tools to 26.3.1


### `1.0.0` (2017-12-19)

- Initial release
