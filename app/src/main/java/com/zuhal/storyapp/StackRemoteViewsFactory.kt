package com.zuhal.storyapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.zuhal.storyapp.data.local.room.StoryDao
import com.zuhal.storyapp.data.local.room.StoryDatabase
import com.zuhal.storyapp.utils.AppExecutors

class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private lateinit var dao: StoryDao
    private val mWidgetItems = ArrayList<Bitmap>()
    private val appExecutors = AppExecutors()

    override fun onCreate() {
        dao = StoryDatabase.getInstance(mContext).storyDao()
    }

    override fun onDataSetChanged() {
        // prevent force close
        val identityToken = Binder.clearCallingIdentity()

        appExecutors.diskIO.execute {
            val stories = dao.getStories()

            stories.map { story ->
                val bitmap = Glide.with(mContext)
                    .asBitmap()
                    .load(story.photoUrl)
                    .submit().get()

                mWidgetItems.add(bitmap)
            }
        }

        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {}

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            StoryWidget.EXTRA_ITEM to position
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}