import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.navigationSafeargs) apply false
 }

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(keystorePropertiesFile.inputStream())

android {

    namespace = "org.sqlunet.browser.wn.lib"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields
        buildConfigField("int", "VERSION_CODE", "${libs.versions.versionCode.get().toInt()}")
        buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
    }

    compileSdk = libs.versions.compileSdk.get().toInt()

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
    coreLibraryDesugaring(libs.desugar)
    implementation(platform(libs.kotlin.bom))
    implementation(kotlin("stdlib"))
    implementation(libs.core.ktx)

    implementation(project(":core"))
    implementation(project(":stub"))
    implementation(project(":common"))
    implementation(project(":expandableListFragment"))
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
    implementation(libs.material)
    implementation(libs.swiperefreshlayout)
    implementation(libs.annotation)
}
