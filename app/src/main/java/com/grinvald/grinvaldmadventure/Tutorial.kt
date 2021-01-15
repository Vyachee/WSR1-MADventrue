package com.grinvald.grinvaldmadventure

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.grinvald.grinvaldmadventure.fragments.Tutorial_1
import com.grinvald.grinvaldmadventure.models.TutorialItem

class Tutorial : AppCompatActivity() {

    lateinit var viewPager : ViewPager2
    lateinit var ll_dots : LinearLayout
    lateinit var tv_next : TextView
    lateinit var tv_skip : TextView
    lateinit var tutorialList : MutableList<TutorialItem>
    var pages_count = 3
    lateinit var dotsList : MutableList<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        dotsList = mutableListOf()
        tutorialList = mutableListOf()
        tutorialList.add(TutorialItem(
                ResourcesCompat.getDrawable(resources, R.drawable.task1, null)!!,
                "Select the quest!",
                "A huge collection of different quests. Historical, children's, outdoors and many others..."
        ))
        tutorialList.add(TutorialItem(
                ResourcesCompat.getDrawable(resources, R.drawable.task2, null)!!,
                "Complete the task!",
                "Search for secret keys, location detection, step counting and much more..."
        ))
        tutorialList.add(TutorialItem(
                ResourcesCompat.getDrawable(resources, R.drawable.task3, null)!!,
                "Become a Top Key Finder",
                "User ratings, quest ratings, quest author ratings..."
        ))

        pages_count = tutorialList.size

        initViews()

        for(i in 1..pages_count) {
            val layoutInflater = LayoutInflater.from(this)
            val dot : LinearLayout = layoutInflater.inflate(R.layout.dot, ll_dots, false) as LinearLayout

            val params : ViewGroup.MarginLayoutParams = dot.layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = 16;
            dot.layoutParams = params

            if(i != 1)
                dot.background = getDrawable(R.drawable.d_dot_inactive)

            dotsList.add(dot)
            ll_dots.addView(dot)
        }

        val pagerAdapter = ScreenSlideAdapter(this)

        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for(x in dotsList)
                    x.background = getDrawable(R.drawable.d_dot_inactive)

                dotsList.get(position).background = getDrawable(R.drawable.d_dot)

                if(position == pages_count-1) {
                    tv_next.text = "Done"
                    tv_skip.visibility = View.INVISIBLE
                }   else {
                    tv_next.text = "Next"
                    tv_skip.visibility = View.VISIBLE
                }
            }
        })

        tv_next.setOnClickListener(View.OnClickListener {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            if (viewPager.currentItem + 1 == tutorialList.size) {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
                finish()
            }
        })

        tv_skip.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        })
    }

    private inner class ScreenSlideAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return pages_count
        }

        override fun createFragment(position: Int): Fragment {

            val tutorialItem : TutorialItem = tutorialList.get(position)

            return Tutorial_1.newInstance(tutorialItem)
        }
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        ll_dots = findViewById(R.id.ll_dots)
        tv_next = findViewById(R.id.tv_next)
        tv_skip = findViewById(R.id.tv_skip)
    }
}