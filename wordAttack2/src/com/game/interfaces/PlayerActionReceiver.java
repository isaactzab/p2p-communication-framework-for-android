package com.game.interfaces;

import com.game.models.Player;

public interface PlayerActionReceiver {
	void onPlayerAction(Player player, String action);
}
