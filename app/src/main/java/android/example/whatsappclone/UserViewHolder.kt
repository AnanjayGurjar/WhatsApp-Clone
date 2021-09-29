package android.example.whatsappclone

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_sign_up.view.*
import kotlinx.android.synthetic.main.people_item.view.*

class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(user: User, onClick:(name: String, photo: String, id: String) -> Unit) = with(itemView){
        tv_msgCount.isVisible = false
        tv_time.isVisible = false

        tv_name.text = user.name
        tv_status.text = user.status
        Picasso.get().load(user.thumbImage)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(shapeableImageView)

        setOnClickListener {
            onClick.invoke(user.name, user.thumbImage, user.uid)
        }



    }
}