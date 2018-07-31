package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.*

@ExternalReflection
data class Post(
        var userId: Long = 0,
        var id: Long = 0,
        var title: String = "",
        var body: String = ""
)

object PostReflection : KxClass<Post> {


    val userId = KxVariable<Post, Long>(
            name = "userId",
            type = KxType(
                    base = Long::class.kxReflect,
                    nullable = false,
                    typeParameters = listOf(),
                    annotations = listOf()
            ),
            get = { owner -> owner.userId as Long },
            set = { owner, value -> owner.userId = value },
            annotations = listOf()
    )
    val id = KxVariable<Post, Long>(
            name = "id",
            type = KxType(
                    base = Long::class.kxReflect,
                    nullable = false,
                    typeParameters = listOf(),
                    annotations = listOf()
            ),
            get = { owner -> owner.id as Long },
            set = { owner, value -> owner.id = value },
            annotations = listOf()
    )
    val title = KxVariable<Post, String>(
            name = "title",
            type = KxType(
                    base = String::class.kxReflect,
                    nullable = false,
                    typeParameters = listOf(),
                    annotations = listOf()
            ),
            get = { owner -> owner.title as String },
            set = { owner, value -> owner.title = value },
            annotations = listOf()
    )
    val body = KxVariable<Post, String>(
            name = "body",
            type = KxType(
                    base = String::class.kxReflect,
                    nullable = false,
                    typeParameters = listOf(),
                    annotations = listOf()
            ),
            get = { owner -> owner.body as String },
            set = { owner, value -> owner.body = value },
            annotations = listOf()
    )

    override val kclass get() = Post::class

    override val simpleName: String = "Post"
    override val qualifiedName: String = "com.lightningkite.kotlinx.ui.test.Post"
    override val values: Map<String, KxValue<Post, *>> by lazy { mapOf<String, KxValue<Post, *>>() }
    override val variables: Map<String, KxVariable<Post, *>> by lazy { mapOf<String, KxVariable<Post, *>>("userId" to userId, "id" to id, "title" to title, "body" to body) }
    override val functions: List<KxFunction<*>> by lazy { listOf<KxFunction<*>>() }
    override val constructors: List<KxFunction<Post>> by lazy {
        listOf<KxFunction<Post>>(KxFunction<Post>(
                name = "",
                type = KxType(
                        base = Post::class.kxReflect,
                        nullable = false,
                        typeParameters = listOf(),
                        annotations = listOf()
                ),
                arguments = listOf(KxArgument(
                        name = "userId",
                        type = KxType(
                                base = Long::class.kxReflect,
                                nullable = false,
                                typeParameters = listOf(),
                                annotations = listOf()
                        ),
                        annotations = listOf(),
                        default = { previousArguments -> 0 }
                ), KxArgument(
                        name = "id",
                        type = KxType(
                                base = Long::class.kxReflect,
                                nullable = false,
                                typeParameters = listOf(),
                                annotations = listOf()
                        ),
                        annotations = listOf(),
                        default = { previousArguments -> 0 }
                ), KxArgument(
                        name = "title",
                        type = KxType(
                                base = String::class.kxReflect,
                                nullable = false,
                                typeParameters = listOf(),
                                annotations = listOf()
                        ),
                        annotations = listOf(),
                        default = { previousArguments -> "" }
                ), KxArgument(
                        name = "body",
                        type = KxType(
                                base = String::class.kxReflect,
                                nullable = false,
                                typeParameters = listOf(),
                                annotations = listOf()
                        ),
                        annotations = listOf(),
                        default = { previousArguments -> "" }
                )),
                call = { Post(it[0] as Long, it[1] as Long, it[2] as String, it[3] as String) },
                annotations = listOf()
        ))
    }
    override val annotations: List<KxAnnotation> = listOf(KxAnnotation(
            name = "ExternalReflection",
            arguments = listOf()
    ))

    override val isInterface: Boolean get() = false
    override val isOpen: Boolean get() = false
    override val isAbstract: Boolean get() = false
    override val enumValues: List<Post>? = null
}