package `in`.ac.nitsikkim.notesapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.notes_card.view.*
import kotlinx.android.synthetic.main.notes_card.view.main_card

class MainActivity : AppCompatActivity() {

    var adapter:NoteAdapter?=null               //adapter for main ListView
    var listOfNotes = ArrayList<Notes>()        //ArrayList for Notes objects

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //function call for retrieving notes from database
        loadQuery("%")

        add_note.setOnClickListener {
            //event listener for adding new note
            val intent = Intent(this,NotesEditor::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        //function call to reload all the notes from the database on returning back after making a note
        super.onResume()
        loadQuery("%")
    }


    fun loadQuery(title:String){

        //instance of database manager class
        val dbManager =DBManager(this)
        //columns to retrieve from the table
        val projections = arrayOf("id","title","description")
        //finding notes on the basis of title
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.query(projections,"title like ?",selectionArgs,"id")
        //clearing the list before loading new notes
        listOfNotes.clear()

        if(cursor.moveToFirst()){   //selects the firs row in the database

            do{
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))

                listOfNotes.add(Notes(id,title,description))
            }while(cursor.moveToNext()) //selects the next node

        }

        //instance of adapter object
        adapter = NoteAdapter(this,listOfNotes)
        main_list_view.adapter = adapter        //assigning the adapter to ListView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //inflate menu
        menuInflater.inflate(R.menu.main_menu,menu)

        //implement SearchView and SearchManager
        val searchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        //listener for search bar
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //function to perform task after submitting the search query that includes the query anywhere in the title
                loadQuery("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //function to perform task whenever the text in the search bar changes
                loadQuery("%$newText%")
                Toast.makeText(applicationContext,newText,Toast.LENGTH_LONG).show()
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    /* Adapter class for the ListView */
    inner class NoteAdapter:BaseAdapter{
        var context: Context?=null
        var listOfNotes = ArrayList<Notes>()

        constructor(context:Context,listOfNotes:ArrayList<Notes>):super(){
            this.listOfNotes = listOfNotes
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = layoutInflater.inflate(R.layout.notes_card,null)

            val note = listOfNotes[position]
            val id = note.noteId!!
            val title = note.noteName!!
            val des = note.des!!
            view.card_title.text = title
            view.card_des.text = des

            //TODO
            if(view.card_des.lineCount >=10){
                view.card_des.append("...")
            }

            view.main_card.setOnClickListener {

                val intent = Intent(context,NotesEditor::class.java)
                intent.putExtra("id",id)
                intent.putExtra("title",title)
                intent.putExtra("des",des)

                context!!.startActivity(intent)
            }

            view.card_delete.setOnClickListener {

                var dbManager = DBManager(context!!)
                val selectionArgs = arrayOf(note.noteId.toString())
                dbManager.delete("id=?",selectionArgs)
                loadQuery("%")

            }

            return view
        }

        override fun getItem(position: Int): Any {
            return listOfNotes[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listOfNotes.size
        }

    }
}
