package `in`.ac.nitsikkim.notesapp

class Notes {

    var noteId:Int?=0
    var noteName:String?=null
    var des:String?=null

    constructor(noteId:Int,noteName:String,des:String){
        this.noteId = noteId
        this.noteName = noteName
        this.des = des
    }
}