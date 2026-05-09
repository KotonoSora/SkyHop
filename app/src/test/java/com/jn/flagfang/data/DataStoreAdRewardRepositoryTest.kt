package com.jn.flagfang.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class DataStoreAdRewardRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: DataStoreAdRewardRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tempFolder.newFile("test_ad_reward_prefs.preferences_pb") }
        )
        repository = DataStoreAdRewardRepository(context, testDataStore)
    }

    @Test
    fun `canWatchAdFlow returns true initially`() = testScope.runTest {
        assertTrue(repository.canWatchAdFlow.first())
    }

    @Test
    fun `canWatchAdFlow returns false after recordAdWatched on same day`() = testScope.runTest {
        repository.recordAdWatched()
        assertFalse(repository.canWatchAdFlow.first())
    }
}
