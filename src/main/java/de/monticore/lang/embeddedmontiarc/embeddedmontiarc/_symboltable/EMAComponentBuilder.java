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

import de.monticore.lang.embeddedmontiarc.helper.SymbolPrinter;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.lang.monticar.ts.MCFieldSymbol;
import de.monticore.lang.monticar.ts.MCTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;

/**
 * Created by Michael von Wenckstern on 13.06.2016.
 *
 * @author Michael von Wenckstern
 *         This class allows to modify {@see ComponentSymbol},
 *         if you do so the symbol table may not be consistent.
 *         Especially you need to call {@see EmbeddedMontiArcExpandedComponentInstanceSymbolCreator#createInstances}
 *         TODO static methods should call a protected doMethod() to allow extending this class
 *         TODO the builder should also be used to create a new ComponentSymbol with a build() method
 */
public class EMAComponentBuilder extends de.monticore.lang.montiarc.montiarc._symboltable.ComponentBuilder {
    protected static EMAComponentBuilder instance = null;

    protected static EMAComponentBuilder getInstance() {
        if (instance == null) {
            instance = new EMAComponentBuilder();
        }
        return instance;
    }

    public EMAComponentBuilder() {
    }

    private static final ResolvingFilter<PortSymbol> portResolvingFilter =
            CommonResolvingFilter.create(PortSymbol.KIND);

    private static final ResolvingFilter<ConnectorSymbol> connectorResolvingFilter =
            CommonResolvingFilter.create(ConnectorSymbol.KIND);

    private static final ResolvingFilter<ComponentSymbol> componentResolvingFilter =
            CommonResolvingFilter.create(ComponentSymbol.KIND);

    private static final ResolvingFilter<MCTypeSymbol> jTypeSymbolResolvingGilter =
            CommonResolvingFilter.create(MCTypeSymbol.KIND);

    private static final ResolvingFilter<MCFieldSymbol> jAttributeResolvingFilter =
            CommonResolvingFilter.create(MCFieldSymbol.KIND);

    private static final ResolvingFilter<ComponentInstanceSymbol> componentInstanceResolvingFilter =
            CommonResolvingFilter.create(ComponentInstanceSymbol.KIND);

    ////////////////////////// ports //////////////////////////////////////////////

    public static EMAComponentBuilder addPort(ComponentSymbol cs, PortSymbol ps) {
        addResolverIfMissing(cs, portResolvingFilter, ps);
        return getInstance();
    }

    public static void addResolverIfMissing(ComponentSymbol cs, ResolvingFilter resolvingFilter, Symbol symbol) {
        if (!cs.getSpannedScope().getResolvingFilters().contains(resolvingFilter)) {
            ((MutableScope) cs.getSpannedScope()).addResolver(resolvingFilter);
        }
        ((MutableScope) cs.getSpannedScope()).add(symbol);
    }

    public static EMAComponentBuilder addPorts(ComponentSymbol cs, PortSymbol... ps) {
        for (PortSymbol p : ps) {
            addPort(cs, p);
        }
        return getInstance();
    }

    public static EMAComponentBuilder addPorts(ComponentSymbol cs, Collection<PortSymbol> ps) {
        ps.stream().forEachOrdered(p -> addPort(cs, p));
        return getInstance();
    }

    public static EMAComponentBuilder removePort(ComponentSymbol cs, PortSymbol ps) {
        ((MutableScope) cs.getSpannedScope()).remove(ps);
        return getInstance();
    }

    public static EMAComponentBuilder removePorts(ComponentSymbol cs, PortSymbol... ps) {
        for (PortSymbol p : ps) {
            removePort(cs, p);
        }
        return getInstance();
    }

    public static EMAComponentBuilder removePorts(ComponentSymbol cs, Collection<PortSymbol> ps) {
        ps.stream().forEachOrdered(p -> removePort(cs, p));
        return getInstance();
    }

    ////////////////////////// connectors //////////////////////////////////////////////

