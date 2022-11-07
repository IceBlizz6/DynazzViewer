dependencies {
    implementation(project(":dynazzviewer.entities"))

    // Query SQL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    // Database connection
    implementation("org.hibernate.orm:hibernate-core:6.1.5.Final")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.1.5.Final")
    implementation("org.xerial:sqlite-jdbc:3.39.3.0")
}
