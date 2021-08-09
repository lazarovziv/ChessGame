package com.zivlazarov.chessengine.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import kotlin.jvm.JvmStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.runApplication

// creating all of server's files in Kotlin

@SpringBootApplication
@EntityScan(
    "com.zivlazarov.chessengine.client.model.player",
    "com.zivlazarov.chessengine.client.model.pieces",
    "com.zivlazarov.chessengine.client.model.board"
)
open class ChessGameServerApplication

fun main(args: Array<String>) {
    runApplication<ChessGameServerApplication>(*args)
}