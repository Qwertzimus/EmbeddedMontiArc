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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponent;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.types2._ast.ASTSimpleReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolutionDeclaration;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberTypeArgument;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import org.jscience.mathematics.number.Rational;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class InstanceInformation {
    protected String compName;
    protected ASTSubComponent astSubComponent;

    public InstanceInformation() {

    }

    public InstanceInformation(String compName, ASTSubComponent astSubComponent) {
        this.compName = compName;
        this.astSubComponent = astSubComponent;
    }


    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public ASTSubComponent getASTSubComponent() {
        return astSubComponent;
    }

    public void setASTSubComponent(ASTSubComponent astSubComponent) {
        this.astSubComponent = astSubComponent;
    }

    public int getInstanceNumberForArgumentIndex(int index) {
        return getInstanceNumberFromASTSubComponent(astSubComponent, index);
    }

    public int getInstanceNumberForPortName(String portName) {
        Symbol symbol = getASTSubComponent().getSymbol().get();
        ComponentInstanceSymbol componentInstanceSymbol = (ComponentInstanceSymbol) symbol;
        Log.debug(componentInstanceSymbol.getComponentType().toString(), "ComponentInstanceSymbol");
        Log.debug(portName, "PortName");
        PortArraySymbol namedArray = componentInstanceSymbol.getComponentType().getPortArray(portName);
        if (namedArray != null && namedArray.getNameSizeDependsOn().isPresent())
            Log.debug(namedArray.getNameSizeDependsOn().get(), "PortArray Depends On:");

        int counter = 0;
        for (ResolutionDeclarationSymbol resolutionDeclarationSymbol : componentInstanceSymbol.getComponentType().getResolutionDeclarationSymbols()) {
            if (componentInstanceSymbol.getComponentType().isPortDependentOnResolutionDeclarationSymbol(portName, resolutionDeclarationSymbol.getNameToResolve())) {
                Log.debug("Name: " + portName + " nameToResolve: " + resolutionDeclarationSymbol.getNameToResolve(), "Porty Depends On:");
                return getInstanceNumberFromASTSubComponent(getASTSubComponent(), counter);
            }
            ++counter;
        }


        return -1;
    }


    public List<Integer> getInstanceNumberForArguments() {
        List<Integer> intList = new ArrayList<>();

        int curIndex = 0;
        int curResult = 0;
        while (true) {
            curResult = getInstanceNumberFromASTSubComponent(getASTSubComponent(), curIndex);
            if (curResult != -1) {
                intList.add(curResult);
            } else {
                break;
            }
        }

        return intList;
    }

    public static int getInstanceNumberFromASTSubComponent(ASTSubComponent subComponent, int index) {
        if (subComponent.getType() instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) subComponent.getType();
            return handleSimpleReferenceType(simpleReferenceType, index);
        }
        return -1;
    }

    private static int handleSimpleReferenceType(ASTSimpleReferenceType simpleReferenceType, int index) {
        if (simpleReferenceType.getTypeArguments().isPresent()) {
            int counter = 0;
            for (ASTTypeArgument astTypeArgument : simpleReferenceType.getTypeArguments().get().getTypeArguments()) {
                int result = handleSimpleReferenceType(astTypeArgument, index, counter);
                if (result != -1)
                    return result;
            }
        }
        return -1;
    }

    public static int handleSimpleReferenceType(ASTTypeArgument astTypeArgument, int index, int counter) {
        int result = -1;
        if (astTypeArgument instanceof ASTUnitNumberTypeArgument) {
            if (((ASTUnitNumberTypeArgument) astTypeArgument).getUnitNumber().getNumber().isPresent()) {
                if (counter == index)
                    result = ((ASTUnitNumberTypeArgument) astTypeArgument).getUnitNumber().getNumber().get().intValue();
                ++counter;
            }

        } else if (astTypeArgument instanceof ASTUnitNumberResolution) {
            if (((ASTUnitNumberResolution) astTypeArgument).getUnitNumber().isPresent()) {
                if (counter == index)
                    result = ((ASTUnitNumberResolution) astTypeArgument).getNumber().get().intValue();
                ++counter;
            }
        }
        return result;
    }

    public static String getInstanceNameFromASTSubComponent(ASTSubComponent subComponent, int index) {
        if (subComponent.getType() instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) subComponent.getType();
            if (simpleReferenceType.getTypeArguments().isPresent()) {
                int counter = 0;
                for (ASTTypeArgument astTypeArgument : simpleReferenceType.getTypeArguments().get().getTypeArguments()) {
                    if (astTypeArgument instanceof ASTUnitNumberResolution) {
                        if (((ASTUnitNumberResolution) astTypeArgument).getName().isPresent()) {
                            if (counter == index)
                                return ((ASTUnitNumberResolution) astTypeArgument).getName().get();
                            ++counter;
                        }

                    }
                }
            }

        }
        return null;
    }

    public static void setInstanceNumberInASTSubComponent(ASTSubComponent subComponent, String nameToSet, int numberToSet) {
        if (subComponent.getType() instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) subComponent.getType();
            if (simpleReferenceType.getTypeArguments().isPresent()) {
                int counter = 0;
                for (ASTTypeArgument astTypeArgument : simpleReferenceType.getTypeArguments().get().getTypeArguments()) {
                    if (astTypeArgument instanceof ASTUnitNumberResolution) {
                        if ((((ASTUnitNumberResolution) astTypeArgument).getName().isPresent())) {
                            String name = ((ASTUnitNumberResolution) astTypeArgument).getName().get();
                            if (name.equals(nameToSet))
                                ((ASTUnitNumberResolution) astTypeArgument).setNumber(Rational.valueOf("" + numberToSet));
                            ++counter;
                        }

                    }
                }
            }

        }
    }
}