
plugins {
    id("com.android.library")
    kotlin("android") version "2.1.0"
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val vTargetSdk by lazy { rootProject.extra["targetSdk"] as Int }
private val coreVersion by lazy { rootProject.extra["coreVersion"] as String }
private val appcompatVersion by lazy { rootProject.extra["appcompatVersion"] as String }
private val materialVersion by lazy { rootProject.extra["materialVersion"] as String }
private val preferenceVersion by lazy { rootProject.extra["preferenceVersion"] as String }
private val desugarVersion by lazy { rootProject.extra["desugarVersion"] as String }

android {

    namespace = "com.bbou.download.common"

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

    @Suppress("UnstableApiUsage")
    testOptions {
        targetSdk = vTargetSdk
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugarVersion")

    implementation("androidx.core:core-ktx:$coreVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
    implementation("com.google.android.material:material:$materialVersion")
}
