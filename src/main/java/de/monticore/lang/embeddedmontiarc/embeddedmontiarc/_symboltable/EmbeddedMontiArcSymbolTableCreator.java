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

import static de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EmbeddedMontiArcExpandedComponentInstanceSymbolCreator.getGlobalScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import de.monticore.lang.monticar.ts.references.MCASTTypeSymbolReference;
import org.jscience.mathematics.number.Rational;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTConnector;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTEMACompilationUnit;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTMontiArcAutoConnect;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTMontiArcAutoInstantiate;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTPort;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponentInstance;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.TypesHelper;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.TypesPrinter;
import de.monticore.lang.embeddedmontiarc.helper.ArcTypePrinter;
import de.monticore.lang.embeddedmontiarc.trafos.AutoConnection;
import de.monticore.lang.monticar.ValueSymbol;
import de.monticore.lang.monticar.common2._ast.ASTArrayAccess;
import de.monticore.lang.monticar.common2._ast.ASTParameter;
import de.monticore.lang.monticar.common2._ast.ASTQualifiedNameWithArray;
import de.monticore.lang.monticar.mcexpressions._ast.ASTExpression;
import de.monticore.lang.monticar.ranges._ast.ASTRange;
import de.monticore.lang.monticar.ranges._ast.ASTRanges;
import de.monticore.lang.monticar.ranges._ast.ASTUnitNumberExpression;
import de.monticore.lang.monticar.resolution._ast.ASTResolutionDeclaration;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbolReference;
import de.monticore.lang.monticar.si._symboltable.SIUnitRangesSymbolReference;
import de.monticore.lang.monticar.ts.MCFieldSymbol;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.monticore.lang.monticar.ts.MontiCarSymbolFactory;
import de.monticore.lang.monticar.ts.references.CommonMCTypeReference;
import de.monticore.lang.monticar.ts.references.MCTypeReference;
import de.monticore.lang.monticar.ts.references.MontiCarTypeSymbolReference;
import de.monticore.lang.monticar.types2._ast.ASTComplexArrayType;
import de.monticore.lang.monticar.types2._ast.ASTComplexReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTImportStatement;
import de.monticore.lang.monticar.types2._ast.ASTReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTSimpleReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTType;
import de.monticore.lang.monticar.types2._ast.ASTTypeNameResolutionDeclaration;
import de.monticore.lang.monticar.types2._ast.ASTTypeParameters;
import de.monticore.lang.monticar.types2._ast.ASTTypeVariableDeclaration;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberTypeArgument;
import de.monticore.lang.monticar.types2._ast.ASTWildcardType;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.TypeReference;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import de.monticore.lang.numberunit._ast.ASTUnitNumber;

//import de.monticore.common.common._ast.ASTStereoValue;
//import de.monticore.types.TypesHelper;
//import de.monticore.types.types._ast.*;

/**
 * Visitor that creats the symboltable of an EmbeddedMontiArc AST.
 *
 * @author Sascha Schneiders, Sining Wang, Yu Qiao
 */
public class EmbeddedMontiArcSymbolTableCreator extends EmbeddedMontiArcSymbolTableCreatorTOP {

    private String compilationUnitPackage = "";

    private EmbeddedMontiArcExpandedComponentInstanceSymbolCreator instanceSymbolCreator = new EmbeddedMontiArcExpandedComponentInstanceSymbolCreator();

    // extra stack of components that is used to determine which components are inner components.
    private Stack<ComponentSymbol> componentStack = new Stack<>();

    private List<ImportStatement> currentImports = new ArrayList<>();

    private AutoConnection autoConnectionTrafo = new AutoConnection();

    private MontiCarSymbolFactory jSymbolFactory = new MontiCarSymbolFactory();

    protected boolean aboartVisitComponent = false;

    protected boolean autoInstantiate = false;

    public EmbeddedMontiArcSymbolTableCreator(
            final ResolvingConfiguration resolverConfig,
            final MutableScope enclosingScope) {
        super(resolverConfig, enclosingScope);
    }

    public EmbeddedMontiArcSymbolTableCreator(
            final ResolvingConfiguration resolvingConfig,
            final Deque<MutableScope> scopeStack) {
        super(resolvingConfig, scopeStack);
    }

