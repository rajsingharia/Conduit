package com.example.conduit.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.conduit.R
import com.example.conduit.model.Article
import com.example.conduit.util.Constants.ARTICLE_TYPE_MY
import com.example.conduit.util.Constants.ARTICLE_TYPE_OTHER
import java.text.SimpleDateFormat
import java.util.*


class ArticleAdapter(type:String?): RecyclerView.Adapter<ArticleAdapter.EmployeeViewHolder>(){

    private val type = type

    inner class EmployeeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val profilePic:ImageView = itemView.findViewById(R.id.g_row_profile_pic)
        val heart:ImageView = itemView.findViewById(R.id.r_row_heart)
        val edit:ImageView = itemView.findViewById(R.id.r_row_edit)
        val username:TextView = itemView.findViewById(R.id.g_row_username)
        val dateTv:TextView = itemView.findViewById(R.id.g_row_date)
        val title:TextView = itemView.findViewById(R.id.g_row_title)
        val description:TextView = itemView.findViewById(R.id.g_row_description)
        val tags:TextView = itemView.findViewById(R.id.g_row_tags)
        val numberOfLikes:TextView = itemView.findViewById(R.id.g_row_numberOfLikes)


    }

    private val diffCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: List<Article>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder( LayoutInflater.from( parent.context).inflate(R.layout.single_article_row,parent,false) )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.apply {

            when(type){
                ARTICLE_TYPE_MY->{
                    edit.visibility = View.VISIBLE
                }
                ARTICLE_TYPE_OTHER->{
                    edit.visibility = View.GONE
                }
            }

            username.text = item.author.username
            val outputFormat = SimpleDateFormat("dd/MM/yyyy (HH:mm:aa)", Locale.US)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)

            val date: Date = inputFormat.parse(item.createdAt)
            val outputText: String = outputFormat.format(date)
            dateTv.text = outputText
            title.text = item.title
            description.text = item.description
            numberOfLikes.text = item.favoritesCount.toString()
            Glide
                .with(profilePic.context)
                .load(item.author.image)
                .placeholder(R.drawable.ic_person)
                .into(profilePic)

            if(!item.favorited){
                heart.setImageResource(R.drawable.ic_heart_not_liked)
            }
            else{
                heart.setImageResource(R.drawable.ic_heart)
            }

            var allTag = ""
            for(s in item.tagList){
                allTag += s
                allTag += " , "
            }
            tags.text = allTag

            heart.setOnClickListener {
                onLikeClickListener?.let{ it(item) }
            }


            this.itemView.setOnClickListener {
                onRowClickListener?.let{ it(item) }
            }

            edit.setOnClickListener {
                onEditClickListner?.let{ it(item) }
            }

        }

    }
    private var onRowClickListener : ((Article) -> Unit)? = null
    private var onLikeClickListener : ((Article) -> Unit)? = null
    private var onEditClickListner : ((Article) -> Unit)? = null

    fun setOnRowClickListener(listener: (Article) -> Unit) {
        onRowClickListener = listener
    }

    fun setOnLikeClickListner(listener: (Article) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnEditClickListner(listener: (Article) -> Unit){
        onEditClickListner = listener
    }
}