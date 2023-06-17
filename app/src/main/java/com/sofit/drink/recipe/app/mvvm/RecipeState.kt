package com.sofit.drink.recipe.app.mvvm

import com.sofit.drink.recipe.app.models.Drink

sealed class RecipeState {
    object Loading : RecipeState()
    object Neutral : RecipeState()
    class Success(val data: List<Drink>?) : RecipeState()
    class Failed(val error:Throwable?) : RecipeState()
}