    public static EMAComponentBuilder addConnector(ComponentSymbol cs, ConnectorSymbol con) {
        addResolverIfMissing(cs, connectorResolvingFilter, con);
        return getInstance();
    }

    public static EMAComponentBuilder addConnectors(ComponentSymbol cs, ConnectorSymbol... con) {
        for (ConnectorSymbol c : con) {
            addConnector(cs, c);
        }
        return getInstance();
    }

    public static EMAComponentBuilder addConnectors(ComponentSymbol cs, Collection<ConnectorSymbol> con) {
        con.stream().forEachOrdered(c -> addConnector(cs, c));
        return getInstance();
    }

    public static EMAComponentBuilder removeConnector(ComponentSymbol cs, ConnectorSymbol con) {
        ((MutableScope) cs.getSpannedScope()).remove(con);
        return getInstance();
    }

    public static EMAComponentBuilder removeConnectors(ComponentSymbol cs, ConnectorSymbol... con) {
        for (ConnectorSymbol c : con) {
            removeConnector(cs, c);
        }
        return getInstance();
    }

    public static EMAComponentBuilder removeConnectors(ComponentSymbol cs, Collection<ConnectorSymbol> con) {
        con.stream().forEachOrdered(c -> removeConnector(cs, c));
        return getInstance();
    }

    ////////////////////////// inner components //////////////////////////////////////////////

    public static EMAComponentBuilder addInnerComponent(ComponentSymbol cs, ComponentSymbol innerComponent) {
        addResolverIfMissing(cs, componentResolvingFilter, innerComponent);
        return getInstance();
    }

    public static EMAComponentBuilder addInnerComponents(ComponentSymbol cs, ComponentSymbol... innerComponent) {
        for (ComponentSymbol c : innerComponent) {
            addInnerComponent(cs, c);
        }
        return getInstance();
    }

    public static EMAComponentBuilder addInnerComponents(ComponentSymbol cs, Collection<ComponentSymbol> innerComponent) {
        innerComponent.stream().forEachOrdered(c -> addInnerComponent(cs, c));
        return getInstance();
    }

    public static EMAComponentBuilder removeInnerComponent(ComponentSymbol cs, ComponentSymbol innerComponent) {
        ((MutableScope) cs.getSpannedScope()).remove(innerComponent);
        return getInstance();
    }

    public static EMAComponentBuilder removeInnerComponents(ComponentSymbol cs, ComponentSymbol... innerComponent) {
        for (ComponentSymbol c : innerComponent) {
            removeInnerComponent(cs, c);
        }
        return getInstance();
    }

    public static EMAComponentBuilder removeInnerComponents(ComponentSymbol cs, Collection<ComponentSymbol> innerComponent) {
        innerComponent.stream().forEachOrdered(c -> removeInnerComponent(cs, c));
        return getInstance();
    }

    ////////////////////////// formal type parameters //////////////////////////////////////////////

    public static EMAComponentBuilder addFormalTypeParameter(ComponentSymbol cs, MCTypeSymbol formalTypeParameter) {
        if (!formalTypeParameter.isFormalTypeParameter()) {
            Log.error(String.format("%s is not a formal type parameter. MCTypeSymbol#isFormalTypeParameter() is false.",
                    SymbolPrinter.printFormalTypeParameters(formalTypeParameter)));
        }
        addResolverIfMissing(cs, jTypeSymbolResolvingGilter, formalTypeParameter);
        return getInstance();
    }

    public static EMAComponentBuilder addFormalTypeParameters(ComponentSymbol cs, MCTypeSymbol... formalTypeParameter) {
        for (MCTypeSymbol f : formalTypeParameter) {
            addFormalTypeParameter(cs, f);
        }
        return getInstance();
    }

    public static EMAComponentBuilder addFormalTypeParameters(ComponentSymbol cs, Collection<MCTypeSymbol> formalTypeParameter) {
        formalTypeParameter.stream().forEachOrdered(f -> addFormalTypeParameter(cs, f));
        return getInstance();
    }

