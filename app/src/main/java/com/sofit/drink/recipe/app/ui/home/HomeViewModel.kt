package com.sofit.drink.recipe.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofit.drink.recipe.app.models.Drink
import com.sofit.drink.recipe.app.mvvm.RecipeState
import com.sofit.drink.recipe.app.retrofit.RecipeApi
import com.sofit.drink.recipe.app.room.FavoritesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private var api: RecipeApi, private var favoritesDao: FavoritesDao) : ViewModel() {

    private val _drinks : MutableStateFlow<RecipeState> = MutableStateFlow(RecipeState.Neutral)
    val drinks : StateFlow<RecipeState> = _drinks

    fun getRecipesByName(name:String){
        viewModelScope.launch(Dispatchers.IO) {
            _drinks.value = RecipeState.Loading

            try{
                val call = api.getRecipeByName(name)

                if(call.isSuccessful){
                    if(call.code()==200){
                        _drinks.value = call.body()?.let {
                            RecipeState.Success(it.drinks)
                        } ?: RecipeState.Failed(
                            Throwable("Something went wrong, Please try again!")
                        )
                    }else{
                        _drinks.value = RecipeState.Failed(Throwable(call.message()))
                    }
                }else{
                    _drinks.value = RecipeState.Failed(Throwable(call.message()))
                }
            }catch (e:Exception){
                _drinks.value = RecipeState.Failed(null)
            }
        }
    }

    fun getRecipesByAlphabet(name:String){
        viewModelScope.launch(Dispatchers.IO) {
            _drinks.value = RecipeState.Loading

            try {
                val call = api.getRecipeByAlphabet(name)

                if(call.isSuccessful){
                    if(call.code()==200){
                        _drinks.value = call.body()?.let {
                            RecipeState.Success(it.drinks)
                        } ?: RecipeState.Failed(
                            Throwable("Something went wrong, Please try again!")
                        )
                    }else{
                        _drinks.value = RecipeState.Failed(Throwable(call.message()))
                    }
                }else{
                    _drinks.value = RecipeState.Failed(Throwable(call.message()))
                }
            } catch (e: Exception) {
                _drinks.value = RecipeState.Failed(null)
            }
        }
    }

    fun addToFavorite(m:Drink){
        viewModelScope.launch(Dispatchers.IO) {
            favoritesDao.insertDrink(m)
        }
    }

    fun removeFromFavorites(m: Drink) {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesDao.deleteDrink(m.idDrink)
        }
    }
}