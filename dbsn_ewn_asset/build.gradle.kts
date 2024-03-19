plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName = "dbsn_ewn_asset" // Directory name for the asset pack
    dynamicDelivery {
        deliveryType = "on-demand"
    }
}
