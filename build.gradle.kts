import org.jenkinsci.gradle.plugins.jpi.JpiDeveloper
import org.jenkinsci.gradle.plugins.jpi.JpiLicense
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktlintVersion by project
val jenkinsCoreVersion by project
val jenkinsCredentialsPluginVersion by project
val sezpozVersion by project
val atriumVersion by project

plugins {
    kotlin("jvm") version "1.2.0"
    kotlin("kapt") version "1.2.0"
    id("org.jenkins-ci.jpi") version "0.24.0"
    id("org.jetbrains.dokka") version "0.9.15"
    id("com.diffplug.gradle.spotless") version "3.7.0"
    jacoco
}

dependencies {
    compile(kotlin("stdlib-jre8"))

    testCompile("junit:junit:4.12")
    testCompile("ch.tutteli:atrium-cc-en_UK-robstoll:$atriumVersion")
    testCompile("ch.tutteli:atrium-verbs:$atriumVersion")

    jenkinsPlugins("org.jenkins-ci.plugins:credentials:$jenkinsCredentialsPluginVersion")

    jenkinsTest("org.jenkins-ci.main:jenkins-test-harness:2.32") { isTransitive = true }

    kapt("net.java.sezpoz:sezpoz:$sezpozVersion")
}

jenkinsPlugin {
    displayName = "Accurev Client Plugin"
    shortName = "accurev-client"
    gitHubUrl = "https://github.com/casz/accurev-client-plugin"
    url = "https://wiki.jenkins.io/display/JENKINS/Accurev+Client+Plugin"

    coreVersion = jenkinsCoreVersion as String
    compatibleSinceVersion = coreVersion
    fileExtension = "jpi"
    pluginFirstClassLoader = true
    workDir = file("$buildDir/work")

    developers = this.Developers().apply {
        developer(delegateClosureOf<JpiDeveloper> {
            setProperty("id", "casz")
            setProperty("name", "Joseph Petersen")
            setProperty("email", "josephp90@gmail.com")
            setProperty("timezone", "UTC+1")
        })
    }
    licenses = this.Licenses().apply {
        license(delegateClosureOf<JpiLicense> {
            setProperty("url", "https://jenkins.io/license/")
        })
    }
}

spotless {
    kotlin {
        // optionally takes a version
        ktlint(ktlintVersion as String)
    }
    kotlinGradle {
        target(listOf("*.gradle.kts"))
        ktlint(ktlintVersion as String)
    }
}

jacoco {
    toolVersion = "0.7.9"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

task<Wrapper>("wrapper") {
    gradleVersion = "4.3.1"
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    maven(url = "https://repo.jenkins-ci.org/public/")
    jcenter()
}

// Workaround for https://issues.jenkins-ci.org/browse/JENKINS-48353
configurations.all { exclude(module = "junit-dep") }

// Workaround for https://github.com/Kotlin/dokka/issues/146
buildscript {
    repositories {
        maven(url = "https://repo.jenkins-ci.org/public/")
        jcenter()
    }
}