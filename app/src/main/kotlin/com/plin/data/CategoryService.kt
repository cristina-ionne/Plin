package com.plin.data

import com.plin.data.models.CategoryEntity
import com.plin.domain.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Service to manage categories.
 * It converts the DB entities into domain objects and vice versa.
 */
class CategoryService(private val categoryDao: CategoryDao) {

    fun observeAll(): Flow<List<Category>> {
        return categoryDao.observeAll().map { entities ->
            entities.map { entity ->
                Category(
                    id = entity.id.toString(),
                    name = entity.name,
                    color = Color(entity.colorArgb),
                    isSystem = false
                )
            }
        }
    }

    suspend fun addCategory(name: String, color: Color) {
        val entity = CategoryEntity(
            name = name.trim(),
            colorArgb = color.toArgb()
        )
        categoryDao.insert(entity)
    }

    suspend fun deleteCategory(id: Int) {
        categoryDao.deleteById(id)
    }
}