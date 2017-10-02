/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael von Wenckstern on 23.05.2016.
 *
 * @author Michael von Wenckstern
 */
public class EmbeddedMontiArcExpandedComponentInstanceSymbolCreator {

    protected LinkedHashSet<ComponentSymbol> topComponents = new LinkedHashSet<>();

    public EmbeddedMontiArcExpandedComponentInstanceSymbolCreator() {
    }

    public static Scope getGlobalScope(final Scope scope) {
        Scope s = scope;
        while (s.getEnclosingScope().isPresent()) {
            s = s.getEnclosingScope().get();
        }
        return s;
    }

    /**
     * @param topComponent this is the scope where the top-level component is defined in
     */
    public void createInstances(ComponentSymbol topComponent) {
        if (getGlobalScope(topComponent.getSpannedScope()).resolveDown(
                Joiners.DOT.join(topComponent.getPackageName(), Character.toLowerCase(topComponent.getName().charAt(0)) +
                        topComponent.getName().substring(1)), ExpandedComponentInstanceSymbol.KIND).isPresent()) {
//    if (!topComponents.add(topComponent)) {
            System.out.println("instances for top component + " + topComponent.getFullName() +
                    " is already created");
            Log.info("instances for top component + " + topComponent.getFullName() +
                            " is already created",
                    EmbeddedMontiArcExpandedComponentInstanceSymbolCreator.class.toString());
            return;
        }

        if (!topComponent.getFormalTypeParameters().isEmpty()) {
            Log.info("expanded component instance is not created, b/c top level has"
                            + " generic parameters and can, therefore, not be instantiated",
                    EmbeddedMontiArcExpandedComponentInstanceSymbolCreator.class.toString());
            return;
        }

        final Set<ResolvingFilter<? extends Symbol>> filters =
                topComponent.getSpannedScope().getResolvingFilters();

        // make first letter to lower case
        // this is needed so that you can differentiate between ComponentDefinition.Port
        // and between ComponentInstance.Port (ComponentInstance has small letter and
        // ComponentDefinition has capital letter)
        String name = topComponent.getName();
        if (name.length() > 1) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        } else {
            name = Character.toLowerCase(name.charAt(0)) + "";
        }

        ExpandedComponentInstanceBuilder builder =
                createInstance(topComponent, filters, null)
                        .setName(name);

        final ExpandedComponentInstanceSymbol sym = builder.addResolvingFilters(filters).build();
        ((MutableScope) topComponent.getEnclosingScope()).add(sym);
    }

    protected ExpandedComponentInstanceBuilder createInstance(ComponentSymbol cmp, final Set<ResolvingFilter<? extends Symbol>> filters, List<ResolutionDeclarationSymbol> resolutionDeclarationSymbols) {
        // TODO resolve generics and parameters
        //    System.err.println("create instance for: " + cmp.getName() + " [" + cmp.getFullName() + "]");
        ExpandedComponentInstanceBuilder builder =
                ExpandedComponentInstanceSymbol.builder()
                        .setSymbolReference(new ComponentSymbolReference(cmp.getName(),
                                cmp.getEnclosingScope()))
                        .addPorts(cmp.getPorts())
                        .addConnectors(cmp.getConnectors()).addResolutionDeclarationSymbols(cmp.getResolutionDeclarationSymbols()).addParameters(cmp.getParameters()).addArguments(cmp.getArguments());
        for (ConnectorSymbol connectorSymbol : cmp.getConnectors())
            Log.info(connectorSymbol.toString(), "Building Connector:");
        // add sub components
        for (ComponentInstanceSymbol inst : cmp.getSubComponents()) {
            //      System.err.println("would create now: " + inst.getName() + "[" + inst.getComponentType().getFullName() + "]");
            Log.info(inst.getComponentType().getReferencedSymbol().howManyResolutionDeclarationSymbol() + "", "Important:");
            Log.debug(inst.toString(), "ComponentInstance CreateInstance PreSub");
            builder.addSubComponent(
                    createInstance(inst.getComponentType(), filters, inst.getComponentType().getReferencedSymbol().getResolutionDeclarationSymbols())
                            .setName(inst.getName())
                            .addActualTypeArguments(inst.getComponentType().getFormalTypeParameters(),
                                    inst.getComponentType().getActualTypeArguments()).addResolvingFilters(filters).addResolutionDeclarationSymbols(inst.getComponentType().getReferencedSymbol().getResolutionDeclarationSymbols()).addParameters(inst.getComponentType().getReferencedSymbol().getParameters()).build());
            Log.debug(inst.getInstanceInformation().get().getInstanceNumberForArgumentIndex(0) + "", "InstanceInformation:");

            Log.debug(inst.toString(), "ComponentInstance CreateInstance PostSub");
        }

        // add inherited ports and sub components
        for (ComponentSymbol superCmp = cmp;
             superCmp.getSuperComponent().isPresent();
             superCmp = superCmp.getSuperComponent().get()) {

            if (superCmp.getSuperComponent().get().getFormalTypeParameters().size() !=
                    superCmp.getSuperComponent().get().getActualTypeArguments().size()) {
                Log.error(String.format("Super component '%s' definition has %d generic parameters, but its"
                                + "instantiation has %d binds generic parameters", superCmp.getFullName(),
                        superCmp.getSuperComponent().get().getFormalTypeParameters().size(),
                        superCmp.getSuperComponent().get().getActualTypeArguments().size()));
                return null;
            }

            builder.addPortsIfNameDoesNotExists(
                    superCmp.getSuperComponent().get().getPorts(),
                    superCmp.getSuperComponent().get().getFormalTypeParameters(),
                    superCmp.getSuperComponent().get().getActualTypeArguments());
            builder.addConnectorsIfNameDoesNotExists(superCmp.getSuperComponent().get().getConnectors());
            //Log.debug(superCmp.toString(), "superCmp pre lambda");
            superCmp.getSuperComponent().get().getSubComponents().stream().forEachOrdered(
                    inst -> builder.addSubComponentIfNameDoesNotExists(
                            createInstance(inst.getComponentType(), filters, null).setName(inst.getName())
                                    .addActualTypeArguments(inst.getComponentType().getFormalTypeParameters(),
                                            inst.getComponentType().getActualTypeArguments())
                                    .addResolvingFilters(filters).addResolutionDeclarationSymbols(inst.getComponentType().getReferencedSymbol().getResolutionDeclarationSymbols()).addParameters(inst.getComponentType().getReferencedSymbol().getParameters()).build())

            );
            //Log.debug(superCmp.toString(), "superCmp post lambda");

        }

        return builder;
    }

}
