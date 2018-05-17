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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.*;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.TypesHelper;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.TypesPrinter;
import de.monticore.lang.embeddedmontiarc.helper.ArcTypePrinter;
import de.monticore.lang.monticar.ValueSymbol;
import de.monticore.lang.monticar.common2._ast.ASTArrayAccess;
import de.monticore.lang.monticar.common2._ast.ASTParameter;
import de.monticore.lang.monticar.common2._ast.ASTQualifiedNameWithArray;
import de.monticore.lang.monticar.ranges._ast.ASTRange;
import de.monticore.lang.monticar.ranges._ast.ASTRanges;
import de.monticore.lang.monticar.resolution._ast.ASTResolutionDeclaration;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbolReference;
import de.monticore.lang.monticar.si._symboltable.SIUnitRangesSymbolReference;
import de.monticore.lang.monticar.ts.MCFieldSymbol;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.monticore.lang.monticar.ts.references.CommonMCTypeReference;
import de.monticore.lang.monticar.ts.references.MCASTTypeSymbolReference;
import de.monticore.lang.monticar.ts.references.MCTypeReference;
import de.monticore.lang.monticar.ts.references.MontiCarTypeSymbolReference;
import de.monticore.lang.monticar.types2._ast.*;
import de.monticore.lang.numberunit._ast.ASTUnitNumber;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.TypeReference;
import de.se_rwth.commons.logging.Log;
import org.jscience.mathematics.number.Rational;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Sascha Schneiders
 */
public class EmbeddedMontiArcSymbolTableHelper {


