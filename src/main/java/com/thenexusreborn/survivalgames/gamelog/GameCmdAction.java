package com.thenexusreborn.survivalgames.gamelog;

import com.thenexusreborn.api.gamearchive.GameAction;

public class GameCmdAction extends GameAction {
    public GameCmdAction(String sender, String command, String... cmdArgs) {
        super(System.currentTimeMillis(), "admincommand");
        addValueData("sender", sender).addValueData("command", command);
        if (cmdArgs != null) {
            StringBuilder argBuilder = new StringBuilder();
            for (String cmdArg : cmdArgs) {
                argBuilder.append(cmdArg).append(",");
            }
            
            argBuilder.deleteCharAt(argBuilder.length() - 1);
            
            addValueData("args", argBuilder.toString());
        }
    }
}
