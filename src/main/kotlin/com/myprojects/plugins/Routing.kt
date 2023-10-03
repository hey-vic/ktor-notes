package com.myprojects.plugins

import com.myprojects.data.note.NoteDataSource
import com.myprojects.data.user.UserDataSource
import com.myprojects.routing.*
import com.myprojects.security.hashing.HashingService
import com.myprojects.security.token.TokenConfig
import com.myprojects.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    noteDataSource: NoteDataSource
) {
    routing {
        signUp(hashingService, userDataSource)
        signIn(hashingService, userDataSource, tokenService, tokenConfig)
        authenticate()
        getSecretInfo()
        getNoteById(noteDataSource)
        insertNote(noteDataSource)
        getAllUserNotes(noteDataSource)
        updateNote(noteDataSource)
        deleteNote(noteDataSource)
    }
}
