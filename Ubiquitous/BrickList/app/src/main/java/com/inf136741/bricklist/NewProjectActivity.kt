package com.inf136741.bricklist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.inf136741.bricklist.database.Database
import com.inf136741.bricklist.database.InventoriesParts
import com.inf136741.bricklist.database.Parts
import kotlinx.android.synthetic.main.activity_new_project.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.lang.Exception
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class NewProjectActivity : AppCompatActivity() {
    var URL=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)
        URL= intent.getStringExtra("URL")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun addOnClick(view: View)
    {
        if(testValues())
        {
            Thread {
                    try{
                        runOnUiThread {
                            button2.isEnabled=false;
                            button2.text = "Loading"
                        }
                        var items = ArrayList<Parts>()
                    val number = editText2.text.toString()
                    val projectURL = URL("$URL$number.xml")
                    println(projectURL);
                    val con = projectURL.openConnection()
                    val documentBuilderFactory = DocumentBuilderFactory.newInstance()
                    val documentBuilder = documentBuilderFactory.newDocumentBuilder()
                    val document = documentBuilder.parse(con.getInputStream())
                    document.documentElement.normalize()
                    val itemList = document.getElementsByTagName("ITEM")
                    var i = 0
                    while (i < itemList.length) {
                        var item: Node = itemList.item(i)
                        if (item.nodeType == Node.ELEMENT_NODE) {
                            var part = Parts()
                            item = item as Element
                            part.ItemType =
                                item.getElementsByTagName("ITEMTYPE").item(0).textContent
                            var alt =
                                item.getElementsByTagName("ALTERNATE").item(0).textContent
                            if (part.ItemType == "P" && alt == "N") {
                                part.QTY = item.getElementsByTagName("QTY").item(0).textContent
                                part.itemID =
                                    item.getElementsByTagName("ITEMID").item(0).textContent
                                part.color = item.getElementsByTagName("COLOR").item(0).textContent
                                part.extra = item.getElementsByTagName("EXTRA").item(0).textContent
                                items.add(part)
                            }
                        }
                        i=i+1;
                    }
                    val database = Database(this)
                    database.newProject(editText.text.toString(), items)
                        runOnUiThread {Toast.makeText(this,"Added",Toast.LENGTH_SHORT).show()
                            editText2.setText("")
                            editText.setText("")
                            button2.isEnabled=true;
                            button2.text = "add"
                        }
                    } catch(e:Exception){
                    runOnUiThread {
                        System.out.println(e)
                        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                        button2.isEnabled=true;
                        button2.text = "add"
                    }
                    }
            }.start()
        }
    }

    fun testValues(): Boolean {
        return editText.text.toString()!="" && editText2.text.toString()!=""
    }

}
