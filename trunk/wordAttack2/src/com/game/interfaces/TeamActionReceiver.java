package com.game.interfaces;

import com.game.models.Player;
import com.game.models.Team;

public interface TeamActionReceiver {
	void onTeamActionReceived(Team team, Integer action, Player player);
}
