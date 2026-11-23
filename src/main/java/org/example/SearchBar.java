package org.example;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/** Search bar shown above the editor. Highlights all matches and steps through them. */
public class SearchBar extends JPanel {

    private static final Highlighter.HighlightPainter ALL_PAINTER =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 235, 130));
    private static final Highlighter.HighlightPainter CURRENT_PAINTER =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 160, 60));

    private final JTextComponent editor;
    private final SearchProvider provider;
    private final JTextField field = new JTextField(28);
    private final JCheckBox caseBox = new JCheckBox("Case sensitive");
    private final JLabel countLabel = new JLabel(" ");

    private final List<Object> highlightTags = new ArrayList<>();
    private List<Match> matches = List.of();
    private int current = -1;

    public SearchBar(JTextComponent editor, SearchProvider provider) {
        super(new BorderLayout());
        this.editor = editor;
        this.provider = provider;

        JButton prev = new JButton("▲");
        JButton next = new JButton("▼");
        prev.setToolTipText("Previous match (Shift+Enter)");
        next.setToolTipText("Next match (Enter)");

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        row.add(new JLabel("Search:"));
        row.add(field);
        row.add(prev);
        row.add(next);
        row.add(caseBox);
        row.add(countLabel);
        add(row, BorderLayout.CENTER);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        prev.addActionListener(e -> step(-1));
        next.addActionListener(e -> step(1));
        field.addActionListener(e -> step(1)); // Enter
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                java.awt.event.InputEvent.SHIFT_DOWN_MASK), "prevMatch");
        field.getActionMap().put("prevMatch", new javax.swing.AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { step(-1); }
        });
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        field.getActionMap().put("close", new javax.swing.AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                field.setText("");
                editor.requestFocusInWindow();
            }
        });

        DocumentListener refresh = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onChange(); }
            @Override public void removeUpdate(DocumentEvent e) { onChange(); }
            @Override public void changedUpdate(DocumentEvent e) { onChange(); }
        };
        field.getDocument().addDocumentListener(refresh);
        caseBox.addActionListener(e -> refresh(true));
        // keep highlights correct while the user edits the text
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { later(); }
            @Override public void removeUpdate(DocumentEvent e) { later(); }
            @Override public void changedUpdate(DocumentEvent e) { }
            private void later() { SwingUtilities.invokeLater(() -> refresh(false)); }
        });
    }

    /** Moves keyboard focus to the search field, prefilling from the editor selection. */
    public void focusSearch() {
        String sel = editor.getSelectedText();
        if (sel != null && !sel.isEmpty() && !sel.contains("\n")) field.setText(sel);
        field.requestFocusInWindow();
        field.selectAll();
    }

    private void onChange() { refresh(true); }

    /** Re-runs the search. If {@code jumpToCaret}, the current match becomes the first one after the caret. */
    private void refresh(boolean jumpToCaret) {
        String query = field.getText();
        matches = query.isEmpty() ? List.of()
                : provider.search(editor.getText(), query, caseBox.isSelected());

        if (matches.isEmpty()) {
            current = -1;
        } else if (jumpToCaret || current < 0) {
            current = firstAtOrAfter(editor.getCaretPosition());
        } else {
            current = Math.min(current, matches.size() - 1);
        }
        paint(jumpToCaret);
    }

    private int firstAtOrAfter(int pos) {
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).start() >= pos) return i;
        }
        return 0; // wrap
    }

    private void step(int dir) {
        if (matches.isEmpty()) return;
        current = Math.floorMod(current + dir, matches.size());
        paint(true);
    }

    private void paint(boolean scroll) {
        Highlighter h = editor.getHighlighter();
        highlightTags.forEach(h::removeHighlight);
        highlightTags.clear();
        try {
            for (int i = 0; i < matches.size(); i++) {
                Match m = matches.get(i);
                highlightTags.add(h.addHighlight(m.start(), m.end(),
                        i == current ? CURRENT_PAINTER : ALL_PAINTER));
            }
        } catch (BadLocationException ignored) {
            // matches were computed from the current text, so this should not happen
        }
        if (field.getText().isEmpty()) {
            countLabel.setText(" ");
        } else if (matches.isEmpty()) {
            countLabel.setText("No results");
        } else {
            countLabel.setText((current + 1) + " of " + matches.size());
        }
        if (scroll && current >= 0) {
            // moving the caret makes the text area scroll the match into view
            editor.setCaretPosition(matches.get(current).start());
        }
    }
}
