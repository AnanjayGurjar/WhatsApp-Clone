package android.example.whatsappclone.main_ui


import android.example.whatsappclone.R
import android.example.whatsappclone.adapter.ScreenSliderAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_chat.toolbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //implementing viewpager
        setSupportActionBar(toolbar)
        viewPager.adapter = ScreenSliderAdapter(this)

        TabLayoutMediator(tabs, viewPager,
            TabLayoutMediator.TabConfigurationStrategy{ tab: TabLayout.Tab, pos: Int ->
                when(pos){
                    0-> tab.text = "CHATS"
                    1-> tab.text = "PEOPLE"
                }

        }).attach()   //only work with viewPager2 not with viewPager1


    }


}