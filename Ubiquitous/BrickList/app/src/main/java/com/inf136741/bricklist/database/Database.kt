package com.inf136741.bricklist.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.FileOutputStream
import java.io.IOException
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.ZoneId


class Database(private val context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,1) {

    companion object {
        val DATABASE_NAME = "BrickList.db";
        val DATABASE_PATH = "/data/data/com.inf136741.bricklist/databases/";
    }


    @Throws(SQLException::class)
    fun open() {
        val dbPath = DATABASE_PATH + DATABASE_NAME;
        SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

    }
    @Synchronized
    override fun close() {
        writableDatabase?.close()
        super.close()
    }
    fun check(): Boolean {
        val dbPath = DATABASE_PATH + DATABASE_NAME;
        var checkDB: SQLiteDatabase
        try {
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (e: Exception) {
            return false;
        }
        if (checkDB == null)
            return false;
        checkDB.close();
        return true;
    }
    @Throws(IOException::class)
    fun copy() {
        val input = context.assets.open(DATABASE_NAME);
        val outputFile = DATABASE_PATH + DATABASE_NAME;
        val output = FileOutputStream(outputFile)
        val buffer = ByteArray(1024)
        var length: Int
        length = input.read(buffer)
        while (length > 0) {
            output.write(buffer, 0, length)
            length = input.read(buffer)
        }
        output.flush()
        output.close()
        input.close()
    }
    fun create() {
        if (!check()) {
            this.readableDatabase.close()
            try {
                copy()
            } catch (e: Exception) {
            }
        }
    }
    override fun onCreate(db: SQLiteDatabase?) {
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
    fun getInventoryNames(activeOnly: Boolean): ArrayList<Inventory> {
        var returns = ArrayList<Inventory>()
        var query: String
        if (!activeOnly) {
            query = "SELECT * FROM Inventories WHERE ACTIVE=1;"
        } else {
            query = "SELECT * FROM Inventories;"
        }
        val database = this.writableDatabase
        val cursor = database.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val inventory = Inventory()
            var id = cursor.getInt(0)
            val name = cursor.getString(1)
            val ac = cursor.getInt(2)
            val la = cursor.getInt(3)
            inventory.id = id;
            inventory.name = name;
            inventory.active = ac
            inventory.lastAccessed = la
            returns.add(inventory)
        }
        cursor.close()
        database.close()
        return returns
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun newProject(name: String, listParts: ArrayList<Parts>) {
        val cv = ContentValues()
        cv.put("Name", name)
        var id = nextid("Inventories")
        cv.put("id", id)
        var zoneId = ZoneId.systemDefault();
        cv.put("LastAccessed", LocalDateTime.now().atZone(zoneId).toEpochSecond().toString())
        val database = this.writableDatabase
        database.insert("Inventories", null, cv)
        database.close()
        for (part in listParts) {
            newProjectPart(id, part)
        }

    }
    fun newProjectPart(inventory_id: Int, part: Parts) {
        val cv = ContentValues()
        cv.put("id", nextid("InventoriesParts"))
        cv.put("InventoryID", inventory_id)
        cv.put("ColorID", colorID(part.color))
        cv.put("Extra", part.extra)
        cv.put("QuantityInSet", part.QTY)
        cv.put("QuantityInStore", 0)
        cv.put("TypeID", 2)
        cv.put("ItemID", itemID(part.itemID))
        val database = this.writableDatabase
        database.insert("InventoriesParts", null, cv)
        database.close()
    }
    fun nextid(name: String): Int {
        val id = "SELECT id FROM $name order by id DESC"
        val database = this.writableDatabase
        val cursor = database.rawQuery(id, null)
        if (cursor.moveToNext())
            return cursor.getInt(0) + 1
        else
            return 0;
    }
    fun colorID(color: String): Int {
        val id = "SELECT id FROM Colors where Code = $color";
        val database = this.writableDatabase
        val cursor = database.rawQuery(id, null)
        if (cursor.moveToNext()) {
            val out = cursor.getInt(0)
            database.close()
            return out
        } else {
            database.close()
            return 1;
        }
    }
    fun itemID(item: String): Int {
        val id = "SELECT id FROM Parts where Code = '$item'";
        val database = this.writableDatabase
        val cursor = database.rawQuery(id, null)
        if (cursor.moveToNext()) {
            val out = cursor.getInt(0)
            database.close()
            return out
        } else {
            database.close()
            return -1;
        }
    }
    fun getProjectsParts(projectID: Int): ArrayList<InventoriesParts> {
        val ret = ArrayList<InventoriesParts>()
        val query  = "Select i.id,i.QuantityInSet,i.QuantityInStore,i.ColorId,p.Code,p.Name,i.ItemID From InventoriesParts i join Parts p  on i.ItemId=p.id  WHERE InventoryID = $projectID"
        val database = this.writableDatabase
        val cursor = database.rawQuery(query,null)
        while(cursor.moveToNext())
        {
            val part = InventoriesParts()
            part.id= cursor.getInt(0)
            part.quantityInSet=cursor.getInt(1)
            part.quantityInStore=cursor.getInt(2)
            part.colorid=cursor.getInt(3)
            val c= color(part.colorid)
            part.color=c[0]
            part.colorCode=c[1].toInt()
            part.code=cursor.getString(4)
            part.name=cursor.getString(5)
            part.itemID=cursor.getInt(6)
            part.codesCode=getCodesCode(part.itemID,part.colorid)
            ret.add(part)
        }
        cursor.close()
        database.close()
        return  ret
    }
    fun color(id:Int):ArrayList<String> {
        var ret = ArrayList<String>()
        val query = "Select Name,Code From Colors where id = $id;"
        val database = this.writableDatabase
        val cursor = database.rawQuery(query,null)
        if(cursor.moveToNext()) {
            ret.add(cursor.getString(0))
            ret.add(cursor.getString(1))
            cursor.close()
            database.close()
            return ret
        }else{
            cursor.close()
            database.close()
            return ret
        }
    }
    fun update(id:Int,value:Int) {
        val db = this.writableDatabase
        val values= ContentValues()
        values.put("QuantityInStore", value)
        db.update("InventoriesParts",values,"id=?", arrayOf((id).toString()))
    }
    fun getCodesCode(ItemID:Int,ColorID:Int):Int
    {
        val query = "Select Code From Codes where ItemID = $ItemID and ColorID=$ColorID;"
        val database = this.writableDatabase
        val cursor = database.rawQuery(query,null)
        if(cursor.moveToNext()) {
            var ret=cursor.getInt(0)
            cursor.close()
            database.close()
            return ret
        }else{
            cursor.close()
            database.close()
            return -1
        }
    }
    fun updateActive(ac:Int,id:Int):Int {
        var ret = ac
        val db = this.writableDatabase
        val values= ContentValues()
        if(ret==1)
            ret=0
        else
            ret=1
        values.put("Active", ret)
        db.update("Inventories",values,"id=?", arrayOf((id).toString()))
        return ret
    }
}

