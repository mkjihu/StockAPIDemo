plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.stockapidemo"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.stockapidemo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            optimization { enable = false }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.material)
    implementation(libs.androidx.coordinatorlayout)
    implementation("com.github.cachapa:ExpandableLayout:2.9.2")
    implementation(libs.appcompat)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.rxjava3)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.brvah)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
// Display test output in Android Studio Run and the Gradle console.
tasks.withType<Test>().configureEach {
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    if (providers.gradleProperty("twseLiveTest").orNull == "true") {
        outputs.upToDateWhen { false }
    }
}
