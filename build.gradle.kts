java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.11"
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

group = "com.extremelyd1"
version = "1.11.1"
description = "MinecraftBingo"