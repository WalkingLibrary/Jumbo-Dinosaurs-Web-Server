package com.jumbodinosaurs.webserver.commands.domain;

import com.jumbodinosaurs.devlib.commands.Command;

public abstract class DomainCommand extends Command
{
    @Override
    public String getCategory()
    {
        return "Domain Commands";
    }
}
