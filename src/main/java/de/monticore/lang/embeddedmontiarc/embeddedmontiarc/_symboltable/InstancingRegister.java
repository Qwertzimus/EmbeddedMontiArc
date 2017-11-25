/**
 *
 *  ******************************************************************************
 *  MontiCAR Modeling Family, www.se-rwth.de
 *  Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 *  All rights reserved.
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3.0 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.se_rwth.commons.logging.Log;

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
        Log.info(i.toString(), "Added InstanceInformation");
    }

    public static Optional<InstanceInformation> getInstanceInformation(String name) {
        for (InstanceInformation i : instanceInformation) {
            if (i.getCompName().equals(name))
                return Optional.of(i);
        }
        return Optional.empty();
    }

    public static void reset() {
        instanceInformation.clear();
    }
}
