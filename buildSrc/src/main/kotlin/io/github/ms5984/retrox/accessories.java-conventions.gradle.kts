plugins {
    java
}

repositories {
    mavenCentral()
    // papermc repo
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

val tagLibVersion by extra("0.0.3")

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
    implementation("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    implementation("io.github.ms5984.libraries:tag-lib:$tagLibVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
