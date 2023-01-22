import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dto.Author
import dto.Comment
import dto.Post
import dto.PostWithComments
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            val posts = getPosts()

            val result = posts.map {
                async {
                    PostWithComments(
                        post = it,
                        author = getAuthor(it.authorId),
                        comments = getComments(it.id).map { comment ->
                            async {
                                comment to getAuthor(comment.authorId)
                            }
                        }.awaitAll()
                    )
                }
            }.awaitAll()

            result.forEach{
                it.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Thread.sleep(5_000)
}

private const val BASE_URL = "http://localhost:9999/api/"

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()

private val gson = Gson()

suspend fun getPosts(): List<Post> = parseResponse(
    "${BASE_URL}posts",
    object : TypeToken<List<Post>>() {}
)

suspend fun getComments(postId: Long): List<Comment> = parseResponse(
    "${BASE_URL}posts/$postId/comments",
    object : TypeToken<List<Comment>>() {}
)

suspend fun getAuthor(authorId: Long): Author = parseResponse(
    "${BASE_URL}authors/$authorId",
    object : TypeToken<Author>() {}
)

suspend fun <T> parseResponse(url: String, typeToken: TypeToken<T>): T {
    val response = makeRequest(url)
    return withContext(Dispatchers.Default) {
        gson.fromJson(requireNotNull(response.body).string(), typeToken.type)
    }
}

suspend fun makeRequest(url: String): Response =
    suspendCoroutine { continuation ->
        client.newCall(
            Request.Builder()
                .url(url)
                .build()
        )
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
    }