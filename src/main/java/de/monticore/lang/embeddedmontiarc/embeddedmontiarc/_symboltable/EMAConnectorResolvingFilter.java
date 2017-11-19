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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingInfo;

/**
 * Created by MichaelvonWenckstern on 02.09.2016.
 */
public class EMAConnectorResolvingFilter<S extends Symbol>
        extends CommonResolvingFilter<S> {

  public EMAConnectorResolvingFilter(SymbolKind targetKind) {
    super(targetKind);
  }

  @Override
  public Optional<Symbol> filter(ResolvingInfo resolvingInfo, String name, Map<String, Collection<Symbol>> symbols) {
    final Set<Symbol> resolvedSymbols = new LinkedHashSet<>();


    final Collection<Symbol> allSymbols = getSymbolsAsCollection(symbols);


    for (Symbol symbol : allSymbols) {
      if (symbol.isKindOf(getTargetKind())) {

        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add(symbol);
        }
      }
    }

    return ResolvingFilter.getResolvedOrThrowException(resolvedSymbols);
  }

  private Collection<Symbol> getSymbolsAsCollection(Map<String, Collection<Symbol>> symbols) {
    final Collection<Symbol> r = new LinkedHashSet<>();
    symbols.values().forEach(r::addAll);
    return r;
  }
}
