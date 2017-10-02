/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

/**
 * TODO Do we really need this?
 *
 * @author Robert Heim
 */
public abstract class EMAAComponentImplementationSymbol
        extends de.monticore.lang.montiarc.montiarc._symboltable.AComponentImplementationSymbol {

  public static final EMAAComponentImplementationKind KIND = EMAAComponentImplementationKind.INSTANCE;

  /**
   * Constructor for de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EMAAComponentImplementationSymbol
   *
   * @param name
   */
  public EMAAComponentImplementationSymbol(String name) {
    super(name);
  }

}
