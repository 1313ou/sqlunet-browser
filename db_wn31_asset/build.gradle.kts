plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName = "db_wn31_asset" // Directory name for the asset pack
    dynamicDelivery {
        deliveryType = "fast-follow"
    }
}
