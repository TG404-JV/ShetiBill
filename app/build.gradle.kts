plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.farmer"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.example.farmer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"



        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // You can also define a different API key for the release build here if needed
        }

        debug {
            // You can define a different API key for debug builds here
        }


    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.firebase.firestore)
    implementation(libs.adapters)
    implementation(libs.gridlayout)
    implementation(libs.play.services.location)
    implementation(libs.coordinatorlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("androidx.room:room-runtime:2.5.0")
    annotationProcessor ("androidx.room:room-compiler:2.5.0")
    implementation ("com.google.firebase:firebase-appcheck-playintegrity:16.0.0")
    implementation ("com.google.firebase:firebase-appcheck-safetynet:16.1.2")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    // add the dependency for the Google AI client SDK for Android
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // ListenableFuture for asynchronous operations

    // Required for one-shot operations (to use `ListenableFuture` from Guava Android)
    implementation("com.google.guava:guava:31.0.1-android")

    // Required for streaming operations (to use `Publisher` from Reactive Streams)
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


    implementation ("androidx.room:room-runtime:2.5.0")
    annotationProcessor ("androidx.room:room-compiler:2.5.0")
    implementation ("androidx.room:room-ktx:2.5.0")

    implementation ("com.github.YarikSOffice:lingver:1.0.0")

    implementation ("com.github.delight-im:Android-AdvancedWebView:v3.2.1")
    implementation ("com.airbnb.android:lottie:6.6.0")
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
        // Firebase Auth with Phone
        implementation ("com.google.firebase:firebase-auth:22.3.0")

        // SafetyNet for device verification
        implementation ("com.google.android.gms:play-services-safetynet:18.0.1")

        // Play Services Auth for reCAPTCHA
        implementation ("com.google.android.gms:play-services-auth:20.7.0")


}