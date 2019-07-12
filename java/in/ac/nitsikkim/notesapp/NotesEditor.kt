package `in`.ac.nitsikkim.notesapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_notes_editor.*
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.Toast
import java.lang.Exception

class NotesEditor : AppCompatActivity() {

    var id:Int?=0
    var title:String?=null
    var des:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_editor)

        try {
            id = intent.getIntExtra("id", 0)
            title = intent.getStringExtra("title")
            des = intent.getStringExtra("des")
        }catch (ex:Exception){

        }
        val actionBar = supportActionBar
        actionBar!!.title = null
        actionBar.setDisplayHomeAsUpEnabled(true)

        title_editor.setText(title)
        des_editor.setText(des)

        listeners()     //function call to invoke the event listeners

    }

    override fun onSupportNavigateUp(): Boolean {
        addToDB()       //function call to save changes in the notes before closing the activity
        onBackPressed()
        return true
    }

    private fun listeners() {
        editor_title_clear.setOnClickListener {
            //to clear the title of the note
            title_editor.setText("")
        }

        editor_discard.setOnClickListener{
            //listener for discard button
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle(R.string.discard_dialog_title)
            dialog.setMessage(R.string.discard_dialog_message)

            dialog.setPositiveButton(R.string.discard_dialog_pos){dialog,which ->
                this.finish()        //close activity without changing
            }
            dialog.setNegativeButton(R.string.discard_dialog_neg){dialog,which ->
                dialog.cancel()
            }

            val alertDialog:AlertDialog = dialog.create()
            alertDialog.show()
        }

        editor_save.setOnClickListener {
            //listener for save button
            addToDB()       //function call to save the note
            finish()
        }
    }

    //utility function to save changes in the note
    private fun addToDB() {

        val dbManager = DBManager(this)
        val values = ContentValues()

        var title = title_editor.text.toString()
        var des = des_editor.text.toString()

        if(title.isEmpty() && des.isNotEmpty()) {
            title = resources.getString(R.string.no_title)
        }else if(des.isEmpty() && title.isNotEmpty()){
            des = resources.getString(R.string.empty_note)
        }

        if(title.isNotEmpty() && des.isNotEmpty()) {

            values.put("title", title)
            values.put("description", des)

            if (id == 0) {
                val id = dbManager.insert(values)
                if (id > 0) {
                    Toast.makeText(this, R.string.note_saved, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, R.string.error_saving, Toast.LENGTH_LONG).show()
                }
            } else {
                val selectionArgs = arrayOf(id.toString())
                val id = dbManager.update(values, "id=?", selectionArgs)

                if (id > 0) {
                    Toast.makeText(this, R.string.note_update, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.error_updating, Toast.LENGTH_SHORT).show()
                }
            }

        }else{
            Toast.makeText(this,R.string.error_empty,Toast.LENGTH_LONG).show()
        }
    }


}
