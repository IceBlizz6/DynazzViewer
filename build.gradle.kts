plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    sourceSets.main {
        java.srcDirs("src")

        resources {
            srcDir("resources")
        }
    }

    sourceSets.test {
        java.srcDirs("test")

        resources {
            srcDir("testresources")
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.20")
    }
}
