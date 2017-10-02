/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.monticar.ValueSymbol;
import de.monticore.lang.embeddedmontiarc.helper.SymbolPrinter;
import de.monticore.lang.montiarc.tagging._symboltable.TaggingScopeSpanningSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.TypeReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Represents an instance of a component.
 *
 * @author Robert Heim
 */
public class ComponentInstanceSymbol extends TaggingScopeSpanningSymbol {

    public static final EMAComponentInstanceKind KIND = EMAComponentInstanceKind.INSTANCE;

    private final ComponentSymbolReference componentType;

    /**
     * List of configuration arguments.
     */
    private List<ValueSymbol<TypeReference<TypeSymbol>>> configArgs = new ArrayList<>();

    private String value = "";

    /**
     * Constructor for de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentInstanceSymbol
     *
     * @param name
     * @param componentType the referenced component definition
     */
    public ComponentInstanceSymbol(String name, ComponentSymbolReference componentType) {
        super(name, KIND);
        this.componentType = componentType;

    }

    /**
     * @return componentType
     */
    public ComponentSymbolReference getComponentType() {
        return this.componentType;
    }

    /**
     * @return connectors of this component
     */
    public Collection<ConnectorSymbol> getSimpleConnectors() {
        return getSpannedScope().<ConnectorSymbol>resolveLocally(ConnectorSymbol.KIND);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return List of configuration arguments
     */
    public List<ValueSymbol<TypeReference<TypeSymbol>>> getConfigArguments() {
        return this.configArgs;
    }

    /**
     * @param cfg configuration arguments to add
     */
    public void addConfigArgument(ValueSymbol<TypeReference<TypeSymbol>> cfg) {
        this.configArgs.add(cfg);
    }

    /**
     * @param cfgList configuration arguments to set
     */
    public void setConfigArgs(List<ValueSymbol<TypeReference<TypeSymbol>>> configArgs) {
        this.configArgs = configArgs;
    }

    @Override
    public String toString() {
        return SymbolPrinter.printComponentInstance(this);
    }

    public Optional<InstanceInformation> getInstanceInformation(){
        for(InstanceInformation instanceInformation:InstancingRegister.instanceInformation){
            if(instanceInformation.getCompName().equals(getName())){
                return Optional.of(instanceInformation);
            }
        }
        return Optional.empty();
    }
}
