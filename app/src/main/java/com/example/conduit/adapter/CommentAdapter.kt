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
import com.example.conduit.model.Comment
import com.example.conduit.model.CommentResponse
import java.text.SimpleDateFormat
import java.util.*


class CommentAdapter(username:String): RecyclerView.Adapter<CommentAdapter.EmployeeViewHolder>(){

    private val username = username

    inner class EmployeeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val title:TextView = itemView.findViewById(R.id.comment_title)
        val profile:ImageView = itemView.findViewById(R.id.comment_user_profile_pic)
        val usernameTv:TextView = itemView.findViewById(R.id.comment_user_name)
        val dateTv:TextView = itemView.findViewById(R.id.comment_date)
        val deleteBtn:ImageView = itemView.findViewById(R.id.comment_delete_btn)

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Comment>(){
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: List<Comment>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder( LayoutInflater.from( parent.context).inflate(R.layout.single_comment_row,parent,false) )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.apply {
            usernameTv.text = item.author.username
            title.text = item.body
            Glide
                .with(profile.context)
                .load(item.author.image)
                .placeholder(R.drawable.ic_person)
                .into(profile)

            val outputFormat = SimpleDateFormat("dd/MM/yyyy (HH:mm:aa)", Locale.US)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)

            val date: Date = inputFormat.parse(item.createdAt)
            val outputText: String = outputFormat.format(date)
            dateTv.text = outputText

            if(username == item.author.username){
                deleteBtn.visibility = View.VISIBLE
            }

            deleteBtn.setOnClickListener {
                onDeleteClickListener?.let{ it(item) }
            }

        }

    }

    private var onDeleteClickListener : ((Comment) -> Unit)? = null

    fun setOnDeleteClickListner(listener: (Comment) -> Unit) {
        onDeleteClickListener = listener
    }
}