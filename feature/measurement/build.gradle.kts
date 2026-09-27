plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.feature)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.wanbaohe.measurement"

dependencies {
    implementation(libs.androidxCore)
    implementation(libs.appCompat)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    api(projects.core.base)
    api(projects.core.model)
    api(projects.core.theme)
    api(projects.feature.common)
}
