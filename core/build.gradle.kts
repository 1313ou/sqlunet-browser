plugins {
    alias(libs.plugins.androidLibrary)
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

android {

    namespace = "org.sqlunet.core"

    defaultConfig {
        minSdk = vMinSdk
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileSdk = vCompileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
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
    implementation(platform(libs.kotlin.bom))
    implementation(kotlin("stdlib"))
    implementation(libs.core.ktx)

    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.material)
}