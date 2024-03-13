package publish

import kotlinx.serialization.json.Json

@Suppress("OPT_IN_USAGE")
internal val jsonSerializer = Json {
  encodeDefaults = true
  ignoreUnknownKeys = true
  prettyPrint = true
  explicitNulls = false
}