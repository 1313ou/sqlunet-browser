
plugins {
    id("com.android.library")
    kotlin("android") version "2.1.0"
}

private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val coreVersion by lazy { rootProject.extra["coreVersion"] as String }
private val appcompatVersion by lazy { rootProject.extra["appcompatVersion"] as String }
private val materialVersion by lazy { rootProject.extra["materialVersion"] as String }
private val preferenceVersion by lazy { rootProject.extra["preferenceVersion"] as String }
private val desugarVersion by lazy { rootProject.extra["desugarVersion"] as String }

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
    implementation("androidx.core:core-ktx:$coreVersion")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugarVersion")

    implementation(project(":deploy"))
    implementation(project(":concurrency"))
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("com.google.android.play:asset-delivery-ktx:2.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}