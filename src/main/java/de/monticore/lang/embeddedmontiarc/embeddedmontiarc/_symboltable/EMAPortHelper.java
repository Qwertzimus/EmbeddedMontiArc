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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTConnector;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTPort;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponent;
import de.monticore.lang.monticar.common2._ast.ASTArrayAccess;
import de.monticore.lang.monticar.common2._ast.ASTQualifiedNameWithArray;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.monticore.lang.monticar.ts.references.MCTypeReference;
import de.monticore.lang.monticar.types2._ast.ASTSimpleReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberTypeArgument;
import de.monticore.lang.numberunit._ast.ASTUnitNumber;
import de.monticore.symboltable.MutableScope;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sascha Schneiders
 */
public class EMAPortHelper {
    /**
     * creates the PortSymbols that belong to a PortArraySymbol
     */
    public static void portCreationIntLiteralPresent(ASTPort node, PortArraySymbol pas, String name,
                                                     MCTypeReference<? extends MCTypeSymbol> typeRef,
                                                     EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        // int num = node.getIntLiteral().get().getValue();
        Log.debug(node.toString(), "ASTPort");
        int num = 0;
        if (node.getUnitNumberResolution().isPresent()
                && node.getUnitNumberResolution().get().getUnitNumber().isPresent()) {
            num = node.getUnitNumberResolution().get().getNumber().get().intValue();
        } else {
            Log.debug("No UnitNumberResolution/UnitNumber present!", "ASTPort");
        }
        pas.setDimension(num);
        for (int i = 1; i <= num; ++i) {
            String nameWithArray = name + "[" + Integer.toString(i) + "]";
            PortSymbol sym = new PortSymbol(nameWithArray);
            sym.setNameDependsOn(pas.getNameDependsOn());
            Log.debug(nameWithArray, "nameWithArray");

            sym.setTypeReference(typeRef);
            sym.setDirection(node.isIncoming());

            symbolTableCreator.addToScopeAndLinkWithNode(sym, node);
        }
    }

    public static void portCreation(ASTPort node, PortArraySymbol pas, String name,
                                    MCTypeReference<? extends MCTypeSymbol> typeRef,
                                    EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        if (node.getUnitNumberResolution().isPresent()) {
            portCreationIntLiteralPresent(node, pas, name, typeRef, symbolTableCreator);
        } else {
            // create PortSymbol with same content as PortArraySymbol
            createPort(node, name, node.isIncoming(), typeRef, pas, symbolTableCreator);
        }
    }

    public static void createPort(String name, boolean isIncoming,
                                  MCTypeReference<? extends MCTypeSymbol> typeRef,
                                  EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        PortSymbol ps = new PortSymbol(name);

        ps.setTypeReference(typeRef);
        ps.setDirection(isIncoming);

        symbolTableCreator.addToScope(ps);
    }

    public static void createPort(ASTPort node, String name, boolean isIncoming,
                                  MCTypeReference<? extends MCTypeSymbol> typeRef, PortArraySymbol pas,
                                  EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        PortSymbol ps = new PortSymbol(name);
        ps.setNameDependsOn(pas.getNameDependsOn());
        ps.setTypeReference(typeRef);
        ps.setDirection(isIncoming);

        symbolTableCreator.addToScopeAndLinkWithNode(ps, node);
    }

    public static String doPortResolution(ASTPort node, EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        String name = null;
        if (node.getUnitNumberResolution().isPresent()) {
            ASTUnitNumberResolution unitNumberResolution = node.getUnitNumberResolution().get();
            name = unitNumberResolution
                    .doResolution(symbolTableCreator.componentStack.
                            peek().getResolutionDeclarationSymbols());

        }
        return name;
    }


    public static List<String> getPortName(ASTQualifiedNameWithArray portName,
                                           EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        List<String> names = new ArrayList<String>();

        List<String> compNameParts = getComponentNameParts(portName, symbolTableCreator);

        List<String> portNameParts;
        portNameParts = getPortNameParts(portName, symbolTableCreator);

        Log.debug("portName: " + portName + " " + compNameParts.size(), "CompNameParts");
        Log.debug("" + portNameParts.size(), "PortNameParts");
        for (String compNamePart : compNameParts) {
            for (String portNamePart : portNameParts) {
                String curName = compNamePart + portNamePart;

                names.add(curName);
            }
        }

        return names;
    }

