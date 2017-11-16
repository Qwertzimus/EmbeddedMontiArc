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

import com.google.common.collect.ImmutableSet;
import de.monticore.ast.ASTNode;
import de.monticore.lang.monticar.ts.MontiCarTypeSymbol;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.types2._symboltable.UnitNumberResolutionSymbol;
import de.monticore.lang.monticar.si._symboltable.SIUnitRangesSymbol;
import de.monticore.lang.monticar.si._symboltable.SIUnitSymbol;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.lang.monticar.ts.MCFieldSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.lang.monticar.ts.MCTypeSymbol;

import java.util.LinkedHashSet;

/**
 * The MontiArc Language
 *
 * @author Robert Heim, Michael von Wenckstern
 */
public class EmbeddedMontiArcLanguage extends EmbeddedMontiArcLanguageTOP {

    public static final String FILE_ENDING = "ema";

    public EmbeddedMontiArcLanguage() {
        super("Embedded MontiArc Language", FILE_ENDING);
    }

    @Override
    protected void initResolvingFilters() {
        super.initResolvingFilters();
        // is done in generated TOP-language addResolver(new
        // CommonResolvingFilter<ComponentSymbol>(ComponentSymbol.class, ComponentSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(ComponentInstanceSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(PortSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(PortArraySymbol.KIND));
        addResolvingFilter(new EMAConnectorResolvingFilter<>(ConnectorSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(ExpandedComponentInstanceSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(SIUnitSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(SIUnitRangesSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(MCTypeSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(MCFieldSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(JMethodSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(ResolutionDeclarationSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(UnitNumberResolutionSymbol.KIND));
        //addResolvingFilter(CommonResolvingFilter.create(ComponentKind.KIND));
        //addResolvingFilter(CommonResolvingFilter.create(TagSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(MontiCarTypeSymbol.KIND));
        setModelNameCalculator(new EmbeddedMontiArcModelNameCalculator());
    }

    /**
     * @see de.monticore.CommonModelingLanguage#provideModelLoader()
     */
    @Override
    protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
        return new EmbeddedMontiArcModelLoader(this);
    }
}
