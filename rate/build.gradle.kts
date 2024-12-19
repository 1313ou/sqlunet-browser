
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android") version "2.1.0"
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val coreVersion by lazy { rootProject.extra["coreVersion"] as String }
private val appcompatVersion by lazy { rootProject.extra["appcompatVersion"] as String }
private val annotationVersion by lazy { rootProject.extra["annotationVersion"] as String }
private val desugarVersion by lazy { rootProject.extra["desugarVersion"] as String }

android {

    namespace = "com.bbou.rate"

    compileSdk = vCompileSdk

    defaultConfig {
        minSdk = vMinSdk
        multiDexEnabled = true
    }

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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22")) // Use the Kotlin BOM
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugarVersion")

    implementation("androidx.core:core-ktx:${coreVersion}")
    implementation("androidx.appcompat:appcompat:${appcompatVersion}")
    implementation("androidx.annotation:annotation:${annotationVersion}")
}
