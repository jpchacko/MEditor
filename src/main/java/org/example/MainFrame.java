package org.example;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

/** The application's main window. */
public class MainFrame extends JFrame {

    private final JTextArea textArea = new JTextArea();
    private FindReplaceDialog findDialog;
    private SearchBar searchBar;

    public MainFrame() {
        super("MEditor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setMargin(new java.awt.Insets(4, 6, 4, 6));
        textArea.setTabSize(4);
        searchBar = new SearchBar(textArea, new LiteralSearchProvider());
        add(searchBar, java.awt.BorderLayout.NORTH);
        add(new JScrollPane(textArea), java.awt.BorderLayout.CENTER);

        setJMenuBar(createMenuBar());

        setPreferredSize(new Dimension(900, 600));
        pack();
        setLocationRelativeTo(null); // center on screen
    }

    private JMenuBar createMenuBar() {
        JMenu edit = new JMenu("Edit");
        edit.add(item(DefaultEditorKit.cutAction, "Cut", KeyEvent.VK_X));
        edit.add(item(DefaultEditorKit.copyAction, "Copy", KeyEvent.VK_C));
        edit.add(item(DefaultEditorKit.pasteAction, "Paste", KeyEvent.VK_V));
        edit.addSeparator();
        edit.add(item(DefaultEditorKit.selectAllAction, "Select All", KeyEvent.VK_A));

        edit.addSeparator();
        JMenuItem search = new JMenuItem("Search");
        search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        search.addActionListener(e -> searchBar.focusSearch());
        edit.add(search);

        JMenuItem find = new JMenuItem("Find & Replace...");
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
                        | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        find.addActionListener(e -> {
            if (findDialog == null) findDialog = new FindReplaceDialog(this, textArea);
            findDialog.open();
        });
        edit.add(find);

        JMenuBar bar = new JMenuBar();
        bar.add(edit);
        return bar;
    }

    /** Menu item that triggers one of the text area's built-in editor actions. */
    private JMenuItem item(String actionName, String label, int key) {
        Action action = textArea.getActionMap().get(actionName);
        JMenuItem mi = new JMenuItem(action);
        mi.setText(label);
        mi.setAccelerator(KeyStroke.getKeyStroke(key,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        return mi;
    }
}
