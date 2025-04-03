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
    // Dipendenze di base
    implementation("androidx.appcompat:appcompat:1.4.0") // Compatibile con API 16
    implementation("com.google.android.material:material:1.3.0") // Compatibile con API 16
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Compatibile con API 16
    implementation("androidx.fragment:fragment:1.3.6") // Aggiunge il supporto per i Fragment

    // Networking - Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Aggiungi Retrofit 2.x
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Conversione JSON con Gson
    implementation ("com.squareup.okhttp3:okhttp:3.12.0") // Usa OkHttp versione 3.14.9 per compatibilità con minSdk 16
    implementation("com.squareup.okhttp3:logging-interceptor:3.14.9") // Interceptor per il logging delle richieste

    // JSON parsing con Gson
    implementation("com.google.code.gson:gson:2.10.1") // Libreria Gson per il parsing JSON

    // Lifecycle & ViewModel (opzionale)
    // implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    // implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // Dipendenze per il test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}



