package com.example.habitday.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HabitApi {
    @GET("todos")
    suspend fun getTodos(): Response<TodoListResponse>

    @POST("todos/add")
    suspend fun addTodo(@Body request: AddTodoRequest): Response<TodoDto>
}
