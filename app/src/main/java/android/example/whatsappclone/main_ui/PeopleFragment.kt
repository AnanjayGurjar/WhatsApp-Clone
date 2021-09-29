package android.example.whatsappclone.main_ui

import android.content.Intent
import android.example.whatsappclone.R
import android.example.whatsappclone.auth_registration.DATA_USER
import android.example.whatsappclone.dataClass.User
import android.example.whatsappclone.viewHolders.EmptyViewHolder
import android.example.whatsappclone.viewHolders.UserViewHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_inbox.*

const val NORMAL_VIEW_TYPE = 1
const val DELETED_VIEW_TYPE = 2

class PeopleFragment : Fragment() {

    lateinit var mAdapter: FirestorePagingAdapter<User, RecyclerView.ViewHolder>
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance().collection(DATA_USER)
            .orderBy("name", Query.Direction.ASCENDING)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setUpAdapter()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    private fun setUpAdapter() {
        val config = PagedList.Config.Builder()
                .setPageSize(10)
                .setPrefetchDistance(2)                 //number of pages that you want to fetch
                .setEnablePlaceholders(false)
                .build()

        val options = FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(database, config, User::class.java)
                .build()

        mAdapter = object : FirestorePagingAdapter<User, RecyclerView.ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when(viewType){
                    NORMAL_VIEW_TYPE -> UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.people_item, parent, false))
                    else -> EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_view, parent, false))
                }
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
                if(holder is UserViewHolder){
                    holder.bind(model){ name: String, photo: String, id: String ->
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.putExtra(NAME, name)
                        intent.putExtra(IMAGE_URL, photo)
                        intent.putExtra(ID, id)
                        startActivity(intent)

                    }
                }
            }

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                return if(auth.uid == item?.uid){
                    NORMAL_VIEW_TYPE
                }else{
                    DELETED_VIEW_TYPE
                }

            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }


}