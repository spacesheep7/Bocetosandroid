// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // REferencia a KSP y otra a Dagger
    alias(libs.plugins.google.devtools.ksp) apply false
    id("com.google.dagger.hilt.android") version "2.56" apply false
}