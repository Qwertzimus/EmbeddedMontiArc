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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.TypesPrinter;
import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.monticore.lang.monticar.ts.MontiCarSymbolFactory;
import de.monticore.lang.monticar.ts.MontiCarTypeSymbol;
import de.monticore.lang.monticar.ts.references.MontiCarTypeSymbolReference;
import de.monticore.lang.monticar.types2._ast.ASTComplexArrayType;
import de.monticore.lang.monticar.types2._ast.ASTComplexReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTNamingResolution;
import de.monticore.lang.monticar.types2._ast.ASTSimpleReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTType;
import de.monticore.lang.monticar.types2._ast.ASTTypeParameters;
import de.monticore.lang.monticar.types2._ast.ASTTypeVariableDeclaration;
import de.monticore.lang.monticar.types2._ast.ASTWildcardType;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO This class should be removed by putting its methods in JavaDSL (or even MC/Types) project.
 *
 * @author Robert Heim
 */
public class EMAJavaHelper extends de.monticore.lang.montiarc.montiarc._symboltable.JavaHelper {
    private final static MontiCarSymbolFactory jSymbolFactory = new MontiCarSymbolFactory();

    /**
     * Adds the TypeParameters to the MontiCarTypeSymbol if the class or interface declares TypeVariables.
     * Example:
     * <p>
     * class Bla<T, S extends SomeClass<T> & SomeInterface>
     * </p>
     * T and S are added to Bla.
     *
     * @param typeSymbol
     * @param optionalTypeParameters
     * @return MontiCarTypeSymbol list to be added to the scope
     */
    // TODO see JavaSymbolTableCreator.addTypeParameters(...),
    // see ComponentSymbol addFormalTypeParameters etc.
    protected static List<MCTypeSymbol> addTypeParametersToType(
            ComponentSymbol typeSymbol,
            Optional<ASTTypeParameters> optionalTypeParameters, Scope currentScope) {
        if (optionalTypeParameters.isPresent()) {
            ASTTypeParameters astTypeParameters = optionalTypeParameters.get();
            for (ASTTypeVariableDeclaration astTypeParameter : astTypeParameters
                    .getTypeVariableDeclarations()) {
                if (astTypeParameter.resolutionDeclarationIsPresent()) {
                    //Not handled here
                    // new type parameter

                    // TypeParameters/TypeVariables are seen as type declarations.
                    // For each variable instantiate a MontiCarTypeSymbol.
                    //TODO FIX if not present
                    if (astTypeParameter.getResolutionDeclaration().get().getTypeName() != null) {
                        final String typeVariableName = astTypeParameter.getResolutionDeclaration().get().getTypeName();

                        addFormalTypeParameter(typeVariableName, astTypeParameter, currentScope, typeSymbol);
                    } else if (astTypeParameter.getResolutionDeclaration().get() instanceof ASTNamingResolution) {

                        final String typeVariableName = astTypeParameter.getResolutionDeclaration().get().getName();
                        addFormalTypeParameter(typeVariableName, astTypeParameter, currentScope, typeSymbol);
                    } else {
                        Log.debug(astTypeParameter.getResolutionDeclaration().get().toString(), "Resolution Declaration");
                        Log.debug("0xADTYPA Case not handled", "Implementation Missing");
                    }
                } else {
                    final String typeVariableName = astTypeParameter.getNamingResolution().get().getName();
                    addFormalTypeParameter(typeVariableName, astTypeParameter, currentScope, typeSymbol);
                }
            }
        }
        return typeSymbol.getFormalTypeParameters();
    }

    private static void addFormalTypeParameter(String typeVariableName, ASTTypeVariableDeclaration astTypeParameter, Scope currentScope, ComponentSymbol typeSymbol) {
        MontiCarTypeSymbol javaTypeVariableSymbol = jSymbolFactory.createTypeVariable(typeVariableName);
        // TODO implement
        // // init type parameter
        // if (astTypeParameter.getTypeBound().isPresent()) {
        // // Treat type bounds are implemented interfaces, even though the first
        // // bound might be a class. See also JLS7.
        // addInterfacesToType(javaTypeVariableSymbol, astTypeParameter.getTypeBound().get()
        // .getTypes());
        // }
        // Treat type bounds are implemented interfaces, even though the
        // first bound might be a class. See also JLS7.
        List<ASTType> types = new ArrayList<ASTType>(astTypeParameter.getUpperBounds());

        addInterfacesToTypeEMA(javaTypeVariableSymbol, types, currentScope);

        // add type parameter
        typeSymbol.addFormalTypeParameter(javaTypeVariableSymbol);
    }

