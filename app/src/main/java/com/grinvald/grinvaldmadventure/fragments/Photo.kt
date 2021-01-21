package com.grinvald.grinvaldmadventure.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.grinvald.grinvaldmadventure.QuestDetail
import com.grinvald.grinvaldmadventure.R
import com.squareup.picasso.Picasso


class Photo(f: Fragment) : Fragment() {
    val f = f

    fun newInstance(url: String): Photo {

        val fragment = Photo(f)
        val args = Bundle()
        args.putString("photo_url", url)
        fragment.arguments = args
        return fragment

    }

    lateinit var url : String
    lateinit var iv_image : ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo, container, false)

        url = requireArguments().getString("photo_url").toString()

        iv_image = view.findViewById(R.id.iv_image)
        iv_image.setOnClickListener(View.OnClickListener {
            (f as QuestDetail).showPhoto(url)
        })

        Picasso.get().load(url).into(iv_image)


        return view
    }
}