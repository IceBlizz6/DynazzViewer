plugins {
    id("io.ktor.plugin") version "2.1.3"
    application
}

application {
    mainClass.set("dynazzviewer.ui.web.WebApplication")
}

dependencies {
    implementation(project(":dynazzviewer.entities"))
    implementation(project(":dynazzviewer.storage"))
    implementation(project(":dynazzviewer.services"))
    implementation(project(":dynazzviewer.files"))

    // Web server
    implementation("io.ktor:ktor-server-core-jvm:2.1.3")
    implementation("io.ktor:ktor-server-netty-jvm:2.1.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

    // GraphQL
    implementation("com.apurebase:kgraphql:0.18.0")
    implementation("com.apurebase:kgraphql-ktor:0.18.0")
}
