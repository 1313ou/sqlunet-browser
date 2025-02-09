
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
rootProject.name = "semantikos"

include (":concurrency")
include (":coroutines")
include (":treeView")
include (":deploy")
include (":download")
include (":download_common")
include (":preference")
include (":nightmode")
include (":speak")
include (":capture")
include (":xNet")
include (":bNC")
include (":propbank")
include (":verbNet")
include (":frameNet")
include (":wordNet")
include (":predicateMatrix")
include (":syntagNet")
include (":common")
include (":expandableListFragment")
include (":rate")
include (":others")
include (":donate")
include (":test")
include (":browser")
include (":browserfn")
include (":browserwncommon")
include (":browserwn")
include (":browserewn")
include (":browservn")
include (":browsersn")
include (":assetpacks")
include (":db_wn31_asset")
include (":db_ewn_asset")
include (":dbwn_wn31_asset")
include (":dbewn_ewn_asset")
include (":dbvn_wn31_asset")
include (":dbvn_ewn_asset")
include (":dbfn_wn31_asset")
include (":dbfn_ewn_asset")
include (":dbsn_wn31_asset")
include (":dbsn_ewn_asset")

include (":test-sql")
