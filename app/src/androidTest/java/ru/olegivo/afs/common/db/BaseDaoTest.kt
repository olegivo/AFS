package ru.olegivo.afs.common.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BaseDaoTest<ROOM : RoomDatabase, DAO>(
    private val roomClass: Class<ROOM>,
    private val daoProvider: (ROOM) -> DAO
) {
    @Rule
    @JvmField
    val instantTaskExecutorRule =
        InstantTaskExecutorRule()

    protected lateinit var room: ROOM
    protected val dao: DAO by lazy { daoProvider(room) }

    @Before
    fun setUp() {
        room = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            roomClass
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        room.close()
    }
}
