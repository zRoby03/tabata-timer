package it.tabatastudio

import android.app.Application
import it.tabatastudio.data.StudioDatabase
import it.tabatastudio.data.StudioRepository
import it.tabatastudio.timer.TimerRuntime

class TabataApplication : Application() {
    override fun onCreate() { super.onCreate(); it.tabatastudio.i18n.AppText.initialize(this) }
    val repository by lazy { StudioRepository(StudioDatabase.open(this)) }
    val timerRuntime by lazy { TimerRuntime(this, repository) }
}
