package com.myprojects.data.note

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Note(
    val ownerId: String,
    val title: String,
    val text: String,
    val modifiedDateTime: String,
    @BsonId val id: ObjectId = ObjectId()
)
