
plugins {
    id("com.android.library")
    kotlin("android") version "2.1.0"
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val appcompatVersion by lazy { rootProject.extra["appcompatVersion"] as String }
private val annotationVersion by lazy { rootProject.extra["annotationVersion"] as String }
private val fragmentVersion by lazy { rootProject.extra["fragmentVersion"] as String }
private val desugarVersion by lazy { rootProject.extra["desugarVersion"] as String }

android {

    namespace = "com.bbou.concurrency"

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

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugarVersion")

    implementation("androidx.appcompat:appcompat:${appcompatVersion}")
    implementation("androidx.fragment:fragment-ktx:${fragmentVersion}")
    implementation("androidx.annotation:annotation:${annotationVersion}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
