package com.example.Valiavskiy_191_351.ui.Lab4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.Valiavskiy_191_351.R
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope


class Lab4Fragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_lab4, container, false)
        val vkBtn = root.findViewById<Button>(R.id.vkbtn)
        vkBtn.setOnClickListener {
            VK.login(requireActivity(), arrayListOf(VKScope.FRIENDS, VKScope.PHOTOS))

        }
        return root
    }



}




