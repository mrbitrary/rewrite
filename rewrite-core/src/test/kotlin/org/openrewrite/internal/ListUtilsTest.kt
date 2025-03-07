/*
 * Copyright 2020 the original author or authors.
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
package org.openrewrite.internal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListUtilsTest {

    @Test
    fun flatMap() {
        val l = listOf(1.0, 2.0, 3.0)
        assertThat(ListUtils.flatMap(l) { l2 ->
            if(l2.toInt() % 2 == 0) {
                listOf(2.0, 2.1, 2.2)
            } else {
                l2
            }
        }).containsExactly(1.0, 2.0, 2.1, 2.2, 3.0)
    }

    @Test
    fun replaceSingleWithMultipleAtPosition0() {
        val l = listOf(1)
        assertThat(ListUtils.flatMap(l) { l1 -> listOf(2, 3) }).containsExactly(2, 3)
    }
    @Test
    fun removeSingleItem() {
        val l = listOf(1, 2, 3)
        assertThat(ListUtils.flatMap(l) { l1 ->
            if (l1.equals(2)) {
                null
            } else {
                l1
            }
        }).containsExactly(1, 3)
    }

    @Test
    fun replaceItemWithCollectionThenRemoveNextItem() {
        val l = listOf(2, 0)
        assertThat(ListUtils.flatMap(l) { l1 ->
            if (l1.equals(2)) {
                listOf(10, 11)
            } else {
                null
            }
        }).containsExactly(10, 11)
    }

    @Test
    fun removeByAddingEmptyCollection() {
        val l = listOf(2, 0)
        assertThat(ListUtils.flatMap(l) { l1 ->
            if (l1.equals(2)) {
                listOf(10, 11)
            } else {
                listOf<Int>()
            }
        }).containsExactly(10, 11)
    }

}
