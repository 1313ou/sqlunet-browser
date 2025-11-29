import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

android {

    namespace = "com.bbou.deploy.workers"

    compileSdk = vCompileSdk

    defaultConfig {
        minSdk = vMinSdk
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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

    implementation(project(":download_common"))
    implementation(project(":concurrency"))

    implementation(libs.appcompat)
}
