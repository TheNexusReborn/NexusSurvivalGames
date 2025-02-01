package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;

public class GameCmdAction extends GameAction {
    public GameCmdAction(String sender, String command, String... cmdArgs) {
        super(System.currentTimeMillis(), "admincommand");
        addValueData("sender", sender).addValueData("command", command);
        if (cmdArgs != null && cmdArgs.length > 0) {
            StringBuilder argBuilder = new StringBuilder();
            for (String cmdArg : cmdArgs) {
                argBuilder.append(cmdArg).append(",");
            }
            
            if (!argBuilder.isEmpty()) {
                argBuilder.deleteCharAt(argBuilder.length() - 1);
            }
            
            addValueData("args", argBuilder.toString());
        }
    }
}