    /**
     * Adds the given ASTTypes as interfaces to the MontiCarTypeSymbol. The MontiCarTypeSymbol can be a type
     * variable. Interfaces may follow after the first extended Type. We treat the first Type also as
     * interface even though it may be a class.
     * <p>
     * class Bla implements SomeInterface, AnotherInterface, ... <br>
     * class Bla&ltT extends SomeClassOrInterface & SomeInterface & ...&gt
     * </p>
     * See also JLS7.
     *
     * @param astInterfaceTypeList
     */
    // TODO this is implemented in JavaDSL, but reimplemented because of ArcTypeSymbol. This should
    // somehow be extracted and implemented only once
    protected static void addInterfacesToTypeEMA(MontiCarTypeSymbol arcTypeSymbol,
                                                 List<ASTType> astInterfaceTypeList, Scope currentScope) {
        for (ASTType astInterfaceType : astInterfaceTypeList) {
            MontiCarTypeSymbolReference javaInterfaceTypeSymbolReference = new MontiCarTypeSymbolReference(
                    TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astInterfaceType), currentScope,
                    0);
            List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();

            // Add the ASTTypeArguments to astInterfaceType
            // Before we can do that we have to cast.
            if (astInterfaceType instanceof ASTSimpleReferenceType) {
                // TODO
                // addTypeParametersToType(javaInterfaceTypeSymbolReference, astInterfaceType);
            } else if (astInterfaceType instanceof ASTComplexReferenceType) {
                ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astInterfaceType;
                for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
                        .getSimpleReferenceTypes()) {
                    // TODO javaInterfaceTypeSymbolReference.getEnclosingScope().resolve("Boolean", MCTypeSymbol.KIND).get()
                    //    javaInterfaceTypeSymbolReference.getEnclosingScope().resolve("Boolean", MCTypeSymbol.KIND))
                    if (astSimpleReferenceType.getTypeArguments().isPresent()) {
                        for (ASTTypeArgument argument : astSimpleReferenceType.getTypeArguments().get().getTypeArguments()) {

                            if (!handleSimpleReferenceType(argument, 0, actualTypeArguments,
                                    javaInterfaceTypeSymbolReference.getEnclosingScope(), false, false, null) &&
                                    (argument instanceof ASTComplexArrayType)) {
                                ASTComplexArrayType arrayArg = (ASTComplexArrayType) argument;
                                ASTType cmpType = arrayArg.getComponentType();
                                handleSimpleReferenceType(cmpType, arrayArg.getDimensions(),
                                        actualTypeArguments, javaInterfaceTypeSymbolReference.getEnclosingScope(), false, false, null);
                            }
                        }

                    }

                    arcTypeSymbol.addInterface(javaInterfaceTypeSymbolReference);
                }
            }
            javaInterfaceTypeSymbolReference.setActualTypeArguments(actualTypeArguments);
        }
    }

    /**
     * if you have questions ask JP ;)
     */
    protected static boolean handleSimpleReferenceType(ASTTypeArgument argument,
                                                       final int dim, final List<ActualTypeArgument> actualTypeArguments, final Scope symbolTable,
                                                       final boolean isLowerBound, final boolean isUpperBound, @Nullable ActualTypeArgument typeArgument) {
        if (argument instanceof ASTSimpleReferenceType) {
            ASTSimpleReferenceType simpleArg = (ASTSimpleReferenceType) argument;
            String name = simpleArg.getNames().stream().collect(Collectors.joining("."));
            Optional<MCTypeSymbol> symbol = symbolTable.resolve(name, MCTypeSymbol.KIND);
            if (symbol.isPresent() && symbol.get().getEnclosingScope() != null) {
                if (typeArgument == null) {
                    typeArgument = new ActualTypeArgumentASTElement(isLowerBound, isUpperBound, new MontiCarTypeSymbolReference(
                            symbol.get().getName(),
                            symbol.get().getEnclosingScope(), dim)).setAstTypeArguments(argument);

                    actualTypeArguments.add(typeArgument);
                }
                if (simpleArg.getTypeArguments().isPresent()) {
                    List<ActualTypeArgument> actualTypeArguments2 = new ArrayList<>();
                    for (ASTTypeArgument astTypeArgument : simpleArg.getTypeArguments().get().getTypeArguments()) {
                        handleSimpleReferenceType(astTypeArgument, 0, actualTypeArguments2, symbolTable, false, false, typeArgument);
                    }
                    typeArgument.getType().setActualTypeArguments(actualTypeArguments2);
                }
            }
            return true;
        } else if (argument instanceof ASTComplexReferenceType) {
            ASTComplexReferenceType complexArg = (ASTComplexReferenceType) argument;
            complexArg.getSimpleReferenceTypes().stream().forEachOrdered(t ->
                    handleSimpleReferenceType(t, dim, actualTypeArguments, symbolTable, isLowerBound, isUpperBound, null)
            );
            return true;
        } else if (argument instanceof ASTWildcardType) {
            ASTWildcardType wildArg = (ASTWildcardType) argument;
            if (wildArg.getLowerBound().isPresent()) {
                return handleSimpleReferenceType(wildArg.getLowerBound().get(), dim, actualTypeArguments,
                        symbolTable, true, false, null);
            } else if (wildArg.getUpperBound().isPresent()) {
                return handleSimpleReferenceType(wildArg.getUpperBound().get(), dim, actualTypeArguments,
                        symbolTable, false, true, null);
            }
        } else if (argument instanceof ASTComplexArrayType) {
            ASTComplexArrayType arrayArg = (ASTComplexArrayType) argument;
            ASTType cmpType = arrayArg.getComponentType();
            return handleSimpleReferenceType(cmpType, arrayArg.getDimensions(),
                    actualTypeArguments, symbolTable, false, false, null);
        }
        return false;
    }

    // TODO this should be part of JavaDSL
    public static void addJavaDefaultTypes(GlobalScope globalScope) {
        // we add default types by putting mock implementations of java.lang and java.util in
        // src/main/resources/defaultTypes and adding it to the model path when creating the global scope!
        // TODO This, however, should be done in JavaDSL and the EmbeddedMontiArcLanguage somehow...
    }

    /**
     * Adds the default imports of the java language to make default types resolvable without
     * qualification (e.g., "String" instead of "java.lang.String").
     *
     * @param imports
     */
    public static void addJavaDefaultImports(List<ImportStatement> imports) {
        imports.add(new ImportStatement("java.lang", true));
        imports.add(new ImportStatement("java.util", true));
    }
}
