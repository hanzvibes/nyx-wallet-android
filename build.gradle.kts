plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

subprojects {
    tasks.withType<Test>().configureEach {
        jvmArgs(
            "-Djava.net.preferIPv4Stack=true",
            "-Djava.net.preferIPv4Addresses=true",
        )
        systemProperty("java.net.preferIPv4Stack", "true")
        systemProperty("java.net.preferIPv4Addresses", "true")
    }
}
