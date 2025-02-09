plugins {
    id("com.android.library")
    kotlin("android") version "2.1.0"
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val coreVersion by lazy { rootProject.extra["coreVersion"] as String }
private val desugarVersion by lazy { rootProject.extra["desugarVersion"] as String }

android {
    namespace = "org.sqlunet.browser.capture"

    defaultConfig {
        minSdk = vMinSdk

        consumerProguardFiles("consumer-rules.pro")
    }

    compileSdk = vCompileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:${coreVersion}")
    implementation("androidx.preference:preference-ktx:1.2.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${desugarVersion}")
}