plugins {
  `kotlin-dsl`
  alias(libs.plugins.shadow)
  id("java")
}

repositories {
  maven("https://maven.google.com")
  mavenCentral()
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
    languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_11.majorVersion))
  }
}

tasks.compileJava {
  sourceCompatibility = JavaVersion.VERSION_11.majorVersion
  targetCompatibility = JavaVersion.VERSION_11.majorVersion
}

tasks {
  shadowJar {
    minimize()
  }
}

//create a single Jar with all dependencies
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
  doFirst {
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
  }
  exclude(
    "META-INF/*.RSA",
    "META-INF/*.SF",
    "META-INF/*.DSA",
    "*.kotlin_module",
    "*.kotlin_metadata",
    "META-INF/maven/*",
  )
  with(tasks.jar.get())
  destinationDirectory = project.file("bin")
}

dependencies {
  implementation(libs.android.tools.common)
  implementation(libs.android.tools.sdk)
  implementation(libs.kotlin.stdlib)
  testImplementation(libs.junit)
}

