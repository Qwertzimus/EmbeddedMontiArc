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
package de.monticore.lang.embeddedmontiarc;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTSubComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._visitor.EmbeddedMontiArcParentAwareVisitor;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._visitor.EmbeddedMontiArcVisitor;
import de.monticore.lang.monticar.literals2._ast.ASTBooleanLiteral;
import de.monticore.lang.monticar.literals2._ast.ASTLiteral;
import de.monticore.lang.monticar.mcexpressions._ast.ASTExpression;
import de.monticore.lang.monticar.mcexpressions._ast.ASTLiteralExpression;
import de.monticore.lang.monticar.mcexpressions._ast.ASTNameExpression;
import de.monticore.lang.monticar.ranges._ast.ASTUnitNumberExpression;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.struct._symboltable.StructSymbol;
import de.monticore.lang.monticar.struct._symboltable.StructSymbolReference;
import de.monticore.lang.monticar.struct.model.type.ScalarStructFieldType;
import de.monticore.lang.monticar.struct.model.type.StructFieldTypeInfo;
import de.monticore.lang.monticar.struct.model.type.StructReferenceFieldType;
import de.monticore.lang.monticar.struct.model.type.VectorStructFieldType;
import de.monticore.lang.monticar.ts.MCFieldSymbol;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.monticore.lang.monticar.ts.references.MCTypeReference;
import de.monticore.lang.monticar.types2._ast.ASTType;
import de.monticore.lang.monticar.types2._ast.ASTTypeArguments;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ComponentParametersSubstitutionsBuilder extends EmbeddedMontiArcParentAwareVisitor {

    @Nonnull
    private final ComponentSymbol component;

    @Nonnull
    private EmbeddedMontiArcVisitor theRealThis = this;

    private FormalTypeParametersSubstitutions formalTypeParametersSubstitutions;
    private ConfigurationParametersSubstitutions configurationParametersSubstitutions;
    private ComponentInstanceSymbol currentChild;
    private List<String> childFormalTypeParameters;
    private List<String> childConfigurationParameters;

    public ComponentParametersSubstitutionsBuilder(@Nonnull ComponentSymbol component) {
        this.component = component;
    }

    @Override
    public void setRealThis(EmbeddedMontiArcVisitor realThis) {
        if (realThis != null) {
            theRealThis = realThis;
        }
    }

    @Override
    @Nonnull
    public EmbeddedMontiArcVisitor getRealThis() {
        return theRealThis;
    }

    public ComponentParametersSubstitutions build() {
        formalTypeParametersSubstitutions = new FormalTypeParametersSubstitutions();
        configurationParametersSubstitutions = new ConfigurationParametersSubstitutions();
        childFormalTypeParameters = new ArrayList<>();
        childConfigurationParameters = new ArrayList<>();
        if (component.hasFormalTypeParameters()) {
            List<String> names = component.getFormalTypeParameters()
                    .stream()
                    .map(Symbol::getName)
                    .collect(Collectors.toList());
            formalTypeParametersSubstitutions.getParentFormalTypeParameterNames().addAll(names);
        }
        if (component.hasConfigParameters()) {
            component.getConfigParameters()
                    .forEach(p -> {
                        StructFieldTypeInfo type = tryConvertToStructFieldTypeInfo(p.getType());
                        if (type != null) {
                            configurationParametersSubstitutions
                                    .getParentConfigurationParameters()
                                    .put(p.getName(), type);
                        } else {
                            Log.error(
                                    "cannot handle configuration" +
                                            " parameter " + p.getName() +
                                            " in component " + component.getFullName(),
                                    p.getSourcePosition()
                            );
                        }
                    });
        }
        ASTComponent astNode = (ASTComponent) component.getAstNode().get();
        astNode.accept(theRealThis);
        return new ComponentParametersSubstitutions(
                formalTypeParametersSubstitutions,
                configurationParametersSubstitutions
        );
    }

    @Override
    public void visit(ASTSubComponent node) {
        ComponentInstanceSymbol subComp = (ComponentInstanceSymbol) node.getSymbol().get();
        currentChild = subComp;
        childFormalTypeParameters.clear();
        childFormalTypeParameters.addAll(
                subComp.getComponentType()
                        .getReferencedSymbol()
                        .getFormalTypeParameters()
                        .stream()
                        .map(MCTypeSymbol::getName)
                        .collect(Collectors.toList())
        );
        childConfigurationParameters.clear();
        childConfigurationParameters.addAll(
                subComp.getComponentType()
                        .getReferencedSymbol()
                        .getConfigParameters()
                        .stream()
                        .map(MCFieldSymbol::getName)
                        .collect(Collectors.toList())
        );
        List<ASTExpression> astArguments = node.getArguments();
        if (astArguments.size() != childConfigurationParameters.size()) {
            Log.error(
                    "number of type arguments do not match number of type parameters",
                    node.get_SourcePositionStart()
            );
            return;
        }
        for (int i = 0, len = astArguments.size(); i < len; i++) {
            ASTExpression astArg = astArguments.get(i);
            String name = childConfigurationParameters.get(i);
            if (!processArgument(name, astArg)) {
                Log.error(
                        "cannot handle" +
                                " configuration argument " + astArg +
                                " in instance " + currentChild +
                                " of component " + component.getFullName(),
                        astArg.get_SourcePositionStart()
                );
            }
        }
    }

    @Override
    public void endVisit(ASTSubComponent node) {
        currentChild = null;
        childFormalTypeParameters.clear();
        childConfigurationParameters.clear();
    }

    public void visit(ASTTypeArguments node) {
        if (currentChild == null) {
            return;
        }
        List<ASTTypeArgument> typeArgs = new ArrayList<>(node.getTypeArguments());
        typeArgs.sort(StartSourcePositionComparator.INSTANCE);
        for (int i = 0, len = typeArgs.size(); i < len; i++) {
            ASTTypeArgument astNode = typeArgs.get(i);
            String formalParameterName = childFormalTypeParameters.get(i);
            if (!processTypeArgument(formalParameterName, astNode)) {
                Log.error(
                        "cannot handle" +
                                " type argument $astNode" +
                                " in instance $currentChild" +
                                " of component ${component.fullName}",
                        astNode.get_SourcePositionStart()
                );
            }
        }
    }

    private boolean processTypeArgument(
            String formalParameterName,
            ASTTypeArgument typeArgument
    ) {
        if (typeArgument instanceof ASTUnitNumberResolution) {
            String actualParameterName = ((ASTUnitNumberResolution) typeArgument).getName().get();
            if (isParentHasTypeParameter(actualParameterName)) {
                formalTypeParametersSubstitutions.getParametersPassedToChildren().add(
                        new PassedParameter(
                                actualParameterName,
                                currentChild.getName(),
                                formalParameterName
                        )
                );
                return true;
            } else {
                // it must be struct
                StructReferenceFieldType struct = tryResolveStructure(actualParameterName);
                if (struct == null) {
                    return false;
                }
                addLiteralTypeParameter(formalParameterName, struct);
                return true;
            }
        }
        if (typeArgument instanceof ASTType) {
            StructFieldTypeInfo p = StructFieldTypeInfo.tryRepresentASTType((ASTType) typeArgument, currentChild.getEnclosingScope());
            if (p == null) {
                return false;
            }
            addLiteralTypeParameter(formalParameterName, p);
            return true;
        }
        return false;
    }

    private boolean processArgument(String name, ASTExpression arg) {
        if (arg instanceof ASTNameExpression) {
            ASTNameExpression arg2 = (ASTNameExpression) arg;
            if (isParentHasConfigurationParameter(arg2.getName())) {
                configurationParametersSubstitutions.getParametersPassedToChildren().add(
                        new PassedParameter(
                                arg2.getName(),
                                currentChild.getName(),
                                name
                        )
                );
                return true;
            }
        }
        if (arg instanceof ASTUnitNumberExpression) {
            ASTUnitNumberExpression arg2 = (ASTUnitNumberExpression) arg;
            if (!arg2.getTUnitNumber().isPresent()) {
                return false;
            }
            double literalValue;
            try {
                literalValue = Double.parseDouble(arg2.getTUnitNumber().get());
            } catch (NumberFormatException ex) {
                return false;
            }
            addLiteralConfigurationParameter(name, literalValue);
            return true;
        }
        if (arg instanceof ASTLiteralExpression) {
            ASTLiteralExpression arg2 = (ASTLiteralExpression) arg;
            ASTLiteral literal = arg2.getLiteral();
            if (literal instanceof ASTBooleanLiteral) {
                addLiteralConfigurationParameter(name, ((ASTBooleanLiteral) literal).getValue());
                return true;
            }
        }
        return false;
    }

    private void addLiteralTypeParameter(
            String formalParamName,
            StructFieldTypeInfo type
    ) {
        String childName = currentChild.getName();
        List<LiteralParameterSubstitution<StructFieldTypeInfo>> literalParams = formalTypeParametersSubstitutions.getLiteralChildParameters().get(childName);
        if (literalParams == null) {
            literalParams = new ArrayList<>();
            formalTypeParametersSubstitutions.getLiteralChildParameters().put(childName, literalParams);
        }
        LiteralParameterSubstitution<StructFieldTypeInfo> p = new LiteralParameterSubstitution<>(formalParamName, type);
        literalParams.add(p);
    }

    private void addLiteralConfigurationParameter(String name, Object literalValue) {
        String childName = currentChild.getName();
        List<LiteralParameterSubstitution<Object>> literalParams = configurationParametersSubstitutions.getLiteralChildParameters().get(childName);
        if (literalParams == null) {
            literalParams = new ArrayList<>();
            configurationParametersSubstitutions.getLiteralChildParameters().put(childName, literalParams);
        }
        LiteralParameterSubstitution<Object> p = new LiteralParameterSubstitution<>(name, literalValue);
        literalParams.add(p);
    }

    private StructFieldTypeInfo tryConvertToStructFieldTypeInfo(MCTypeReference<?> type) {
        if (type == null || type.getName() == null) {
            return null;
        }
        String typeName = type.getName();
        StructFieldTypeInfo baseType = null;
        switch (typeName) {
            case "scalar.B":
                baseType = ScalarStructFieldType.BOOL;
                break;
            case "scalar.C":
                baseType = ScalarStructFieldType.COMPLEX;
                break;
            case "scalar.Q":
                baseType = ScalarStructFieldType.RATIONAL;
                break;
            case "scalar.Z":
                baseType = ScalarStructFieldType.INTEGRAL;
                break;
        }
        if (baseType == null && isParentHasTypeParameter(typeName)) {
            baseType = new FormalTypeParameterFieldType(typeName);
        }
        if (baseType == null) {
            baseType = tryResolveStructure(typeName);
        }
        if (type.getDimension() > 0) {
            VectorStructFieldType vsft = new VectorStructFieldType();
            vsft.setTypeOfElements(baseType);
            vsft.setDimensionality(type.getDimension());
            return vsft;
        }
        return baseType;
    }

    private StructReferenceFieldType tryResolveStructure(String name) {
        Scope scope = component.getEnclosingScope();
        StructSymbol struct = scope.<StructSymbol>resolve(name, StructSymbol.KIND).orElse(null);
        if (struct == null) {
            return null;
        }
        StructReferenceFieldType ref = new StructReferenceFieldType();
        ref.setReference(new StructSymbolReference(struct.getFullName(), scope));
        return ref;
    }

    private boolean isParentHasTypeParameter(String name) {
        return formalTypeParametersSubstitutions.getParentFormalTypeParameterNames().contains(name);
    }

    private boolean isParentHasConfigurationParameter(String name) {
        return configurationParametersSubstitutions.getParentConfigurationParameters().containsKey(name);
    }
}
