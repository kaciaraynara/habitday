package com.example.habitday.data.remote

data class TodoListResponse(
    val todos: List<TodoDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class TodoDto(
    val id: Int,
    val todo: String,
    val completed: Boolean,
    val userId: Int
)

data class AddTodoRequest(
    val todo: String,
    val completed: Boolean,
    val userId: Int
)
