package com.inf136741.bricklist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView
import com.inf136741.bricklist.database.Database
import com.inf136741.bricklist.database.Inventory

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.FieldPosition

class MainActivity : AppCompatActivity() {
    var inv=ArrayList<Inventory>()
    var URL = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    var ACTIVE_ONLY = false;
    val REQUEST_CODE = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        var database = Database(this)
        database.create();
        fab.setOnClickListener {
            fabOnClick();
        }
        update()
        println(database.colorID("1"))
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.action_settings)
        {
            var intent = Intent(this,SettingsActivity::class.java);
            intent.putExtra("URL",URL)
            intent.putExtra("ACTIVE_ONLY",ACTIVE_ONLY)
            startActivityForResult(intent,REQUEST_CODE);
            return true;
        }
            else
        {
            return super.onOptionsItemSelected(item)
        }
    }
    fun fabOnClick()
    {
        var intent = Intent(this,NewProjectActivity::class.java);
        intent.putExtra("URL",URL);
        startActivity(intent);

    }
    fun update()
    {

        var database = Database(this)
        database.open()
        inv = database.getInventoryNames(ACTIVE_ONLY);
        val show = ArrayList<String>();
        for(x in inv)
        {
            show.add(x.name.toString())
        }
        projects.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,show)
        projects.setOnItemClickListener{_, _, position, _ ->  listviewOnClick(position);}
        projects.setOnItemLongClickListener{_, _, position, _ ->  listviewLongClick(position);}
        database.close()
    }
    fun listviewOnClick(position:Int)
    {
        var intent = Intent(this,ProjectActivity::class.java);
        var id = inv[position].id
        intent.putExtra("NAME",inv[position].name)
        intent.putExtra("ID",id)
        intent.putExtra("AC",inv[position].active)
        startActivity(intent)
    }
    fun listviewLongClick(position: Int): Boolean
    {
        return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode==REQUEST_CODE)&&(resultCode== Activity.RESULT_OK))
        {
            if (data != null) {
                if(data.hasExtra("URL"))
                    URL=data.getStringExtra("URL")
                if(data.hasExtra("ACTIVE_ONLY"))
                    ACTIVE_ONLY=data.getBooleanExtra("ACTIVE_ONLY",true)
            }
        }
        update()
    }
    override fun onResume() {
        super.onResume()
        update()
    }
}
