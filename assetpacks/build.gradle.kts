
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

android {

    namespace = "org.sqlunet.assetpack"

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

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.core.ktx)
    coreLibraryDesugaring(libs.desugar)

    implementation(project(":deploy"))
    implementation(project(":concurrency"))

    implementation(libs.appcompat)
    implementation(libs.preference.ktx)
    implementation(libs.material)
    implementation(libs.asset.delivery.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.junit.ktx)
    androidTestImplementation(libs.espresso.core)
}