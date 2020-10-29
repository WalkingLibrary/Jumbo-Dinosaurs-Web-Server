package com.jumbodinosaurs.webserver.commands.database;

import com.jumbodinosaurs.devlib.commands.Command;

public abstract class DataBaseCommand extends Command
{
    @Override
    public String getCategory()
    {
        return "Data Base Commands";
    }
}
