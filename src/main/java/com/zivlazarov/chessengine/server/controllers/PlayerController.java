package com.zivlazarov.chessengine.server.controllers;

import com.zivlazarov.chessengine.client.model.player.Player;
import com.zivlazarov.chessengine.server.model.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    PlayerRepository playerRepository;

    @GetMapping
    public Iterable<Player> findAll() {
        return playerRepository.findAll();
    }

    @GetMapping("/api/{id}")
    public Player findPlayerByID(@PathVariable int id) {
        return playerRepository.findById(id)
                .orElseThrow();
    }
}
