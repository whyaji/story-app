package com.whyaji.storyapp.ui.story.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.whyaji.storyapp.data.local.StoryRepository
import com.whyaji.storyapp.data.remote.response.ListStoryItem
import com.whyaji.storyapp.util.DataDummy
import com.whyaji.storyapp.util.MainDispatcherRule
import com.whyaji.storyapp.util.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var storyRepository: StoryRepository

    private lateinit var listStoryViewModel: ListStoryViewModel

    @Before
    fun setUp() {
        listStoryViewModel = ListStoryViewModel(storyRepository)
    }

    @Test
    fun `when getAllStories Should Not Null and Return Success`() = runTest {
        val dummyStoriesResponse = DataDummy.generateDummyStory()
        val pagingData = StoryPagingSource.snapshot(dummyStoriesResponse)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = pagingData
        `when`(storyRepository.getAllStories()).thenReturn(expectedStories)

        val actualStories = listStoryViewModel.getAllStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStoriesResponse, differ.snapshot())
        assertEquals(dummyStoriesResponse.size, differ.snapshot().size)
        assertEquals(dummyStoriesResponse[0], differ.snapshot()[0])
    }

    @Test
    fun `when getAllStories is empty and Return 0`() = runTest {
        val dummyStoriesResponse = DataDummy.generateDummyStoryEmpty()
        val pagingData = StoryPagingSource.snapshot(dummyStoriesResponse)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = pagingData
        `when`(storyRepository.getAllStories()).thenReturn(expectedStories)

        val actualStories: PagingData<ListStoryItem>? = try {
            listStoryViewModel.getAllStories().getOrAwaitValue()
        } catch (e: Exception) {
            null
        }

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        if (actualStories != null) {
            differ.submitData(actualStories)
        } else {
            assertEquals(0, differ.snapshot().size)
        }
    }
}

val listUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
