package com.myprojects

import com.myprojects.data.note.MongoNoteDataSource
import com.myprojects.data.user.MongoUserDataSource
import com.myprojects.plugins.configureMonitoring
import com.myprojects.plugins.configureRouting
import com.myprojects.plugins.configureSecurity
import com.myprojects.plugins.configureSerialization
import com.myprojects.security.hashing.SHA256HashingService
import com.myprojects.security.token.JwtTokenService
import com.myprojects.security.token.TokenConfig
import io.ktor.server.application.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val mongoPassword = System.getenv("MONGO_PASSWORD")
    val databaseName = "ktor-notes"

    val database = KMongo
        .createClient(
            connectionString = "mongodb+srv://kot25573:$mongoPassword@cluster0.aiiebr4.mongodb.net/$databaseName?retryWrites=true&w=majority"
        )
        .coroutine
        .getDatabase(databaseName)

    val userDataSource = MongoUserDataSource(database)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    val noteDataSource = MongoNoteDataSource(database)

    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig, noteDataSource)
}
