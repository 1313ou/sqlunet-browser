// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.6")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.navigationSafeargs) apply false
    id("idea")
}

allprojects {

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    ext {
        set("versionCode", 183)
        set("versionName", "4.183")
        set("minSdk", 21)
        set("targetSdk", 34)
        set("compileSdk", 35)
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
