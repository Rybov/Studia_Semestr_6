package inf136741.fazyksiezyca

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.JulianFields
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    var moonPhase = MoonPhase();
    val REQUEST_CODE = 1000
    var alg = 1;
    var site = 1;

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readData()
        update()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update() {
        var thisday = LocalDate.now()
        var td = moonPhase.alg(thisday.year, thisday.monthValue, thisday.dayOfMonth, alg)
        moonImage.setImageResource(moonPhase.getMoonImage(td, site))
        var progg = round(td / 29 * 100) / 100
        if (td > 15) {
            next.text =
                "Następna Pełnia: " + thisday.plusDays((30.0 + 15.0 - td).toLong()).toString()
            last.text = "Poprzedni Nów: " + thisday.minusDays((td).toLong()).toString()
        } else {
            if (td == 15.0) {
                next.text = "Pełnia jest Dzisiaj"
                last.text = "Poprzedni Nów: " + thisday.minusDays((td).toLong()).toString()
            } else {
                if (td == 0.0) {
                    next.text =
                        "Następna Pełnia: " + thisday.plusDays((15.0 - td).toLong()).toString()
                    last.text = "Nów jest dzisiaj";
                } else {
                    next.text =
                        "Następna Pełnia: " + thisday.plusDays((15.0 - td).toLong()).toString()
                    last.text = "Poprzedni Nów: " + thisday.minusDays((td).toLong()).toString()

                }

            }
        }
        today.text = "Dzisiaj: " + progg.toString() + "%";
        moonImage.setImageResource(moonPhase.getMoonImage(td, site))
    }

    fun settingsClick(view: View) {
        val i = Intent(this, SettingsActivity::class.java);
        var data = arrayListOf(alg, site);
        i.putExtra("dane", data);
        startActivityForResult(i, REQUEST_CODE);
    }

    fun wholeYear(view: View)
    {
        val i = Intent(this,WholeYear::class.java)
        var data = arrayListOf(alg, site)
        i.putExtra("data",data);
        startActivity(i);

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var savetrue=false;
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode==REQUEST_CODE)&&(resultCode== Activity.RESULT_OK)){
            if(data!=null)
            {
                if(data.hasExtra("returndata"))
                {
                    var d = data.extras?.getIntegerArrayList("returndata");
                    if (d != null) {
                        if(this.alg!=d[0] || this.site !=d[1] )
                        {
                            this.alg= d[0];
                            this.site = d[1];
                            savetrue=true;
                        }
                    }

                }

            }
            if(savetrue)
            {
                saveData()
                update()
            }
        }
    }
    private fun saveData()
    {
        val filename = "data.txt";
        val file = OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE))
        file.write(alg.toString()+"\n");
        file.write(site.toString());
        file.flush()
        file.close()
        Toast.makeText(this,"Zmiany zostały zapisane",Toast.LENGTH_SHORT).show()
    }
    private fun readData() {
        try {
            val filename = "data.txt";
            if(fileExists(filename))
            {
                val file = InputStreamReader(openFileInput(filename))
                val br = BufferedReader(file)
                alg = br.readLine().toInt()
                site  = br.readLine().toInt()
                file.close()
            }
        } catch (e: Exception) {
        }
    }
    private fun fileExists(path:String):Boolean{
        val file = baseContext.getFileStreamPath(path)
        return file.exists()
    }
}
