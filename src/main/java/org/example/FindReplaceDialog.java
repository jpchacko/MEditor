package org.example;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Non-modal Find & Replace dialog operating on a text component. */
public class FindReplaceDialog extends JDialog {

    private final JTextComponent target;
    private final JTextField findField = new JTextField(20);
    private final JTextField replaceField = new JTextField(20);
    private final JCheckBox caseBox = new JCheckBox("Case sensitive");
    private final JLabel status = new JLabel(" ");

    public FindReplaceDialog(JFrame owner, JTextComponent target) {
        super(owner, "Find & Replace", false);
        this.target = target;

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Find:"), c);
        c.gridx = 1; c.weightx = 1; p.add(findField, c);
        c.gridx = 0; c.gridy = 1; c.weightx = 0; p.add(new JLabel("Replace:"), c);
        c.gridx = 1; c.weightx = 1; p.add(replaceField, c);
        c.gridx = 1; c.gridy = 2; p.add(caseBox, c);

        JPanel buttons = new JPanel();
        JButton next = new JButton("Find Next");
        JButton replace = new JButton("Replace");
        JButton all = new JButton("Replace All");
        buttons.add(next);
        buttons.add(replace);
        buttons.add(all);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; p.add(buttons, c);
        c.gridy = 4; p.add(status, c);

        next.addActionListener(e -> findNext());
        replace.addActionListener(e -> replaceCurrent());
        all.addActionListener(e -> replaceAll());
        findField.addActionListener(e -> findNext()); // Enter in the Find field
        getRootPane().setDefaultButton(next);

        setContentPane(p);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /** Shows the dialog, prefilling the Find field from the current selection. */
    public void open() {
        String sel = target.getSelectedText();
        if (sel != null && !sel.isEmpty() && !sel.contains("\n")) findField.setText(sel);
        status.setText(" ");
        setVisible(true);
        findField.requestFocusInWindow();
        findField.selectAll();
    }

    private boolean cs() { return caseBox.isSelected(); }

    private void findNext() {
        String query = findField.getText();
        int idx = TextSearch.findNext(target.getText(), query, target.getSelectionEnd(), cs());
        if (idx < 0) {
            status.setText("Not found");
            return;
        }
        target.select(idx, idx + query.length());
        status.setText(" ");
    }

    private void replaceCurrent() {
        String query = findField.getText();
        String sel = target.getSelectedText();
        if (sel != null && TextSearch.matches(sel, query, cs())) {
            target.replaceSelection(replaceField.getText());
        }
        findNext();
    }

    private void replaceAll() {
        var result = TextSearch.replaceAll(target.getText(), findField.getText(),
                replaceField.getText(), cs());
        if (result.count() > 0) target.setText(result.text());
        status.setText(result.count() + " replaced");
    }
}
