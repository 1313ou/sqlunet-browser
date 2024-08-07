/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        mavenCentral()
        google()
        maven(url = System.getenv("HOME") + "/.m2/repository/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.5.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}

plugins {
    id("idea")
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
}

allprojects {

    repositories {
        mavenCentral()
        google()
    }

    ext {
        set("versionCode", 166)
        set("versionName", "4.166")
        set("minSdk", 21)
        set("targetSdk", 34)
        set("compileSdk", 34)
        set("desugarVersion", "2.0.4")

        set("coroutinesVersion", "1.8.1")
        set("workVersion", "2.9.0")
        set("coreVersion", "1.13.1")
        set("appcompatVersion", "1.7.0")
        set("activityVersion", "1.9.0")
        set("fragmentVersion", "1.8.1")
        set("lifecycleVersion", "2.8.3")
        set("navVersion", "2.7.7")
        set("preferenceVersion", "1.2.1")
        set("materialVersion", "1.12.0")
        set("constraintlayoutVersion", "2.1.4")
        set("annotationVersion", "1.7.1")
    }

    gradle.projectsEvaluated {
        tasks.withType<JavaCompile> {
            options.compilerArgs.addAll(arrayOf("-Xlint:deprecation", "-Xlint:unchecked"))
        }
    }

    tasks.register<Delete>("cleanroot") {
        delete(rootProject.layout.buildDirectory.get().asFile)
    }
}

idea {
    module {
        // println("$project @ " + project.projectDir)

        excludeDirs.addAll(
            files(
                "reference",
                "captures",
                "data",
                "data2",
                "tools",
                "utils",
                "dist",
                "gradle",
            )
        )

        project.subprojects.forEach { p ->
            // println("sub $p @ ${p.projectDir}")
            listOf("artwork", "artwork-assets", "artwork-relations", "data")
                .forEach { excluded ->
                    fileTree("${p.projectDir}").visit {
                        if (isDirectory && name == excluded) {
                            // println("EXCLUDE $this")
                            excludeDirs.add(file(file.absolutePath))
                        }
                    }
                }
        }
        //excludeDirs.forEach {
        // println("- $it")
        //}
    }
}

//apply from: file('projectDependencyGraph.gradle')
