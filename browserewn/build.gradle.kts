import java.util.Properties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Scanner

val buildTime = SimpleDateFormat("yyyy-MM-dd_HH:mm").format(Date())

fun getGitHash(workingDir: File = File(".")): String? {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
            .directory(workingDir)
            .redirectErrorStream(true)
            .start()
        val result = process.inputStream.bufferedReader().use { it.readText() }.trim()
        val exitCode = process.waitFor()
        if (exitCode == 0) result else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.navigationSafeargs)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(keystorePropertiesFile.inputStream())

android {

    namespace = "org.sqlunet.browser.ewn"

    defaultConfig {
        applicationId = "org.sqlunet.browser.ewn"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get() as String?
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields
        buildConfigField("int", "VERSION_CODE", "${libs.versions.versionCode.get().toInt()}")
        buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
        buildConfigField("boolean", "DROP_DATA", "false")
        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
        buildConfigField("String", "GIT_HASH", "\"${getGitHash()}\"")
    }

    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    assetPacks.add(":dbewn_ewn_asset")

    signingConfigs {
        create("sqlunet") {
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
            storeFile = file(keystoreProperties["storeFile"].toString())
            storePassword = keystoreProperties["storePassword"].toString()
        }
    }

    buildFeatures {
        buildConfig = true
        compose = false
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.txt")
            signingConfig = signingConfigs.getByName("sqlunet")
            versionNameSuffix = "signed"
        }
        debug {
            signingConfig = signingConfigs.getByName("sqlunet")
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
    implementation(platform(libs.kotlin.bom))
    implementation(kotlin("stdlib"))
    implementation(libs.core.ktx)

    implementation(project(":browserwncommon"))
    implementation(project(":core"))
    implementation(project(":stub"))
    implementation(project(":common"))
    // for manifest
    implementation(project(":wordNet"))
    implementation(project(":bNC"))
    implementation(project(":xNet"))
    implementation(project(":download"))
    implementation(project(":download_common"))
    implementation(project(":donate"))
    implementation(project(":others"))

    implementation(libs.appcompat) // for resources validation
    implementation(libs.material) // for resource include

    androidTestImplementation(project(":browserwncommon"))
    androidTestImplementation(project(":common"))
    androidTestImplementation(project(":xNet"))
    androidTestImplementation(project(":test"))
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.annotation)
    androidTestImplementation(libs.test.junit.ktx)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.web)
    androidTestImplementation(libs.uiautomator)
}
