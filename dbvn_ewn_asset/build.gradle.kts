plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName = "dbvn_ewn_asset" // Directory name for the asset pack
    dynamicDelivery {
        deliveryType = "on-demand"
    }
}
