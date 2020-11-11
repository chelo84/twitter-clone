package com.example.oauthdemo.model.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Community {
    @Id
    var uid: String? = null

}