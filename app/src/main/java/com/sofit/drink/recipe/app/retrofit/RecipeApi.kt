package com.sofit.drink.recipe.app.retrofit

import com.sofit.drink.recipe.app.models.RecipeModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    @GET("search.php")
    suspend fun getRecipeByName(@Query("s") name: String): Response<RecipeModel>

    @GET("search.php")
    suspend fun getRecipeByAlphabet(@Query("f") alphabet: String): Response<RecipeModel>
}
