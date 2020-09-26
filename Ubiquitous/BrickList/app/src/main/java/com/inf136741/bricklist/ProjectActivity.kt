package com.inf136741.bricklist
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inf136741.bricklist.database.Database
import com.inf136741.bricklist.database.InventoriesParts
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.InputStream
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class ProjectActivity : AppCompatActivity() {
    var id = 0
    var AC = 0
    var NAME = ""
    var inventoryParts = ArrayList<InventoriesParts>()
    val TAG = "ProjectActivity"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        id = intent.getIntExtra("ID", 0)
        AC = intent.getIntExtra("AC",0)
        NAME = intent.getStringExtra("NAME")
        val database = Database(this);
        inventoryParts = database.getProjectsParts(id)
        this.supportActionBar?.title =NAME.toUpperCase();
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if(item.itemId==R.id.action_settings1)
        {
            xml(this)
            return super.onOptionsItemSelected(item)
        }
        else
        {
            if(item.itemId==R.id.action_settings2)
            {
                val database=Database(this)
                AC=database.updateActive(AC,id)
                activeText(item)
                return true;
            }
            else
            {
                return super.onOptionsItemSelected(item)

            }
        }
    }
    fun activeText(item: MenuItem)
    {
        if(AC==1)
        {
            item.title="Make Archive"
        }else
            item.title="Make Active"
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_project, menu)
        val ac = menu.getItem(1)
        activeText(ac)
        return true
    }
    override fun onStart()
    {
        super.onStart()
        val layoutInflater = LayoutInflater.from(this)
        val parent = findViewById<ViewGroup>(R.id.parts_layout)
        Thread{
            for(inventory in inventoryParts)
            {
                newPart(layoutInflater,parent,inventory)
            }

        }.start()
    }
    @SuppressLint("SetTextI18n")
    private fun newPart(layoutInflater: LayoutInflater, parent: ViewGroup, inventory:InventoriesParts)
    {

        val view: View =layoutInflater.inflate(R.layout.list_view_custom_layout,parent,false)
        val name: TextView = view.findViewById(R.id.Brickname)
        val color: TextView = view.findViewById(R.id.ColorName)
        val value: TextView = view.findViewById(R.id.Value)
        val plusButton: Button = view.findViewById(R.id.plus)
        val minusButton: Button = view.findViewById(R.id.minus)
        val image:ImageView = view.findViewById(R.id.imageView)
        name.text=inventory.name
        color.text=inventory.color+"("+inventory.code+")"
        value.text=inventory.quantityInStore.toString()+" of " +inventory.quantityInSet;
        plusButton.setOnClickListener{plusOnClickButton(inventory,value)}
        minusButton.setOnClickListener{minusOnClickButton(inventory,value)}
        addImage(image,inventory)
        runOnUiThread{
            parent.addView(view)
        }
    }
    private fun plusOnClickButton(inventory:InventoriesParts,value:TextView)
    {
        if(inventory.quantityInStore<inventory.quantityInSet)
        {
            inventory.quantityInStore++;
            value.text=inventory.quantityInStore.toString()+" of " +inventory.quantityInSet;
            val database = Database(this)
            database.update(inventory.id,inventory.quantityInStore);
        }
    }
    private fun minusOnClickButton(inventory:InventoriesParts,value:TextView)
    {
        if(inventory.quantityInStore>0)
        {
            inventory.quantityInStore--;
            value.text=inventory.quantityInStore.toString()+" of " +inventory.quantityInSet;
            val database = Database(this)
            database.update(inventory.id,inventory.quantityInStore);
        }
    }
    private fun addImage(image:ImageView,inventory: InventoriesParts)
    {
        try{
            val addr= "https://www.lego.com/service/bricks/5/2/${inventory.codesCode}"
            val input= URL(addr).content
            image.setImageDrawable(Drawable.createFromStream(input as InputStream,null))
        }catch(e:Exception)
        {
            try {
                val addr= "http://img.bricklink.com/P/7/3001old.gif/P/${inventory.colorCode}/${inventory.code}.gif"
                val input= URL(addr).content
                image.setImageDrawable(Drawable.createFromStream(input as InputStream,null))
            }catch (e:Exception)
            {
                try{
                    val addr= "https://www.bricklink.com/PL/${inventory.code}.jpg"
                    val input= URL(addr).content
                    image.setImageDrawable(Drawable.createFromStream(input as InputStream,null))
                }catch (e:Exception){}
            }
        }
    }
    fun xml(context:Context)
    {
        val database=Database(this)
        val project = database.getProjectsParts(id)
        val docBuilder:DocumentBuilder= DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()
        val rootElement:Element=doc.createElement("INVENTORY")
        for(element in project)
        {
            if(element.quantityInSet>element.quantityInStore)
            {
                val itemElement:Element=doc.createElement("ITEM")
                val itemTypeElement:Element=doc.createElement("ITEMTYPE")
                itemTypeElement.appendChild(doc.createTextNode("P"))
                val itemIdElement:Element=doc.createElement("ITEMID")
                itemIdElement.appendChild(doc.createTextNode(element.itemID.toString()))
                val colorElement:Element=doc.createElement("COLOR")
                colorElement.appendChild(doc.createTextNode(element.colorCode.toString()))
                val qtyElement:Element=doc.createElement("QTYFILLED")
                qtyElement.appendChild(doc.createTextNode((element.quantityInSet-element.quantityInStore).toString()))
                val conditionElement:Element=doc.createElement("CONDITION")
                conditionElement.appendChild(doc.createTextNode("N"))
                itemElement.appendChild(itemTypeElement)
                itemElement.appendChild(itemIdElement)
                itemElement.appendChild(colorElement)
                itemElement.appendChild(qtyElement)
                itemElement.appendChild(conditionElement)
                rootElement.appendChild(itemElement)
            }
        }
        doc.appendChild(rootElement)
        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        val path = getExternalFilesDir(null)
        val outDir = File(path,"XMLOutput")
        outDir.mkdir()
        val title = NAME.replace(" ","").trim()
        val file = File(outDir, "$title.xml")
        transformer.transform(DOMSource(doc),StreamResult(file))
        Toast.makeText(this,"Exported", Toast.LENGTH_SHORT).show()
    }

}
