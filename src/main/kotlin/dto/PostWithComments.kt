package dto

data class PostWithComments(
    val post: Post,
    val author: Author,
    val comments: List<Pair<Comment, Author>>
) {
    override fun toString(): String = buildString {
        append("=============\n")
        append("post.id = ${post.id}\n")
        append("post.authorId = ${post.authorId}\n")
        append("    author.id = ${author.id}\n")
        append("    author.name = ${author.name}\n")
        append("    author.avatar = ${author.avatar}\n")
        append("post.content = ${post.content}\n")
        append("post.published = ${post.published}\n")
        append("post.likedByMe = ${post.likedByMe}\n")
        append("post.likes = ${post.likes}\n")
        post.attachment?.let { attachment ->
            append("post.attachment:\n")
            append("    attachment.url = ${attachment.url}\n")
            append("    attachment.description = ${attachment.description}\n")
            append("    attachment.type = ${attachment.type.name}\n")
        } ?: append("post.attachment = ${post.attachment}\n")
        if (comments.isNotEmpty()) {
            append("comments:\n")
            comments.forEach {
                append("    comment.id = ${it.first.id}\n")
                append("    comment.postId = ${it.first.postId}\n")
                append("    comment.authorId = ${it.first.authorId}\n")
                append("        author.id = ${it.second.id}\n")
                append("        author.name = ${it.second.name}\n")
                append("        author.avatar = ${it.second.avatar}\n")
                append("    comment.content = ${it.first.content}\n")
                append("    comment.published = ${it.first.published}\n")
                append("    comment.likedByMe = ${it.first.likedByMe}\n")
                append("    comment.likes = ${it.first.likes}\n")
                append("    ---------------\n")
            }
        } else {
            append("comments = 0\n")
        }
        append("\n")
    }
}