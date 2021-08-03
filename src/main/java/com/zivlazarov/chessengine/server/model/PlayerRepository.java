package com.zivlazarov.chessengine.server.model;

import com.zivlazarov.chessengine.client.model.player.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

}
