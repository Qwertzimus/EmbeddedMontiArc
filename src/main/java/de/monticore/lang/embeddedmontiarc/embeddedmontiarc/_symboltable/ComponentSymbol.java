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

import com.google.common.collect.ImmutableList;
import de.monticore.lang.embeddedmontiarc.EmbeddedMontiArcConstants;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types.EMAVariable;
import de.monticore.lang.embeddedmontiarc.helper.SymbolPrinter;
import de.monticore.lang.embeddedmontiarc.helper.Timing;
import de.monticore.lang.montiarc.tagging._symboltable.TaggingScopeSpanningSymbol;
import de.monticore.lang.monticar.common2._ast.ASTParameter;
import de.monticore.lang.monticar.mcexpressions._ast.ASTExpression;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

/**
 * Symbol for component definitions.
 *
 * @author Robert Heim
 */
public class ComponentSymbol extends TaggingScopeSpanningSymbol {

    public static final ComponentKind KIND = new ComponentKind();
    private final List<EMAAComponentImplementationSymbol> implementations = new ArrayList<>();
    private final Map<String, Optional<String>> stereotype = new HashMap<>();
    private boolean isInnerComponent = false;

    private Optional<ComponentSymbolReference> superComponent = Optional.empty();
    private Timing timing = EmbeddedMontiArcConstants.DEFAULT_TIME_PARADIGM;
    private boolean delayed = false;
    // when "this" not actually is a component, but a reference to a component, this optional
    // attribute is set by the symbol-table creator to the referenced component and must be used for
    // implementation.
    private Optional<ComponentSymbol> referencedComponent = Optional.empty();
    private List<ImportStatement> imports = new ArrayList<>();
    private List<ResolutionDeclarationSymbol> resolutionDeclarationSymbols = new ArrayList<>();
    private List<EMAVariable> parameters = new ArrayList<>();
    private List<ASTExpression> arguments = new ArrayList<>();

    public ComponentSymbol(String name) {
        super(name, KIND);
    }

    public ComponentSymbol(String name, SymbolKind kind) {
        super(name, kind);
    }

    /**
     * @return referencedComponent
     */
    public Optional<ComponentSymbol> getReferencedComponent() {
        return this.referencedComponent;
    }

    /**
     * @param referencedComponent the referencedComponent to set
     */
    public void setReferencedComponent(Optional<ComponentSymbol> referencedComponent) {
        //to fix port instancing
        this.referencedComponent = referencedComponent;
    }

    /**
     * @param parameterType configuration parameter to add
     */
    public void addConfigParameter(JFieldSymbol parameterType) {
        if (referencedComponent.isPresent())
            referencedComponent.get().addConfigParameter(parameterType);
        else {
            Log.errorIfNull(parameterType);
            checkArgument(parameterType.isParameter(), "Only parameters can be added.");
            getMutableSpannedScope().add(parameterType);
        }
    }

    /**
     * @param target target of the connector to get
     * @return a connector with the given target, absent optional, if it does not exist
     */
    public Optional<ConnectorSymbol> getConnector(String target) {
        // no check for reference required
        for (ConnectorSymbol con : getConnectors()) {
            if (con.getTarget().equals(target)) {
                return Optional.of(con);
            }
        }
        return Optional.empty();
    }

    /**
     * @return connectors of this component
     */
    public Collection<ConnectorSymbol> getConnectors() {
        Collection<ConnectorSymbol> c = referencedComponent.orElse(this)
                .getSpannedScope().<ConnectorSymbol>resolveLocally(ConnectorSymbol.KIND);

        return c.stream().sorted((o1, o2) -> o1.getSourcePosition().compareTo(o2.getSourcePosition())).collect(Collectors.toList());
    }

    /**
     * @param visibility visibility
     * @return connectors with the given visibility
     */
    public Collection<ConnectorSymbol> getConnectors(AccessModifier visibility) {
        // no check for reference required
        return getConnectors().stream()
                .filter(c -> c.getAccessModifier().includes(visibility))
                .collect(Collectors.toList());
    }

