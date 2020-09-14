import java.net.URI

rootProject.name = "watchfuleye"
include(":sample", ":library")

val env: Map<String, String> = System.getenv()
if (env.containsKey("GRADLE_CACHE_URL")) {
    buildCache {
        remote(HttpBuildCache::class) {
            url = URI.create(env["GRADLE_CACHE_URL"] ?: error("bad config -> ${env.filterKeys { "GRADLE" in it }}"))
            isPush = env.containsKey("CI")
            credentials {
                username = env["GRADLE_CACHE_USERNAME"]
                password = env["GRADLE_CACHE_PASSWORD"]
            }
        }
    }
}