    public static List<String> getComponentNameParts(ASTQualifiedNameWithArray portName,
                                                     EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        List<String> names = new ArrayList<String>();
        String name = "";
        if (portName.getCompName().isPresent()) {
            name += portName.getCompName().get();
            if (portName.getCompArray().isPresent()) {
                if (portName.getCompArray().get().getIntLiteral().isPresent()) {
                    name += "[" + portName.getCompArray().get().getIntLiteral().get().getNumber().toString()
                            + "]";
                    name += ".";
                    names.add(name);
                } else if (portName.getCompArray().get().getLowerbound().isPresent()) {
                    names = getmnCompNameParts(name, portName);
                } else {
                    int size = countComponentArrayInstances(name, symbolTableCreator);
                    for (int i = 1; i <= size; ++i) {
                        String instanceName = name;
                        instanceName += "[" + i + "].";
                        names.add(instanceName);
                    }
                }
            } else {
                names.add(portName.getCompName().get() + ".");
            }
        } else {
            names.add("");
        }
        return names;
    }

    public static List<String> getmnCompNameParts(String name, ASTQualifiedNameWithArray portName) {
        List<String> names = new ArrayList<String>();
        int lower = portName.getCompArray().get().getLowerbound().get().getNumber().get().intValue();
        int upper = portName.getCompArray().get().getUpperbound().get().getNumber().get().intValue();
        for (int i = lower; i <= upper; ++i) {
            String instanceName = name;
            instanceName += "[" + i + "].";
            names.add(instanceName);
        }
        return names;
    }


    public static List<String> getPortNameParts(ASTQualifiedNameWithArray portName,
                                                EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        List<String> names = new ArrayList<String>();
        String name = "";
        // ignore for now
        if (portName.getCompName().isPresent())
            name += portName.getCompName().get() + ".";
        name = portName.getPortName();
        if (portName.getPortArray().isPresent()) {
            if (portName.getPortArray().get().getIntLiteral().isPresent()) {
                name += "["
                        + portName.getPortArray().get().getIntLiteral().get().getNumber().get().intValue()
                        + "]";
                names.add(name);
            } else if (portName.getPortArray().get().getLowerbound().isPresent()) {
                names = getmnPortNameParts(name, portName);
            } else {
                Log.debug(portName.toString(), "PortName:");
                int size = countPortArrayInstances(name, portName.getCompName().orElse(null),
                        portName.getCompArray().orElse(null), symbolTableCreator);

                Log.debug("Size" + size, "PortNameParts");
                for (int i = 1; i <= size; ++i) {
                    String instanceName = name;

                    instanceName += "[" + i + "]";

                    names.add(instanceName);
                }
            }
        } else {
            Log.debug("No PortArrayName was specified", "PortArray");
            names.add(portName.getPortName());
        }
        return names;
    }

    public static List<String> getmnPortNameParts(String name, ASTQualifiedNameWithArray portName) {
        List<String> names = new ArrayList<String>();
        int lower = portName.getPortArray().get().getLowerbound().get().getNumber().get().intValue();
        int upper = portName.getPortArray().get().getUpperbound().get().getNumber().get().intValue();
        for (int i = lower; i <= upper; ++i) {
            String instanceName = name;
            instanceName += "[" + i + "]";
            names.add(instanceName);
            Log.debug("Name:", "Added MNPortName");
        }
        return names;
    }

    public static String getNameArrayPart(ASTArrayAccess arrayPart) {
        String result = "";
        if (arrayPart.getIntLiteral().isPresent())
            result += "[" + arrayPart.getIntLiteral().get().getNumber().get().intValue() + "]";
        // Not handled here change handling this case after refactoring
        /* else if (arrayPart.getLowerbound().isPresent() && arrayPart.getUpperbound().isPresent()) {
         * } */
        return result;
    }

    public static int countPortArrayInstances(String portName, String compName, ASTArrayAccess arrayPart, EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        MutableScope curScope = symbolTableCreator.currentScope().get();

        boolean present = true;
        int counter = 0;
        if (arrayPart != null) {
            compName += getNameArrayPart(arrayPart);
        }
        while (present) {
            Log.debug(compName, "ComponentName:");
            present = curScope.resolve(portName + "[" + (counter + 1) + "]", PortSymbol.KIND).isPresent();
            if (present)
                ++counter;
            else {
                Log.debug(curScope.toString(), "CurScope:");
                Log.debug("Could not resolve " + portName + "[" + (counter + 1) + "]",
                        "countPortArrayInstances");
            }
        }
        if (counter == 0) {
            // TODO
            present = true;
            Log.debug("compInstanceName: " + compName, "Resolving");
            Log.debug(compName, "ComponentName");
            if (compName != null) {
                ComponentInstanceSymbol symbol;
                symbol = curScope.<ComponentInstanceSymbol>resolve(compName, ComponentInstanceSymbol.KIND)
                        .get();
                for (PortSymbol portSymbol : symbol.getComponentType().getAllPorts()) {

                    Log.debug(portSymbol.toString(), "PortInfo");
                    if (portSymbol.getNameWithoutArrayBracketPart().startsWith(portName)) {
                        ++counter;
                    }
                }
            }
        }

        return counter;
    }