    /**
     * Checks, if this component has a connector with the given receiver name.
     *
     * @param receiver name of the receiver to find a connector for
     * @return true, if this component has a connector with the given receiver name, else false.
     */
    public boolean hasConnector(String receiver) {
        // no check for reference required
        return getConnectors().stream()
                .filter(c -> c.getName().equals(receiver))
                .findAny().isPresent();
    }

    /**
     * Checks, if this component has one or more connectors with the given sender.
     *
     * @param sender name of the sender to find a connector for
     * @return true, if this component has one ore more connectors with the given sender name, else
     * false.
     */
    public boolean hasConnectors(String sender) {
        // no check for reference required
        return getConnectors().stream()
                .filter(c -> c.getSource().equals(sender))
                .findAny().isPresent();
    }

    /**
     * @param impl the implementation to add
     */
    public void addImplementation(EMAAComponentImplementationSymbol impl) {
        referencedComponent.orElse(this).implementations.add(impl);
    }

    /**
     * @return implementations
     */
    public List<EMAAComponentImplementationSymbol> getImplementations() {
        return ImmutableList.copyOf(referencedComponent.orElse(this).implementations);
    }

    public Optional<EMAAComponentImplementationSymbol> getImplementation(String name) {
        // no check for reference required
        return getImplementations().stream()
                .filter(i -> i.getName().equals(name))
                .findFirst();
    }

    /**
     * @param visibility visibility
     * @return implementations with the given visibility
     */
    public Collection<EMAAComponentImplementationSymbol> getImplementations(AccessModifier visibility) {
        // no check for reference required
        return getImplementations().stream()
                .filter(s -> s.getAccessModifier().includes(visibility))
                .collect(Collectors.toList());
    }

    /**
     * @return innerComponents
     */
    public Collection<ComponentSymbol> getInnerComponents() {
        return referencedComponent.orElse(this).getSpannedScope()
                .<ComponentSymbol>resolveLocally(ComponentSymbol.KIND);
    }

