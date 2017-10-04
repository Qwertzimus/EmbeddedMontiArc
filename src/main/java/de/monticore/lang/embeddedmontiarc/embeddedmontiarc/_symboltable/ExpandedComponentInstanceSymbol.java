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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.EMAVariable;
import de.monticore.lang.embeddedmontiarc.helper.SymbolPrinter;
import de.monticore.lang.montiarc.tagging._symboltable.TaggingScopeSpanningSymbol;
import de.monticore.lang.monticar.mcexpressions._ast.ASTExpression;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.types2._ast.ASTUnitNumberResolution;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Michael von Wenckstern
 *         Created by Michael von Wenckstern on 23.05.2016.
 *         The aim of this class is to have real component instances<br>
 *         component A {
 *         component B<Integer> b1;
 *         component B<Double> b2;
 *         }
 *         component B<T> {
 *         component C<T> c1, c2;
 *         }
 *         component C<T> {
 *         ports in T a,
 *         in T b,
 *         out T c;
 *         }
 *         after expanding the component's definitions we will get the following instance:
 *         component instance A {
 *         component instance b1 {
 *         component instance c1 {
 *         ports in Integer a,
 *         ports in Integer b,
 *         ports out Integer c;
 *         }
 *         component instance c2 {
 *         ports in Integer a,
 *         ports in Integer b,
 *         ports out Integer c;
 *         }
 *         }
 *         component instance b2 {
 *         component instance c1 {
 *         ports in Double a,
 *         ports in Double b,
 *         ports out Double c;
 *         }
 *         component instance c2 {
 *         ports in Double a,
 *         ports in Double b,
 *         ports out Double c;
 *         }
 *         }
 *         }
 *         These instances are important for:
 *         * executing the simulation order later on (to be compatible with industry, Simulink:
 *         they only care about the atomic elements and give them an execution order.
 *         In this example these would be the four elements: A.b1.c1, A.b1.c2, A.b2.c1, A.b2.c2
 *         *  for tagging instances differently (e.g. A.b1.c1 may be deployed on another processor
 *         than A.b2.c1)
 *         *  different C%C analysis techniques, e.g. Control-Flow-Analysis (need to differentiate
 *         between instances)
 *         This class is the basic class for instances so that you can resolve them using the
 *         standard symbol table mechanism
 */
public class ExpandedComponentInstanceSymbol
        extends TaggingScopeSpanningSymbol {

    public static final EMAExpandedComponentInstanceKind KIND = new EMAExpandedComponentInstanceKind();

    protected ComponentSymbolReference type;
    protected List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
    protected List<ResolutionDeclarationSymbol> resolutionDeclarationSymbols;
    protected List<EMAVariable> parameters = new ArrayList<>();
    protected List<ASTExpression> arguments = new ArrayList<>();

    /**
     * use {@link #builder()}
     */
    protected ExpandedComponentInstanceSymbol(String name, ComponentSymbolReference type) {
        super(name, KIND);
        this.type = type;
    }

    public static ExpandedComponentInstanceBuilder builder() {
        return new ExpandedComponentInstanceBuilder();
    }

    /**
     * this is only needed as temp variable to derive generics
     */
    @Deprecated
    public List<ActualTypeArgument> getActualTypeArguments() {
        return actualTypeArguments;
    }

    public void setActualTypeArguments(List<ActualTypeArgument> actualTypeArguments) {
        this.actualTypeArguments = actualTypeArguments;
    }

    public ComponentSymbolReference getComponentType() {
        return type;
    }

    public boolean hasPorts() {
        return !getPorts().isEmpty();
    }

    public List<ResolutionDeclarationSymbol> getResolutionDeclarationSymbols() {
        return resolutionDeclarationSymbols;
    }

    public Optional<InstanceInformation> getInstanceInformation() {
        return InstancingRegister.getInstanceInformation(getName());
    }

    public void fixWrongPortsInInstances() {
        for (ExpandedComponentInstanceSymbol instanceSymbol : getSubComponents()) {
            instanceSymbol.fixWrongPortsInInstances();

        }
        MutableScope scope = getSpannedScope().getAsMutableScope();
        Log.debug(toString(), "Current Instance");
        //TODO fix to work for more than one arguments

        for (PortSymbol portSymbolMain : getPorts()) {
            int counter = 1;
            InstanceInformation info = InstancingRegister.getInstanceInformation(getName()).orElse(null);
            int number = -1;
            if (info != null) {
                Log.debug(info.getInstanceNumberForArgumentIndex(0) + "", "Instance Information");
                //number = info.getInstanceNumberForArgumentIndex(0);
                number = info.getInstanceNumberForPortName(portSymbolMain.getNameWithoutArrayBracketPart());
            } else {
                Log.info("No instance information for " + portSymbolMain.getName(), "Missing:");
            }
            for (PortSymbol portSymbol : getPorts()) {
                if (portSymbol.getName().startsWith(portSymbolMain.getNameWithoutArrayBracketPart() + "[") && portSymbol.isPartOfPortArray()) {
                    if (number > -1 && counter > number) {
                        scope.remove(portSymbol);
                        Log.info(portSymbol.getName(), "Removed:");
                    }
                    ++counter;
                }
            }
            for (int i = 1; i <= number; ++i) {
                if (!getPort(portSymbolMain.getNameWithoutArrayBracketPart() + "[" + i + "]").isPresent()) {
                    PortSymbol portSymbolNew = new PortSymbol(portSymbolMain.getNameWithoutArrayBracketPart() + "[" + i + "]");
                    portSymbolNew.setTypeReference(portSymbolMain.getTypeReference());
                    portSymbolNew.setNameDependsOn(portSymbolMain.getNameDependsOn());
                    portSymbolNew.setDirection(portSymbolMain.isIncoming());

                    scope.add(portSymbolNew);
                }
            }
        }
    }

    /**
     * ExpandedComponentInstanceSymbol::getPorts() may return different
     * results than ComponentSymbol::getPorts()
     * "MontiArc provides a structural inheritance mechanism that allows to define a component as
     * an extension of another component type (see requirement LRQ1.1.1). The new type inherits the
     * interface as well as the architectural configuration from the supercomponent. Thus, all ports,
     * inner component type definitions, subcomponents, and connectors are inherited." (p. 42, Ph.D. AH)
     */
    public Collection<PortSymbol> getPorts() {
        return getSpannedScope().<PortSymbol>resolveLocally(PortSymbol.KIND);
    }

    public Collection<PortArraySymbol> getPortArrays() {
        return getSpannedScope().<PortArraySymbol>resolveLocally(PortArraySymbol.KIND);
    }

    public Optional<PortSymbol> getPort(String name) {
        return getSpannedScope().resolveLocally(name, PortSymbol.KIND);
    }

    public Collection<PortSymbol> getIncomingPorts() {
        return getPorts().stream().filter(PortSymbol::isIncoming).collect(Collectors.toList());
    }

    public Optional<PortSymbol> getIncomingPort(String name) {
        // no check for reference required
        return getIncomingPorts().stream().filter(p -> p.getName().equals(name)).findFirst();
    }

    public Collection<PortSymbol> getOutgoingPorts() {
        return getPorts().stream().filter(PortSymbol::isOutgoing).collect(Collectors.toList());
    }

    public Optional<PortSymbol> getOutgoingPort(String name) {
        // no check for reference required
        return getOutgoingPorts().stream().filter(p -> p.getName().equals(name)).findFirst();
    }

    /**
     * ExpandedComponentInstanceSymbol::getSubComponents() may return different
     * results than the union of ComponentSymbol::getSubComponents() and
     * ComponentSymbol::getInnerComponents.
     * "MontiArc provides a structural inheritance mechanism that allows to define a component as
     * an extension of another component type (see requirement LRQ1.1.1). The new type inherits the
     * interface as well as the architectural configuration from the supercomponent. Thus, all ports,
     * inner component type definitions, subcomponents, and connectors are inherited." (p. 42, Ph.D. AH)
     */
    public Collection<ExpandedComponentInstanceSymbol> getSubComponents() {
        return getSpannedScope().<ExpandedComponentInstanceSymbol>resolveLocally(ExpandedComponentInstanceSymbol.KIND);
    }

    public Optional<ExpandedComponentInstanceSymbol> getSubComponent(String name) {
        return getSpannedScope().<ExpandedComponentInstanceSymbol>resolveLocally(name, ExpandedComponentInstanceSymbol.KIND);
    }

    /**
     * ExpandedComponentInstanceSymbol::getPorts() may return different
     * results than ComponentSymbol::getPorts()
     * "MontiArc provides a structural inheritance mechanism that allows to define a component as
     * an extension of another component type (see requirement LRQ1.1.1). The new type inherits the
     * interface as well as the architectural configuration from the supercomponent. Thus, all ports,
     * inner component type definitions, subcomponents, and connectors are inherited." (p. 42, Ph.D. AH)
     */
    public Collection<ConnectorSymbol> getConnectors() {
        return getSpannedScope().<ConnectorSymbol>resolveLocally(ConnectorSymbol.KIND);
    }

    @Override
    public String toString() {
        return SymbolPrinter.printExpandedComponentInstance(this);
    }
    /*
    @Override
    public void addTag(TagSymbol tag) {
        Map localSymbols = this.getMutableSpannedScope().getLocalSymbols();
        if(localSymbols.get(tag.getName()) == null || !((Collection)localSymbols.get(tag.getName())).contains(tag)) {
            Log.info(this.getMutableSpannedScope().toString(),"Scope Before Add :");
            this.getMutableSpannedScope().add(tag);
            Log.info(this.getMutableSpannedScope().toString(),"Scope After Add :");
            Log.info("size: "+getTags((TagKind) tag.getKind()).size()+" "+getTags().contains(tag)+"","Contains Added Tag:");
        }else{
            Log.info(tag.getName(),"Tag was not added:");
        }

    }*/

    public boolean containsPort(PortSymbol portSymbol) {
        for (PortSymbol symbol : getPorts())
            if (symbol.equals(portSymbol))
                return true;
        return false;
    }

    /**
     * returns the connectors which connect an in or output port of a subcomponent of this component
     * to another subcomponent's port
     *
     * @return
     */
    public List<ConnectorSymbol> getSubComponentConnectors() {
        List<ConnectorSymbol> list = new ArrayList<>();

        for (ConnectorSymbol connectorSymbol : getConnectors()) {
            for (ExpandedComponentInstanceSymbol symbol : getSubComponents()) {
                for (ExpandedComponentInstanceSymbol symbol2 : getSubComponents()) {
                    if (symbol.containsPort(connectorSymbol.getSourcePort())
                            && symbol2.containsPort(connectorSymbol.getTargetPort())) {
                        if (!list.contains(connectorSymbol))
                            list.add(connectorSymbol);
                    }
                }
            }
        }
        return list;
    }


    public List<ExpandedComponentInstanceSymbol> getIndependentSubComponents() {
        List<ExpandedComponentInstanceSymbol> instances = new ArrayList<>();
        for (ExpandedComponentInstanceSymbol symbol : getSubComponents()) {
            boolean noInputDependency = true;
            for (PortSymbol portSymbol : symbol.getOutgoingPorts()) {
                for (ConnectorSymbol connectorSymbol : getSubComponentConnectors()) {
                    if (portSymbol.equals(connectorSymbol.getSourcePort()) || portSymbol.equals(connectorSymbol.getTargetPort())) {
                        noInputDependency = false;
                    }
                }
            }
            if (noInputDependency) {
                instances.add(symbol);
            }


        }
        return instances;
    }

    public boolean isTemplateComponent() {
        return getActualTypeArguments().size() > 0;
    }

    public int getUnitNumberResolutionSubComponents(String name) {
        ASTUnitNumberResolution unitNumberResolution = (ASTUnitNumberResolution) getSubComponents().iterator().next().getComponentType().getReferencedSymbol().getResolutionDeclarationSymbol(name).get().getASTResolution();

        return unitNumberResolution.getNumber().get().intValue();
    }

    public List<ResolutionDeclarationSymbol> getResolutionDeclarationsSubComponent(String name) {
        return getSubComponent(name).get().getComponentType().getReferencedSymbol().getResolutionDeclarationSymbols();
    }

    public void setResolutionDeclarationSymbols(List<ResolutionDeclarationSymbol> resolutionDeclarationSymbols) {
        this.resolutionDeclarationSymbols = resolutionDeclarationSymbols;
    }

    public boolean isSubComponent(String name) {
        for (ExpandedComponentInstanceSymbol subComponent : getSubComponents()) {
            if (subComponent.getFullName().equals(name))
                return true;
        }
        return false;
    }


    public List<EMAVariable> getParameters() {
        return parameters;
    }

    public void setParameters(List<EMAVariable> parameters) {
        this.parameters = parameters;
    }

    public List<ASTExpression> getArguments() {
        return arguments;
    }

    public void setArguments(List<ASTExpression> arguments) {
        this.arguments = arguments;
    }
}
