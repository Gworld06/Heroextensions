package com.nxsha

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class NxshaPlugin : Plugin() {

    override fun load(context: Context) {
        registerMainAPI(NxshaProvider())
    }
}
