import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.navigationSafeargs)
}

private val vCode by lazy { rootProject.extra["versionCode"] as Int }
private val vName by lazy { rootProject.extra["versionName"] as String }
private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val vTargetSdk by lazy { rootProject.extra["targetSdk"] as Int }

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(keystorePropertiesFile.inputStream())

android {

    namespace = "org.sqlunet.browser.ewn"

    defaultConfig {
        applicationId = "org.sqlunet.browser.ewn"
        versionCode = vCode
        versionName = vName
        minSdk = vMinSdk
        targetSdk = vTargetSdk
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields
        buildConfigField("int", "VERSION_CODE", vCode.toString())
        buildConfigField("String", "VERSION_NAME", "\"$vName\"")
        buildConfigField("boolean", "DROP_DATA", "false")
    }

    compileSdk = vCompileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDirs("src/main/assets", "src/debug/assets/")
        }
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
    implementation(platform(libs.kotlin.bom)) // Use the Kotlin BOM
    implementation(libs.core.ktx)
    coreLibraryDesugaring(libs.desugar)

    implementation(project(":browserwncommon"))
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
    androidTestImplementation(project(":nightmode"))
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
