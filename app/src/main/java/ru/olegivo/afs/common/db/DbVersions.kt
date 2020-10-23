/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.common.db

import ru.olegivo.afs.common.db.migrations.migration1_2
import ru.olegivo.afs.common.db.migrations.migration2_1
import ru.olegivo.afs.common.db.migrations.migration2_3
import ru.olegivo.afs.common.db.migrations.migration3_2

object DbVersions {
    const val v1 = 1
    const val v2 = 2
    const val v3 = 3

    const val current = v3

    val migrations = arrayOf(
        migration1_2,
        migration2_1,
        migration2_3,
        migration3_2
    )
}
