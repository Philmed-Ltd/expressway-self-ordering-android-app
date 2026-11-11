package com.clicks.app.feature.checkout.data

import com.clicks.app.feature.checkout.data.dto.OrderRequest
import com.clicks.app.feature.checkout.data.dto.InitializePaymentRequest
import com.clicks.app.feature.checkout.data.dto.PlaceOrderResponse
import com.clicks.app.feature.checkout.data.dto.CustomerSalesOrderView
import com.clicks.app.network.dto.ApiResponse
import com.clicks.app.network.dto.EntityApiResponse
import com.clicks.app.network.dto.PagedEntityApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface OrdersApi {
    @POST("customer/sales-orders/register")
    suspend fun registerOrder(
        @Body request: OrderRequest
    ): EntityApiResponse<PlaceOrderResponse>

    @POST("customer/sales-orders/pay/{salesOrderId}")
    suspend fun initializePayment(
        @Path("salesOrderId") salesOrderId: String,
        @Body request: InitializePaymentRequest
    ): ApiResponse

    @GET("customer/sales-orders/list")
    suspend fun listOrders(
        @Query("pageNum") pageNum: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): PagedEntityApiResponse<CustomerSalesOrderView>
}
