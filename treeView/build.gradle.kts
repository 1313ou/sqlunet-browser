import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.sqlunet.plugin.querybuilder") version "1.1.0"
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

android {

    namespace = "org.sqlunet.treeview"


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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("17")
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom)) // Use the Kotlin BOM
    coreLibraryDesugaring(libs.desugar)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.preference.ktx)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
}
