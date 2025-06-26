import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    java
    idea
    eclipse
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.github.ben-manes.versions")
    id("com.diffplug.spotless")
    id("jacoco")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    create("functionalTest") {
        java {
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            srcDir("src/functional-test/java")
        }
    }
    create("integrationTest") {
        java {
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            srcDir("src/integration-test/java")
        }
    }
}

idea {
    module {
        testSources.from(sourceSets["functionalTest"].java.srcDirs)
        testSources.from(sourceSets["integrationTest"].java.srcDirs)
    }
}

val functionalTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val functionalTestRuntimeOnly: Configuration by configurations.getting

configurations {
    configurations["functionalTestImplementation"].extendsFrom(configurations.testImplementation.get())
    configurations["functionalTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val integrationTestRuntimeOnly: Configuration by configurations.getting

configurations {
    configurations["integrationTestImplementation"].extendsFrom(configurations.testImplementation.get())
    configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())
}

val functionalTest = task<Test>("functionalTest") {
    description = "Runs functional tests."
    group = "verification"

    testClassesDirs = sourceSets["functionalTest"].output.classesDirs
    classpath = sourceSets["functionalTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events ("failed", "passed", "skipped", "standard_out")
    }
}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events ("failed", "passed", "skipped", "standard_out")
    }
}

dependencies {
    /* Spring Boot */
    implementation ("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude (group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    testLogging {
        events ("failed", "passed", "skipped", "standard_out")
    }
}

tasks.check {
    dependsOn(functionalTest)
    dependsOn(integrationTest)
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
    gradleReleaseChannel="current"
}

spotless {
    java {
        palantirJavaFormat()
        formatAnnotations()
    }
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))
    reports {
        html.required.set(true)
        xml.required.set(false)
    }
}

tasks.register<JacocoReport>("jacocoFunctionalTestReport") {
    dependsOn(tasks.named("functionalTest"))
    executionData.setFrom(fileTree(buildDir).include("/jacoco/functionalTest.exec"))
    sourceSets(sourceSets["main"])
    reports {
        html.required.set(true)
        xml.required.set(false)
    }
}

tasks.register<JacocoReport>("jacocoIntegrationTestReport") {
    dependsOn(tasks.named("integrationTest"))
    executionData.setFrom(fileTree(buildDir).include("/jacoco/integrationTest.exec"))
    sourceSets(sourceSets["main"])
    reports {
        html.required.set(true)
        xml.required.set(false)
    }
}

tasks.register<JacocoReport>("jacocoAllTestReport") {
    dependsOn(tasks.named("test"), tasks.named("functionalTest"), tasks.named("integrationTest"))
    executionData.setFrom(
        fileTree(buildDir).include(
            "**/jacoco/test.exec",
            "**/jacoco/functionalTest.exec",
            "**/jacoco/integrationTest.exec"
        )
    )
    sourceSets(sourceSets["main"])
    reports {
        html.required.set(true)
        xml.required.set(false)
    }
}