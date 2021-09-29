package android.example.whatsappclone


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        //implementing viewpager
//        setSupportActionBar(toolbar)
//        viewPager.adapter = ScreenSliderAdapter(this)
//
//        TabLayoutMediator(tabs, viewPager,
//            TabLayoutMediator.TabConfigurationStrategy{tab: TabLayout.Tab, pos: Int ->
//                when(pos){
//                    0-> tab.text = "CHATS"
//                    1-> tab.text = "PEOPLE"
//                }
//
//        }).attach()   //only work with viewPager2 not with viewPager1


    }


}