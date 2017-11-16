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

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTEMACompilationUnit;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

/**
 * Created by Michael von Wenckstern on 30.05.2016.
 *
 * @author Michael von Wenckstern
 */
public class EmbeddedMontiArcModelLoader extends EmbeddedMontiArcModelLoaderTOP {

  public EmbeddedMontiArcModelLoader(EmbeddedMontiArcLanguage language) {
    super(language);
  }

  /**
   * this method should be implemented into the ModelPath class
   */
  private static Collection<Path> getEntriesFromModelPath(ModelPath modelPath) {
    String s = modelPath.toString().replace("[", "").replace("]", "").replace(" ", "");
    String ss[] = s.split(",");
    return Arrays.stream(ss).map(str -> Paths.get(URI.create(str))).collect(Collectors.toSet());
  }

  @Override
  public Collection<ASTEMACompilationUnit> loadModelsIntoScope(final String qualifiedModelName,
      final ModelPath modelPath, final MutableScope enclosingScope,
      final ResolvingConfiguration ResolvingConfiguration) {

    final Collection<ASTEMACompilationUnit> asts = loadModels(qualifiedModelName, modelPath);

    for (ASTEMACompilationUnit ast : asts) {
      createSymbolTableFromAST(ast, qualifiedModelName, enclosingScope, ResolvingConfiguration);
    }

    return asts;
  }
}
