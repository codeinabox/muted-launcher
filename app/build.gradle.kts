plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktfmt)
}

android {
  namespace = "io.github.codeinabox.mutedlauncher"
  compileSdk { version = release(37) }

  defaultConfig {
    applicationId = "io.github.codeinabox.mutedlauncher"
    minSdk = 31
    targetSdk = 37
    versionCode = 1
    versionName = "0.1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  dependenciesInfo {
    includeInApk = false
    includeInBundle = false
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro",
      )
    }
  }

  buildFeatures { compose = true }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }

detekt {
  config.setFrom(file("$rootDir/config/detekt/detekt.yml"))
  buildUponDefaultConfig = true
}

tasks.check { dependsOn("detekt", "ktfmtCheck", "lintDebug") }

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.foundation)
  detektPlugins(libs.detekt.compose)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
}
