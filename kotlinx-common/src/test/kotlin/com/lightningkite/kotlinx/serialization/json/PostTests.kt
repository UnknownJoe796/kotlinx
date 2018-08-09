package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.KxTypeProjection
import com.lightningkite.kotlinx.reflection.ListReflection
import com.lightningkite.kotlinx.reflection.kxReflect
import kotlin.test.Test

class PostTests {

    val data = """[
  {
    "userId": 1,
    "id": 1,
    "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
    "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
  },
  {
    "userId": 1,
    "id": 2,
    "title": "qui est esse",
    "body": "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla"
  },
  {
    "userId": "1",
    "id": 3,
    "title": "Porque nós o usamos?",
    "body": "É um fato conhecido de todos que um leitor se distrairá com o conteúdo de texto legível de uma página quando estiver examinando sua diagramação. A vantagem de usar Lorem Ipsum é que ele tem uma distribuição normal de letras, ao contrário de \"Conteúdo aqui, conteúdo aqui\", fazendo com que ele tenha uma aparência similar a de um texto legível. Muitos softwares de publicação e editores de páginas na internet agora usam Lorem Ipsum como texto-modelo padrão, e uma rápida busca por 'lorem ipsum' mostra vários websites ainda em sua fase de construção. Várias versões novas surgiram ao longo dos anos, eventualmente por acidente, e às vezes de propósito (injetando humor, e coisas do gênero)."
  }]"""


    @Test
    fun reflectiveTest() {

        Post::class.kxReflect = PostReflection

        val typeInfo = KxType(ListReflection, false, listOf(KxTypeProjection(KxType(PostReflection, false))))

        val cycled = JsonSerializer.read(
                type = typeInfo,
                from = data
        )
        println(cycled)
    }
}