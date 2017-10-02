/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EmbeddedMontiArcLanguage;
import de.monticore.lang.monticar.stream._symboltable.StreamLanguage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

import java.nio.file.Paths;

/**
 * Common methods for symboltable tests
 *
 */
public class AbstractSymtabTest {
  protected static Scope createSymTab(String... modelPath) {
    ModelingLanguageFamily fam = new ModelingLanguageFamily();
    fam.addModelingLanguage(new EmbeddedMontiArcLanguage());
    // TODO should we use JavaDSLLanguage or add the resolvers in MALang?
    fam.addModelingLanguage(new JavaDSLLanguage());
    fam.addModelingLanguage(new StreamLanguage());
    // TODO how to add java default types?
    final ModelPath mp = new ModelPath(Paths.get("src/main/resources/defaultTypes"));
    for (String m : modelPath) {
      mp.addEntry(Paths.get(m));
    }
    GlobalScope scope = new GlobalScope(mp, fam);
    return scope;
  }
}
