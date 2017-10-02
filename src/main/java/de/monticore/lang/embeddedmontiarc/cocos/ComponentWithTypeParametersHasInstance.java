/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author (last commit) Crispin Kirchner
 */
public class ComponentWithTypeParametersHasInstance
    implements EmbeddedMontiArcASTComponentCoCo {

  /**
   * @see EmbeddedMontiArcASTComponentCoCo#check(ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbol().get();

    Collection<ComponentInstanceSymbol> subComponents = componentSymbol.getSubComponents();

    Set<ComponentSymbol> instantiatedInnerComponents = subComponents
        .stream()
        .map(instanceSymbol -> instanceSymbol.getComponentType().getReferencedSymbol())
        .filter(symbol -> symbol.hasFormalTypeParameters())
        .collect(Collectors.toSet());

    List<ComponentSymbol> notInstantiatedInnerComponents = componentSymbol
        .getInnerComponents()
        .stream()
        .filter(symbol -> symbol.hasFormalTypeParameters())
        .filter(innerComponent -> !instantiatedInnerComponents.contains(innerComponent))
        .collect(Collectors.toList());

    for (ComponentSymbol notInstantiatedInnerComponent : notInstantiatedInnerComponents) {
      Log.error(
          String.format(
              "0x79C00 Inner component \"%s\" must have an instance defining its formal type parameters.",
              notInstantiatedInnerComponent.getName()),
          notInstantiatedInnerComponent.getSourcePosition());
    }
  }
}
