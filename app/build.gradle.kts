plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktfmt)
}

// Version lifecycle: release-please rewrites the digits between the marker pair below.
fun versionCodeFor(versionName: String): Int {
  val parts = versionName.split(".")
  check(parts.size == 3) { "versionName must be major.minor.patch: $versionName" }
  val (major, minor, patch) = parts.map { it.toInt() }
  return major * 1_000_000 + minor * 1_000 + patch
}

// x-release-please-start-version
val appVersionName = "0.1.0"
// x-release-please-end

val appVersionCode = versionCodeFor(appVersionName)

val signingKeystorePath = System.getenv("KEYSTORE_PATH")

android {
  namespace = "io.github.codeinabox.mutedlauncher"
  compileSdk { version = release(37) }

  defaultConfig {
    applicationId = "io.github.codeinabox.mutedlauncher"
    minSdk = 31
    targetSdk = 37
    versionCode = appVersionCode
    versionName = appVersionName
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  dependenciesInfo {
    includeInApk = false
    includeInBundle = false
  }

  signingConfigs {
    if (signingKeystorePath != null) {
      create("release") {
        storeFile = file(signingKeystorePath)
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
      }
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro",
      )
      if (signingKeystorePath != null) {
        signingConfig = signingConfigs.getByName("release")
      }
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

tasks.register("versionCodeSelfCheck") {
  group = "verification"
  description = "Pins versionCodeFor() against known semver values and the current appVersionName."
  doLast {
    val pins =
        mapOf(
            "0.1.0" to 1_000,
            "0.2.0" to 2_000,
            "1.2.3" to 1_002_003,
        )
    pins.forEach { (version, expected) ->
      check(versionCodeFor(version) == expected) { "versionCodeFor($version) must be $expected" }
    }
    check(versionCodeFor(appVersionName) == appVersionCode) {
      "versionCode must be derived from appVersionName"
    }
    check(appVersionCode > 0)
  }
}

tasks.check { dependsOn("versionCodeSelfCheck", "detekt", "ktfmtCheck", "lintDebug") }

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
