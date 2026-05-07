plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.attendance_tracking_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.attendance_tracking_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
// CardView (for item_class.xml)
    implementation("androidx.cardview:cardview:1.0.0")
// ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
// Material Design (FloatingActionButton, AlertDialog)
    implementation("com.google.android.material:material:1.11.0")
// AppCompat (AppCompatActivity, Toolbar)
    implementation("androidx.appcompat:appcompat:1.6.1")
// CoordinatorLayout (activity_main.xml)
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
}