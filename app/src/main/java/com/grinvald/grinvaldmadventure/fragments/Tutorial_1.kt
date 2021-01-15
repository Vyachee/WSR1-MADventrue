package com.grinvald.grinvaldmadventure.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.grinvald.grinvaldmadventure.R
import com.grinvald.grinvaldmadventure.models.TutorialItem


class Tutorial_1 : Fragment() {

    companion object {
        fun newInstance(data: TutorialItem): Tutorial_1 {
            val args = Bundle()

            val fragment = Tutorial_1()
            fragment.arguments = Bundle().apply {
                putSerializable("data", data)
            }
            return fragment
        }
    }

    lateinit var tv_title: TextView
    lateinit var tv_description: TextView
    lateinit var iv_image: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tutorial_1, container, false)

        tv_title = view.findViewById(R.id.tv_title)
        tv_description = view.findViewById(R.id.tv_description)
        iv_image = view.findViewById(R.id.iv_image)

        val data : TutorialItem = arguments?.getSerializable("data") as TutorialItem

        iv_image.setImageDrawable(data.img)
        tv_title.text = data.title
        tv_description.text = data.description

        return view;
    }

}