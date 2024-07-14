# Svg2VectorAndroid

Batch conversion of SVG files into Vector Drawable XML files using the same conversion tool Android Studio provides. Under the hood, it uses Android Studio's Svg2Vector implementation ([reference](https://android.googlesource.com/platform/tools/base/+/master/sdk-common/src/main/java/com/android/ide/common/vectordrawable/Svg2Vector.java)).

## Kotlin Usage

Simply pass a config with your source directory path to SvgFilesProcessor and call process.

```kotlin
val config = SvgFilesProcessorConfig("/Volumes/Development/Features/MySvgs")
val processor = SvgFilesProcessor(config)
processor.process()
```

If no destination directory is provided, this will create a new folder "generated" inside source folder.

### Config options

| Option                            | Type      | Description                                                                                                              |
| --------------------------------- | --------- | ------------------------------------------------------------------------------------------------------------------------ |
| `sourceDirectory`                 | `String`  | The path to a folder containing your SVGs.                                                                               |
| `destinationDirectory` (optional) | `String`  | The path to a folder where generated XML files should go. Defaults to a subfolder "generated" in your `sourceDirectory`. |
| `logSkipped` (optional)           | `Boolean` | If true, logs out the files walked over but not processed by the XML converted. Defaults to false.                       |

## CLI Usage

If you directly want to use the jar , use as below:

```bash
# No destination
java -jar bin/Svg2VectorAndroid.jar "/Path/to/my/svgs"

# Or with a destination
java -jar bin/Svg2VectorAndroid.jar "/Path/to/my/svgs" "/Path/to/generated/xmls/"
```

## Build from source

To build the .jar file from source, you can simply run the following and your JAR will be created in `bin/`.

```bash
gradlew :prepareBinary
```

## Changelog

### `1.1.0` (2024-07-14)

- Converted project to Kotlin
- Switched to using R8 for binary compression

### `1.0.3` (2024-06-28)

- Added exception handling to file conversion
- Replaced a check that skips parsing the generated output directory
- Removed old JAR binaries for a single, latest binary

### `1.0.2` (2024-06-26)

- Upgraded Android Studio tools to 31.5.0. This requires Java 11+
- Upgraded Gradle to 8.8
- Added support for optionally providing an output directory via CLI
- Added support for creating the output directory if it does not already exist

### `1.0.1` (2018-12-26)

- Upgraded Android Studio tools to 26.3.1

### `1.0.0` (2017-12-19)

- Initial release
