package com.sofit.drink.recipe.app.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sofit.drink.recipe.app.models.Drink

@Database(entities = [Drink::class],version = 1,exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun favoriteDao() : FavoritesDao
}