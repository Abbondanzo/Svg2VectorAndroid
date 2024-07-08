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

application {
  mainModule = "com.vector.svg2vectorandroid" // name defined in module-info.java
  mainClass = "com.vector.svg2vectorandroid.Runner"
}

distributions {
  main {
    contents {
      duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
  }
}

tasks.compileJava {
  sourceCompatibility = libs.versions.java.get()
  targetCompatibility = libs.versions.java.get()
}

// Create a single Jar with all dependencies
tasks.register("fatJar", Jar::class.java) {
  group = "build"
  manifest {
    attributes(
      "Implementation-Title" to "Svg2VectorAndroid",
      "Implementation-Version" to "${rootProject.version}",
      "Main-Class" to "${rootProject.group}.Runner"
    )
  }
  archiveBaseName = project.name
  archiveFileName = "${project.name}.jar"
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  // Output classpath exceeds 2^16 ZIP capacity
  isZip64 = true
  from(
    configurations.runtimeClasspath.get()
      .map { if (it.isDirectory) it else zipTree(it) }
  )
  exclude(
    "META-INF/*.RSA",
    "META-INF/*.SF",
    "META-INF/*.DSA",
//    "META-INF/INDEX.LIST",
//    "META-INF/LICENSE",
  )
  with(tasks.jar.get())
}

val fatJar = tasks.register<Jar>("uberJar") {
  archiveClassifier = "uber"
  // Remove duplicate index lists and notification files
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  // Output classpath exceeds 2^16 ZIP capacity
  isZip64 = true
  from(sourceSets.main.get().output)
  dependsOn(configurations.runtimeClasspath)

  from({
    val usedDependencies = mutableSetOf<String>()
    configurations.runtimeClasspath
      .get()
      .filter { it.name.endsWith("jar") && !usedDependencies.contains(it.name) }
      .onEach {
        println(it.path)
//        usedDependencies.add(it.name)
      }
      .map { zipTree(it) }
  })
  exclude(
    "META-INF/*.RSA",
    "META-INF/*.SF",
    "META-INF/*.DSA",
    // Explicit duplicate class removals (with the included version in the comments)
    "org/gradle/internal/impldep/META-INF/versions/9/com/jcraft/jsch/**", // version 10
    "org/gradle/internal/impldep/META-INF/versions/9/org/codehaus/plexus/**", // version 10
    "org/gradle/internal/impldep/META-INF/versions/15/org/bouncycastle/**", // version 11
    "org/gradle/internal/impldep/META-INF/versions/17/com/fasterxml/jackson/core/**", // version 11
    "org/gradle/internal/impldep/META-INF/versions/21/com/fasterxml/jackson/core/**", // version 11
    "org/gradle/internal/impldep/META-INF/**",
  )
  doLast {
    exclude(
      "META-INF/versions/15/org/bouncycastle/**",
      "org/gradle/internal/impldep/META-INF/versions/15/org/bouncycastle/**",
    )
  }
}

val r8: Configuration by configurations.creating

// Use Google's R8 to compress the jar
tasks.register("compressFatJar", JavaExec::class.java) {
  group = "build"
  dependsOn(fatJar)
  // Ensure we use the same executor version as our Jar was created
  javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
  val fatJarFile = fatJar.get().archiveFile
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
  implementation(libs.android.tools.common) {
    exclude("org.bouncycastle:bcprov-jdk18on")
  }
  implementation(libs.android.tools.sdk) {
    exclude("org.bouncycastle:bcprov-jdk18on")
  }
  implementation(libs.kotlin.stdlib)
  testImplementation(libs.junit)

  r8(libs.r8) {
    exclude("org.bouncycastle:bcprov-jdk18on")
  }
}
