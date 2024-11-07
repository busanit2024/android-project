// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  id("org.jetbrains.kotlin.kapt") version "2.0.21" apply false
  id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
  // Add the dependency for the Google services Gradle plugin
  id("com.google.gms.google-services") version "4.4.2" apply false
}




buildscript{

  dependencies {
    classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
  }
}

