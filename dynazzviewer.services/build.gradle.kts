dependencies {
    implementation(project(":dynazzviewer.entities"))
    implementation(project(":dynazzviewer.storage"))

    // JSON
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")

    // API service communication
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
}
