package com.aleor.exchangesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aleor.exchangesapp.Fragments.ChatsFragment
import com.aleor.exchangesapp.Fragments.SearchFragment
import com.aleor.exchangesapp.Fragments.SettingsFragment
import com.aleor.exchangesapp.R
import com.google.android.material.tabs.TabLayout

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
        viewPagerAdapter.addFragment(SearchFragment(), "Buscar")
        viewPagerAdapter.addFragment(SettingsFragment(), "Mi cuenta")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    //Regresar a Home
    private fun backToHome(){
        val intent =  Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun goToMap(){
        val intent =  Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){ //Cambiar el constructor

        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init{
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title: String){
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence?{
            return titles[i]
        }
    }
}
