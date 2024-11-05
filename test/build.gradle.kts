plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android") version "2.0.20"
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val coreVersion by lazy { rootProject.extra["coreVersion"] as String }
private val annotationVersion by lazy { rootProject.extra["annotationVersion"] as String }

android {

    namespace = "org.sqlunet.test"


    defaultConfig {
        minSdk = vMinSdk
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileSdk = vCompileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:$coreVersion")
    implementation("androidx.annotation:annotation:$annotationVersion")
    implementation("androidx.test:core:1.6.1")
    implementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.test.espresso:espresso-contrib:3.6.1")
}
