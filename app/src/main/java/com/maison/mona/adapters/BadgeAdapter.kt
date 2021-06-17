package com.maison.mona.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
    //Liste des headers
    private var itemList = emptyList<Any>()
    //Liste des badges
    private var badgeList = emptyList<Badge>()

    private var mToolbar: Toolbar? = null
    private var mActivity: Activity? = null
    private var mFragmentSupportManager: FragmentManager? = null
    private var mRecyclerView: RecyclerView? = null

    private var inBadge = false

    //onClickListener quand on est sur la premiere page (liste des categories)
    private val onClickCategoryLayer: View.OnClickListener = View.OnClickListener { mActivity?.finish() }

    //onClickListener quand on est sur la deuxieme page (liste des badges par categories)
    private val onClickBadgeLayer: View.OnClickListener = View.OnClickListener {
        val listTypes: List<String> = listOf(
            mActivity!!.getString(R.string.badges_type_borough),
            mActivity!!.getString(R.string.badges_type_artworks),
            mActivity!!.getString(R.string.badges_type_category),
            mActivity!!.getString(R.string.badges_type_other))

        submitList(listTypes)
        mToolbar?.title = mActivity!!.getString(R.string.badge_toolbar_title_main)
        mToolbar?.setOnClickListener(onClickCategoryLayer)
        mRecyclerView?.layoutManager = LinearLayoutManager(mActivity)
    }

    //onClickListener quand on est sur la page du badge
    private val onClickDetailLayer: View.OnClickListener = View.OnClickListener{
        //si on clique sur le retour quand on est sur le detail d'un badge, on detruit le fragment sur lequel on etait
        val ft2 = mFragmentSupportManager?.beginTransaction()
        mFragmentSupportManager?.popBackStack()
        ft2?.commit()
        inBadge = false
        mToolbar?.title = mToolbar?.title?.substring(0, mToolbar?.title?.lastIndexOf('>')!! - 1)
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
                    val badge = binding.badge
                    val ft = mFragmentSupportManager?.beginTransaction()

                    val fragment = BadgeDetailFragment(badge)
                    fragment.view?.isFocusableInTouchMode = true

                    //on passe en argument le badge que l'on veut acceder
                    ft?.add(R.id.fragment_container, fragment)
                    ft?.addToBackStack(null)
                    ft?.commit()
                    inBadge = true

                    mToolbar?.title = mToolbar?.title.toString() + " > " + binding.badge!!.title_fr.toString()

                    mToolbar?.setOnClickListener(onClickDetailLayer)
                }
            }
        }

        override fun bind(item: Badge) {
            binding.apply{
                badge = item
                executePendingBindings()
            }

            when {
                binding.badge!!.optional_args!!.contains("borough") -> {
                    val borough = binding.badge!!.optional_args

                    when {
                        borough!!.contains("Côte-des-Neiges") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_cdn_color, R.drawable.badge_icon_cdn_grey)
                        }
                        borough.contains("Ville-Marie") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_vm_color, R.drawable.badge_icon_vm_grey)
                        }
                        borough.contains("Rosemont") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_rosemont_color, R.drawable.badge_icon_rosemont_grey)
                        }
                        borough.contains("Le Plateau") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_pmr_color, R.drawable.badge_icon_pmr_grey)
                        }
                        borough.contains("Le Sud-Ouest") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_so_color, R.drawable.badge_icon_so_grey)
                        }
                        borough.contains("Mercier") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_hochelaga_color, R.drawable.badge_icon_hochelaga_grey)
                        }
                        borough.contains("Rivière-des-Prairies") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_riviere_des_prairies_color, R.drawable.badge_icon_riviere_des_prairies_grey)
                        }
                        borough.contains("Verdun") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_verdun_color, R.drawable.badge_icon_verdun_grey)
                        }
                        borough.contains("Villeray") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_villeray_color, R.drawable.badge_icon_villeray_grey)
                        }
                        borough.contains("Lachine") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_lachine_color, R.drawable.badge_icon_lachine_grey)
                        }
                        borough.contains("LaSalle") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_lasalle_color, R.drawable.badge_icon_lasalle_grey)
                        }
                        borough.contains("Ahuntsic") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_ac_color, R.drawable.badge_icon_ac_grey)
                        }
                        borough.contains("Outremont") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_outremont_color, R.drawable.badge_icon_outremont_grey)
                        }
                    }
                }
                binding.badge!!.optional_args!!.contains("category") -> {
                    val category = binding.badge!!.optional_args

                    when {
                        category!!.contains("Decorative") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_art_decoratif_color, R.drawable.badge_icon_art_decoratif_grey)
                        }
                        category.contains("Beaux-Arts") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_beaux_arts_color, R.drawable.badge_icon_beaux_arts_grey)
                        }
                        category.contains("Murals") -> {
                            setDrawableBadge(binding, R.drawable.badge_icon_murales_color, R.drawable.badge_icon_murales_grey)
                        }
                    }
                }
                binding.badge!!.optional_args!!.length <= 3 -> {

                    when(binding.badge!!.goal){
                        1 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_1_color, R.drawable.badge_icon_quantite_1_grey) }
                        3 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_3_color, R.drawable.badge_icon_quantite_3_grey) }
                        5 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_5_color, R.drawable.badge_icon_quantite_5_grey) }
                        8 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_8_color, R.drawable.badge_icon_quantite_8_grey) }
                        10 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_10_color, R.drawable.badge_icon_quantite_10_grey) }
                        15 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_15_color, R.drawable.badge_icon_quantite_15_grey) }
                        20 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_20_color, R.drawable.badge_icon_quantite_20_grey) }
                        25 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_25_color, R.drawable.badge_icon_quantite_25_grey) }
                        30 -> { setDrawableBadge(binding, R.drawable.badge_icon_quantite_30_color, R.drawable.badge_icon_quantite_30_grey) }
                    }
                }
                binding.badge!!.optional_args!!.contains("collection") -> {
                    setDrawableBadge(binding, R.drawable.badge_icon_udem_color, R.drawable.badge_icon_udem_grey)
                }
            }
        }
    }

    fun setDrawableBadge(
        binding: BadgeRecyclerviewBadgeBinding,
        imageCollected: Int,
        imageNotCollected: Int
    ){
        if(binding.badge!!.isCollected){
            binding.imageView.setImageResource(imageCollected)
        } else {
            binding.imageView.setImageResource(imageNotCollected)
        }
    }

    //on gere l'affichage si c'est un titre (header)
    inner class BadgeHeaderViewHolder(
        private val binding: BadgeRecyclerviewHeaderBinding
    ) : BaseViewHolder<String>(binding.root){

        init {
            binding.setClickListener {
                mToolbar?.setOnClickListener(onClickBadgeLayer)
                mRecyclerView?.layoutManager = GridLayoutManager(mActivity, 2)

                val string: String? = mActivity?.getString(R.string.badge_toolbar_title_main)
                mToolbar?.title = string + " > " + binding.messageHeader.text

                when(binding.messageHeader.text){
                    mActivity?.getString(R.string.badges_type_borough) -> {
                        submitList(badgeList.filter { it.optional_args!!.contains("borough") })
                    }
                    mActivity?.getString(R.string.badges_type_artworks) -> {
                        submitList(badgeList.filter { it.optional_args!!.length < 3 })
                    }
                    mActivity?.getString(R.string.badges_type_category) -> {
                        submitList(badgeList.filter { it.optional_args!!.contains("category") })
                    }
                    mActivity?.getString(R.string.badges_type_other) -> {
                        submitList(badgeList.filter{ it.optional_args!!.length >= 3 && !it.optional_args.contains("borough") && !it.optional_args.contains("category")})
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
        when (holder) {
            is BadgeHeaderViewHolder -> {
                //si c'est un header on bind comme un string
                holder.bind(element as String)
            }
            is Badge2ViewHolder -> {
                //sinon comme un badge
                holder.bind(element as Badge)
            }
            else -> {
                throw IllegalArgumentException()
            }
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
        return when (itemList[position]) {
            is Badge -> TYPE_BADGE
            is String -> TYPE_HEADER
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    //fonction pour donner l'activity, la toolbar, le support manager et la liste des badges a l'adapter, un peu bourrin mais ca marche
    internal fun giveAdapter(tool: Toolbar?, badges: List<Badge>, activity: Activity, fragmentM: FragmentManager, recyclerview: RecyclerView){
        mToolbar = tool
        badgeList = badges
        mActivity = activity
        mFragmentSupportManager = fragmentM
        mRecyclerView = recyclerview
    }
}