/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

plugins {
    id("org.sqlunet.plugin.querybuilder") version "1.0.0"
    id("com.android.library")
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val vTargetSdk by lazy { rootProject.extra["targetSdk"] as Int }
private val appcompatVersion by lazy { rootProject.extra["appcompatVersion"] as String }
private val lifecycleExtensionsVersion by lazy { rootProject.extra["lifecycleExtensionsVersion"] as String }
private val preferenceVersion by lazy { rootProject.extra["preferenceVersion"] as String }
private val materialVersion by lazy { rootProject.extra["materialVersion"] as String }
private val annotationVersion by lazy { rootProject.extra["annotationVersion"] as String }
private val coreVersion by lazy { rootProject.extra["coreVersion"] as String }
private val desugarVersion by lazy { rootProject.extra["desugarVersion"] as String }
private val coroutinesVersion by lazy { rootProject.extra["coroutinesVersion"] as String }

android {

    namespace = "org.sqlunet.propbank"

    defaultConfig {
        minSdk = vMinSdk
        multiDexEnabled = true
    }

    compileSdk = vCompileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.txt")
        }
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugarVersion")

    implementation(project(":xNet"))
    implementation(project(":treeView"))

    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycleExtensionsVersion")
    implementation("androidx.annotation:annotation:$annotationVersion")
    implementation("com.google.android.material:material:$materialVersion")


    testImplementation(project(":test-sql"))
    testImplementation("junit:junit:4.13.2")
}

// C O D E   G E N E R A T I O N

private var protoSrcDir = File(project.projectDir, "/src/proto")
private var generatedSrcDir = project.layout.buildDirectory.dir("generated/source/queries").get()
println("ProtoDir $protoSrcDir")
println("Generated $generatedSrcDir")

android {
    sourceSets {
        getByName("main") {
            java.srcDirs(generatedSrcDir)
        }
        getByName("test") {
            java.srcDirs(generatedSrcDir)
        }
    }
}

tasks.register("dumpSourceSets") {
    doLast {
        android.sourceSets.forEach { srcSet ->
            println(
                """[${srcSet.name}]\n
                -->Source directories: ${srcSet.java.srcDirs}\n
                -->Resource directories: ${srcSet.resources.srcDirs}\n
            """
            )
        }
    }
}

querybuilder_args {

    inDir = "${protoSrcDir}/propbank"
    factory = "Factory.java"
    variables = arrayOf("Names.properties", "NamesExtra.properties")
    instantiates = arrayOf("SqLiteDialect.java")

    outDir = generatedSrcDir.toString()
    v = "V"
    q = "Q"
    qv = "QV"
    qPackage = "org.sqlunet.propbank.provider"
    instantiateDest = "org.sqlunet.propbank.sql"
}

tasks.register("generateAll") {
    dependsOn("generateQV")
        .dependsOn("generateQ")
        .dependsOn("generateV")
        .dependsOn("instantiate")
}

tasks.preBuild {
    dependsOn("generateAll")
}
