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

import de.monticore.symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.names.CommonQualifiedNamesCalculator;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by Michael von Wenckstern on 23.05.2016.
 * this is only a hack, it must be fixed in the SymbolTable
 *
 * @see <a href="https://sselab.de/lab2/private/trac/MontiArc4/ticket/36">Ticket 36</a>
 */
public class EmbeddedMontiArcArtifactScope extends de.monticore.lang.montiarc.montiarc._symboltable.MontiArcArtifactScope {

  private CommonQualifiedNamesCalculator qualifiedNamesCalculator = new CommonQualifiedNamesCalculator();

  public EmbeddedMontiArcArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public EmbeddedMontiArcArtifactScope(Optional<MutableScope> enclosingScope, String packageName, List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }

  @Override
  protected <T extends Symbol> Collection<T> continueWithEnclosingScope(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    final Collection<T> result = new LinkedHashSet<>();

    if (checkIfContinueWithEnclosing(resolvingInfo.areSymbolsFound()) && (getEnclosingScope().isPresent())) {
      if (!(enclosingScope instanceof GlobalScope)) {
        Log.warn("0xA1039 An artifact scope should have the global scope as enclosing scope or no "
            + "enclosing scope at all.");
      }

      final Set<String> potentialQualifiedNames = qualifiedNamesCalculator.calculateQualifiedNames(name, getPackageName(), getImports());

      for (final String potentialQualifiedName : potentialQualifiedNames) {
        final Collection<T> resolvedFromEnclosing = enclosingScope.resolveMany(resolvingInfo, potentialQualifiedName, kind, modifier, predicate);

        if (!resolvedFromEnclosing.isEmpty()) {
          return resolvedFromEnclosing;
        }

        result.addAll(resolvedFromEnclosing);
      }
    }
    return result;
  }
}
