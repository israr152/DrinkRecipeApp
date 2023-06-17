package com.sofit.drink.recipe.app.di

import android.content.Context
import androidx.room.Room
import com.sofit.drink.recipe.app.retrofit.ApiConstants
import com.sofit.drink.recipe.app.retrofit.RecipeApi
import com.sofit.drink.recipe.app.room.FavoritesDao
import com.sofit.drink.recipe.app.room.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object GlobalModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance() : Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ApiConstants.baseUrl)
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeApi(retrofit: Retrofit) : RecipeApi = retrofit.create(RecipeApi::class.java)

    @Provides
    @Singleton
    fun provideRecipeDatabase(@ApplicationContext c: Context): RecipeDatabase {
        return Room.databaseBuilder(
            c,
            RecipeDatabase::class.java,
            "recipe_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFavoriteDao(db: RecipeDatabase): FavoritesDao {
        return db.favoriteDao()
    }
}