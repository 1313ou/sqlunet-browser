import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.navigationSafeargs)
}

private val vCode by lazy { rootProject.extra["versionCode"] as Int }
private val vName by lazy { rootProject.extra["versionName"] as String }
private val vCompileSdk by lazy { rootProject.extra["compileSdk"] as Int }
private val vMinSdk by lazy { rootProject.extra["minSdk"] as Int }
private val vTargetSdk by lazy { rootProject.extra["targetSdk"] as Int }

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(keystorePropertiesFile.inputStream())

android {

    namespace = "org.sqlunet.browser.fn"

    defaultConfig {
        applicationId = "org.sqlunet.browser.fn"
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

    assetPacks.add(":dbfn_ewn_asset")

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
    implementation(platform(libs.kotlin.bom)) // Use the Kotlin BOM
    coreLibraryDesugaring(libs.desugar)

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
    implementation(project(":frameNet"))

    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.swiperefreshlayout)
    implementation(libs.preference.ktx)
    implementation(libs.annotation)
    implementation(libs.material)

    androidTestImplementation(project(":common"))
    androidTestImplementation(project(":nightmode"))
    androidTestImplementation(project(":test"))
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.annotation)
    androidTestImplementation(libs.test)
    androidTestImplementation(libs.rules)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso.core)
}
