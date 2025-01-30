plugins {
    id("com.android.application")
}

android {
    namespace = "com.cao.classtrack"
    compileSdk = 34 // Updated to satisfy the latest library requirements

    defaultConfig {
        applicationId = "com.cao.classtrack"
        minSdk = 16 // Android 4.1
        targetSdk = 34 // Updated to align with compileSdk requirements
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Updated dependencies to versions compatible with minSdk 16
    implementation("androidx.appcompat:appcompat:1.4.0") // Compatible with API 16
    implementation("com.google.android.material:material:1.3.0") // Compatible with API 16
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Compatible with API 16
    implementation("androidx.fragment:fragment:1.3.6") // Add Fragment support

    // Test dependencies (no changes needed)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

