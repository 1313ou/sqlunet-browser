import java.util.Properties

plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.android") version "2.1.0"
}

private val vCode by lazy { rootProject.extra["versionCode"] as Int }
private val vName by lazy { rootProject.extra["versionName"] as String }
private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val vTargetSdk by lazy { rootProject.extra["targetSdk"] as Int }
private val appcompatVersion by lazy { rootProject.extra["appcompatVersion"] as String }
private val lifecycleVersion by lazy { rootProject.extra["lifecycleVersion"] as String }
private val navVersion by lazy { rootProject.extra["navVersion"] as String }
private val preferenceVersion by lazy { rootProject.extra["preferenceVersion"] as String }
private val materialVersion by lazy { rootProject.extra["materialVersion"] as String }
private val annotationVersion by lazy { rootProject.extra["annotationVersion"] as String }
private val coreVersion by lazy { rootProject.extra["coreVersion"] as String }
private val desugarVersion by lazy { rootProject.extra["desugarVersion"] as String }

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(keystorePropertiesFile.inputStream())

android {

    namespace = "org.sqlunet.browser.sn"

    defaultConfig {
        applicationId = "org.sqlunet.browser.sn"
        versionCode = vCode
        versionName = vName
        minSdk = vMinSdk
        targetSdk = vTargetSdk
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields
        buildConfigField("int", "VERSION_CODE", vCode.toString())
        buildConfigField("String", "VERSION_NAME", "\"$vName\"")
        buildConfigField("boolean", "DROP_DATA", "false")
    }

    compileSdk = vCompileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDirs("src/main/assets", "src/debug/assets/")
        }
    }

    // assetPacks.addAll(listOf(":dbsn_wn31_asset", ":dbsn_ewn_asset"))
    assetPacks.add(":dbsn_ewn_asset")

    signingConfigs {
        create("sqlunet") {
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
            storeFile = file(keystoreProperties["storeFile"].toString())
            storePassword = keystoreProperties["storePassword"].toString()
        }
    }

    buildFeatures {
        buildConfig = true
        compose = false
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.txt")
            signingConfig = signingConfigs.getByName("sqlunet")
            versionNameSuffix = "signed"
        }
        debug {
            signingConfig = signingConfigs.getByName("sqlunet")
        }
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugarVersion")

    implementation(project(":common"))
    implementation(project(":expandableListFragment"))
    implementation(project(":assetpacks"))
    implementation(project(":nightmode"))
    implementation(project(":download"))
    implementation(project(":download_common"))
    implementation(project(":deploy"))
    implementation(project(":others"))
    implementation(project(":donate"))
    implementation(project(":rate"))
    implementation(project(":xNet"))
    implementation(project(":wordNet"))
    implementation(project(":syntagNet"))
    implementation(project(":bNC"))

    implementation("androidx.core:core-ktx:$coreVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
    implementation("androidx.annotation:annotation:$annotationVersion")
    implementation("com.google.android.material:material:$materialVersion")

    androidTestImplementation(project(":common"))
    androidTestImplementation(project(":nightmode"))
    androidTestImplementation(project(":test"))
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.annotation:annotation:$annotationVersion")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
