plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

android {

    namespace = "android.support.local.app"

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

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom)) // Use the Kotlin BOM
    coreLibraryDesugaring(libs.desugar)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.annotation)
}