    public static void doSubComponentInstanceResolution(ASTSubComponentInstance node,
                                                        ComponentSymbolReference componentSymbolReference,
                                                        EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {

        if (node.getUnitNumberResolution().isPresent()) {
            ASTUnitNumberResolution unitNumberResolution = node.getUnitNumberResolution().get();
            ASTUnitNumber toSet = null;
            if (unitNumberResolution.getUnitNumber().isPresent()) {
                toSet = unitNumberResolution.getUnitNumber().get();

            } else if (unitNumberResolution.getName().isPresent()) {

                ResolutionDeclarationSymbol resDeclSym = symbolTableCreator.
                        componentStack.peek()
                        .getResolutionDeclarationSymbol(unitNumberResolution.getName().get()).get();
                Log.debug(resDeclSym.getASTResolution().toString(), "Found ResolutionDeclarationSymbol:");
                toSet = ((ASTUnitNumberResolution) resDeclSym.getASTResolution()).getUnitNumber().get();

                Log.debug("" + toSet.getNumber().get().intValue(), "ToSet Number:");
            }
            node.getUnitNumberResolution().get().setUnit(toSet.getUnit().get());
            node.getUnitNumberResolution().get().setNumber(toSet.getNumber().get());

            Log.debug("" + node.getUnitNumberResolution().get().getNumber().get().intValue(),
                    "SubComponentResolution Number:");
        }
    }

    public static void setActualResolutionDeclaration(ASTSubComponent node,
                                                      ComponentSymbolReference componentSymbolReference) {
        int index = 0;
        int size = EMAPortHelper.handleSizeResolution(node, index);
        if (size > 0 && componentSymbolReference.getResolutionDeclarationSymbols().size() > 0) {
            if (componentSymbolReference.getResolutionDeclarationSymbols().get(index)
                    .getASTResolution() instanceof ASTUnitNumberResolution) {
                Log.debug(size + "", "Set new Resolution");
                ((ASTUnitNumberResolution) componentSymbolReference.getResolutionDeclarationSymbols()
                        .get(index).getASTResolution()).setNumber(Rational.valueOf("" + size));
            }
        } else {
            for (int i = 0; i < componentSymbolReference.getResolutionDeclarationSymbols().size(); ++i) {
                Rational numberToSetTo = ((ASTUnitNumberResolution) componentSymbolReference
                        .getReferencedSymbol().getResolutionDeclarationSymbols().get(i).getASTResolution())
                        .getNumber().get();
                ((ASTUnitNumberResolution) componentSymbolReference.getResolutionDeclarationSymbols().get(i)
                        .getASTResolution()).setNumber(numberToSetTo);
            }
        }
    }

    /**
     * Creates the instance and adds it to the symTab.
     */

    public static void createInstance(String name, ASTSubComponent node,
                                      ComponentSymbolReference componentTypeReference,
                                      List<ValueSymbol<TypeReference<TypeSymbol>>> configArguments,
                                      EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        ComponentInstanceSymbol instance = new ComponentInstanceSymbol(name,
                componentTypeReference);
        for (ValueSymbol<TypeReference<TypeSymbol>> valueSymbol : configArguments)
            configArguments.forEach(v -> instance.addConfigArgument(v));
        // create a subscope for the instance
        symbolTableCreator.addToScopeAndLinkWithNode(instance, node);
        Log.debug(symbolTableCreator.currentScope().get().toString(),
                "SubComponentInstance Scope");
        // remove the created instance's scope
        symbolTableCreator.removeCurrentScope();
        InstanceInformation instanceInformation = new InstanceInformation();
        instanceInformation.setCompName(name);
        instanceInformation.setASTSubComponent(node);
        String reslString = "";
        for (ResolutionDeclarationSymbol resolutionDeclarationSymbol : componentTypeReference
                .getResolutionDeclarationSymbols()) {
            reslString += "Name:" + resolutionDeclarationSymbol.getNameToResolve() + "value: "
                    + ((ASTUnitNumberResolution) resolutionDeclarationSymbol.getASTResolution()).getNumber()
                    .get().intValue();
        }
        Log.info(reslString, "CompInst");
        InstancingRegister.addInstanceInformation(instanceInformation);
        Log.debug(name, "created SubComponentInstance:");
    }


    public static void handleResolutionDeclaration(ComponentSymbol typeSymbol,
                                                   ASTTypeParameters astTypeParameters, Scope currentScope,
                                                   ASTComponent node,
                                                   EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        for (ASTTypeVariableDeclaration astTypeParameter : astTypeParameters
                .getTypeVariableDeclarations()) {
            if (astTypeParameter.resolutionDeclarationIsPresent() && astTypeParameter
                    .getResolutionDeclaration().get() instanceof ASTTypeNameResolutionDeclaration) {
                Log.debug(astTypeParameter.toString(), "Resolution Declaration:");
                ASTResolutionDeclaration astResDecl = astTypeParameter.getResolutionDeclaration().get();

                ResolutionDeclarationSymbolReference resDeclSymRef;
                resDeclSymRef = ResolutionDeclarationSymbolReference.constructResolutionDeclSymbolRef(
                        ((ASTTypeNameResolutionDeclaration) astResDecl).getName(),
                        ((ASTTypeNameResolutionDeclaration) astResDecl).getResolution());

                Log.debug(resDeclSymRef.getNameToResolve(),
                        "Added ResolutionDeclarationSymbol with name: ");
                typeSymbol.addResolutionDeclarationSymbol(resDeclSymRef);
                // TODO Resolution maybe link with node
                symbolTableCreator.addToScopeAndLinkWithNode(resDeclSymRef,
                        astTypeParameter);
            }
        }
    }


    public static void setParametersOfComponent(final ComponentSymbol componentSymbol, ASTComponent cmp
            , EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        Log.debug(componentSymbol.toString(), "ComponentPreParam");
        for (ASTParameter astParameter : cmp.getParameters()) {
            final String paramName = astParameter.getName();
            Log.debug(astParameter.toString(), "ASTParam");
            int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astParameter.getType());

            // TODO enable if needed and remove line below
            MCTypeReference<? extends MCTypeSymbol> paramTypeSymbol = new MontiCarTypeSymbolReference(
                    TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astParameter
                            .getType()),
                    symbolTableCreator.currentScope().get(), dimension);

            EMATypeHelper.addTypeArgumentsToTypeSymbol(paramTypeSymbol, astParameter.getType(), symbolTableCreator);

            final MCFieldSymbol parameterSymbol = symbolTableCreator.
                    jSymbolFactory.createFormalParameterSymbol(paramName,
                    (MontiCarTypeSymbolReference) paramTypeSymbol);
            componentSymbol.addConfigParameter(parameterSymbol);
            componentSymbol.addParameter(astParameter);
        }
        Log.debug(componentSymbol.toString(), "ComponentPostParam");
    }

    public static boolean needsInstanceCreation(ASTComponent node, ComponentSymbol symbol,
                                                EmbeddedMontiArcSymbolTableCreator symbolTableCreator) {
        boolean instanceNameGiven = false;// node.getInstanceName().isPresent();
        boolean autoCreationPossible = symbol.getFormalTypeParameters().size() == 0;

        return symbolTableCreator.autoInstantiate && (instanceNameGiven ||
                autoCreationPossible);
    }
}
