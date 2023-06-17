package com.sofit.drink.recipe.app.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.sofit.drink.recipe.app.models.Drink
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert(onConflict = REPLACE)
    fun insertDrink(m:Drink)

    @Query("Delete from favorite_drinks where idDrink = :id")
    fun deleteDrink(id:String)

    @Query("SELECT * FROM favorite_drinks")
    fun getFavoriteDrinks():Flow<List<Drink>>

    @Query("SELECT * FROM favorite_drinks WHERE idDrink = :id")
    fun isFavorite(id: String):List<Drink>
}