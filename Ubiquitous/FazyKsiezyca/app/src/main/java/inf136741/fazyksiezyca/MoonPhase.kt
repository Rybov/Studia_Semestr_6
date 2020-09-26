package inf136741.fazyksiezyca

import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.ceil
import kotlin.math.round

class MoonPhase {

    var n = listOf<Int>(R.drawable.n0,R.drawable.n1,R.drawable.n2,R.drawable.n3,R.drawable.n4,R.drawable.n6,R.drawable.n7,R.drawable.n8,R.drawable.n9,R.drawable.n10,R.drawable.n11,R.drawable.n12,R.drawable.n13,R.drawable.n14,R.drawable.n15,R.drawable.n16,R.drawable.n17,R.drawable.n18,R.drawable.n19,R.drawable.n20,R.drawable.n21,R.drawable.n22,R.drawable.n23,R.drawable.n24,R.drawable.n25,R.drawable.n26,R.drawable.n27,R.drawable.n28,R.drawable.n29,R.drawable.n30)
    var s = listOf<Int>(R.drawable.s0,R.drawable.s1,R.drawable.s2,R.drawable.s3,R.drawable.s4,R.drawable.s5,R.drawable.s6,R.drawable.s7,R.drawable.s8,R.drawable.s9,R.drawable.s10,R.drawable.s11,R.drawable.s12,R.drawable.s13,R.drawable.s14,R.drawable.s15,R.drawable.s16,R.drawable.s17,R.drawable.s18,R.drawable.s19,R.drawable.s20,R.drawable.s21,R.drawable.s22,R.drawable.s23,R.drawable.s24,R.drawable.s25,R.drawable.s26,R.drawable.s27,R.drawable.s28,R.drawable.s29,R.drawable.s30,R.drawable.s31)

    @RequiresApi(Build.VERSION_CODES.O)
    fun simple(year: Int, month: Int, day: Int): Double {
        var lp = 2551443;
        var now = LocalDateTime.of(year, month, day, 20, 35, 0).atZone(ZoneOffset.UTC).toInstant()
            .toEpochMilli();
        var new_moon = LocalDateTime.of(1880, 2, 9, 20, 35, 0).atZone(ZoneOffset.UTC).toInstant()
            .toEpochMilli();
        var phase = ((now - new_moon) / 1000) % lp;
        return kotlin.math.floor(phase / (24 * 3600).toDouble())+1;
    }

    fun conway(year: Int, month: Int, day: Int): Double {
        var r = year % 100.0;
        r %= 19.0;
        if (r > 9) {
            r -= 19;
        }
        r = r * 11 % 30 + month + day;
        if (month < 3) {
            r += 2;
        }
        if (year < 2000) {
            r -= 4.0;
        } else {
            r -= 8.3;
        }

        r = kotlin.math.floor(r + 0.5) % 30.0;
        if (r < 0)
            return r + 30;
        else
            return r;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun trig1(year: Int, month: Int, day: Int): Double {
        var thisJD = julday(year, month, day)
        var degToRad = 3.14159265 / 180;
        var K0 = Math.floor((year - 1900) * 12.3685);
        var T = (year - 1899.5) / 100;
        var T2 = T * T;
        var T3 = T * T * T;
        var J0 = 2415020 + 29 * K0;
        var F0 =
            0.0001178 * T2 - 0.000000155 * T3 + (0.75933 + 0.53058868 * K0) - (0.000837 * T + 0.000335 * T2);
        var M0 = 360 * (getFrac(K0 * 0.08084821133)) + 359.2242 - 0.0000333 * T2 - 0.00000347 * T3;
        var M1 = 360 * (getFrac(K0 * 0.07171366128)) + 306.0253 + 0.0107306 * T2 + 0.00001236 * T3;
        var B1 =
            360 * (getFrac(K0 * 0.08519585128)) + 21.2964 - (0.0016528 * T2) - (0.00000239 * T3);
        var phase = 0.0;
        var jday = 0.0;
        var M5 = 0.0;
        var M6 = 0.0;
        var B6 = 0.0;
        var oldJ = 0.0;
        while (jday < thisJD) {
            var F = F0 + 1.530588 * phase;
            M5 = (M0 + phase * 29.10535608) * degToRad;
            M6 = (M1 + phase * 385.81691806) * degToRad;
            B6 = (B1 + phase * 390.67050646) * degToRad;
            F -= 0.4068 * Math.sin(M6) + (0.1734 - 0.000393 * T) * Math.sin(M5);
            F += 0.0161 * Math.sin(2 * M6) + 0.0104 * Math.sin(2 * B6);
            F -= 0.0074 * Math.sin(M5 - M6) - 0.0051 * Math.sin(M5 + M6);
            F += 0.0021 * Math.sin(2 * M5) + 0.0010 * Math.sin(2 * B6 - M6);
            F += 0.5 / 1440;
            oldJ = jday;
            jday = J0 + 28 * phase + Math.floor(F);
            phase++;
        }
        return (thisJD - oldJ) % 30;
    }

    fun getFrac(fr: Double): Double {
        return (fr - kotlin.math.floor(fr));
    }
    fun trig2(year: Int, month: Int, day: Int): Double {
        var n = kotlin.math.floor(12.37 * (year - 1900 + ((1.0 * month - 0.5) / 12.0)));
        var RAD = 3.14159265 / 180.0;
        var t = n / 1236.85;
        var t2 = t * t;
        var as1 = 359.2242 + 29.105356 * n;
        var am = 306.0253 + 385.816918 * n + 0.010730 * t2;
        var xtra = 0.75933 + 1.53058868 * n + ((1.178e-4) - (1.55e-7) * t) * t2;
        xtra += (0.1734 - 3.93e-4 * t) * Math.sin(RAD * as1) - 0.4068 * Math.sin(RAD * am);
        var i = 0.0
        if (xtra > 0.0) {
            i = kotlin.math.floor(xtra);
        } else {
            i = ceil(xtra - 1.0)
        }
        var j1 = julday(year, month, day);
        var jd = (2415020 + 28 * n) + i;
        return (j1 - jd + 30) % 30;
    }
    fun julday(year: Int, month: Int, day: Int): Double {
        var year=year;
        if (year < 0) {
            year++; }
        var jy = year;
        var jm = month + 1;
        if (month <= 2) {
            jy--; jm += 12; }
        var jul = kotlin.math.floor(365.25 * jy) + kotlin.math.floor(30.6001 * jm) + day + 1720995;
        if (day + 31 * (month + 12 * year) >= (15 + 31 * (10 + 12 * 1582))) {
            var ja = kotlin.math.floor(0.01 * jy);
            jul = jul + 2 - ja + kotlin.math.floor(0.25 * ja);
        }
        return jul;


    }
    fun getMoonImage(td:Double,nORs:Int):Int
    {
        if(nORs == 1)
        {
            return n[round(td).toInt()];
        }
        else
        {
            if(nORs == 2)
            {
                return s[round(td/29*31).toInt()];
            }
        }
        return 0;
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun alg(year: Int, month: Int, day: Int, a:Int):Double
    {
        var x=0.0
        if(a==1){x= simple(year, month, day);}
        if(a==2){x= conway(year,month, day);}
        if(a==3){x=  trig1(year, month, day);}
        if(a==4){ x= trig2(year, month, day);}
        return x
    }
}