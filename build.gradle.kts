// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.5")
    }
}

plugins {
    id("idea")
    kotlin("android") version "2.1.0" apply false
}

allprojects {

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    ext {
        set("versionCode", 178)
        set("versionName", "4.178")
        set("minSdk", 21)
        set("targetSdk", 34)
        set("compileSdk", 35)
        set("desugarVersion", "2.1.3")

        set("kotlinVersion", "2.1.0")
        set("coroutinesVersion", "1.9.0")
        set("workVersion", "2.10.0")
        set("coreVersion", "1.15.0")
        set("appcompatVersion", "1.7.0")
        set("activityVersion", "1.9.3")
        set("fragmentVersion", "1.8.5")
        set("lifecycleVersion", "2.8.7")
        set("navVersion", "2.8.5")
        set("preferenceVersion", "1.2.1")
        set("materialVersion", "1.12.0")
        set("constraintlayoutVersion", "2.2.0")
        set("annotationVersion", "1.9.1")
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
