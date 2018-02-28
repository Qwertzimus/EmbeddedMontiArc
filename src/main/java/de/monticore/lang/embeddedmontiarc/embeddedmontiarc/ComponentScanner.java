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

    public Set<ComponentSymbol> scan() {
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
        private final Set<ComponentSymbol> set = new HashSet<>();

        ComponentLanguageFileVisitor(ComponentScanner scanner) {
            this.scanner = scanner;
        }

        Set<ComponentSymbol> getSet() {
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
            String streamModelName = getStreamModelName(relativePath);
            ComponentSymbol s = scanner.symTab.<ComponentSymbol>resolve(streamModelName, ComponentSymbol.KIND).orElse(null);
            if (s != null) {
                set.add(s);
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
