import java.util.Date

plugins {
    id("org.sqlunet.plugin.querybuilder") version "1.0.0"
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val vTargetSdk by lazy { rootProject.extra["targetSdk"] as Int }
private val appcompatVersion by lazy { rootProject.extra["appcompatVersion"] as String }
private val lifecycleVersion by lazy { rootProject.extra["lifecycleVersion"] as String }
private val preferenceVersion by lazy { rootProject.extra["preferenceVersion"] as String }
private val materialVersion by lazy { rootProject.extra["materialVersion"] as String }
private val annotationVersion by lazy { rootProject.extra["annotationVersion"] as String }
private val coreVersion by lazy { rootProject.extra["coreVersion"] as String }
private val desugarVersion by lazy { rootProject.extra["desugarVersion"] as String }

android {

    namespace = "org.sqlunet.predicatematrix"

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

    kotlinOptions {
        jvmTarget = "17"
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

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugarVersion")

    implementation(project(":xNet"))
    implementation(project(":treeView"))
    implementation(project(":frameNet"))
    implementation(project(":propbank"))
    implementation(project(":verbNet"))

    implementation("androidx.core:core-ktx:$coreVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
    implementation("androidx.annotation:annotation:$annotationVersion")

    testImplementation(project(":test-sql"))
    testImplementation("junit:junit:4.13.2")
}

// C O D E   G E N E R A T I O N

private var protoSrcDir = File(project.projectDir, "/src/proto")
private var generatedSrcDir = project.layout.buildDirectory.dir("generated/source/queries").get()
//println("ProtoDir $protoSrcDir")
//println("Generated $generatedSrcDir")

android {
    sourceSets {
        getByName("main") {
            java.srcDirs(generatedSrcDir)
        }
        getByName("test") {
            java.srcDirs(generatedSrcDir)
        }
    }
    kotlinOptions {
        jvmTarget = "17"
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

    inDir = "${protoSrcDir}/predicatematrix"
    factory = "Factory.java"
    variables = arrayOf("Names.properties", "NamesExtra.properties")
    instantiates = arrayOf("SqLiteDialect.java")

    outDir = "$generatedSrcDir"
    v = "V"
    q = "Q"
    qv = "QV"
    qPackage = "org.sqlunet.predicatematrix.provider"
    instantiateDest = "org.sqlunet.predicatematrix.sql"
}

fun allExist(outFiles: Sequence<File>): Boolean {
    for (f in outFiles)
        if (!f.exists())
            return false
    return true
}

fun newest(outFiles: Sequence<File>): Long? {
    return outFiles
        //.onEach { print("### $it ")}
        .map { it.lastModified() }
        //.onEach { println(it)}
        .maxOrNull()
}

tasks.register("generateAll") {
     onlyIf {
        val generatedQVFiles = listOf(querybuilder_args.v, querybuilder_args.q, querybuilder_args.qv)
            .map { file("$generatedSrcDir/${querybuilder_args.qPackage.replace('.', '/')}/$it.java") }
        val generatedInstantiateFiles = querybuilder_args.instantiates
            .map { file("$generatedSrcDir/${querybuilder_args.instantiateDest.replace('.', '/')}/$it") }
        val generatedFiles = generatedQVFiles + generatedInstantiateFiles
        //println("GENERATED $generatedFiles")
        if (!allExist(generatedFiles.asSequence()))
            true
        else {
            val protoFactoryFile = listOf(file("${querybuilder_args.inDir}/${querybuilder_args.factory}"))
            val protoInstantiatesFiles = querybuilder_args.instantiates.map { file("${querybuilder_args.inDir}/$it") }
            val protoVariableFiles = querybuilder_args.variables.map { file("${querybuilder_args.inDir}/$it") }
            val protoFiles = protoFactoryFile + protoInstantiatesFiles + protoVariableFiles
            //println("PROTO $protoFiles")

            val newestProtoStamp = newest(protoFiles.asSequence())
            val generatedStamp = newest(generatedFiles.asSequence())
            val result = (newestProtoStamp ?: 0L) > (generatedStamp ?: 0L)
            if (result) { println("NEWEST ${Date(newestProtoStamp!!)} PROTO\nNEWEST ${Date(generatedStamp!!)} GENERATED\nGENERATING SOURCES")}
            result
        }
    }
    doLast {
        println("Generation of source code from prototypes")
        task("generateQV")
        task("generateQ")
        task("generateV")
        task("instantiate")
    }
}

tasks.preBuild {
    dependsOn("generateAll")
}
