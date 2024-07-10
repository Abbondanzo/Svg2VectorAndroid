plugins {
  `kotlin-dsl`
  id("java")
  application
}

group = "com.vector.svg2vectorandroid"
version = "1.1.0"

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath(libs.kotlin.gradle.plugin)
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
  }
}

tasks.compileJava {
  sourceCompatibility = libs.versions.java.get()
  targetCompatibility = libs.versions.java.get()
}

val uberJar = tasks.register<Jar>("uberJar") {
  archiveClassifier = "uber"
  manifest {
    attributes(
      "Implementation-Title" to "Svg2VectorAndroid",
      "Implementation-Version" to "${rootProject.version}",
      "Main-Class" to "${rootProject.group}.Runner"
    )
  }
  // Remove duplicate index lists and notification files
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  // Output classpath exceeds 2^16 ZIP capacity
  isZip64 = true
  // Include source files here
  from(sourceSets.main.get().output)
  // Ensure configuration is fully set up
  dependsOn(configurations.runtimeClasspath)
  // Include all dependencies in the uber jar
  from({
    configurations.runtimeClasspath
      .get()
      // Explicitly omit Gradle as a dependency, since it includes itself
      .filter { it.name.endsWith("jar") && !it.name.contains("gradle") }
      .map { zipTree(it) }
  })
  exclude(
    "META-INF/*.RSA",
    "META-INF/*.SF",
    "META-INF/*.DSA",
  )
}

val r8: Configuration by configurations.creating

// Use Google's R8 to compress the jar
tasks.register("compressFatJar", JavaExec::class.java) {
  group = "build"
  dependsOn(uberJar)
  // Ensure we use the same executor version as our Jar was created
  javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
  val fatJarFile = uberJar.get().archiveFile
  inputs.file(fatJarFile)
  val proguardFile = file("src/main/proguard-rules.pro")
  inputs.file(proguardFile)
  val outputFile = layout.buildDirectory.file("libs/r8.jar").get().asFile
  outputs.file(outputFile)
  classpath = r8
  mainClass = "com.android.tools.r8.R8"
  args =
    listOf(
      "--release",
      "--classfile",
      "--output", outputFile.path,
      "--pg-conf", proguardFile.path,
      "--lib", System.getProperty("java.home").toString(),
      fatJarFile.get().toString(),
    )
}

dependencies {
  implementation(libs.android.tools.common)
  implementation(libs.android.tools.sdk)
  implementation(libs.kotlin.stdlib)
  testImplementation(libs.junit)

  r8(libs.r8)
}
