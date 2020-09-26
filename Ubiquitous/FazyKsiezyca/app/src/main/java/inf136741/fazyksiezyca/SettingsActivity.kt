package inf136741.fazyksiezyca

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    var data = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {

        val extras = intent.extras ?:return
        data = extras.getIntegerArrayList("dane") as ArrayList<Int>
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        prepare()
    }

    fun prepare()
    {
        if(data[0]==1)
            simple.setTextColor(Color.RED)
        if(data[0]==2)
            conway.setTextColor(Color.RED)
        if(data[0]==3)
            trig1.setTextColor(Color.RED)
        if(data[0]==4)
            trig2.setTextColor(Color.RED)
        if(data[1]==1)
            n.setTextColor(Color.RED)
        if(data[1]==2)
            s.setTextColor(Color.RED)


    }
    fun simpleonclick(view: View)
    {
        simple.setTextColor(Color.RED);
        conway.setTextColor(Color.BLACK);
        trig1.setTextColor(Color.BLACK);
        trig2.setTextColor(Color.BLACK);
        data[0]=1;
    }
    fun conwayonclick(view: View)
    {
        simple.setTextColor(Color.BLACK);
        conway.setTextColor(Color.RED);
        trig1.setTextColor(Color.BLACK);
        trig2.setTextColor(Color.BLACK);
        data[0]=2;
    }
    fun trig1onclick(view: View)
    {
        simple.setTextColor(Color.BLACK);
        conway.setTextColor(Color.BLACK);
        trig1.setTextColor(Color.RED);
        trig2.setTextColor(Color.BLACK);
        data[0]=3;
    }
    fun trig2onclick(view: View)
    {
        simple.setTextColor(Color.BLACK)
        conway.setTextColor(Color.BLACK);
        trig1.setTextColor(Color.BLACK);
        trig2.setTextColor(Color.RED);
        data[0]=4;
    }

    fun nonclick(view: View)
    {
        n.setTextColor(Color.RED)
        s.setTextColor(Color.BLACK)
        data[1]=1
    }
    fun sonclick(view: View)
    {
        n.setTextColor(Color.BLACK)
        s.setTextColor(Color.RED)
        data[1]=2
    }

    override fun finish() {
        val data1 = Intent()
        data1.putExtra("returndata",this.data)
        setResult(Activity.RESULT_OK,data1)
        super.finish()
    }
}

