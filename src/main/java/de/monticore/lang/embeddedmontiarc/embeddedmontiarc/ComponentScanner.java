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
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
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
import java.util.*;

/**
 * @author Sascha Schneiders
 */
public class ComponentScanner {
    protected String FILE_ENDING = "ema";
    private final Path basePath;
    private final Scope symTab;

    public ComponentScanner(Path basePath, Scope symTab) {
        this.basePath = Log.errorIfNull(basePath);
        this.symTab = Log.errorIfNull(symTab);
    }


    public ComponentScanner(Path basePath, Scope symTab, String fileEnding) {
        this.basePath = Log.errorIfNull(basePath);
        this.symTab = Log.errorIfNull(symTab);
        this.FILE_ENDING = fileEnding;
    }

    public Set<String> scan() {
        ComponentScanner.ComponentLanguageFileVisitor v = new ComponentScanner.ComponentLanguageFileVisitor(this);
        try {
            Files.walkFileTree(basePath, v);
        } catch (IOException e) {
            Log.error("error while processing stream files", e);
        }
        return new HashSet<>(v.getSet());
    }

    private static class ComponentLanguageFileVisitor extends SimpleFileVisitor<Path> {

        private final ComponentScanner scanner;
        private final Set<String> set = new HashSet<>();

        ComponentLanguageFileVisitor(ComponentScanner scanner) {
            this.scanner = scanner;
        }

        Set<String> getSet() {
            return Collections.unmodifiableSet(set);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (attrs.isSymbolicLink() || !attrs.isRegularFile()) {
                return FileVisitResult.CONTINUE;
            }
            File f = file.toFile();
            if (!isProcessFile(f)) {
                return FileVisitResult.CONTINUE;
            }
            Path relativePath = scanner.basePath.relativize(file);
            String modelName = getModelName(relativePath);
            //ComponentSymbol s = scanner.symTab.<ComponentSymbol>resolve(streamModelName, ComponentSymbol.KIND).orElse(null);

            if (modelName != null) {
                set.add(modelName);
            } else {
                Log.warn("could not resolve stream model defined in file " + f.getAbsolutePath());
            }
            return FileVisitResult.CONTINUE;
        }


        private boolean isProcessFile(File f) {
            if (f == null) {
                return false;
            }
            if (!f.exists() || !f.isFile()) {
                return false;
            }
            String fName = f.getName().toLowerCase();
            return fName.endsWith(scanner.FILE_ENDING);
        }

        private static String getModelName(Path p) {
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
