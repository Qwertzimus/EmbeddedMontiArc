/**
 * ******************************************************************************
 * MontiCAR Modeling Family, www.se-rwth.de
 * Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 * All rights reserved.
 * <p>
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.monticar.stream._symboltable.StreamLanguage;
import de.monticore.lang.monticar.streamunits._symboltable.ComponentStreamUnitsSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class StreamScanner {

    private final Path basePath;
    private final Scope symTab;

    public StreamScanner(Path basePath, Scope symTab) {
        this.basePath = Log.errorIfNull(basePath);
        this.symTab = Log.errorIfNull(symTab);
    }

    public Map<ComponentSymbol, Set<ComponentStreamUnitsSymbol>> scan() {
        StreamLanguageFileVisitor v = new StreamLanguageFileVisitor(this);
        try {
            Files.walkFileTree(basePath, v);
        } catch (IOException e) {
            Log.error("error while processing stream files", e);
        }
        return new HashMap<>(v.getMapping());
    }

    private static class StreamLanguageFileVisitor extends SimpleFileVisitor<Path> {

        private final StreamScanner scanner;
        private final Map<ComponentSymbol, Set<ComponentStreamUnitsSymbol>> mapping = new HashMap<>();

        StreamLanguageFileVisitor(StreamScanner scanner) {
            this.scanner = scanner;
        }

        Map<ComponentSymbol, Set<ComponentStreamUnitsSymbol>> getMapping() {
            return Collections.unmodifiableMap(mapping);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (!attrs.isSymbolicLink() && attrs.isRegularFile()) {
                File f = file.toFile();
                if (f.exists() && f.isFile() && f.getName().toLowerCase().endsWith(StreamLanguage.FILE_ENDING)) {
                    Path relativePath = scanner.basePath.relativize(file);
                    String streamModelName = getStreamModelName(relativePath);
                    ComponentStreamUnitsSymbol s = scanner.symTab.<ComponentStreamUnitsSymbol>resolve(streamModelName, ComponentStreamUnitsSymbol.KIND).orElse(null);
                    if (s != null) {
                        ComponentSymbol relatedComponent = s.<ComponentSymbol>getComponentSymbol(ComponentSymbol.KIND).orElse(null);
                        if (relatedComponent != null) {
                            if (!mapping.containsKey(relatedComponent)) {
                                mapping.put(relatedComponent, new HashSet<>());
                            }
                            mapping.get(relatedComponent).add(s);
                        } else {
                            Log.warn("could not resolve component for which stream is defined in " + f.getAbsolutePath());
                        }
                    } else {
                        Log.warn("could not resolve stream model defined in file " + f.getAbsolutePath());
                    }
                }
            }
            return FileVisitResult.CONTINUE;
        }

        private static String getStreamModelName(Path p) {
            List<String> parts = new ArrayList<>();
            for (Path dirName : p.getParent()) {
                parts.add(dirName.toString());
            }
            String fileNameWithoutExtension = (p.getFileName().toString().split("\\."))[0];
            parts.add(fileNameWithoutExtension);
            return Names.getQualifiedName(parts);
        }
    }
}
