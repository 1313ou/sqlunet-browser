import java.util.Date

plugins {
    id("org.sqlunet.plugin.querybuilder") version "1.1.0"
    alias(libs.plugins.androidLibrary)
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

android {

    namespace = "org.sqlunet.framenet"

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

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.txt")
        }
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(17)
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)

    implementation(project(":xNet"))
    implementation(project(":treeView"))

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.lifecycle.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.annotation)
    implementation(libs.material)

    testImplementation(project(":test-sql"))
    testImplementation(libs.junit)
    testImplementation(libs.querybuilder)
}

// C O D E   G E N E R A T I O N

private var protoSrcDir = File(project.projectDir, "/src/proto")
private var generatedSrcDir = project.layout.buildDirectory.dir("generated/source/queries").get()
//println("ProtoDir $protoSrcDir")
//println("Generated $generatedSrcDir")

android {
    sourceSets {
        getByName("main") {
            java.directories.add(generatedSrcDir.toString())
        }
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(17)
    }
}

tasks.register("dumpSourceSets") {
    doLast {
        android.sourceSets.forEach { srcSet ->
            println(
                """[${srcSet.name}]

                -->Source directories: ${srcSet.java.srcDirs}

                -->Resource directories: ${srcSet.resources.srcDirs}

            """
            )
        }
    }
}

querybuilder_args {

    inDir = "${protoSrcDir}/framenet"
    factory = "Factory.java"
    variables = arrayOf("Names.properties", "NamesExtra.properties")
    instantiates = arrayOf("SqLiteDialect.java")

    outDir = "$generatedSrcDir"
    v = "V"
    q = "Q"
    qv = "QV"
    qPackage = "org.sqlunet.framenet.provider"
    instantiateDest = "org.sqlunet.framenet.sql"
}

fun allExist(outFiles: Sequence<File>): Boolean {
    for (f in outFiles)
        if (!f.exists())
            return false
    return true
}

fun newest(outFiles: Sequence<File>): Long? {
    return outFiles
        //.onEach { print("### $it ") }
        .map { it.lastModified() }
        //.onEach { println(it) }
        .maxOrNull()
}

fun needed(): Boolean {
    val generatedQVFiles = listOf(querybuilder_args.v, querybuilder_args.q, querybuilder_args.qv)
        .map { file("$generatedSrcDir/${querybuilder_args.qPackage.replace('.', '/')}/$it.java") }
    val generatedInstantiateFiles = querybuilder_args.instantiates
        .map { file("$generatedSrcDir/${querybuilder_args.instantiateDest.replace('.', '/')}/$it") }
    val generatedFiles = generatedQVFiles + generatedInstantiateFiles
    //println("GENERATED $generatedFiles")
    if (!allExist(generatedFiles.asSequence()))
        return true
    else {
        val protoFactoryFile = listOf(file("${querybuilder_args.inDir}/${querybuilder_args.factory}"))
        val protoInstantiatesFiles = querybuilder_args.instantiates.map { file("${querybuilder_args.inDir}/$it") }
        val protoVariableFiles = querybuilder_args.variables.map { file("${querybuilder_args.inDir}/$it") }
        val protoFiles = protoFactoryFile + protoInstantiatesFiles + protoVariableFiles
        //println("PROTO $protoFiles")
        val newestProtoStamp = newest(protoFiles.asSequence())
        val generatedStamp = newest(generatedFiles.asSequence())
        val result = (newestProtoStamp ?: 0L) > (generatedStamp ?: 0L)
        if (result) {
            println("frameNet module\nNewest prototype: ${Date(newestProtoStamp!!)}\nNewest generated: ${Date(generatedStamp!!)}\nGenerating sources from prototypes")
        }
        return result
    }
}

tasks.register("generateAll") {
    dependsOn("generateQV")
    dependsOn("generateQ")
    dependsOn("generateV")
    dependsOn("instantiate")
    onlyIf {
        needed()
    }
    doLast {
        println("Generation of source code from prototypes")
    }
}

val generateAll = tasks.named("generateAll") {
    // Tell Gradle where it writes files
    outputs.dir(generatedSrcDir)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateAll")
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn("generateAll")
}
