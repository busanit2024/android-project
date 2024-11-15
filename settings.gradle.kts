pluginManagement {
  repositories {
    google()  // content 블록 제거
    mavenCentral()
    gradlePluginPortal()
  }
  plugins {
    id("com.android.application") version "8.7.2"
    id("org.jetbrains.kotlin.android") version "1.8.20"
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }

  }
}

rootProject.name = "SearchRestroom"
include(":app")