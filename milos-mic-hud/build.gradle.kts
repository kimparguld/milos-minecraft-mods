plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
    id("io.freefair.lombok") version "9.1.0"
}

version = "${project.property("mod_version")}+mc${project.property("minecraft_version")}"
group = project.property("maven_group") as String

repositories {
    maven {
        url = uri("https://maven.uku3lig.net/releases")
    }
    maven {
        name = "MaxHenkel"
        url = uri("https://maven.maxhenkel.de/repository/public")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    modApi("net.uku3lig:ukulib:${project.property("ukulib_version")}")

    modCompileOnly("de.maxhenkel.voicechat:voicechat-api:${project.property("voicechat_api_version")}")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

base {
    archivesName = project.property("archives_base_name") as String
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

tasks.test {
    useJUnitPlatform()
}
