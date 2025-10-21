import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.1")
        }
//        commonTest.dependencies {
//            implementation(libs.kotlin.test)
//        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            // https://mvnrepository.com/artifact/org.eclipse.jgit/org.eclipse.jgit
            implementation("org.eclipse.jgit:org.eclipse.jgit:7.4.0.202509020913-r")
            // https://mvnrepository.com/artifact/org.eclipse.jgit/org.eclipse.jgit.ssh.jsch
            implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:7.4.0.202509020913-r")
            // https://mvnrepository.com/artifact/org.json/json
            implementation("org.json:json:20250517")
            implementation(libs.androidx.runtime.saveable.desktop)
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.lollipop.mvh.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.lollipop.mvh"
            packageVersion = "1.0.0"
        }
    }
}
