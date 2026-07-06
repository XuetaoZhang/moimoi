package com.example.myapplication.api

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// ── Vision API ────────────────────────────────────────────────────────

data class VisionRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<VisionMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 300
)

data class VisionMessage(
    val role: String,
    val content: List<Map<String, Any>>
)

data class VisionResponse(
    val choices: List<VisionChoice>
)

data class VisionChoice(
    val message: VisionMessageResponse
)

data class VisionMessageResponse(
    val content: String
)

// ── Image Generation API ──────────────────────────────────────────────

data class ImageGenRequest(
    val model: String = "grok-imagine-image-quality",
    val prompt: String,
    val image: String? = null,   // base64 data URI for image-to-image
    val n: Int = 1,
    val size: String = "1024x1024",
    @SerializedName("response_format") val responseFormat: String = "url"
)

data class ImageGenResponse(
    val created: Long = 0,
    val data: List<ImageGenData>
)

data class ImageGenData(
    val url: String? = null,
    @SerializedName("b64_json") val b64Json: String? = null
)

// ── Retrofit Interface ────────────────────────────────────────────────

interface OpenAIApi {
    @POST("v1/chat/completions")
    suspend fun analyzeImage(@Body request: VisionRequest): VisionResponse

    @POST("v1/images/generations")
    suspend fun generateImages(@Body request: ImageGenRequest): ImageGenResponse
}

// ── Singleton ─────────────────────────────────────────────────────────

object OpenAIService {
    const val BASE_URL = "YOUR-BASE_URL"
    const val API_KEY = "YOUR-API_KEY"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY")
                // Cloudflare 1010 bypass — provider requires browser-like UA
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
                .build()
            chain.proceed(req)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    val api: OpenAIApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAIApi::class.java)
}
