plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName = "dbwn_wn31_asset" // Directory name for the asset pack
    dynamicDelivery {
        deliveryType = "fast-follow"
    }
}
