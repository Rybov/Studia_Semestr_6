package com.inf136741.bricklist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.inf136741.bricklist.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    var URL:String=""
    var ACTIVE_ONLY:Boolean=false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        URL= intent.getStringExtra("URL")
        ACTIVE_ONLY=intent.getBooleanExtra("ACTIVE_ONLY",true);
        switch1.isChecked=ACTIVE_ONLY;
        editText4.setText(URL)
    }
    override fun finish() {
        URL=editText4.text.toString()
        ACTIVE_ONLY=switch1.isChecked
        val intent = Intent()
        intent.putExtra("URL",this.URL)
        intent.putExtra("ACTIVE_ONLY",ACTIVE_ONLY)
        setResult(Activity.RESULT_OK,intent)
        super.finish()
    }


}
