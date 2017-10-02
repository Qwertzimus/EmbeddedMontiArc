package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Sascha Schneiders
 */
public class InstancingRegister {
    public static List<InstanceInformation> instanceInformation = new ArrayList();

    public static void addInstanceInformation(InstanceInformation i) {
        instanceInformation.add(i);
    }

    public static Optional<InstanceInformation> getInstanceInformation(String name) {
        for (InstanceInformation i : instanceInformation) {
            if (i.getCompName().equals(name))
                return Optional.of(i);
        }
        return Optional.empty();
    }
}
