import java.util.Base64

plugins {
    java
    `maven-publish`
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:reference", true)
    options.quiet()
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>(name) {
                val artifactName = "${rootProject.name}-${project.name}"
                artifactId = artifactName
                pom {
                    name.set(artifactName)
                    description.set(project.description ?: throw IllegalStateException("publish-conventions: A description is required for publishing"))
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
                        connection.set("scm:git:git://github.com/retrox-gg/accessories.git")
                        developerConnection.set("scm:git:ssh://github.com/retrox-gg/accessories")
                        url.set("https://github.com/retrox-gg/accessories")
                    }
                }
                from(components["java"])
            }
            if (hasProperty("signingKeyPassphrase")) {
                apply(plugin = "signing")
                configure<SigningExtension> {
                    useInMemoryPgpKeys(
                        base64Decode(findProperty("base64SigningKey") as String?),
                        findProperty("signingKeyPassphrase") as String
                    )
                    sign(publishing.publications[name])
                }
            }
        }
    }
}

fun base64Decode(base64: String?) : String? {
    if (base64 == null) return null
    return Base64.getDecoder().decode(base64).toString(Charsets.UTF_8)
}
