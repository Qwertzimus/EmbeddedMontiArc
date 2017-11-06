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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTPort;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution;
import de.monticore.symboltable.*;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.monticore.lang.monticar.ts.references.MCTypeReference;
import de.se_rwth.commons.logging.Log;
import org.jscience.mathematics.number.Rational;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Symboltable entry for port arrays
 */
public class PortArraySymbol extends PortSymbol {
    public static final PortArraySymbolKind KIND = PortArraySymbolKind.INSTANCE;

    protected Optional<String> nameSizeDependsOn;

    public PortArraySymbol(String name, String nameSizeDependsOn) {
        super(name, KIND);
        this.nameSizeDependsOn = Optional.ofNullable(nameSizeDependsOn);
        Log.debug(getFullName(), "PortArraySymbol ");
        Log.debug(this.nameSizeDependsOn.orElse(null), "set NameSizeDependsOn to:");
    }

    private int dimension = 1;

    public Optional<String> getNameSizeDependsOn() {
        return nameSizeDependsOn;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public List<? extends PortSymbol> getConcretePortSymbols() {
        //TODO fix wrong port return
        return getEnclosingScope().<PortSymbol>resolveLocally(PortSymbol.KIND)
                .stream().filter(s -> s.getName().startsWith(this.getName()/*+"["*/))
                .collect(Collectors.toList());
    }

    /**
     * starts with 1
     *
     * @param index
     * @return
     */
    public Optional<PortSymbol> getPortSymbolWithIndex(int index) {
        for (PortSymbol portSymbol : getConcretePortSymbols()) {
            if (portSymbol.getName().contains("[" + index + "]")) {
                return Optional.of(portSymbol);
            }
        }
        return Optional.ofNullable(null);
    }

    public void recreatePortArray(ResolutionDeclarationSymbol resDeclSym, EmbeddedMontiArcSymbolTableCreator emastc, ComponentSymbolReference componentSymbolReference) {
        Log.debug(componentSymbolReference.toString(), "recreate");
        Log.debug(getNameSizeDependsOn().toString(), "String info:");
        if (getNameSizeDependsOn().isPresent() && getNameSizeDependsOn().get().equals(resDeclSym.getNameToResolve())) {
            int size = -1;
            if (resDeclSym.getASTResolution() instanceof ASTUnitNumberResolution) {
                size = ((ASTUnitNumberResolution) resDeclSym.getASTResolution()).getNumber().get().intValue();
            }
            List<? extends PortSymbol> portSymbols = getConcretePortSymbols();

            PortSymbol firstPort = getPortSymbolWithIndex(1).get();

            int oldSize = portSymbols.size();
            if (size == 0) {
                size = oldSize;
                ((ASTUnitNumberResolution) resDeclSym.getASTResolution()).setNumber(Rational.valueOf("" + oldSize));
            }
            Log.debug(componentSymbolReference.toString(), "FullName:");
            Log.debug(oldSize + "", "old Port Size:");
            Log.debug(size + "", "new Port Size:");

            for (int i = 0; i <= size; ++i) {
                if (oldSize < i) {
                    //Log.debug();
                    createPortSymbolForArrayIndex(componentSymbolReference, (ASTPort) firstPort.getAstNode().get(), this.getName() + "[" + i + "]", firstPort.getStereotype(), firstPort.getTypeReference());
                }
            }
            //just add missing ports here and fix actual size after expandedcomponentinstance creation
            /*for (int i = size + 1; i <= oldSize; ++i) {
                if (getPortSymbolWithIndex(i).isPresent())
                    getEnclosingScope().getAsMutableScope().remove(getPortSymbolWithIndex(i).get());
            }
            */
        } else {
            Log.debug("Is not Present", "NameSizeDependsOn:");
        }
    }

    private void createPortSymbolForArrayIndex(ComponentSymbolReference componentSymbolReference, ASTPort node, String name, Map<String, Optional<String>> stereoType, MCTypeReference<? extends MCTypeSymbol> typeRef) {
        PortSymbol ps;
        if (name.startsWith("CONSTANTPORT")) {
            ps = new ConstantPortSymbol(name);
        } else {
            ps = new PortSymbol(name);
        }
        ps.setNameDependsOn(nameSizeDependsOn);
        ps.setTypeReference(typeRef);
        ps.setDirection(node.isIncoming());

        stereoType
                .forEach(ps::addStereotype);

        getEnclosingScope().getAsMutableScope().add(ps);

        //emastc.addToScopeAndLinkWithNode(ps, node);

        Log.debug(name + " " + componentSymbolReference.getAllIncomingPorts().size(), "Added PortSymbol From PortArray:");
    }


    public static class PortArraySymbolKind implements SymbolKind {

        public static final PortArraySymbolKind INSTANCE = new PortArraySymbolKind();

        protected PortArraySymbolKind() {

        }
    }
}
