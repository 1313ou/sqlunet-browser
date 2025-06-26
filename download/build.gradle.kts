
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val vTargetSdk by lazy { rootProject.extra["targetSdk"] as Int }
private val workVersion by lazy { rootProject.extra["workVersion"] as String }

android {

    namespace = "com.bbou.download.workers"

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
    coreLibraryDesugaring(libs.desugar)

    implementation(project(":download_common"))
    implementation(project(":deploy"))
    implementation(project(":concurrency"))

    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.work)
    implementation(libs.material)
}
