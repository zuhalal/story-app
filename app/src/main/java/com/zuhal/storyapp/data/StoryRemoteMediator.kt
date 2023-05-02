package com.zuhal.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.zuhal.storyapp.data.local.entity.StoryEntity
import com.zuhal.storyapp.data.local.room.StoryDatabase
import com.zuhal.storyapp.data.remote.retrofit.ApiService
import androidx.room.withTransaction

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = INITIAL_PAGE_INDEX
        return try {
            val responseData = apiService.getAllStories(token, page, state.config.pageSize)
            val endOfPaginationReached = responseData.listStory.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyDao().deleteAllStories()
                }

                val data = responseData.listStory.map {
                    StoryEntity(
                        it.id,
                        it.photoUrl,
                        it.createdAt,
                        it.name,
                        it.description,
                        it.lat,
                        it.lon
                    )
                }
                database.storyDao().insert(data)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}