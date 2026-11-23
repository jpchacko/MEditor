# MEditor

A desktop text editor in Java 21 + Swing, built with Maven. Long-term goal: semantic search
inside the editor. Shipping goal: a double-clickable macOS `.app`.

## Status
- [x] 1. Window scaffold
- [x] 2. Typing/deleting (JTextArea), Edit menu (Cut/Copy/Paste/Select All)
- [x] 3. Find & Replace dialog
- [x] 3b. Search bar at top (live highlight, next/prev, case toggle)
- [x] 4. Package as macOS `.app` (jpackage) via `scripts/package-mac.sh`
- [ ] 5. File support: New/Open/Save/Save As, dirty flag, title shows `*`
- [ ] 6. Extras: undo/redo, line numbers, status bar (line/col)
- [ ] 7. Semantic search provider

## Commands
- Build + test + JAR: `mvn package`
- Run JAR: `java -jar target/MEditor-1.0-SNAPSHOT.jar`
- Run via Maven: `mvn compile exec:java`
- Build macOS app: `scripts/package-mac.sh` -> `target/dist/MEditor.app` (open with `open`)
- Regenerate icon: `scripts/make-icon.sh` (draws via `scripts/IconGen.java` -> `packaging/MEditor.icns`)
- Tests only: `mvn test`
- In IntelliJ: run `org.example.Main` (module SDK must be Temurin 21).

## Architecture (package `org.example`)
```
Main -> MainFrame -> JTextArea (the editor)
          |-> SearchBar (top)  -> SearchProvider -> LiteralSearchProvider -> TextSearch
          '-> FindReplaceDialog (Cmd+Shift+F)    -> TextSearch
```
- `Main`: entry point; sets macOS menu-bar properties; starts UI on the EDT.
- `MainFrame`: window, Edit menu, owns the `JTextArea`.
- `SearchBar`: live search UI; highlights all matches (orange = current); depends only on
  `SearchProvider`, so a semantic provider is a one-argument swap in `MainFrame`.
- `SearchProvider` / `Match(start,end,score)`: search seam. Exact matches score 1.0.
- `LiteralSearchProvider`: substring search, delegates to `TextSearch.findAll`.
- `FindReplaceDialog`: non-modal find/replace; takes a `JTextComponent`, not `MainFrame`.
- `TextSearch`: pure logic (no Swing) — findNext (wraps), findAll, replaceAll, matches.
  Uses `regionMatches` for case-insensitive compare (lower-casing can change string length).

## Conventions / decisions
- Keep logic out of UI classes so it is unit-testable (see `TextSearchTest`, JUnit 5).
- Editor is Swing's `JTextArea` (built-in Document, clipboard, selection); no custom buffer.
- Shortcuts use `Toolkit.getMenuShortcutKeyMaskEx()` (Cmd on macOS). Cmd+F = search bar,
  Cmd+Shift+F = Find & Replace.
- Edit menu items reuse the text area's built-in `DefaultEditorKit` actions.

## Known issues / TODO
- The `.app` is unsigned and ~166 MB (full JDK runtime bundled). Fine locally; if shared, Gatekeeper
  needs right-click > Open. Could shrink with jlink / make a `.dmg` (`--type dmg`).
- Icon only applies to the packaged `.app`; running from IntelliJ or `java -jar` shows the default Java Dock icon.
- jpackage app-version is hard-coded to 1.0.0 in the script (macOS rejects `-SNAPSHOT`).
- Replace All uses `setText`, which will break undo; make it one undoable edit when adding undo.
- Search runs synchronously on every keystroke. A semantic provider needs async execution,
  cancellation of stale queries, and probably score-based ranking.
- Semantic search embedding source is undecided (local model vs API) — decide in step 7.
- Package name `org.example` is IntelliJ's default; rename when convenient.
- Startup prints a harmless macOS "Carbon menus" warning from the JDK.
