package com.myprojects.data.note

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.litote.kmongo.setTo

class MongoNoteDataSource(
    db: CoroutineDatabase
) : NoteDataSource {

    private val notes = db.getCollection<Note>()

    override suspend fun getNoteById(id: String): Note? {
        return try {
            notes.findOne(Note::id eq ObjectId(id))
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun insertNote(note: Note): Boolean {
        return notes.insertOne(note).wasAcknowledged()
    }

    override suspend fun updateNoteById(noteId: String, newNote: Note): Boolean {
        return try {
            notes.updateOne(
                Note::id eq ObjectId(noteId),
                set(
                    Note::title setTo newNote.title,
                    Note::text setTo newNote.text
                )
            )
                .wasAcknowledged()
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun deleteNoteById(id: String): Boolean {
        return try {
            notes.deleteOne(Note::id eq ObjectId(id)).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAllNotesOfUserByUserId(userId: String): List<Note> {
        return notes.find(Note::ownerId eq userId).toList()
    }
}