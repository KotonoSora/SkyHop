package com.jn.flagfang.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val context: Context) {
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    // Test Ad Unit ID for Rewarded Ads
    private val adUnitId = "ca-app-pub-3940256099942544/5224354917"

    init {
        MobileAds.initialize(context) {}
        loadRewardedAd()
    }

    fun loadRewardedAd() {
        if (isLoading || (rewardedAd != null)) return
        isLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    isLoading = false
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading = false
                }
            },
        )
    }

    fun showRewardedAd(activity: Activity, onRewardEarned: () -> Unit) {
        rewardedAd?.let { ad ->
            ad.show(activity) {
                onRewardEarned()
                rewardedAd = null
                loadRewardedAd()
            }
        } ?: run {
            loadRewardedAd()
        }
    }
}
