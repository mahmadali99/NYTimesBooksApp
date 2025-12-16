import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties()
localProperties.load(rootProject.file("local.properties").inputStream())

val apiKey = localProperties.getProperty("API_KEY")

android {
    namespace = "com.example.nytimesbooksapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.nytimesbooksapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "BASE_URL", "\"https://api.nytimes.com/svc/books/v3/\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}


dependencies {
    //  Jetpack Compose + Material 3
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")


    //  Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")

    //  Lifecycle + ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    //  Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    //  Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14")

    //  Room
    implementation("androidx.room:room-runtime:2.8.3")
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.firebase.messaging) // version updated
    kapt("androidx.room:room-compiler:2.8.3")
    implementation("androidx.room:room-ktx:2.8.3")

    //  Hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //  Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // dark theme
    implementation ("androidx.compose.material:material-icons-extended")
    implementation("com.google.accompanist:accompanist-placeholder-material:0.32.0") // added just now

//    implementation("com.airbnb.android:lottie-compose:6.7.1")
    implementation("com.airbnb.android:lottie-compose:6.4.0")



    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.02"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")
    implementation("androidx.compose.ui:ui:1.6.0-alpha03")
    implementation("androidx.compose.material3:material3:1.3.0-alpha02")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0-alpha03")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0-alpha03")
    implementation("androidx.compose.material:material:1.5.0") // very important related to pull refresh
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
}