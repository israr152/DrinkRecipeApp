package com.sofit.drink.recipe.app.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofit.drink.recipe.app.databinding.FragmentFavoriteBinding
import com.sofit.drink.recipe.app.mvvm.RecipeState
import com.sofit.drink.recipe.app.ui.adapters.RecipeAdapter
import com.sofit.drink.recipe.app.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var binding: FragmentFavoriteBinding? = null

    private val favViewModel : FavoriteViewModel by viewModels()

    @Inject
    lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        collectRecipes()
        favViewModel.loadFavoriteDrinks()
    }

    private fun collectRecipes() {
        lifecycleScope.launch {
            favViewModel.drinks.collect {
                when(it){
                    is RecipeState.Loading -> showProgress()
                    is RecipeState.Failed -> {
                        showProgress(false)
                        it.error?.message?.let { msg->
                            context?.showToast(msg)
                        }
                    }
                    is RecipeState.Success -> {
                        showProgress(false)
                        showList()
                        it.data?.let { it1 -> recipeAdapter.submitList(it1) }
                    }
                    else->{}
                }
            }
        }
    }

    private fun showList(show:Boolean = true) {
        binding?.apply {
            rvList.isVisible = show
        }
    }

    private fun showProgress(show:Boolean = true) {
        binding?.apply {
            progressBar.isVisible = show
        }
    }

    private fun initRecycler() {
        binding?.apply {
            rvList.layoutManager = LinearLayoutManager(root.context)
            rvList.adapter = recipeAdapter

            recipeAdapter.onFavoriteCLicked = { add, m->
                if(add){
                    favViewModel.addToFavorite(m)
                }else{
                    favViewModel.removeFromFavorites(m)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}