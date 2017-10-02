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

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTEMACompilationUnit;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EmbeddedMontiArcModelLoaderTOP;
import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.montiarc.tagging._parser.TaggingParser;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EmbeddedMontiArcLanguage.TAG_FILE_ENDING;

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

      // load tags of ast
      for (ASTTaggingUnit unit : loadTags(ast.getPackage(), modelPath)) {
        this.getModelingLanguage().getTagSymbolCreators().stream()
            .forEachOrdered(tc -> tc.create(unit, enclosingScope));
      }
    }

    return asts;
  }

  protected Collection<ASTTaggingUnit> loadTags(final List<String> packageName, final ModelPath modelPath) {
    // TODO use File.separator instead of "\\" or "/"
    String qualifiedModelName = Joiners.DOT.join(packageName);
    checkArgument(!isNullOrEmpty(qualifiedModelName));

    final Collection<ASTTaggingUnit> foundModels = new ArrayList<>();
    for (Path mp : getEntriesFromModelPath(modelPath)) {
      for (String pN : packageName) {
        final Path completePath = Paths.get(mp.toString(), pN);
        final File f = completePath.toFile();
        if (f != null && f.isDirectory()) {
          List<String> tagFiles = Arrays.stream(f.listFiles())
              .filter(s -> s.isFile())
              .map(s -> s.getPath())
              .filter(s -> s.endsWith(TAG_FILE_ENDING))
              .collect(Collectors.toList());

          tagFiles.stream().forEachOrdered(t -> {
            final TaggingParser parser = new TaggingParser();
            Optional<ASTTaggingUnit> ast = Optional.empty();
            try {
              ast = parser.parse(t);
            }
            catch (IOException e) {
              Log.error("could not open file " + t, e);
            }
            if (ast.isPresent()) {
              if (!completePath.endsWith(
                  ast.get().getPackage().stream().collect(Collectors.joining(File.separator)))) {
                Path p = Paths.get(t);
                String expectedPackage = mp.toUri().relativize(p.toUri()).getPath();
                if (p.getParent() != null) {
                  expectedPackage = mp.toUri().relativize(p.getParent().toUri()).getPath();
                }
                expectedPackage = expectedPackage.replace(File.separator, ".").replace("/", ".");
                if (expectedPackage.endsWith(".")) {
                  expectedPackage = expectedPackage.substring(0, expectedPackage.length() - 1);
                }
                Log.error(
                    String.format("0xAC050 package name in '%s' is wrong. package name is '%s' but should be '%s'",
                        t, Joiners.DOT.join(ast.get().getPackage()), expectedPackage));
              }
              else {
                foundModels.add(ast.get());
              }
            }
          });
        }
      }
    }
    return foundModels;
  }
}
