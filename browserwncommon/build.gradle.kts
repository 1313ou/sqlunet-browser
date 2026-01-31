import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.navigationSafeargs) apply false
 }

private val vCode by lazy { rootProject.extra["versionCode"] as Int }
private val vName by lazy { rootProject.extra["versionName"] as String }
private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(keystorePropertiesFile.inputStream())

android {

    namespace = "org.sqlunet.browser.wn.lib"

    defaultConfig {
        minSdk = vMinSdk
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields
        buildConfigField("int", "VERSION_CODE", vCode.toString())
        buildConfigField("String", "VERSION_NAME", "\"$vName\"")
    }

    compileSdk = vCompileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        buildConfig = true
        compose = false
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
    implementation(platform(libs.kotlin.bom)) // Use the Kotlin BOM
    implementation(libs.kotlin.stdlib)
    coreLibraryDesugaring(libs.desugar)

    implementation(project(":common"))
    implementation(project(":expandableListFragment"))
    implementation(project(":nightmode"))
    implementation(project(":assetpacks"))
    implementation(project(":download"))
    implementation(project(":download_common"))
    implementation(project(":deploy"))
	implementation(project(":speak"))
    implementation(project(":others"))
    implementation(project(":donate"))
    implementation(project(":xNet"))
    implementation(project(":wordNet"))
    implementation(project(":bNC"))

    implementation(libs.appcompat)
    implementation(libs.lifecycle.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.annotation)
    implementation(libs.material)
    implementation(libs.swiperefreshlayout)
}
