plugins { alias(libs.plugins.android.application); alias(libs.plugins.kotlin.android); alias(libs.plugins.kotlin.compose) }

android {
    namespace = "com.calcmate.allinonecalculator"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.calcmate.allinonecalculator"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes { release { isMinifyEnabled = true; isShrinkResources = true; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro") } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    buildFeatures { compose = true; buildConfig = true }
    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    lint { checkReleaseBuilds = false; abortOnError = false }
    testOptions.unitTests.isReturnDefaultValues = true
}
kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies {
    implementation(libs.androidx.core.ktx); implementation(libs.androidx.lifecycle.runtime.ktx); implementation(libs.androidx.lifecycle.viewmodel.compose); implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)); implementation(libs.androidx.compose.ui); implementation(libs.androidx.compose.ui.graphics); implementation(libs.androidx.compose.ui.tooling.preview); implementation(libs.androidx.compose.material3); implementation(libs.androidx.compose.material.icons.extended); implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences); implementation(libs.kotlinx.coroutines.android); implementation(libs.admob); implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
