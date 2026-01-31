plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.51.1.0")
    implementation(libs.annotation)
}