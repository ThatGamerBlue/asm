# Spectral ASM 
![build](https://github.com/spectral-powered/asm/workflows/build/badge.svg)
![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/spectral-powered/asm?include_prereleases)
![Libraries.io dependency status for GitHub repo](https://img.shields.io/librariesio/github/spectral-powered/asm)
![GitHub](https://img.shields.io/github/license/spectral-powered/asm)

Spectral ASM is a library designed to expand the current ASM analysis and tree libraries and provide
additional features out of the box. This project was built to be used by various parts of all the Spectral projects.

## Features
* Object Based Structure
* Support for both Java 11 and Kotlin 1.4
* Method execution simulator
* Call graph
* Inheritance graph
* Control-flow graph
* Data-flow graph
* Member references

## Download

#### Gradle (Groovy DSL)
Add the Spectral maven repository to your **build.gradle** file.
```groovy
repositories {
    maven { url 'https://repo.spectralclient.org/repository/spectral/' }
}
```

Add the following to your dependencies closure.
```groovy
dependencies {
    implementation "org.spectral.asm:asm-core:1.0.0"
}
```

#### Gradle (Kotlin DSL)
Add the Spectral maven repository to your **build.gradle.kts** file.
```kotlin
repositories {
    maven {
        url = uri("https://repo.spectralclient.org/repository/spectral/")
    }
}
```

Add the following to your dependencies block.
```kotlin
dependencies {
    implementation("org.spectral.asm:asm-core:1.0.0")
}
```