    public static int countComponentArrayInstances(String componentName, EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        MutableScope curScope = symbolTableCreator.currentScope().get();
        boolean present = true;
        int counter = 0;
        Log.debug("" + componentName, "RESOLVING");
        while (present) {
            present = curScope
                    .resolve(componentName + "[" + (counter + 1) + "]", ComponentInstanceSymbol.KIND)
                    .isPresent();
            if (present)
                ++counter;
        }
        return counter;
    }

    public static void nonConstantPortSetup(List<String> sourceNames,
                                            de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.
                                                    ASTConnector node, EmbeddedMontiArcSymbolTableCreator
                                                    symbolTableCreator) {

        Log.info("" + sourceNames.size(), "SourcePorts");
        int counter = 0, targetnum = 0;
        for (ASTQualifiedNameWithArray target : node.getTargets().getQualifiedNameWithArrays()) {
            counter = 0;
            targetnum = 0;
            for (String sourceName : sourceNames) {
                List<String> targetNames = getPortName(target, symbolTableCreator);
                targetnum = targetNames.size();
                if (counter < targetnum) {
                    String targetName = targetNames.get(counter);
                    Log.info("" + targetName, "target");
                    Log.info("" + sourceName, "source");

                    ConnectorSymbol sym = new ConnectorSymbol(targetName);
                    sym.setSource(sourceName);
                    sym.setTarget(targetName);
                    Log.info(sym.getTarget(), "TARGETNAME SET TO");

                    symbolTableCreator.addToScopeAndLinkWithNode(sym, node);
                    ++counter;
                }
            }
            // TODO enable checking again if it is fixed
            /* if(counter!=targetnum) { Log.error("source port number "+ counter +" and target port num"+
             * targetnum+" don't match"); } */
        }
    }

    public static void constantPortSetup(
            de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTConnector node,
            EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        int counter = 0, targetnum = 0;
        ConstantPortSymbol constantPortSymbol = ConstantPortSymbol.createConstantPortSymbol(node,
                symbolTableCreator);
        symbolTableCreator.addToScope(constantPortSymbol);
        for (ASTQualifiedNameWithArray target : node.getTargets().getQualifiedNameWithArrays()) {
            counter = 0;
            targetnum = 0;
            List<String> targetNames = getPortName(target, symbolTableCreator);
            targetnum = targetNames.size();
            String targetName = targetNames.get(counter);
            Log.debug("" + targetName, "target");

            ConnectorSymbol sym = new ConnectorSymbol(targetName);
            sym.setConstantPortSymbol(constantPortSymbol);
            sym.setSource(constantPortSymbol.getName());
            sym.setTarget(targetName);
            Log.debug(sym.getTarget(), "TARGETNAME SET TO");

            symbolTableCreator.addToScopeAndLinkWithNode(sym, node);
            ++counter;

        }
    }

    public static void doConnectorResolution(ASTConnector node, EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        if (node.getUnitNumberResolution().isPresent()) {
            ASTUnitNumberResolution unitNumberResolution = node.getUnitNumberResolution().get();
            ASTUnitNumber toSet = null;
            if (unitNumberResolution.getUnitNumber().isPresent()) {
                toSet = unitNumberResolution.getUnitNumber().get();
            } else if (unitNumberResolution.getName().isPresent()) {

                ResolutionDeclarationSymbol resDeclSym = symbolTableCreator.componentStack.peek()
                        .getResolutionDeclarationSymbol(unitNumberResolution.getName().get()).get();
                toSet = ((ASTUnitNumberResolution) resDeclSym.getASTResolution()).getUnitNumber().get();
            }

            unitNumberResolution.setUnit(toSet.getUnit().get());
            unitNumberResolution.setNumber(toSet.getNumber().get());
        }

    }


    public static int handleSizeResolution(ASTSubComponent node, int index) {
        int counter = 0;
        if (node.getType() instanceof ASTSimpleReferenceType) {
            if (((ASTSimpleReferenceType) node.getType()).getTypeArguments().isPresent()) {
                for (ASTTypeArgument typeArgument : ((ASTSimpleReferenceType) node.getType())
                        .getTypeArguments().get().getTypeArguments()) {
                    if (typeArgument instanceof ASTUnitNumberTypeArgument) {
                        Log.debug("" + ((ASTUnitNumberTypeArgument) typeArgument).getUnitNumber().getNumber()
                                .get().intValue(), "New Resolution Value:");
                        if (counter == index)
                            return ((ASTUnitNumberTypeArgument) typeArgument).getUnitNumber().getNumber().get()
                                    .intValue();
                        ++counter;
                    }
                }
            }
        }

        return -1;
    }
}
