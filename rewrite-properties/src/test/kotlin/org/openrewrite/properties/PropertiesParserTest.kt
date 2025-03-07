/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openrewrite.properties.tree.Properties

@Suppress("UnusedProperty")
class PropertiesParserTest {
    @Test
    fun noEndOfLine() {
        val props = PropertiesParser().parse(
            """
            key=value
        """.trimIndent()
        )[0]

        val entries = props.content.map { it as Properties.Entry }
        assertThat(entries).hasSize(1)
        val entry = entries[0]
        assertThat(entry.key).isEqualTo("key")
        assertThat(entry.value.text).isEqualTo("value")
        assertThat(props.eof).isEqualTo("")
    }

    @Test
    fun endOfLine() {
        val props = PropertiesParser().parse("key=value\n")[0]

        val entries = props.content.map { it as Properties.Entry }
        assertThat(entries).hasSize(1)
        val entry = entries[0]
        assertThat(entry.key).isEqualTo("key")
        assertThat(entry.value.text).isEqualTo("value")
        assertThat(props.eof).isEqualTo("\n")
    }

    @Test
    fun endOfFile() {
        val props = PropertiesParser().parse("key=value\n\n")[0]
        val entries = props.content.map { it as Properties.Entry }
        assertThat(entries).hasSize(1)
        val entry = entries[0]
        assertThat(entry.key).isEqualTo("key")
        assertThat(entry.value.text).isEqualTo("value")
        assertThat(props.eof).isEqualTo("\n\n")
    }

    @Test
    @Suppress("WrongPropertyKeyValueDelimiter")
    fun garbageEndOfFile() {
        val props = PropertiesParser().parse("key=value\nasdf\n")[0]
        val entries = props.content.map { it as Properties.Entry }
        assertThat(entries).hasSize(1)
        val entry = entries[0]
        assertThat(entry.key).isEqualTo("key")
        assertThat(entry.value.text).isEqualTo("value")
        assertThat(props.eof).isEqualTo("\nasdf\n")
    }

    @Test
    fun commentThenEntry() {
        val props = PropertiesParser().parse(
            """
            # this is a comment
            key=value
        """.trimIndent()
        )[0]

        val comment = props.content[0] as Properties.Comment
        assertThat(comment.message).isEqualTo(" this is a comment")
        val entry = props.content[1] as Properties.Entry
        assertThat(entry.prefix).isEqualTo("\n")
        assertThat(entry.key).isEqualTo("key")
        assertThat(entry.value.text).isEqualTo("value")
    }

    @Test
    fun entryCommentEntry() {
        val props = PropertiesParser().parse(
            """
            key1=value1
            # comment
            key2=value2
        """.trimIndent()
        )[0]

        assertThat(props.content).hasSize(3)
        val entry1 = props.content[0] as Properties.Entry
        assertThat(entry1.key).isEqualTo("key1")
        assertThat(entry1.value.text).isEqualTo("value1")
        assertThat(entry1.prefix).isEqualTo("")
        val comment = props.content[1] as Properties.Comment
        assertThat(comment.prefix).isEqualTo("\n")
        assertThat(comment.message).isEqualTo(" comment")
        val entry2 = props.content[2] as Properties.Entry
        assertThat(entry2.prefix).isEqualTo("\n")
        assertThat(entry2.key).isEqualTo("key2")
        assertThat(entry2.value.text).isEqualTo("value2")
    }

    @Test
    fun multipleEntries() {
        val props = PropertiesParser().parse(
            """
            key=value
            key2 = value2
        """.trimIndent()
        )[0]

        assertThat(props.content.map { it as Properties.Entry }.map { it.key })
            .hasSize(2).containsExactly("key", "key2")
        assertThat(props.content.map { it as Properties.Entry }.map { it.value.text })
            .hasSize(2).containsExactly("value", "value2")
    }
}
