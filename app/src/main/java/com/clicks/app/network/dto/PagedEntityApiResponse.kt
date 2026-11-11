package com.clicks.app.network.dto

class PagedEntityApiResponse<T> {
    var message: String? = null
    var status: Boolean = false
    var statusCode: Int = 0
    var time: String? = null
    var totalElementCount: Int = 0
    var pageCount: Int = 0
    var pageSize: Int = 20
    var pageNum: Int = 0
    var hasPrevious: Boolean = false
    var hasNext: Boolean = false
    var data: List<T>? = null
}
