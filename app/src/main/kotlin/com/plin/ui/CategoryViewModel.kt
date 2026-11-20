package com.plin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.plin.data.CategoryService
import com.plin.domain.enums.TaskCategory
import com.plin.domain.models.Category

class CategoryViewModel(private val categoryService: CategoryService) : ViewModel() {

    val allCategories: StateFlow<List<Category>> = combine(
        flowOf(TaskCategory.entries.filter { it != TaskCategory.CUSTOM }),
        categoryService.observeAll()
    ) { predefined, custom ->
        val system = predefined.map { Category(it.name, it.toDisplayString(), it.color, isSystem = true) }
        system + custom
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addCategory(name: String, color: Color) {
        viewModelScope.launch {
            categoryService.addCategory(name, color)
        }
    }
}

class CategoryViewModelFactory(private val categoryService: CategoryService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(categoryService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}