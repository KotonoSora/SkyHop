package com.jn.flagfang.viewmodel

import com.jn.flagfang.ads.AdManager
import com.jn.flagfang.billing.BillingManager
import com.jn.flagfang.data.SettingsRepository
import com.jn.flagfang.domain.repository.AdRewardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModelTest {

    private lateinit var viewModel: ShopViewModel
    private val settingsRepository: SettingsRepository = mock()
    private val billingManager: BillingManager = mock()
    private val adRewardRepository: AdRewardRepository = mock()
    private val adManager: AdManager = mock()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(settingsRepository.coinsFlow).thenReturn(flowOf(100))
        whenever(settingsRepository.purchasedItemsFlow).thenReturn(flowOf(emptySet()))
        whenever(settingsRepository.selectedSkinFlow).thenReturn(flowOf("default"))
        whenever(settingsRepository.shieldCountFlow).thenReturn(flowOf(0))
        whenever(settingsRepository.multiplierCountFlow).thenReturn(flowOf(0))
        whenever(billingManager.products).thenReturn(MutableStateFlow(emptyList()))
        whenever(billingManager.status).thenReturn(MutableStateFlow(com.jn.flagfang.billing.BillingStatus.IDLE))
        whenever(adRewardRepository.canWatchAdFlow).thenReturn(flowOf(true))

        viewModel = ShopViewModel(
            settingsRepository,
            billingManager,
            adRewardRepository,
            adManager,
            isDebug = true
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `watchRewardedAd emits adShowRequests when canWatchAd is true`() = testScope.runTest {
        var received = false
        val job = launch {
            viewModel.adShowRequests.first()
            received = true
        }
        viewModel.watchRewardedAd()
        advanceUntilIdle()
        assertTrue(received)
        job.cancel()
    }

    @Test
    fun `onAdRewardEarned adds coins and records ad watched`() = testScope.runTest {
        viewModel.onAdRewardEarned()

        verify(settingsRepository).addCoins(500)
        verify(adRewardRepository).recordAdWatched()
    }
}
