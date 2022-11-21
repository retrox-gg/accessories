@file:Suppress("UnstableApiUsage")

plugins {
    id("accessories.java-conventions")
    id("accessories.publish-conventions")
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val kotlinVersion by extra(kotlin.coreLibrariesVersion)

dependencies {
    api(project(":api"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

description = "A player accessories system for Retrox"

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
}

tasks.withType<ProcessResources> {
    expand(project.properties)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("plugin")
    dependencies {
        include(project(":api"))
    }
}
