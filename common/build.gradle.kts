import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.navigationSafeargs) apply false
 }

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

android {

    namespace = "org.sqlunet.browser.common"

    defaultConfig {
        minSdk = vMinSdk
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
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

    implementation(project(":expandableListFragment"))
    implementation(project(":concurrency"))
    implementation(project(":preference"))
    implementation(project(":nightmode"))
    implementation(project(":download"))
    implementation(project(":download_common"))
    implementation(project(":deploy"))
    implementation(project(":capture"))
    implementation(project(":others"))
    implementation(project(":donate"))
    implementation(project(":rate"))
    implementation(project(":xNet"))
    implementation(project(":assetpacks"))

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.lifecycle.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.viewpager2)
    implementation(libs.swiperefreshlayout)
    implementation(libs.annotation)
    implementation(libs.material)
    implementation(libs.asset.delivery.ktx)
}
