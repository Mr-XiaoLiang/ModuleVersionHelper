import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
//    alias(libs.plugins.composeHotReload)
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
            implementation("org.eclipse.jgit:org.eclipse.jgit:7.4.0.202509020913-r") {
                exclude("com.jcraft", "jsch")
            }
            implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:7.4.0.202509020913-r") {
                exclude("com.jcraft", "jsch")
            }
            // https://github.com/mwiede/jsch
            implementation("com.github.mwiede:jsch:2.27.5")

            // https://mvnrepository.com/artifact/org.json/json
            implementation("org.json:json:20250517")
            implementation(libs.androidx.runtime.saveable.desktop)
            // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
            implementation("org.slf4j:slf4j-simple:2.0.17")
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.lollipop.mvh.MainKt"

        buildTypes.release {
            proguard {
                configurationFiles.from(file("proguard-rules.pro"))
                isEnabled = false
            }
        }

        jvmArgs += listOf("-Xmx2G")
        val appName = "ModuleVersionHelper"
        val versionName = "1.0.0"
        val pkgName = "com.lollipop.mvh"
        val sdf = SimpleDateFormat("yyyyMMdd-HHmmss")
        val buildVersion = "${versionName}-${sdf.format(Date(System.currentTimeMillis()))}"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appName
            packageVersion = versionName
            description = appName
            macOS {
                dockName = appName
                bundleID = pkgName
                pkgPackageVersion = versionName
                pkgPackageBuildVersion = buildVersion
//                iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
            }
            windows {
                menuGroup = appName
                dirChooser = true
//                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
            }
            linux {
                packageName = appName
                menuGroup = appName
//                iconFile.set(project.file("src/desktopMain/resources/icon.png"))
            }
        }
    }
}
