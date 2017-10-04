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
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;
import de.monticore.lang.monticar.si._symboltable.ResolutionDeclarationSymbol;
import de.monticore.lang.monticar.types2._symboltable.UnitNumberResolutionSymbol;
import de.monticore.lang.monticar.si._symboltable.SIUnitRangesSymbol;
import de.monticore.lang.monticar.si._symboltable.SIUnitSymbol;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbolCreator;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;

import java.util.LinkedHashSet;

/**
 * The MontiArc Language
 *
 * @author Robert Heim, Michael von Wenckstern
 */
public class EmbeddedMontiArcLanguage extends EmbeddedMontiArcLanguageTOP implements TagableModelingLanguage {

    public static final String FILE_ENDING = "ema";

    protected LinkedHashSet<TagSymbolCreator> tagSymbolCreators = new LinkedHashSet<>();

    public EmbeddedMontiArcLanguage() {
        super("Embedded MontiArc Language", FILE_ENDING);
    }

    @Override
    protected void initResolvingFilters() {
        super.initResolvingFilters();
        // is done in generated TOP-language addResolver(new
        // CommonResolvingFilter<ComponentSymbol>(ComponentSymbol.class, ComponentSymbol.KIND));
        addResolver(new CommonResolvingFilter<>(ComponentInstanceSymbol.KIND));
        addResolver(new CommonResolvingFilter<>(PortSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(PortArraySymbol.KIND));
        addResolver(new EMAConnectorResolvingFilter<>(ConnectorSymbol.KIND));
        addResolver(new CommonResolvingFilter<>(ExpandedComponentInstanceSymbol.KIND));
        addResolver(new CommonResolvingFilter<>(SIUnitSymbol.KIND));
        addResolver(new CommonResolvingFilter<>(SIUnitRangesSymbol.KIND));
        addResolver(new CommonResolvingFilter<>(JTypeSymbol.KIND));
        addResolver(new CommonResolvingFilter<>(JFieldSymbol.KIND));
        addResolver(new CommonResolvingFilter<>(JMethodSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(ResolutionDeclarationSymbol.KIND));
        addResolvingFilter(CommonResolvingFilter.create(UnitNumberResolutionSymbol.KIND));
        //addResolvingFilter(CommonResolvingFilter.create(ComponentKind.KIND));
        //addResolvingFilter(CommonResolvingFilter.create(TagSymbol.KIND));
        setModelNameCalculator(new EmbeddedMontiArcModelNameCalculator());
    }

    public void addTagSymbolCreator(TagSymbolCreator tagSymbolCreator) {
        this.tagSymbolCreators.add(tagSymbolCreator);
    }

    public ImmutableSet<TagSymbolCreator> getTagSymbolCreators() {
        return ImmutableSet.copyOf(this.tagSymbolCreators);
    }

    /**
     * @see de.monticore.CommonModelingLanguage#provideModelLoader()
     */
    @Override
    protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
        return new EmbeddedMontiArcModelLoader(this);
    }
}
