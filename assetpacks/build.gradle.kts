plugins {
    alias(libs.plugins.androidLibrary)
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }

android {

    namespace = "org.sqlunet.assetpack"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        multiDexEnabled = true
    }

    compileSdk = libs.versions.compileSdk.get().toInt()

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
    coreLibraryDesugaring(libs.desugar)
    implementation(platform(libs.kotlin.bom))
    implementation(kotlin("stdlib"))
    implementation(libs.core.ktx)

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
