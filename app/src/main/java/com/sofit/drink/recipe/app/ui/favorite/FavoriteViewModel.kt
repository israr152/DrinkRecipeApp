package com.sofit.drink.recipe.app.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofit.drink.recipe.app.models.Drink
import com.sofit.drink.recipe.app.mvvm.RecipeState
import com.sofit.drink.recipe.app.room.FavoritesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val dao: FavoritesDao) : ViewModel() {

    private val _drinks: MutableStateFlow<RecipeState> = MutableStateFlow(RecipeState.Neutral)
    val drinks: StateFlow<RecipeState> = _drinks

    fun loadFavoriteDrinks() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getFavoriteDrinks()
                .onStart {
                    _drinks.value = RecipeState.Loading
                }
                .catch {
                    _drinks.value = RecipeState.Failed(it)
                }
                .collect {
                    _drinks.value = RecipeState.Success(it)
                }
        }
    }

    fun addToFavorite(m: Drink){
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertDrink(m)
        }
    }

    fun removeFromFavorites(m: Drink) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteDrink(m.idDrink)
        }
    }
}