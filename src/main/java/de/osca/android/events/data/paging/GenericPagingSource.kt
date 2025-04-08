package de.osca.android.events.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class GenericPagingSource<T : Any>(
    val onLoad: suspend (skip: Int, limit: Int) -> List<T>,
) : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> =
        try {
            val currentPage = params.key ?: 0
            val pageSize = params.loadSize

            val data = onLoad(pageSize * currentPage, pageSize)

            LoadResult.Page(
                data = data,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (data.isEmpty()) null else currentPage + 1,
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
}
