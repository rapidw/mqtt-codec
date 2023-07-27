import org.gradle.api.credentials.PasswordCredentials

plugins {
    `java-library`
    `jvm-test-suite`
    `maven-publish`
    signing
    jacoco
    id("me.champeau.jmh") version "0.6.5"
    id("net.researchgate.release") version "3.0.2"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
//    id "com.github.spotbugs" version "4.7.3"
//    id "com.github.hierynomus.license" version "0.16.1"
    id("io.freefair.lombok") version "6.6.1"
}
group = "io.rapidw.mqtt"

val projectUrl by extra("https://github.com/rapidw/mqtt-codec")
val vcsUrl by extra("${projectUrl}.git")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.30")
    api("io.netty:netty-codec:4.1.45.Final")

    testImplementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation("org.assertj:assertj-core:3.11.1")

    jmh("io.netty:netty-codec-mqtt:4.1.45.Final")
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

//license {
//    header project.file("gradle/license-header")
//}

tasks.withType<Javadoc> {
    options {
        encoding = "UTF-8"
        locale("en_US")
        jFlags("-Duser.language=en_US")
    }

    if (JavaVersion.current().isJava9Compatible) {
        (options as CoreJavadocOptions).addBooleanOption("html5", true)
    }
}

testing {
    suites {
        getting(JvmTestSuite::class) {
            useSpock()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(artifactId)
                description.set("rapidw mqtt codec")
                url.set(url)

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        name.set("pvtyuan")
                        email.set("yyz139@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:${vcsUrl}")
                    developerConnection.set("scm:git:${vcsUrl}")
                    url.set(url)
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

// include pom in jar
//tasks.jar {
//    dependsOn ["generatePomFileForMavenJavaPublication"]
//    into("META-INF/maven/$project.group/$project.name") {
//        from({ generatePomFileForMavenJavaPublication })
//        rename(".*", "pom.xml")
//    }
//}
nexusPublishing.repositories {
    sonatype {
        username.set(providers.gradleProperty("mavenCentralUsername"))
        password.set(providers.gradleProperty("mavenCentralPassword"))
    }
}

release {
    failOnUnversionedFiles.set(false)
    pushReleaseVersionBranch.set("master")
    git {
        requireBranch.set("master")
    }
}

tasks.named("closeAndReleaseSonatypeStagingRepository") {
    dependsOn("publishToSonatype")
}
tasks.afterReleaseBuild {
    dependsOn("closeAndReleaseSonatypeStagingRepository")
}


//spotbugsMain {
//    reports {
//        html {
//            enabled = true
//            destination = file("$buildDir/reports/spotbugs/main/spotbugs.html")
//            stylesheet = 'fancy-hist.xsl'
//        }
//    }
//}

//afterReleaseBuild.dependsOn publish
