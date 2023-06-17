package com.sofit.drink.recipe.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofit.drink.recipe.app.R
import com.sofit.drink.recipe.app.databinding.ListItemRecipeBinding
import com.sofit.drink.recipe.app.models.Drink
import com.sofit.drink.recipe.app.room.FavoritesDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeAdapter @Inject constructor(private val dao: FavoritesDao) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onFavoriteCLicked : ((Boolean, Drink)->Unit)? = null

    private val diffCallback = object : DiffUtil.ItemCallback<Drink>() {
        override fun areItemsTheSame(oldItem: Drink, newItem: Drink): Boolean {
            return oldItem.idDrink==newItem.idDrink
        }

        override fun areContentsTheSame(oldItem: Drink, newItem: Drink): Boolean {
            return oldItem==newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list:List<Drink>){
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VH(
            ListItemRecipeBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VH).bindData(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class VH(private val binding : ListItemRecipeBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(m:Drink){
            Glide.with(binding.ivImage)
                .load(m.strDrinkThumb)
                .circleCrop()
                .into(binding.ivImage)

            binding.tvName.text = m.strDrink
            binding.tvDesc.text = m.strInstructions

            binding.cvAlcoholic.isChecked = m.strAlcoholic=="Alcoholic"
            binding.cvAlcoholic.isClickable = false

            CoroutineScope(Dispatchers.IO).launch {
                val isFav = dao.isFavorite(m.idDrink).isNotEmpty()
                withContext(Dispatchers.Main){
                    binding.ivFavorite.setImageResource(
                        if(isFav) R.drawable.ic_fav else R.drawable.ic_unfav
                    )
                    binding.ivFavorite.setOnClickListener {
                        val res = if(isFav){
                            onFavoriteCLicked?.invoke(false, m)
                            R.drawable.ic_unfav
                        }else{
                            onFavoriteCLicked?.invoke(true, m)
                            R.drawable.ic_fav
                        }
                        binding.ivFavorite.setImageResource(res)
                    }
                }
            }
        }
    }
}