    @Override
    public void visit(ASTEMACompilationUnit compilationUnit) {
        Log.debug("Building Symboltable for Component: " + compilationUnit.getComponent().getName(),
                EmbeddedMontiArcSymbolTableCreator.class.getSimpleName());
        compilationUnitPackage = Names.getQualifiedName(compilationUnit.getPackage());

        // imports
        List<ImportStatement> imports = new ArrayList<>();
        for (ASTImportStatement astImportStatement : compilationUnit.getImportStatements()) {
            String qualifiedImport = Names.getQualifiedName(astImportStatement.getImportList());
            ImportStatement importStatement = new ImportStatement(qualifiedImport,
                    astImportStatement.isStar());
            imports.add(importStatement);
        }
        EMAJavaHelper.addJavaDefaultImports(imports);

        ArtifactScope artifactScope = new EmbeddedMontiArcArtifactScope(
                Optional.empty(),
                compilationUnitPackage,
                imports);
        this.currentImports = imports;
        putOnStack(artifactScope);
    }

    public void endVisit(ASTEMACompilationUnit node) {
        // TODO clean up component types from references to inner components
        // cleanUpReferences();

        // artifact scope
        removeCurrentScope();

        if (aboartVisitComponent) {
            return;
        }
        // creates all instances which are created through the top level component
        Log.debug("endVisit of " + node.getComponent().getSymbol().get().getFullName(),
                "SymbolTableCreator:"); // ,"MontiArcSymbolTableCreator");
        // new Error().printStackTrace();
        instanceSymbolCreator.createInstances(
                (ComponentSymbol) (Log.errorIfNull(node.getComponent().getSymbol().orElse(null))));
    }

    /**
     * handles typeref creation of an SIUnitRangeType from ASTRange
     */
    protected MCTypeReference<? extends MCTypeSymbol> initTypeRefASTRange(StringBuilder typeName,
                                                                          ASTRange astType) {
        typeName.append("SIUnitRangesType");
        Log.debug(astType.toString(), "Type:");
        Log.debug(typeName.toString(), "TypeName:");

        SIUnitRangesSymbolReference ref = SIUnitRangesSymbolReference
                .constructSIUnitRangesSymbolReference(astType);
        return ref;
    }

    /**
     * handles typeref creation of an SIUnitRangesType from ASTRanges
     */
    protected MCTypeReference<? extends MCTypeSymbol> initTypeRefASTRanges(StringBuilder typeName,
                                                                           ASTRanges astType) {
        typeName.append("SIUnitRangesType");
        Log.debug(astType.toString(), "Type:");
        Log.debug(typeName.toString(), "TypeName:");

        SIUnitRangesSymbolReference ref = SIUnitRangesSymbolReference
                .constructSIUnitRangesSymbolReference(astType.getRanges());
        return ref;
    }

    /**
     * handles typeref creation of a GeneralType
     */
    protected MCTypeReference<? extends MCTypeSymbol> initTypeRefGeneralType(StringBuilder typeName,
                                                                             ASTType astType) {
        MCTypeReference<? extends MCTypeSymbol> typeRef = null;
        typeName.append(ArcTypePrinter.printTypeWithoutTypeArgumentsAndDimension(astType));
        // Log.debug(astType.toString(),"TYPE:");
        // Log.debug(typeName,"TYPEName:");
        typeRef = new CommonMCTypeReference<MCTypeSymbol>(typeName.toString(), MCTypeSymbol.KIND,
                currentScope().get());
        typeRef.setDimension(TypesHelper.getArrayDimensionIfArrayOrZero(astType));
        typeRef = addTypeArgumentsToTypeSymbol(typeRef, astType);
        return typeRef;
    }

    /**
     * handles typeref creation of a GeneralType
     */
    protected MCTypeReference<? extends MCTypeSymbol> initTypeRefGeneralType(String typeName) {
        MCTypeReference<? extends MCTypeSymbol> typeRef = null;
        typeRef = new CommonMCTypeReference<MCTypeSymbol>(typeName, MCTypeSymbol.KIND,
                currentScope().get());
        typeRef.setDimension(0);
        // addTypeArgumentsToTypeSymbol(typeRef, astType);
        return typeRef;
    }

