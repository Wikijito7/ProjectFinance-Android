package es.wokis.projectfinance.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.domain.invoice.SynchronizeInvoicesUseCase
import es.wokis.projectfinance.data.domain.user.UserLoggedInUseCase
import es.wokis.projectfinance.data.response.AsyncResult
import kotlinx.coroutines.flow.last

@HiltWorker
class SynchronizeWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val synchronizeInvoicesUseCase: SynchronizeInvoicesUseCase,
    private val userLoggedInUseCase: UserLoggedInUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        setForeground(createForegroundNotification())
        if (userLoggedInUseCase().not()) {
            return Result.failure()
        }
        val result = synchronizeInvoicesUseCase().last()
        return if (result is AsyncResult.Success) {
            Result.success()

        } else {
            Result.failure()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        // Create the NotificationChannel.
        val name = appContext.getString(R.string.general__worker_channel_name)
        val descriptionText = appContext.getString(R.string.general__worker_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        with(NotificationManagerCompat.from(appContext)) {
            createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification(): ForegroundInfo {
        val title = appContext.getString(R.string.cloud_sync__worker_title)
        val description = appContext.getString(R.string.cloud_sync__worker_description)
        val notification = NotificationCompat.Builder(applicationContext, id.toString())
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_deposit)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setChannelId(CHANNEL_ID)
            .build()

        return ForegroundInfo(SYNC_WORKER_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "WORKER_CHANNEL_ID"
        private const val SYNC_WORKER_ID = 1
    }
}