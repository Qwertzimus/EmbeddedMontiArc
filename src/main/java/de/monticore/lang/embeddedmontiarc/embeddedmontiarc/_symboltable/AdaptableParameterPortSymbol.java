package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.se_rwth.commons.logging.Log;

public class AdaptableParameterPortSymbol extends PortSymbol{
    protected AdaptableParameterPortSymbol(String name) {
        super(name);
        setDirection(PortSymbol.INCOMING);
    }

    @Override
    protected boolean isAdaptableParameter(){
        return true;
    }

    @Override
    public void setDirection(boolean isIncoming) {
        if(!isIncoming)
            Log.error("AdaptableParameterPorts can only be incoming!");

        super.setDirection(INCOMING);
    }
}