    /**
     * @param name inner component name
     * @return inner component with the given name, empty Optional, if it does not exist
     */
    public Optional<ComponentSymbol> getInnerComponent(String name) {
        // no check for reference required
        return getInnerComponents().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    /**
     * @param visibility visibility
     * @return inner components with the given visibility
     */
    public Collection<ComponentSymbol> getInnerComponents(AccessModifier visibility) {
        // no check for reference require
        return getInnerComponents().stream()
                .filter(s -> s.getAccessModifier().includes(visibility))
                .collect(Collectors.toList());
    }

    /**
     * @return true, if this is an inner component, else false.
     */
    public boolean isInnerComponent() {
        return referencedComponent.orElse(this).isInnerComponent;
    }

    /**
     * Sets, if this is an inner component or not.
     *
     * @param isInnerComponent true, if this is an inner component
     */
    public void setIsInnerComponent(boolean isInnerComponent) {
        referencedComponent.orElse(this).isInnerComponent = isInnerComponent;
    }

    /**
     * @param typeParameter generic type parameter to add
     */
    public void addFormalTypeParameter(JTypeSymbol formalTypeParameter) {
        if (referencedComponent.isPresent()) {
            referencedComponent.get().addFormalTypeParameter(formalTypeParameter);
        } else {
            checkArgument(formalTypeParameter.isFormalTypeParameter());
            getMutableSpannedScope().add(formalTypeParameter);
        }
    }

    public List<JTypeSymbol> getFormalTypeParameters() {
        final Collection<JTypeSymbol> resolvedTypes =
                referencedComponent.orElse(this).getSpannedScope().resolveLocally(JTypeSymbol.KIND);
        return resolvedTypes.stream().filter(JTypeSymbol::isFormalTypeParameter)
                .collect(Collectors.toList());
    }

    public boolean hasFormalTypeParameters() {
        return !getFormalTypeParameters().isEmpty();
    }

    public boolean hasConfigParameters() {
        return !getConfigParameters().isEmpty();
    }

    public boolean hasPorts() {
        return !getPorts().isEmpty();
    }

    /**
     * Adds the stereotype key=value to this entry's map of stereotypes
     *
     * @param key   the stereotype's key
     * @param value the stereotype's value
     */
    public void addStereotype(String key, @Nullable String value) {
        if (value != null && value.isEmpty()) {
            value = null;
        }
        referencedComponent.orElse(this).stereotype.put(key, Optional.ofNullable(value));
    }

    /**
     * Ports of this component.
     *
     * @return ports of this component.
     */
    public Collection<PortSymbol> getPorts() {
        Collection<PortSymbol> symbols = referencedComponent.orElse(this).getSpannedScope()
                .<PortSymbol>resolveLocally(PortSymbol.KIND);
        /*for (PortSymbol portSymbol : symbols) {
            System.out.println(portSymbol.toString());
        }*/
        return symbols;
    }

    /**
     * Ports of this component.
     *
     * @return ports of this component.
     */
    public Collection<PortArraySymbol> getPortArrays() {
        Collection<PortArraySymbol> symbols = referencedComponent.orElse(this).getSpannedScope()
                .<PortArraySymbol>resolveLocally(PortArraySymbol.KIND);
        return symbols;
    }

    public PortArraySymbol getPortArray(String name) {
        Log.debug(name, "Looking for Pas:");
        for (PortArraySymbol pas : getPortArrays()) {
            Log.debug(pas.getName(), "Cur Pas:");
            if (pas.getName().equals(name)) {
                Log.debug(pas.getName(), "Found Pas");
                return pas;
            }
        }
        return null;
    }

    public boolean isPortDependentOnResolutionDeclarationSymbol(String portName, String nameToDependentOn) {
        PortArraySymbol portArraySymbol = getPortArray(portName);
        Log.debug(portName, "PortName:");
        Log.debug(nameToDependentOn, "Expected NameToDependOn:");
        if (portArraySymbol.getNameSizeDependsOn().isPresent()) {
            Log.debug(portArraySymbol.getNameSizeDependsOn().get(), "Actual NameToDependOn:");
            if (portArraySymbol.getNameSizeDependsOn().get().equals(nameToDependentOn))
                return true;
        }
        return false;
    }

    //Overwrite in other classes
    public List<ActualTypeArgument> getActualTypeArguments() {
        return null;
    }

    /**
     * @return incomingPorts of this component
     */
    public Collection<PortSymbol> getIncomingPorts() {
        return referencedComponent.orElse(this).getSpannedScope()
                .<PortSymbol>resolveLocally(PortSymbol.KIND)
                .stream()
                .filter(p -> p.isIncoming())
                .collect(Collectors.toList());
    }

    /**
     * @param name port name
     * @return incoming port with the given name, empty optional, if it does not exist
     */
    public Optional<PortSymbol> getIncomingPort(String name) {
        // no check for reference required
        return getIncomingPorts().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }

    /**
     * @param visibility
     * @return incoming ports with the given visibility
     */
    public Collection<PortSymbol> getIncomingPorts(AccessModifier visibility) {
        // no check for reference required
        return getIncomingPorts().stream()
                .filter(s -> s.getAccessModifier().includes(visibility))
                .collect(Collectors.toList());
    }

    /**
     * @return outgoingPorts of this component
     */
    public Collection<PortSymbol> getOutgoingPorts() {
        return referencedComponent.orElse(this).getSpannedScope()
                .<PortSymbol>resolveLocally(PortSymbol.KIND)
                .stream()
                .filter(p -> p.isOutgoing())
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all incoming ports that also contains ports from a super component.
     *
     * @param loader        used to load full version of super component (if needed)
     * @param deserializers used to load full version of super component (if needed)
     * @return list of all incoming ports.
     */
    public List<PortSymbol> getAllIncomingPorts() {
        return referencedComponent.orElse(this).getAllPorts(true);
    }

    /**
     * @param name port name
     * @return outgoing port with the given name, empty optional, if it does not exist
     */
    public Optional<PortSymbol> getOutgoingPort(String name) {
        // no check for reference required
        return getOutgoingPorts().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }

    /**
     * @param visibility visibility
     * @return outgoing ports with the given visibility
     */
    public Collection<PortSymbol> getOutgoingPorts(AccessModifier visibility) {
        // no check for reference required
        return getOutgoingPorts().stream()
                .filter(s -> s.getAccessModifier().includes(visibility))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all outgoing ports that also contains ports from a super component.
     *
     * @param loader        used to load full version of super component (if needed)
     * @param deserializers used to load full version of super component (if needed)
     * @return list of all outgoing ports.
     */
    public List<PortSymbol> getAllOutgoingPorts() {
        return referencedComponent.orElse(this).getAllPorts(false);
    }

    protected List<PortSymbol> getAllPorts() {
        List<PortSymbol> result = new ArrayList<PortSymbol>();

        // own ports
        result.addAll(getPorts());

        // ports from super components
        Optional<ComponentSymbolReference> superCompOpt = getSuperComponent();
        if (superCompOpt.isPresent()) {
            for (PortSymbol superPort : superCompOpt.get().getAllPorts()) {
                boolean alreadyAdded = false;
                for (PortSymbol pToAdd : result) {
                    if (pToAdd.getName().equals(superPort.getName())) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    result.add(superPort);
                }
            }
        }
        return result;
    }

    private List<PortSymbol> getAllPorts(boolean isIncoming) {
        return getAllPorts().stream().filter(p -> p.isIncoming() == isIncoming)
                .collect(Collectors.toList());
    }

    /**
     * @return super component of this component, empty optional, if it does not have a super
     * component
     */
    public Optional<ComponentSymbolReference> getSuperComponent() {
        if (referencedComponent.isPresent()) {
            return referencedComponent.get().getSuperComponent();
        } else {
            return superComponent;
        }
    }

    /**
     * @param superComponent the super component to set
     */
    public void setSuperComponent(Optional<ComponentSymbolReference> superComponent) {
        referencedComponent.orElse(this).superComponent = superComponent;
    }

    /**
     * @return subComponents
     */
    public Collection<ComponentInstanceSymbol> getSubComponents() {
        return referencedComponent.orElse(this).getSpannedScope()
                .resolveLocally(ComponentInstanceSymbol.KIND);
    }

    /**
     * @param name subcomponent instance name
     * @return subcomponent with the given name, empty optional, if it does not exist
     */
    public Optional<ComponentInstanceSymbol> getSubComponent(String name) {
        // no check for reference required
        return getSubComponents().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }

    /**
     * @param visibility visibility
     * @return subcomponents with the given visibility
     */
    public Collection<ComponentInstanceSymbol> getSubComponents(AccessModifier visibility) {
        // no check for reference required
        return getSubComponents().stream()
                .filter(s -> s.getAccessModifier().includes(visibility))
                .collect(Collectors.toList());
    }

    /**
     * @return configParameters
     */
    public List<JFieldSymbol> getConfigParameters() {
        if (referencedComponent.isPresent()) {
            return referencedComponent.get().getConfigParameters();
        } else {
            final Collection<JFieldSymbol> resolvedAttributes = getMutableSpannedScope()
                    .resolveLocally(JFieldSymbol.KIND);
            final List<JFieldSymbol> parameters = sortSymbolsByPosition(resolvedAttributes.stream()
                    .filter(JFieldSymbol::isParameter).collect(Collectors.toList()));
            return parameters;
        }
    }

    /**
     * @return List of configuration parameters that are to be set during instantiation with the given
     * visibility
     */
    public Collection<JFieldSymbol> getConfigParameters(AccessModifier visibility) {
        // no need to check for reference, as getParameres() does so.
        return getConfigParameters().stream()
                .filter(s -> s.getAccessModifier().includes(visibility))
                .collect(Collectors.toList());
    }

    /**
     * Sets, if the component has a delay.
     *
     * @param delayed true, if the component has a delay, else false.
     */
    public void setDelayed(boolean delayed) {
        referencedComponent.orElse(this).delayed = delayed;
    }

    /**
     * @return true, if the component has a delay, else false.
     */
    public boolean hasDelay() {
        return referencedComponent.orElse(this).delayed;
    }

    /**
     * Adds the stereotype key=value to this entry's map of stereotypes
     *
     * @param key      the stereotype's key
     * @param optional the stereotype's value
     */
    public void addStereotype(String key, Optional<String> optional) {
        referencedComponent.orElse(this).stereotype.put(key, optional);
    }

    /**
     * @return map representing the stereotype of this component
     */
    public Map<String, Optional<String>> getStereotype() {
        return referencedComponent.orElse(this).stereotype;
    }

    /**
     * @return the timing
     */
    public Timing getBehaviorKind() {
        return referencedComponent.orElse(this).timing;
    }

    /**
     * @param timing the timing to set
     */
    public void setBehaviorKind(Timing behaviorKind) {
        referencedComponent.orElse(this).timing = behaviorKind;
        if (behaviorKind.isDelaying()) {
            referencedComponent.orElse(this).setDelayed(true);
        }
    }

    public boolean isDecomposed() {
        return !isAtomic();
    }

    public boolean isAtomic() {
        return getSubComponents().isEmpty();
    }

    @Override
    public String toString() {
        return SymbolPrinter.printComponent(this);
    }

    /**
     * TODO reuse ArtifactScope? see TODO in {@link #setImports(List)}
     *
     * @return imports
     */
    public List<ImportStatement> getImports() {
        return this.imports;
    }

    /**
     * TODO could we get these somehow from the ArtifactScope? there the imports are private, but we
     * want (some?) imports to be printed in a generated java file, when e.g. aggregated with Java and
     * other Java-types are referenced.
     *
     * @param imports
     */
    public void setImports(List<ImportStatement> imports) {
        this.imports = imports;
    }

    public Optional<ResolutionDeclarationSymbol> getResolutionDeclarationSymbol(String name) {
        for (ResolutionDeclarationSymbol symbol : getResolutionDeclarationSymbols()) {
            if (symbol.getNameToResolve().equals(name))
                return Optional.of(symbol);
        }
        return Optional.empty();
    }

    public boolean hasResolutionDeclaration(String name) {
        for (ResolutionDeclarationSymbol resDeclSym : resolutionDeclarationSymbols)
            if (resDeclSym.getNameToResolve().equals(name)) {
                return true;
            }
        return false;
    }

    public int howManyResolutionDeclarationSymbol() {
        return resolutionDeclarationSymbols.size();
    }

    public void addResolutionDeclarationSymbol(ResolutionDeclarationSymbol resolutionDeclarationSymbol) {
        if (hasResolutionDeclaration(resolutionDeclarationSymbol.getNameToResolve())) {
            Log.error("0x0S0001 Name " + resolutionDeclarationSymbol.getNameToResolve() + " to resolve is a duplicate");
        }
        resolutionDeclarationSymbols.add(resolutionDeclarationSymbol);
        Log.debug(getFullName(), "Added ResolutionDeclarationSymbol to ComponentSymbol with name:");
    }

    public List<ResolutionDeclarationSymbol> getResolutionDeclarationSymbols() {
        return resolutionDeclarationSymbols;
    }

    public static EMAComponentBuilder builder() {
        return EMAComponentBuilder.getInstance();
    }

    public void addParameter(ASTParameter astParameter) {

        if (referencedComponent.isPresent())
            referencedComponent.get().addParameter(astParameter);
        else {
            EMAVariable param = new EMAVariable();
            param.setName(astParameter.getName());
            param.setType(astParameter.getType());
            parameters.add(param);
        }
    }

    public List<EMAVariable> getParameters() {
        return parameters;
    }

    public void setParameters(List<EMAVariable> parameters) {
        this.parameters = parameters;
    }

    public List<ASTExpression> getArguments() {
        if (referencedComponent.isPresent())
            return referencedComponent.get().getArguments();
        return arguments;
    }

    public void addArgument(ASTExpression astExpression) {
        if (referencedComponent.isPresent())
            referencedComponent.get().addArgument(astExpression);
        else
            arguments.add(astExpression);
    }

    public void setArguments(List<ASTExpression> arguments) {
        this.arguments = arguments;
    }

    public void addIncomingPort(PortSymbol symbol) {
        //TODO implement me
    }
}
