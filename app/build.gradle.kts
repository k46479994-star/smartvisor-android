plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.smartvisor.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.smartvisor.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 5
        versionName = "1.2.0"
    }

    signingConfigs {
        create("update") {
            storeFile = file("signing/smartvisor-update.jks")
            storePassword = "smartvisor-update-2026"
            keyAlias = "smartvisor"
            keyPassword = "smartvisor-update-2026"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("update")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("update")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
}