    public static EMAComponentBuilder removeFormalTypeParameter(ComponentSymbol cs, MCTypeSymbol formalTypeParameter) {
        ((MutableScope) cs.getSpannedScope()).remove(formalTypeParameter);
        return getInstance();
    }

    public static EMAComponentBuilder removeFormalTypeParameters(ComponentSymbol cs, MCTypeSymbol... formalTypeParameter) {
        for (MCTypeSymbol f : formalTypeParameter) {
            removeFormalTypeParameter(cs, f);
        }
        return getInstance();
    }

    public static EMAComponentBuilder removeFormalTypeParameters(ComponentSymbol cs, Collection<MCTypeSymbol> formalTypeParameter) {
        formalTypeParameter.stream().forEachOrdered(f -> removeFormalTypeParameter(cs, f));
        return getInstance();
    }

    ////////////////////////// config parameters //////////////////////////////////////////////

    public static EMAComponentBuilder addConfigParameter(ComponentSymbol cs, MCFieldSymbol configParameter) {
        addResolverIfMissing(cs, jAttributeResolvingFilter, configParameter);
        return getInstance();
    }

    public static EMAComponentBuilder addConfigParameters(ComponentSymbol cs, MCFieldSymbol... configParameter) {
        for (MCFieldSymbol c : configParameter) {
            addConfigParameter(cs, c);
        }
        return getInstance();
    }

    public static EMAComponentBuilder addConfigParameters(ComponentSymbol cs, Collection<MCFieldSymbol> configParameter) {
        configParameter.stream().forEachOrdered(c -> addConfigParameter(cs, c));
        return getInstance();
    }

    public static EMAComponentBuilder removeConfigParameter(ComponentSymbol cs, MCFieldSymbol configParameter) {
        ((MutableScope) cs.getSpannedScope()).remove(configParameter);
        return getInstance();
    }

    public static EMAComponentBuilder removeConfigParameters(ComponentSymbol cs, MCFieldSymbol... configParameter) {
        for (MCFieldSymbol c : configParameter) {
            removeConfigParameter(cs, c);
        }
        return getInstance();
    }

    public static EMAComponentBuilder removeConfigParameters(ComponentSymbol cs, Collection<MCFieldSymbol> configParameter) {
        configParameter.stream().forEachOrdered(c -> removeConfigParameter(cs, c));
        return getInstance();
    }

    ////////////////////////// sub components //////////////////////////////////////////////

    public static EMAComponentBuilder addSubComponent(ComponentSymbol cs, ComponentInstanceSymbol subComponent) {
        addResolverIfMissing(cs, componentInstanceResolvingFilter, subComponent);
        return getInstance();
    }

    public static EMAComponentBuilder addSubComponents(ComponentSymbol cs, ComponentInstanceSymbol... subComponent) {
        for (ComponentInstanceSymbol s : subComponent) {
            addSubComponent(cs, s);
        }
        return getInstance();
    }

    public static EMAComponentBuilder addSubComponents(ComponentSymbol cs, Collection<ComponentInstanceSymbol> subComponent) {
        subComponent.stream().forEachOrdered(s -> addSubComponent(cs, s));
        return getInstance();
    }

    public static EMAComponentBuilder removeSubComponent(ComponentSymbol cs, ComponentInstanceSymbol subComponent) {
        ((MutableScope) cs.getSpannedScope()).remove(subComponent);
        return getInstance();
    }

    public static EMAComponentBuilder removeSubComponents(ComponentSymbol cs, ComponentInstanceSymbol... subComponent) {
        for (ComponentInstanceSymbol s : subComponent) {
            removeSubComponent(cs, s);
        }
        return getInstance();
    }

    public static EMAComponentBuilder removeSubComponents(ComponentSymbol cs, Collection<ComponentInstanceSymbol> subComponent) {
        subComponent.stream().forEachOrdered(s -> removeSubComponent(cs, s));
        return getInstance();
    }

}
