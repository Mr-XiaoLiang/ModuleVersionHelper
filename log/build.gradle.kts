plugins {
    alias(libs.plugins.kotlinMultiplatform)
//    kotlin("jvm") version "2.2.20"
}

group = "com.lollipop"
version = "1.0.0"

repositories {
    mavenCentral()
}


kotlin {
    jvmToolchain(21)
    jvm()
    sourceSets {
        jvmMain.dependencies {
            // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
            api("org.slf4j:slf4j-simple:2.0.17")
        }
    }
}