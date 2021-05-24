package com.maison.mona.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.maison.mona.R
import com.maison.mona.databinding.BadgeRecyclerviewBadgeBinding
import com.maison.mona.databinding.BadgeRecyclerviewHeaderBinding
import com.maison.mona.entity.Badge
import com.maison.mona.fragment.BadgeDetailFragment

class BadgeAdapter internal constructor (
    context: Context?
): RecyclerView.Adapter<BadgeAdapter.BaseViewHolder<*>>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    //liste des headers
    private var itemList = emptyList<Any>()
    //liste des badges
    private var badgeList = emptyList<Badge>()

    private var mToolbar: Toolbar? = null
    private var mActivity: Activity? = null
    private var mFragmentSupportManager: FragmentManager? = null

    private var inBadge = false

    //onClickListener quand on est sur la premiere page (liste des categories)
    private val onClickCategoryLayer: View.OnClickListener = View.OnClickListener { mActivity?.finish() }

    //onClickListener quand on est sur la deuxieme page (liste des badges par categories)
    private val onClickBadgeLayer: View.OnClickListener = View.OnClickListener {
        val test: List<String> = listOf("Quartiers", "Oeuvres", "Autres")
        submitList(test)
        mToolbar?.setOnClickListener(onClickCategoryLayer)
    }

    //onClickListener quand on est sur la page du badge
    private val onClickDetailLayer: View.OnClickListener = View.OnClickListener{
        //si on clique sur le retour quand on est sur le detail d'un badge, on detruit le fragment sur lequel on etait
        val ft2 = mFragmentSupportManager?.beginTransaction()
        mFragmentSupportManager?.popBackStack()
        ft2?.commit()
        inBadge = false
        mToolbar?.setOnClickListener(onClickBadgeLayer)
    }

    //pour que le recyclerview differencie les badges des headers
    companion object{
        private var TYPE_HEADER = 0
        private var TYPE_BADGE = 1
    }

    //On gère l'affichage si c'est un badge
    inner class Badge2ViewHolder(
        private val binding: BadgeRecyclerviewBadgeBinding
    ) : BaseViewHolder<Badge>(binding.root){
        init{
            binding.setClickListener {
                if(!inBadge){
                    //si on est pas deja dans un badge, on accede au detail du badge dans un autre fragment
                    val badge = binding.badge2
                    val ft = mFragmentSupportManager?.beginTransaction()

                    //on passe en argument le badge que l'on veut acceder
                    ft?.add(R.id.fragment_container, BadgeDetailFragment(badge))
                    ft?.addToBackStack(null)
                    ft?.commit()
                    inBadge = true
                    mToolbar?.setOnClickListener(onClickDetailLayer)
                }
            }
        }

        override fun bind(item: Badge) {
            binding.apply{
                badge2 = item
                executePendingBindings()
            }

            //on check si le badge a ete collecte ou non, on met la bonne image en fonction
            if(binding.badge2!!.isCollected){
                binding.imageView.setImageResource(R.drawable.badge_icon_verdun_color)
            } else{
                binding.imageView.setImageResource(R.drawable.badge_icon_verdun_grey)
            }
        }
    }

    //on gere l'affichage si c'est un titre (header)
    inner class BadgeHeaderViewHolder(
        private val binding: BadgeRecyclerviewHeaderBinding
    ) : BaseViewHolder<String>(binding.root){

        init {
            binding.setClickListener {
                mToolbar?.setOnClickListener(onClickBadgeLayer)

                when(binding.messageHeader.text){
                    "Quartiers" -> {
                        submitList(badgeList.filter { it.optional_args!!.contains("borough") })
                    }
                    "Oeuvres" -> {
                        submitList(badgeList.filter { it.optional_args!!.length < 5 })
                    }
                    "Autres" -> {
                        submitList(badgeList.filter{ it.optional_args!!.length >= 5 && !it.optional_args!!.contains("borough")})
                    }
                }
            }
        }

        override fun bind(item: String) {
            val featuredView: TextView = binding.messageHeader
            featuredView.text = item
        }
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(item: T)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when(viewType) {
            TYPE_HEADER -> {
                val itemBinding = BadgeRecyclerviewHeaderBinding.inflate(inflater, parent, false)
                BadgeHeaderViewHolder(itemBinding)
            }
            TYPE_BADGE -> {
                val itemBinding = BadgeRecyclerviewBadgeBinding.inflate(inflater, parent, false)
                Badge2ViewHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    //
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = itemList[position]
        if(holder is BadgeHeaderViewHolder){
            //si c'est un header on bind comme un string
            holder.bind(element as String)
        } else if (holder is Badge2ViewHolder){
            //sinon comme un badge
            holder.bind(element as Badge)
        } else{
            throw IllegalArgumentException()
        }
    }

    override fun getItemCount() = itemList.size

    //methode pour donner les elements du recyclerview
    internal fun submitList(items: List<Any>){
        this.itemList = items
        notifyDataSetChanged()
    }

    //assigne en int si c'est un badge ou un header (cf le companion object)
    override fun getItemViewType(position: Int): Int {
        val comparable = itemList[position]
        return when (comparable) {
            is Badge -> TYPE_BADGE
            is String -> TYPE_HEADER
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    //fonction pour donner l'activity, la toolbar, le support manager et la liste des badges a l'adapter, un peu bourrin mais ca marche
    internal fun giveAdapter(tool: Toolbar?, badges: List<Badge>, activity: Activity, fragmentM: FragmentManager){
        mToolbar = tool
        badgeList = badges
        mActivity = activity
        mFragmentSupportManager = fragmentM
    }
}