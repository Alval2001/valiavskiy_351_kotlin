package com.example.Valiavskiy_191_351.ui.Lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.Valiavskiy_191_351.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class Lab3Fragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_lab3, container, false)
        val btn = root.findViewById<Button>(R.id.button)
        var text_html = root.findViewById<TextView>(R.id.editTextTextMultiLine)
        var data = root.findViewById<TextView>(R.id.textView2)

        btn.setOnClickListener{
            GlobalScope.async {
                val doc: Document = Jsoup.connect("https://kalendata.ru/today/").get()
                val numBlock = doc.select("h4.list-group-item-heading")

                data.text = numBlock.text()
                text_html.text = doc.text()


            }
        }





        return root
    }



}