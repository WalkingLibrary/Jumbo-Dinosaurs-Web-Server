package com.jumbodinosaurs.webserver.commands.email;

import com.jumbodinosaurs.devlib.commands.Command;

public abstract class EmailCommand extends Command
{
    @Override
    public String getCategory()
    {
        return "Email Commands";
    }
}
