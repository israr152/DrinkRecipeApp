package com.sofit.drink.recipe.app.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofit.drink.recipe.app.databinding.FragmentHomeBinding
import com.sofit.drink.recipe.app.mvvm.RecipeState
import com.sofit.drink.recipe.app.ui.adapters.RecipeAdapter
import com.sofit.drink.recipe.app.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    private val homeViewModel : HomeViewModel by viewModels()

    @Inject
    lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        collectRecipes()
        initSearchView()

        homeViewModel.getRecipesByName("margarita")
    }

    private var searchHandler = Handler(Looper.getMainLooper())
    private fun initSearchView() {
        binding?.apply {
            etQuery.addTextChangedListener {
                val q = it?.toString()?:""
                searchHandler.removeCallbacksAndMessages(null)
                if(q.isEmpty()){
                    homeViewModel.getRecipesByName("margarita")
                }else{
                    searchHandler.postDelayed({
                        if(rbAlphabet.isChecked){
                            homeViewModel.getRecipesByAlphabet(q)
                        }else{
                            homeViewModel.getRecipesByName(q)
                        }
                    },300)
                }
            }
        }
    }

    private fun collectRecipes() {
        lifecycleScope.launch {
            homeViewModel.drinks.collect {
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
                    homeViewModel.addToFavorite(m)
                }else{
                    homeViewModel.removeFromFavorites(m)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}