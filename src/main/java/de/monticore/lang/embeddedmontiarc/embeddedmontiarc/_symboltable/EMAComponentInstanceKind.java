/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

/**
 * Symbol kind of component instances.
 *
 * @author Robert Heim
 */
public class EMAComponentInstanceKind
  extends de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceKind
    implements de.monticore.symboltable.SymbolKind {

  public static final EMAComponentInstanceKind INSTANCE = new EMAComponentInstanceKind();

  protected EMAComponentInstanceKind() {
  }

}
