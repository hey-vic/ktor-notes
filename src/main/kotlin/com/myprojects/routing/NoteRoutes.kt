package com.myprojects.routing

import com.myprojects.data.note.Note
import com.myprojects.data.note.NoteDataSource
import com.myprojects.data.requests.NoteRequest
import com.myprojects.data.responses.NoteResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.getNoteById(
    noteDataSource: NoteDataSource
) {
    authenticate {
        get("/note/{id?}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val note = noteDataSource.getNoteById(id)
            if (note == null || note.ownerId != userId) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(
                status = HttpStatusCode.OK,
                message = NoteResponse(
                    text = note.text,
                    title = note.title,
                    id = note.id.toString(),
                    modifiedDateTime = note.modifiedDateTime
                )
            )
        }
    }
}

fun Route.insertNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        post("/note") {
            val request = call.receiveNullable<NoteRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val isValidDateTime = try {
                LocalDateTime.parse(request.modifiedDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            if (!isValidDateTime) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val note = Note(
                ownerId = userId,
                title = request.title,
                text = request.text,
                modifiedDateTime = request.modifiedDateTime
            )

            val wasAcknowledged = noteDataSource.insertNote(note)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.updateNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        put("/note/{id?}") {
            val request = call.receiveNullable<NoteRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            val isValidDateTime = try {
                LocalDateTime.parse(request.modifiedDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            if (!isValidDateTime) {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val note = noteDataSource.getNoteById(id)
            if (note == null || note.ownerId != userId) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            val newNote = Note(
                ownerId = userId,
                title = request.title,
                text = request.text,
                modifiedDateTime = request.modifiedDateTime
            )

            val wasAcknowledged = noteDataSource.updateNoteById(id, newNote)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@put
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.deleteNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        delete("/note/{id?}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val note = noteDataSource.getNoteById(id)
            if (note == null || note.ownerId != userId) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            val wasAcknowledged = noteDataSource.deleteNoteById(id)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@delete
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getAllUserNotes(
    noteDataSource: NoteDataSource
) {
    authenticate {
        get("/note") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val notes = noteDataSource.getAllNotesOfUserByUserId(userId)

            call.respond(
                status = HttpStatusCode.OK,
                message = notes.map { note ->
                    NoteResponse(
                        text = note.text,
                        title = note.title,
                        id = note.id.toString(),
                        modifiedDateTime = note.modifiedDateTime
                    )
                }
            )
        }
    }
}