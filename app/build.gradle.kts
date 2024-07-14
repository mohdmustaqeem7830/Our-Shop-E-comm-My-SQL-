plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "mustaqeem.zubair.ourshop"
    compileSdk = 34

    buildFeatures{
        viewBinding = true
    }

    defaultConfig {
        applicationId = "mustaqeem.zubair.ourshop"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
//    glide library
    implementation("com.github.bumptech.glide:glide:4.16.0")
//    round image library
    implementation("com.makeramen:roundedimageview:2.3.0")
//    searbar material library
    implementation("com.github.mancj:MaterialSearchBar:0.8.5")
//    crousel means slide bar library

    implementation("com.google.android.material:material:1.5.0")
    implementation("me.relex:circleindicator:2.1.6")
    implementation("org.imaginativeworld.whynotimagecarousel:whynotimagecarousel:2.1.0")

    //volley library
    implementation("com.android.volley:volley:1.2.1")

    //cart manage library
    implementation("com.github.hishd:TinyCart:1.0.1")

    //Advance web view for payment
    implementation("com.github.delight-im:Android-AdvancedWebView:v3.2.1")
}