    /**
     * returns an initialized type reference for a port
     *
     * @param node the node which is
     * @param typeName
     * @param astType
     * @return
     */
    protected MCTypeReference<? extends MCTypeSymbol> initTypeRef(ASTPort node,
                                                                  StringBuilder typeName, ASTType astType) {
        if (node.getType() instanceof ASTRange) {
            return initTypeRefASTRange(typeName, (ASTRange) astType);
        } else if (node.getType() instanceof ASTRanges) {
            return initTypeRefASTRanges(typeName, (ASTRanges) astType);
        }
        Log.debug(node.getName().get() + " " + astType.toString(), "info");
        return initTypeRefGeneralType(typeName, astType);
    }

    /**
     * creates the PortSymbols that belong to a PortArraySymbol
     */
    private void portCreationIntLiteralPresent(ASTPort node, PortArraySymbol pas, String name,
                                               MCTypeReference<? extends MCTypeSymbol> typeRef) {
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

            addToScopeAndLinkWithNode(sym, node);
        }
    }

    private void portCreation(ASTPort node, PortArraySymbol pas, String name,
                              MCTypeReference<? extends MCTypeSymbol> typeRef) {
        if (node.getUnitNumberResolution().isPresent()) {
            portCreationIntLiteralPresent(node, pas, name, typeRef);
        } else {
            // create PortSymbol with same content as PortArraySymbol
            createPort(node, name, node.isIncoming(), typeRef, pas);
        }
    }

    public void createPort(String name, boolean isIncoming,
                           MCTypeReference<? extends MCTypeSymbol> typeRef) {
        PortSymbol ps = new PortSymbol(name);

        ps.setTypeReference(typeRef);
        ps.setDirection(isIncoming);

        addToScope(ps);
    }

    public void createPort(ASTPort node, String name, boolean isIncoming,
                           MCTypeReference<? extends MCTypeSymbol> typeRef, PortArraySymbol pas) {
        PortSymbol ps = new PortSymbol(name);
        ps.setNameDependsOn(pas.getNameDependsOn());
        ps.setTypeReference(typeRef);
        ps.setDirection(isIncoming);

        addToScopeAndLinkWithNode(ps, node);
    }

    public String doPortResolution(ASTPort node) {
        String name = null;
        if (node.getUnitNumberResolution().isPresent()) {
            ASTUnitNumberResolution unitNumberResolution = node.getUnitNumberResolution().get();
            name = unitNumberResolution
                    .doResolution(componentStack.peek().getResolutionDeclarationSymbols());

        }
        return name;
    }

    @Override
    public void visit(ASTUnitNumberExpression node) {
        UnitNumberExpressionSymbol symbol = new UnitNumberExpressionSymbol(node);

        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTPort node) {

        String nameTO = doPortResolution(node);
        ASTType astType = node.getType();
        StringBuilder typeName = new StringBuilder();
        MCTypeReference<? extends MCTypeSymbol> typeRef = initTypeRef(node, typeName, astType);
        String name = node.getName().orElse(StringTransformations.uncapitalize(typeName.toString()));
    /* Log.debug(nameTO, "NameResolution:"); Log.debug(name, "Full Name:");
     * Log.debug(node.getType().toString(), "Node:"); Log.debug("" +
     * currentScope().get().toString(), "Scope:"); */
        PortArraySymbol pas = new PortArraySymbol(name, nameTO);

        pas.setTypeReference(typeRef);
        pas.setDirection(node.isIncoming());

        addToScopeAndLinkWithNode(pas, node);

        portCreation(node, pas, name, typeRef);
    }

    private List<String> getPortName(ASTQualifiedNameWithArray portName, int amountSources) {
        List<String> names = new ArrayList<String>();

        List<String> compNameParts = getComponentNameParts(portName);

        List<String> portNameParts;
        portNameParts = getPortNameParts(portName);

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

    private List<String> getComponentNameParts(ASTQualifiedNameWithArray portName) {
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
                    int size = countComponentArrayInstances(name);
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

    private List<String> getmnCompNameParts(String name, ASTQualifiedNameWithArray portName) {
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

    private List<String> getPortNameParts(ASTQualifiedNameWithArray portName) {
        return getPortNameParts(portName, 0);
    }

    private List<String> getPortNameParts(ASTQualifiedNameWithArray portName, int amountSources) {
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
                        portName.getCompArray().orElse(null));

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

    private List<String> getmnPortNameParts(String name, ASTQualifiedNameWithArray portName) {
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

    private String getNameArrayPart(ASTArrayAccess arrayPart) {
        String result = "";
        if (arrayPart.getIntLiteral().isPresent())
            result += "[" + arrayPart.getIntLiteral().get().getNumber().get().intValue() + "]";
        // Not handled here change handling this case after refactoring
    /* else if (arrayPart.getLowerbound().isPresent() && arrayPart.getUpperbound().isPresent()) {
     * } */
        return result;
    }

    private int countPortArrayInstances(String portName, String compName, ASTArrayAccess arrayPart) {
        MutableScope curScope = currentScope().get();

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

    private int countComponentArrayInstances(String componentName) {
        MutableScope curScope = currentScope().get();
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

    private void nonConstantPortSetup(List<String> sourceNames,
                                      de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTConnector node) {

        Log.info("" + sourceNames.size(), "SourcePorts");
        int counter = 0, targetnum = 0;
        for (ASTQualifiedNameWithArray target : node.getTargets().getQualifiedNameWithArrays()) {
            counter = 0;
            targetnum = 0;
            for (String sourceName : sourceNames) {
                List<String> targetNames = getPortName(target, sourceNames.size());
                targetnum = targetNames.size();
                if (counter < targetnum) {
                    String targetName = targetNames.get(counter);
                    Log.info("" + targetName, "target");
                    Log.info("" + sourceName, "source");

                    ConnectorSymbol sym = new ConnectorSymbol(targetName);
                    sym.setSource(sourceName);
                    sym.setTarget(targetName);
                    Log.info(sym.getTarget(), "TARGETNAME SET TO");

                    addToScopeAndLinkWithNode(sym, node);
                    ++counter;
                }
            }
            // TODO enable checking again if it is fixed
      /* if(counter!=targetnum) { Log.error("source port number "+ counter +" and target port num"+
       * targetnum+" don't match"); } */
        }
    }

    private void constantPortSetup(
            de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTConnector node) {
        int counter = 0, targetnum = 0;
        ConstantPortSymbol constantPortSymbol = ConstantPortSymbol.createConstantPortSymbol(node, this);
        addToScope(constantPortSymbol);
        for (ASTQualifiedNameWithArray target : node.getTargets().getQualifiedNameWithArrays()) {
            counter = 0;
            targetnum = 0;
            List<String> targetNames = getPortName(target, 0);
            targetnum = targetNames.size();
            String targetName = targetNames.get(counter);
            Log.debug("" + targetName, "target");

            ConnectorSymbol sym = new ConnectorSymbol(targetName);
            sym.setConstantPortSymbol(constantPortSymbol);
            sym.setSource(constantPortSymbol.getName());
            sym.setTarget(targetName);
            Log.debug(sym.getTarget(), "TARGETNAME SET TO");

            addToScopeAndLinkWithNode(sym, node);
            ++counter;

        }
    }

    public void doConnectorResolution(ASTConnector node) {
        if (node.getUnitNumberResolution().isPresent()) {
            ASTUnitNumberResolution unitNumberResolution = node.getUnitNumberResolution().get();
            ASTUnitNumber toSet = null;
            if (unitNumberResolution.getUnitNumber().isPresent()) {
                toSet = unitNumberResolution.getUnitNumber().get();
            } else if (unitNumberResolution.getName().isPresent()) {

                ResolutionDeclarationSymbol resDeclSym = componentStack.peek()
                        .getResolutionDeclarationSymbol(unitNumberResolution.getName().get()).get();
                toSet = ((ASTUnitNumberResolution) resDeclSym.getASTResolution()).getUnitNumber().get();
            }

            unitNumberResolution.setUnit(toSet.getUnit().get());
            unitNumberResolution.setNumber(toSet.getNumber().get());
        }

    }

    @Override
    public void visit(de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTConnector node) {
        doConnectorResolution(node);
        ASTQualifiedNameWithArray portName;
        List<String> sourceNames = null;
        boolean isConstant = false;
        if (node.getSource().isPresent()) {
            portName = node.getSource().get();

            sourceNames = getPortName(portName, 0);
            // Log.debug(node.getSource().get().toString(),"port content");
        } else {
            isConstant = true;
            // Log.debug(node.getSI_Unit().get().toString(), "port content else ");
            constantPortSetup(node);
        }
        if (!isConstant) {
            nonConstantPortSetup(sourceNames, node);
        }

    }

    @Override
    public void visit(ASTMontiArcAutoInstantiate node) {
        autoInstantiate = node.isOn();
    }

    public int handleSizeResolution(ASTSubComponent node, int index) {
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

    public void doSubComponentInstanceResolution(ASTSubComponentInstance node,
                                                 ComponentSymbolReference componentSymbolReference) {

        if (node.getUnitNumberResolution().isPresent()) {
            ASTUnitNumberResolution unitNumberResolution = node.getUnitNumberResolution().get();
            ASTUnitNumber toSet = null;
            if (unitNumberResolution.getUnitNumber().isPresent()) {
                toSet = unitNumberResolution.getUnitNumber().get();

            } else if (unitNumberResolution.getName().isPresent()) {

                ResolutionDeclarationSymbol resDeclSym = componentStack.peek()
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

    public void setActualResolutionDeclaration(ASTSubComponent node,
                                               ComponentSymbolReference componentSymbolReference) {
        int index = 0;
        int size = handleSizeResolution(node, index);
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

    @Override
    public void visit(ASTSubComponent node) {
        String referencedCompName;
    /* if (node.getType() instanceof ASTSimpleReferenceType) referencedCompName =
     * ArcTypePrinter.printSubComponentName(node); else */
        referencedCompName = ArcTypePrinter
                .printTypeWithoutTypeArgumentsAndDimension(node.getType());
        Log.debug(node.getType().toString(), "Type");
        // String refCompPackage = Names.getQualifier(referencedCompName);
        String simpleCompName = Names.getSimpleName(referencedCompName);
        Log.debug(referencedCompName, "referencedCompName");
        Log.debug(currentScope().get().toString(), "Scope");
        ComponentSymbolReference componentTypeReference = new ComponentSymbolReference(
                referencedCompName,
                currentScope().get(), this);

        // set actual Resolution values
        setActualResolutionDeclaration(node, componentTypeReference);

        // actual type arguments
        // TODO enable if needed
        addTypeArgumentsToTypeSymbol(componentTypeReference, node.getType());

        // ref.setPackageName(refCompPackage);

        // TODO internal representation of ValueSymbol ? that was heavily based on CommonValues
        // language and its expressions, but we use JavaDSL.
        List<ValueSymbol<TypeReference<TypeSymbol>>> configArgs = new ArrayList<>();
    /* for (ASTExpression arg : node.getArguments()) { String value = new JavaDSLPrettyPrinter(new
     * IndentPrinter()).prettyprint(arg); value = value.replace("\"", "\\\"").replace("\n", "");
     * configArgs.add(new ValueSymbol<>(value, Kind.Expression)); } */
        for (ASTExpression astExpression : node.getArguments())
            componentTypeReference.addArgument(astExpression);
        componentTypeReference.fixResolutions(this);
        // instances

        if (!node.getInstances().isEmpty()) {
            // create instances of the referenced components.
            for (ASTSubComponentInstance i : node.getInstances()) {
                // For generic type resolution Example: <N1 n=4> with instance being <6> to change value of
                // n accordingly
                doSubComponentInstanceResolution(i, componentTypeReference);
                Log.debug(node.getType().toString(), "Pre Handle Size:");

                if (i.getUnitNumberResolution().isPresent()) {
                    int size = i.getUnitNumberResolution().get().getNumber().get().intValue();

                    Log.debug(node.getType().toString(), "First: ");
                    Log.debug(node.getType().toString(), "Second: ");

                    for (int ii = 1; ii <= size; ++ii) {
                        createInstance(i.getName() + "[" + ii + "]", node, componentTypeReference, configArgs);
                    }
                } else {
                    createInstance(i.getName(), node, componentTypeReference, configArgs);
                }
            }
        } else {
            // auto instance because instance name is missing
            createInstance(StringTransformations.uncapitalize(simpleCompName), node,
                    componentTypeReference, new ArrayList<>());
        }

        node.setEnclosingScope(currentScope().get());
    }

    /**
     * Creates the instance and adds it to the symTab.
     */

    private void createInstance(String name, ASTSubComponent node,
                                ComponentSymbolReference componentTypeReference,
                                List<ValueSymbol<TypeReference<TypeSymbol>>> configArguments) {
        ComponentInstanceSymbol instance = new ComponentInstanceSymbol(name,
                componentTypeReference);
        for (ValueSymbol<TypeReference<TypeSymbol>> valueSymbol : configArguments)
            configArguments.forEach(v -> instance.addConfigArgument(v));
        // create a subscope for the instance
        addToScopeAndLinkWithNode(instance, node);
        Log.debug(currentScope().get().toString(), "SubComponentInstance Scope");
        // remove the created instance's scope
        removeCurrentScope();
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

    @Override
    public void handle(ASTComponent node) {
        getRealThis().visit(node);
        if (!aboartVisitComponent) {
            getRealThis().traverse(node);
            getRealThis().endVisit(node);
        }
    }

    private void handleResolutionDeclaration(ComponentSymbol typeSymbol,
                                             ASTTypeParameters astTypeParameters, Scope currentScope, ASTComponent node) {
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
                addToScopeAndLinkWithNode(resDeclSymRef, astTypeParameter);
            }
        }
    }

    @Override
    public void visit(ASTComponent node) {
        String componentName = node.getName();

        String componentPackageName = "";
        if (componentStack.isEmpty()) {
            // root component (most outer component of the diagram)
            componentPackageName = compilationUnitPackage;
        } else {
            // inner component uses its parents component full name as package
            componentPackageName = componentStack.peek().getFullName();
        }
        ComponentSymbol component = new ComponentSymbol(componentName);
        component.setImports(currentImports);
        component.setPackageName(componentPackageName);

        // Handle ResolutionDeclaration of stuff like <N1 n=5>
        handleResolutionDeclaration(component, node.getGenericTypeParameters(), currentScope().get(),
                node);

        Log.debug(component.toString(), "ComponentPreGeneric");
        // generic type parameters
        EMAJavaHelper.addTypeParametersToType(component, node.getGenericTypeParameters(),
                currentScope().get());

        Log.debug(component.toString(), "ComponentPostGeneric");
        // parameters
        // Log.debug(node.getHead().toString(),"ASTComponentHead");
        setParametersOfComponent(component, node);
        // Log.debug(component.toString(),"ComponentPostParam");

        // super component
        if (node.getSuperComponent().isPresent()) {
            ASTReferenceType superCompRef = node.getSuperComponent().get();
            String superCompName = ArcTypePrinter.printTypeWithoutTypeArgumentsAndDimension(superCompRef);

            ComponentSymbolReference ref = new ComponentSymbolReference(superCompName,
                    currentScope().get());
            ref.setAccessModifier(BasicAccessModifier.PUBLIC);
            // actual type arguments
            addTypeArgumentsToTypeSymbol(ref, superCompRef);

            component.setSuperComponent(Optional.of(ref));
        }

        // check if this component is an inner component
        if (!componentStack.isEmpty()) {
            component.setIsInnerComponent(true);
        }

        componentStack.push(component);

        addToScopeAndLinkWithNode(component, node);

        // TODO this is a hack to avoid loading one component symbol twice
        // --> must be changed in future
        Collection<Symbol> c = getGlobalScope(currentScope().get())
                .resolveDownMany(component.getFullName(), ComponentSymbol.KIND);
        if (c.size() > 1) {
            aboartVisitComponent = true;
            component.getEnclosingScope().getAsMutableScope()
                    .removeSubScope(component.getSpannedScope().getAsMutableScope());

            return;
        }

        autoConnectionTrafo.transformAtStart(node, component);
    }

    @Override
    public void visit(ASTMontiArcAutoConnect node) {
        autoConnectionTrafo.transform(node, componentStack.peek());
    }

    private void setParametersOfComponent(final ComponentSymbol componentSymbol, ASTComponent cmp) {
        Log.debug(componentSymbol.toString(), "ComponentPreParam");
        for (ASTParameter astParameter : cmp.getParameters()) {
            final String paramName = astParameter.getName();
            Log.debug(astParameter.toString(), "ASTParam");
            int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astParameter.getType());

            // TODO enable if needed and remove line below
            MCTypeReference<? extends MCTypeSymbol> paramTypeSymbol = new MontiCarTypeSymbolReference(
                    TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astParameter
                            .getType()),
                    currentScope().get(), dimension);

            addTypeArgumentsToTypeSymbol(paramTypeSymbol, astParameter.getType());

            final MCFieldSymbol parameterSymbol = jSymbolFactory.createFormalParameterSymbol(paramName,
                    (MontiCarTypeSymbolReference) paramTypeSymbol);
            componentSymbol.addConfigParameter(parameterSymbol);
            componentSymbol.addParameter(astParameter);

            if(astParameter.adaptableKeywordIsPresent())
                addConfigPort(componentSymbol,parameterSymbol);
        }
        Log.debug(componentSymbol.toString(), "ComponentPostParam");
    }

    private void addConfigPort(ComponentSymbol componentSymbol, MCFieldSymbol parameterSymbol) {
        ConfigPortSymbol adaptPort = new ConfigPortSymbol(parameterSymbol.getName());
        adaptPort.setTypeReference(parameterSymbol.getType());

        componentSymbol.addIncomingPort(adaptPort);
    }

    private boolean needsInstanceCreation(ASTComponent node, ComponentSymbol symbol) {
        boolean instanceNameGiven = false;// node.getInstanceName().isPresent();
        boolean autoCreationPossible = symbol.getFormalTypeParameters().size() == 0;

        return autoInstantiate && (instanceNameGiven || autoCreationPossible);
    }

    @Override
    public void endVisit(ASTComponent node) {
        ComponentSymbol component = componentStack.pop();
        autoConnectionTrafo.transformAtEnd(node, component);

        removeCurrentScope();

    }

    // TODO remove after GV's refactoring of such methodology to mc4/types.
    @Deprecated
    private MCTypeReference<? extends MCTypeSymbol> addTypeArgumentsToTypeSymbol(MCTypeReference<? extends MCTypeSymbol> typeReference,
                                                                                 ASTType astType) {
        if (astType instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
            if (!astSimpleReferenceType.getTypeArguments().isPresent()) {
                return typeReference;
            }
            List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
            for (ASTTypeArgument astTypeArgument : astSimpleReferenceType.getTypeArguments().get()
                    .getTypeArguments()) {
                if (astTypeArgument instanceof ASTWildcardType) {
                    ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;

                    // Three cases can occur here: lower bound, upper bound, no bound
                    if (astWildcardType.lowerBoundIsPresent() || astWildcardType.upperBoundIsPresent()) {
                        // We have a bound.
                        // Examples: Set<? extends Number>, Set<? super Integer>

                        // new bound
                        boolean lowerBound = astWildcardType.lowerBoundIsPresent();
                        ASTType typeBound = lowerBound
                                ? astWildcardType.getLowerBound().get()
                                : astWildcardType
                                .getUpperBound().get();

                        int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
                        MCTypeReference<? extends MCTypeSymbol> typeBoundSymbolReference = new MontiCarTypeSymbolReference(
                                ArcTypePrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound),
                                currentScope().get(), dimension);
                        // TODO string representation?
                        // typeBoundSymbolReference.setStringRepresentation(ArcTypePrinter
                        // .printWildcardType(astWildcardType));
                        ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
                                typeBoundSymbolReference);

                        // init bound
                        addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound);

                        actualTypeArguments.add(actualTypeArgument);
                    } else {
                        // No bound. Example: Set<?>
                        actualTypeArguments.add(new ActualTypeArgument(false, false, null));
                    }
                } else if (astTypeArgument instanceof ASTType) {
                    // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
                    ASTType astTypeNoBound = (ASTType) astTypeArgument;
                    int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound);
                    MCTypeReference<? extends MCTypeSymbol> typeArgumentSymbolReference = new MontiCarTypeSymbolReference(
                            ArcTypePrinter.printTypeWithoutTypeArgumentsAndDimension(astTypeNoBound),
                            currentScope().get(), dimension);

                    // TODO string representation?
                    // typeArgumentSymbolReference.setStringRepresentation(TypesPrinter
                    // .printType(astTypeNoBound));

                    addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound);

                    actualTypeArguments
                            .add(new ActualTypeArgumentNode(typeArgumentSymbolReference, astTypeNoBound));
                } else {
                    Log.error("0xU0401 Unknown type arguments " + astTypeArgument + " of type "
                            + typeReference);
                }
                typeReference.setActualTypeArguments(actualTypeArguments);
            }
        } else if (astType instanceof ASTComplexReferenceType) {
            ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
            for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
                    .getSimpleReferenceTypes()) {
                // TODO
        /* ASTComplexReferenceType represents types like class or interface types which always have
         * ASTSimpleReferenceType as qualification. For example: a.b.c<Arg>.d.e<Arg> */
            }
        } else if (astType instanceof ASTComplexArrayType) {
            ASTComplexArrayType astComplexArrayType = (ASTComplexArrayType) astType;
            // references to types with dimension>0, e.g., String[]
            addTypeArgumentsToTypeSymbol(typeReference, astComplexArrayType.getComponentType());
            int dimension = astComplexArrayType.getDimensions();
            typeReference.setDimension(dimension);
        } else {
            String name = typeReference.getName();
            assert typeReference.getEnclosingScope() instanceof MutableScope;
            MutableScope enclosingScope = (MutableScope) typeReference.getEnclosingScope();
            typeReference = MCASTTypeSymbolReference.constructReference(name, enclosingScope, astType);
        }
        return typeReference;
    }

    private void setActualTypeArguments(ComponentSymbolReference typeReference,
                                        List<ASTTypeArgument> astTypeArguments) {
        List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
        for (ASTTypeArgument astTypeArgument : astTypeArguments) {
            if (astTypeArgument instanceof ASTWildcardType) {
                ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;

                // Three cases can occur here: lower bound, upper bound, no bound
                if (astWildcardType.lowerBoundIsPresent() || astWildcardType.upperBoundIsPresent()) {
                    // We have a bound.
                    // Examples: Set<? extends Number>, Set<? super Integer>

                    // new bound
                    boolean lowerBound = astWildcardType.lowerBoundIsPresent();
                    ASTType typeBound = lowerBound
                            ? astWildcardType.getLowerBound().get()
                            : astWildcardType
                            .getUpperBound().get();
                    int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
                    MCTypeReference<? extends MCTypeSymbol> typeBoundSymbolReference = new MontiCarTypeSymbolReference(
                            ArcTypePrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound),
                            currentScope().get(), dimension);
                    // TODO string representation?
                    // typeBoundSymbolReference.setStringRepresentation(ArcTypePrinter
                    // .printWildcardType(astWildcardType));
                    ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
                            typeBoundSymbolReference);

                    // init bound
                    addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound);

                    actualTypeArguments.add(actualTypeArgument);
                } else {
                    // No bound. Example: Set<?>
                    actualTypeArguments.add(new ActualTypeArgument(false, false, null));
                }
            } else if (astTypeArgument instanceof ASTType) {
                // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
                ASTType astTypeNoBound = (ASTType) astTypeArgument;
                int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound);
                MCTypeReference<? extends MCTypeSymbol> typeArgumentSymbolReference = new MontiCarTypeSymbolReference(
                        ArcTypePrinter.printTypeWithoutTypeArgumentsAndDimension(astTypeNoBound),
                        currentScope().get(), dimension);

                // TODO string representation?
                // typeArgumentSymbolReference.setStringRepresentation(TypesPrinter
                // .printType(astTypeNoBound));

                addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound);

                actualTypeArguments.add(new ActualTypeArgument(typeArgumentSymbolReference));
            } else {
                Log.error("0xU0401 Unknown type arguments " + astTypeArgument + " of type "
                        + typeReference);
            }
        }
        typeReference.setActualTypeArguments(actualTypeArguments);
    }

    // TODO references to component symbols should not differ from MontiCarTypeSymbolReference?
    @Deprecated
    private void addTypeArgumentsToTypeSymbol(ComponentSymbolReference typeReference,
                                              ASTType astType) {
        if (astType instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
            if (!astSimpleReferenceType.getTypeArguments().isPresent()) {
                // Log.error("Not TypeArgs present");
                return;
            }
            setActualTypeArguments(typeReference,
                    astSimpleReferenceType.getTypeArguments().get().getTypeArguments());
        } else if (astType instanceof ASTComplexReferenceType) {
            ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
            for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
                    .getSimpleReferenceTypes()) {
                // TODO
        /* ASTComplexReferenceType represents types like class or interface types which always have
         * ASTSimpleReferenceType as qualification. For example: a.b.c<Arg>.d.e<Arg> */

            }
        }

    }

    public void removeFromScope(Symbol symbol) {
        currentScope().get().remove(symbol);
    }

    public MutableScope getCurrentScopeAsMutableScope() {
        return currentScope().get();
    }
}
