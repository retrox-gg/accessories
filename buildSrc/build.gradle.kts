plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

kotlinDslPluginOptions {
    jvmTarget.set("17")
}
