// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        mavenLocal()
        mavenCentral()
        google()
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
        set("versionCode", 193)
        set("versionName", "4.193")
        set("minSdk", 21)
        set("targetSdk", 36)
        set("compileSdk", 36)
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
