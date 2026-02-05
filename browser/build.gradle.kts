import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
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

    namespace = "org.sqlunet.browser"

    defaultConfig {
        applicationId = "org.sqlunet.browser"
        versionCode = vCode
        versionName = vName
        minSdk = vMinSdk
        vectorDrawables.useSupportLibrary = true
        targetSdk = vTargetSdk
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

    // assetPacks.addAll(listOf(":db_wn31_asset", ":db_ewn_asset"))
    assetPacks.add(":db_ewn_asset")

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

    implementation(project(":activities"))
    implementation(project(":common"))
    implementation(project(":expandableListFragment"))
    implementation(project(":assetpacks"))
    implementation(project(":download"))
    implementation(project(":download_common"))
    implementation(project(":deploy"))
    implementation(project(":concurrency"))
    implementation(project(":speak"))
    implementation(project(":others"))
    implementation(project(":donate"))
    implementation(project(":rate"))
    implementation(project(":xNet"))
    implementation(project(":wordNet"))
    implementation(project(":verbNet"))
    implementation(project(":propbank"))
    implementation(project(":frameNet"))
    implementation(project(":predicateMatrix"))
    implementation(project(":bNC"))

    implementation(libs.appcompat)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.swiperefreshlayout)
    implementation(libs.preference.ktx)
    implementation(libs.material)
    implementation(libs.annotation)

    androidTestImplementation(project(":common"))
    androidTestImplementation(project(":activities"))
    androidTestImplementation(project(":test"))
    androidTestImplementation(libs.annotation)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.test.junit.ktx)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.espresso.core)
}
