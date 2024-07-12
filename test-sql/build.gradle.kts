plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.44.0.0")
    implementation("androidx.annotation:annotation:1.8.0")
}