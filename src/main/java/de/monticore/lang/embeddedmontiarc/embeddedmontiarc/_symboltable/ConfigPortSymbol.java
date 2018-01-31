package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.se_rwth.commons.logging.Log;

public class ConfigPortSymbol extends PortSymbol{
    protected ConfigPortSymbol(String name) {
        super(name);
        setDirection(PortSymbol.INCOMING);
    }

    @Override
    protected boolean isConfig(){
        return true;
    }

    @Override
    public void setDirection(boolean isIncoming) {
        if(!isIncoming)
            Log.error("ConfigPorts can only be incoming!");

        super.setDirection(INCOMING);
    }
}
