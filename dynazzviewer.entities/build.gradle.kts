plugins {
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.jpa") version "1.7.20"
}

dependencies {
    // Entity annotations
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // Query SQL
    implementation("com.querydsl:querydsl-kotlin:5.0.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-kotlin-codegen:5.0.0")
}
