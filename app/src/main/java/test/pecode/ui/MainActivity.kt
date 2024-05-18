package test.pecode.ui

import android.Manifest.permission
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import test.pecode.R
import test.pecode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewPager: ViewPager2

    private var numOfFragments = 1

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            sentNotification()
        } else {
            return@registerForActivityResult
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewPager()
        setUpSavedTotalFragments()
        createFragment()
        deleteFragment()
        createNotification()
        createNotificationChannel()
    }

    private fun setUpViewPager() {
        viewPager = binding.pager

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = numOfFragments

            override fun createFragment(position: Int) = PageFragment.newInstance(position + 1)
        }
    }

    private fun setUpSavedTotalFragments() {
        val sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val fragmentCount = sharedPref.getInt(KEY_FRAGMENT_COUNT, 1)

        if (fragmentCount > numOfFragments) {
            numOfFragments = fragmentCount
            updateDeleteButtonVisibility()
        }
    }

    private fun deleteFragment() {
        binding.deleteButton.setOnClickListener {
            with(NotificationManagerCompat.from(this)) {
                cancel(numOfFragments)
            }

            numOfFragments--

            updateDeleteButtonVisibility()

            (viewPager.adapter as FragmentStateAdapter).notifyDataSetChanged()
            viewPager.setCurrentItem(numOfFragments - 1, false)

            saveFragmentCount(numOfFragments)
        }
    }

    private fun createFragment() {
        binding.createButton.setOnClickListener {
            numOfFragments++

             updateDeleteButtonVisibility()

            (viewPager.adapter as FragmentStateAdapter).notifyDataSetChanged()
            viewPager.setCurrentItem(numOfFragments - 1, true)

            saveFragmentCount(numOfFragments)
        }
    }

    private fun updateDeleteButtonVisibility() = with(binding) {
        if (numOfFragments > 1) {
            deleteButton.visibility = View.VISIBLE
        } else {
            deleteButton.visibility = View.INVISIBLE
        }
    }

    private fun createNotification() {
        binding.createNotification.setOnClickListener {
            sentNotification()
        }
    }

    private fun saveFragmentCount(count: Int) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(KEY_FRAGMENT_COUNT, count)
            apply()
        }
    }

    private fun sentNotification() {
        val idNotification = viewPager.currentItem + 1

        val myIntent = Intent(this@MainActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(KEY_PAGE, idNotification)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, myIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Chat heads active")
            .setContentText("Notification $idNotification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent).setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity, permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                activityResultLauncher.launch(permission.POST_NOTIFICATIONS)
            }
            notify(idNotification, builder.build())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val page = intent?.extras?.getInt(KEY_PAGE) ?: 0
        viewPager.setCurrentItem(page, false)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "General chanel"
            val descriptionText = "All notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val chanel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(chanel)
        }
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    companion object {
        private const val CHANNEL_ID = "channelID"
        private const val KEY_PAGE = "1"
        private const val PREFS_NAME = "fragment_count"
        private const val KEY_FRAGMENT_COUNT = "total_fragments"
    }
}
