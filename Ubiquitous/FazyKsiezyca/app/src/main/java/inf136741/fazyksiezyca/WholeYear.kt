package inf136741.fazyksiezyca

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_whole_year.*
import java.time.LocalDate

class WholeYear : AppCompatActivity() {

    var data = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        val extras = intent.extras ?:return
        data = extras.getIntegerArrayList("data") as ArrayList<Int>
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whole_year)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun findAllFullMoon(view: View) {
        var year = editText.text.toString().toInt()
        if (year > 2200 || year < 1900) {
            Toast.makeText(this, "Rok musi być w zakresie 1900-2000", Toast.LENGTH_SHORT).show();
            editText.setTextColor(Color.RED)
        } else {
            editText.setTextColor(Color.BLACK)
            var date = LocalDate.of(year, 1, 1);
            date=findDay(date);
            dateview.text=date.toString();
            date=date.plusDays(1)
            date=findDay(date);
            dateview2.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview3.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview4.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview5.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview6.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview7.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview8.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview9.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview10.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview11.text=date.toString()
            date=date.plusDays(1)
            date=findDay(date);
            dateview12.text=date.toString()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun findDay(date:LocalDate):LocalDate
    {
        var date = date
        var moonPhase = MoonPhase()
        var value = moonPhase.alg(date.year,date.monthValue,date.dayOfMonth,data[0])
        while(value!=15.0)
        {
            date=date.plusDays(1)
            value = moonPhase.alg(date.year,date.monthValue,date.dayOfMonth,data[0])
        }
        return date
    }

}
