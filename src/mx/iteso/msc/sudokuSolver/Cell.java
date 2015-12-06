/*
 * Copyright 2015 Mario Contreras & Erick González.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.iteso.msc.sudokuSolver;

import java.awt.Color;


/**
 *
 * @author Mario Contreras
 */
public class Cell extends javax.swing.JPanel {
    private boolean locked;
    private boolean error;
    private int across, down, region, value, index;
    

    /**
     * Creates new form Cell
     */
    public Cell() {
        initComponents();
        switch(valueField.getText()) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                this.value = Integer.parseInt(valueField.getText());
                break;
            default:
                this.value = 0;
                valueField.setText("");
                break;
        }
    }

    // "Properties"
    public int getValue() {
        if(!valueField.getText().isEmpty()) {
            this.value = Integer.parseInt(valueField.getText());
            return this.value;
        }
        else
            return 0;
    }
    
    public void setValue(int value) {
        if (value < 0 || value > 9) return;
        this.value = value;
        if(this.value != 0)
            valueField.setText(Integer.toString(value));
        else
            valueField.setText("");
    }

    public char getLetter() {
        return (char)letterLabel.getText().charAt(0);
    }
    
    public void setLetter(char letter) {
        switch(letter) {
            case 's':
            case 'm':
            case 'l':
                letter = Character.toUpperCase(letter);
            case 'S':
            case 'M':
            case 'L':
                letterLabel.setText(Character.toString(letter));
                break;
            default:
                letterLabel.setText(" ");
                break;
        }
    }
    
    public int getAcross() {
        return this.across;
    }
    
    public void setAcross(int across) {
        this.across = across;
    }
    
    public int getDown() {
        return this.down;
    }
    
    public void setDown(int down) {
        this.down = down;
    }
    
    public int getRegion() {
        return this.region;
    }
    
    public void setRegion(int region) {
        this.region = region;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public boolean getLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
        this.valueField.setEditable(!locked);
        valueField.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.background"));
        if(locked)
            valueField.setForeground(Color.BLUE);
        else if(error)
            valueField.setForeground(Color.RED);
        else
            valueField.setForeground(Color.BLACK);
    }
    
    public boolean getError() {
        return locked;
    }
    
    public void setError(boolean error) {
        this.error = error;
        if(this.error)
            valueField.setForeground(Color.RED);
        else if(locked)
            valueField.setForeground(Color.BLUE);
        else
            valueField.setForeground(Color.BLACK);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        valueField = new javax.swing.JTextField();
        letterLabel = new javax.swing.JLabel();

        setBackground(java.awt.Color.white);
        setMaximumSize(new java.awt.Dimension(50, 50));
        setMinimumSize(new java.awt.Dimension(50, 50));
        setPreferredSize(new java.awt.Dimension(50, 50));

        valueField.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        valueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        valueField.setText("0");
        valueField.setBorder(null);
        valueField.setDisabledTextColor(javax.swing.UIManager.getDefaults().getColor("TextField.foreground"));
        valueField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                valueFieldFocusLost(evt);
            }
        });
        valueField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueFieldActionPerformed(evt);
            }
        });

        letterLabel.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        letterLabel.setText("S");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(letterLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(valueField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(letterLabel)
                .addGap(0, 0, 0)
                .addComponent(valueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void valueFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueFieldActionPerformed
        switch(valueField.getText()) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                this.value = Integer.parseInt(valueField.getText());
                break;
            default:
                this.value = 0;
                valueField.setText("");
                break;
        }
    }//GEN-LAST:event_valueFieldActionPerformed

    private void valueFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_valueFieldFocusLost
        switch(valueField.getText()) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                this.value = Integer.parseInt(valueField.getText());
                break;
            default:
                this.value = 0;
                valueField.setText("");
                break;
        }
    }//GEN-LAST:event_valueFieldFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel letterLabel;
    private javax.swing.JTextField valueField;
    // End of variables declaration//GEN-END:variables
}
