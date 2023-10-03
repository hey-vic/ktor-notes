package com.myprojects.data.note

interface NoteDataSource {
    suspend fun getNoteById(id: String): Note?
    suspend fun insertNote(note: Note): Boolean
    suspend fun updateNoteById(noteId: String, newNote: Note): Boolean
    suspend fun deleteNoteById(id: String): Boolean
    suspend fun getAllNotesOfUserByUserId(userId: String): List<Note>
}