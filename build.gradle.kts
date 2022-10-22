@file:Suppress("UnstableApiUsage")

import java.util.Base64

plugins {
    java
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

repositories {
    mavenCentral()
    // papermc repo
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
}

group = "io.github.ms5984.retrox"
version = "0.1.0"
description = "A player accessories system for Retrox"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<ProcessResources> {
    expand(project.properties)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:reference", true)
    options.quiet()
}

publishing {
    publications {
        create<MavenPublication>("impl") {
            pom {
                name.set("Accessories")
                description.set(project.description)
                url.set(project.properties["url"] as String)
                inceptionYear.set(project.properties["inceptionYear"] as String)

                organization {
                    name.set("Retrox")
                }

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("ms5984")
                        name.set("Matt")
                        url.set("https://github.com/ms5984")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Retrox-gg/accessories.git")
                    developerConnection.set("scm:git:ssh://github.com/Retrox-gg/accessories")
                    url.set("https://github.com/Retrox-gg/accessories")
                }
            }
            from(components["java"])
        }
    }
}

nexusPublishing {
    repositories {
        create("sonatype") {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

if (hasProperty("signingKeyPassphrase")) {
    signing {
        useInMemoryPgpKeys(
            base64Decode(findProperty("base64SigningKey") as String?),
            findProperty("signingKeyPassphrase") as String
        )
        sign(publishing.publications["impl"])
    }
}

fun base64Decode(base64: String?) : String? {
    if (base64 == null) return null
    return Base64.getDecoder().decode(base64).toString(Charsets.UTF_8)
}
