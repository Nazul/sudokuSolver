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

import javax.swing.UIManager;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;


/**
 *
 * @author Mario Contreras
 */
public class SudokuSolver extends javax.swing.JFrame {
    private Cell[] cells = new Cell[81];
    private Cell[] solution;
    private Difficulty difficulty;

    private enum Difficulty {
        Easy,
        Medium,
        Hard
    }
    
    private class NewGridTask extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            return null;
        }
    }
   
    /**
     * Creates new form SudokuSolver
     */
    public SudokuSolver() {
        initComponents();
        // Center form
        setLocationRelativeTo(null);
        // For quick access to each cell, we store a reference of the cellxy objects in cells array
        int i = 0, j = 0;
        for(Component c : this.getContentPane().getComponents()) {
            i = ((Cell)c).getName().charAt(0) - 48;
            j = ((Cell)c).getName().charAt(1) - 48;
            if(c instanceof Cell) {
                cells[8 * i + i + j] = (Cell)c;
            }
        }
        // Set easy start startup difficulty
        setDifficulty(Difficulty.Easy);
        // Set form icon
        this.setIconImage(new ImageIcon(getClass().getResource("/mx/iteso/msc/sudokuSolver/resources/sudoku_small.gif")).getImage());
        // New game
        //(new NewGridTask()).execute();
        newGame();
    }
    
    private void setDifficulty(Difficulty d) {
        this.difficulty = d;
        switch(d) {
            case Easy:
                easyMenuItem.setSelected(true);
                mediumMenuItem.setSelected(false);
                hardMenuItem.setSelected(false);
                break;
            case Medium:
                easyMenuItem.setSelected(false);
                mediumMenuItem.setSelected(true);
                hardMenuItem.setSelected(false);
                break;
            case Hard:
                easyMenuItem.setSelected(false);
                mediumMenuItem.setSelected(false);
                hardMenuItem.setSelected(true);
                break;
        }
    }
    
    private void newGame() {
        solution = new Cell[81];
        for(int i = 0; i < solution.length; i++)
            solution[i] = new Cell();

        ArrayList<Integer>[] available = new ArrayList[81];
        for(int i = 0; i < available.length; i++) {
            available[i] = new ArrayList<>();
            for(int j = 1; j < 10; j++)
                available[i].add(j);
        }

        int c = 0;
        while(c < 81) {
            if(!available[c].isEmpty()) {
                int i = (int)(Math.random() * (available[c].size() - 1));
                int n = available[c].get(i);
                Cell cell = newCell(c, n);
                if(!conflict(solution, cell)) {
                    copyCell(cell, solution[c]);
                    available[c].remove(i);
                    c++;
                }
                else {
                    available[c].remove(i);
                }
            }
            else {
                for(int x = 1; x < 10; x++) {
                    available[c].add(x);
                }
                c--;
                solution[c].setAcross(0);
                solution[c].setDown(0);
                solution[c].setRegion(0);
                solution[c].setValue(0);
                solution[c].setIndex(0);
            }
        }
        int top = 0;
        ArrayList<Integer> values = new ArrayList<>();
        switch(difficulty) {
            case Easy:
                top = 30;
                break;
            case Medium:
                top = 45;
                break;
            case Hard:
                top = 60;
                break;
        }
        c = 0;
        while(c < top) {
            int v = (int)(Math.random() * 81);
            if(!values.contains(v)) {
                values.add(v);
                c++;
            }
        }
        for(int i = 0; i < solution.length; i++) {
            copyCell(solution[i], cells[i]);
            cells[i].setLocked(true);
        }
        for(int v : values) {
            cells[v].setValue(0);
            cells[v].setLocked(false);
        }
    }

    private boolean conflict(Cell[] cs, Cell cell) {
        for(Cell c : cs) {
            if((c.getAcross() != 0 && c.getAcross() == cell.getAcross()) ||
               (c.getDown() != 0 && c.getDown() == cell.getDown()) ||
               (c.getRegion() != 0 && c.getRegion() == cell.getRegion()))
               if(c.getValue() == cell.getValue())
                   return true;
        }
        return false;
    }

    private void copyCell(Cell c1, Cell c2) {
        c2.setAcross(c1.getAcross());
        c2.setDown(c1.getDown());
        c2.setRegion(c1.getRegion());
        c2.setValue(c1.getValue());
        c2.setIndex(c1.getIndex());
    }

    private Cell newCell(int index, int value) {
        Cell c = new Cell();
        c.setAcross(getAcrossFromNumber(index + 1));
        c.setDown(getDownFromNumber(index + 1));
        c.setRegion(getRegionFromNumber(index + 1));
        c.setValue(value);
        c.setIndex(index);
        return c;
    }

    private int getAcrossFromNumber(int n) {
        return n % 9 != 0 ? n % 9 : 9;
    }

    private int getDownFromNumber(int n) {
        if(getAcrossFromNumber(n) == 9)
            return n / 9;
        else
            return n / 9 + 1;
    }

    private int getRegionFromNumber(int n) {
        int a = getAcrossFromNumber(n);
        int d = getDownFromNumber(n);
        int k = 0;

        if(1 <= a && a < 4 && 1 <= d && d < 4) {
            k = 1;
        } else if( 4 <= a && a < 7 && 1 <= d && d < 4) {
            k = 2;
        } else if( 7 <= a && a < 10 && 1 <= d && d < 4) {
            k = 3;
        } else if( 1 <= a && a < 4 && 4 <= d && d < 7) {
            k = 4;
        } else if( 4 <= a && a < 7 && 4 <= d && d < 7) {
            k = 5;
        } else if( 7 <= a && a < 10 && 4 <= d && d < 7) {
            k = 6;
        } else if( 1 <= a && a < 4 && 7 <= d && d < 10) {
            k = 7;
        } else if( 4 <= a && a < 7 && 7 <= d && d < 10) {
            k = 8;
        } else if( 7 <= a && a < 10 && 7 <= d && d < 10) {
            k = 9;
        }
        return k;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cell00 = new mx.iteso.msc.sudokuSolver.Cell();
        cell01 = new mx.iteso.msc.sudokuSolver.Cell();
        cell02 = new mx.iteso.msc.sudokuSolver.Cell();
        cell03 = new mx.iteso.msc.sudokuSolver.Cell();
        cell04 = new mx.iteso.msc.sudokuSolver.Cell();
        cell05 = new mx.iteso.msc.sudokuSolver.Cell();
        cell06 = new mx.iteso.msc.sudokuSolver.Cell();
        cell07 = new mx.iteso.msc.sudokuSolver.Cell();
        cell08 = new mx.iteso.msc.sudokuSolver.Cell();
        cell10 = new mx.iteso.msc.sudokuSolver.Cell();
        cell11 = new mx.iteso.msc.sudokuSolver.Cell();
        cell12 = new mx.iteso.msc.sudokuSolver.Cell();
        cell13 = new mx.iteso.msc.sudokuSolver.Cell();
        cell14 = new mx.iteso.msc.sudokuSolver.Cell();
        cell15 = new mx.iteso.msc.sudokuSolver.Cell();
        cell16 = new mx.iteso.msc.sudokuSolver.Cell();
        cell17 = new mx.iteso.msc.sudokuSolver.Cell();
        cell18 = new mx.iteso.msc.sudokuSolver.Cell();
        cell20 = new mx.iteso.msc.sudokuSolver.Cell();
        cell21 = new mx.iteso.msc.sudokuSolver.Cell();
        cell22 = new mx.iteso.msc.sudokuSolver.Cell();
        cell23 = new mx.iteso.msc.sudokuSolver.Cell();
        cell24 = new mx.iteso.msc.sudokuSolver.Cell();
        cell25 = new mx.iteso.msc.sudokuSolver.Cell();
        cell26 = new mx.iteso.msc.sudokuSolver.Cell();
        cell27 = new mx.iteso.msc.sudokuSolver.Cell();
        cell28 = new mx.iteso.msc.sudokuSolver.Cell();
        cell30 = new mx.iteso.msc.sudokuSolver.Cell();
        cell31 = new mx.iteso.msc.sudokuSolver.Cell();
        cell32 = new mx.iteso.msc.sudokuSolver.Cell();
        cell33 = new mx.iteso.msc.sudokuSolver.Cell();
        cell34 = new mx.iteso.msc.sudokuSolver.Cell();
        cell35 = new mx.iteso.msc.sudokuSolver.Cell();
        cell36 = new mx.iteso.msc.sudokuSolver.Cell();
        cell37 = new mx.iteso.msc.sudokuSolver.Cell();
        cell38 = new mx.iteso.msc.sudokuSolver.Cell();
        cell40 = new mx.iteso.msc.sudokuSolver.Cell();
        cell41 = new mx.iteso.msc.sudokuSolver.Cell();
        cell42 = new mx.iteso.msc.sudokuSolver.Cell();
        cell43 = new mx.iteso.msc.sudokuSolver.Cell();
        cell44 = new mx.iteso.msc.sudokuSolver.Cell();
        cell45 = new mx.iteso.msc.sudokuSolver.Cell();
        cell46 = new mx.iteso.msc.sudokuSolver.Cell();
        cell47 = new mx.iteso.msc.sudokuSolver.Cell();
        cell48 = new mx.iteso.msc.sudokuSolver.Cell();
        cell50 = new mx.iteso.msc.sudokuSolver.Cell();
        cell51 = new mx.iteso.msc.sudokuSolver.Cell();
        cell52 = new mx.iteso.msc.sudokuSolver.Cell();
        cell53 = new mx.iteso.msc.sudokuSolver.Cell();
        cell54 = new mx.iteso.msc.sudokuSolver.Cell();
        cell55 = new mx.iteso.msc.sudokuSolver.Cell();
        cell56 = new mx.iteso.msc.sudokuSolver.Cell();
        cell57 = new mx.iteso.msc.sudokuSolver.Cell();
        cell58 = new mx.iteso.msc.sudokuSolver.Cell();
        cell60 = new mx.iteso.msc.sudokuSolver.Cell();
        cell61 = new mx.iteso.msc.sudokuSolver.Cell();
        cell62 = new mx.iteso.msc.sudokuSolver.Cell();
        cell63 = new mx.iteso.msc.sudokuSolver.Cell();
        cell64 = new mx.iteso.msc.sudokuSolver.Cell();
        cell65 = new mx.iteso.msc.sudokuSolver.Cell();
        cell66 = new mx.iteso.msc.sudokuSolver.Cell();
        cell67 = new mx.iteso.msc.sudokuSolver.Cell();
        cell68 = new mx.iteso.msc.sudokuSolver.Cell();
        cell70 = new mx.iteso.msc.sudokuSolver.Cell();
        cell71 = new mx.iteso.msc.sudokuSolver.Cell();
        cell72 = new mx.iteso.msc.sudokuSolver.Cell();
        cell73 = new mx.iteso.msc.sudokuSolver.Cell();
        cell74 = new mx.iteso.msc.sudokuSolver.Cell();
        cell75 = new mx.iteso.msc.sudokuSolver.Cell();
        cell76 = new mx.iteso.msc.sudokuSolver.Cell();
        cell77 = new mx.iteso.msc.sudokuSolver.Cell();
        cell78 = new mx.iteso.msc.sudokuSolver.Cell();
        cell80 = new mx.iteso.msc.sudokuSolver.Cell();
        cell81 = new mx.iteso.msc.sudokuSolver.Cell();
        cell82 = new mx.iteso.msc.sudokuSolver.Cell();
        cell83 = new mx.iteso.msc.sudokuSolver.Cell();
        cell84 = new mx.iteso.msc.sudokuSolver.Cell();
        cell85 = new mx.iteso.msc.sudokuSolver.Cell();
        cell86 = new mx.iteso.msc.sudokuSolver.Cell();
        cell87 = new mx.iteso.msc.sudokuSolver.Cell();
        cell88 = new mx.iteso.msc.sudokuSolver.Cell();
        menuBar = new javax.swing.JMenuBar();
        gameMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        difficultyMenu = new javax.swing.JMenu();
        easyMenuItem = new javax.swing.JRadioButtonMenuItem();
        mediumMenuItem = new javax.swing.JRadioButtonMenuItem();
        hardMenuItem = new javax.swing.JRadioButtonMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sudoku Solver");
        setResizable(false);

        cell00.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell00.setName("00"); // NOI18N

        cell01.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell01.setName("01"); // NOI18N

        cell02.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell02.setName("02"); // NOI18N

        cell03.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell03.setName("03"); // NOI18N

        cell04.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell04.setName("04"); // NOI18N

        cell05.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell05.setName("05"); // NOI18N

        cell06.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell06.setName("06"); // NOI18N

        cell07.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell07.setName("07"); // NOI18N

        cell08.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell08.setName("08"); // NOI18N

        cell10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell10.setName("10"); // NOI18N

        cell11.setBorder(new javax.swing.border.MatteBorder(null));
        cell11.setName("11"); // NOI18N

        cell12.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell12.setName("12"); // NOI18N

        cell13.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell13.setName("13"); // NOI18N

        cell14.setBorder(new javax.swing.border.MatteBorder(null));
        cell14.setName("14"); // NOI18N

        cell15.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell15.setName("15"); // NOI18N

        cell16.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell16.setName("16"); // NOI18N

        cell17.setBorder(new javax.swing.border.MatteBorder(null));
        cell17.setName("17"); // NOI18N

        cell18.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell18.setName("18"); // NOI18N

        cell20.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell20.setName("20"); // NOI18N

        cell21.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell21.setName("21"); // NOI18N

        cell22.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell22.setName("22"); // NOI18N

        cell23.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell23.setName("23"); // NOI18N

        cell24.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell24.setName("24"); // NOI18N

        cell25.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell25.setName("25"); // NOI18N

        cell26.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell26.setName("26"); // NOI18N

        cell27.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell27.setName("27"); // NOI18N

        cell28.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell28.setName("28"); // NOI18N

        cell30.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell30.setName("30"); // NOI18N

        cell31.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell31.setName("31"); // NOI18N

        cell32.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell32.setName("32"); // NOI18N

        cell33.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell33.setName("33"); // NOI18N

        cell34.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell34.setName("34"); // NOI18N

        cell35.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell35.setName("35"); // NOI18N

        cell36.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell36.setName("36"); // NOI18N

        cell37.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell37.setName("37"); // NOI18N

        cell38.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell38.setName("38"); // NOI18N

        cell40.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell40.setName("40"); // NOI18N

        cell41.setBorder(new javax.swing.border.MatteBorder(null));
        cell41.setName("41"); // NOI18N

        cell42.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell42.setName("42"); // NOI18N

        cell43.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell43.setName("43"); // NOI18N

        cell44.setBorder(new javax.swing.border.MatteBorder(null));
        cell44.setName("44"); // NOI18N

        cell45.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell45.setName("45"); // NOI18N

        cell46.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell46.setName("46"); // NOI18N

        cell47.setBorder(new javax.swing.border.MatteBorder(null));
        cell47.setName("47"); // NOI18N

        cell48.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell48.setName("48"); // NOI18N

        cell50.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell50.setName("50"); // NOI18N

        cell51.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell51.setName("51"); // NOI18N

        cell52.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell52.setName("52"); // NOI18N

        cell53.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell53.setName("53"); // NOI18N

        cell54.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell54.setName("54"); // NOI18N

        cell55.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell55.setName("55"); // NOI18N

        cell56.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell56.setName("56"); // NOI18N

        cell57.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell57.setName("57"); // NOI18N

        cell58.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell58.setName("58"); // NOI18N

        cell60.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell60.setName("60"); // NOI18N

        cell61.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell61.setName("61"); // NOI18N

        cell62.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell62.setName("62"); // NOI18N

        cell63.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell63.setName("63"); // NOI18N

        cell64.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell64.setName("64"); // NOI18N

        cell65.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell65.setName("65"); // NOI18N

        cell66.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 1, 1, new java.awt.Color(0, 0, 0)));
        cell66.setName("66"); // NOI18N

        cell67.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        cell67.setName("67"); // NOI18N

        cell68.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 2, new java.awt.Color(0, 0, 0)));
        cell68.setName("68"); // NOI18N

        cell70.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell70.setName("70"); // NOI18N

        cell71.setBorder(new javax.swing.border.MatteBorder(null));
        cell71.setName("71"); // NOI18N

        cell72.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell72.setName("72"); // NOI18N

        cell73.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell73.setName("73"); // NOI18N

        cell74.setBorder(new javax.swing.border.MatteBorder(null));
        cell74.setName("74"); // NOI18N

        cell75.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell75.setName("75"); // NOI18N

        cell76.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 1, new java.awt.Color(0, 0, 0)));
        cell76.setName("76"); // NOI18N

        cell77.setBorder(new javax.swing.border.MatteBorder(null));
        cell77.setName("77"); // NOI18N

        cell78.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 2, new java.awt.Color(0, 0, 0)));
        cell78.setName("78"); // NOI18N

        cell80.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell80.setName("80"); // NOI18N

        cell81.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell81.setName("81"); // NOI18N

        cell82.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell82.setName("82"); // NOI18N

        cell83.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell83.setName("83"); // NOI18N

        cell84.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell84.setName("84"); // NOI18N

        cell85.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell85.setName("85"); // NOI18N

        cell86.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 2, 2, 1, new java.awt.Color(0, 0, 0)));
        cell86.setName("86"); // NOI18N

        cell87.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        cell87.setName("87"); // NOI18N

        cell88.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 2, new java.awt.Color(0, 0, 0)));
        cell88.setName("88"); // NOI18N

        gameMenu.setMnemonic('g');
        gameMenu.setText("Game");

        newMenuItem.setMnemonic('o');
        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(newMenuItem);
        gameMenu.add(jSeparator1);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(exitMenuItem);

        menuBar.add(gameMenu);

        difficultyMenu.setText("Difficulty");

        easyMenuItem.setSelected(true);
        easyMenuItem.setText("Easy (51-30)");
        easyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                easyMenuItemActionPerformed(evt);
            }
        });
        difficultyMenu.add(easyMenuItem);

        mediumMenuItem.setText("Medium (36-45)");
        mediumMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediumMenuItemActionPerformed(evt);
            }
        });
        difficultyMenu.add(mediumMenuItem);

        hardMenuItem.setText("Hard (21-60)");
        hardMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hardMenuItemActionPerformed(evt);
            }
        });
        difficultyMenu.add(hardMenuItem);

        menuBar.add(difficultyMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cell10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cell01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cell02, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cell03, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cell04, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cell05, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(cell06, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(cell07, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(cell16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(cell17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 0, 0)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cell08, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cell25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(cell26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(cell27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(cell28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cell31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cell32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cell33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, 0)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(cell34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(cell35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(cell44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, 0)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(cell55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(cell64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cell83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(cell84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cell36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(cell37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cell46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, 0)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cell87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cell47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cell38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cell88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cell40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cell51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cell00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell04, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addComponent(cell10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cell20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cell32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cell30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addComponent(cell40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(cell81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cell01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cell02, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cell03, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cell05, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cell06, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cell07, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cell08, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(0, 0, 0)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(cell14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 0, 0)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cell26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 0, 0)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(cell34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cell54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(cell33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cell53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(cell36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cell37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cell35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cell38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addGap(50, 50, 50)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(cell52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cell51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cell50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGap(0, 0, Short.MAX_VALUE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(cell55, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cell56, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cell57, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cell58, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cell11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(100, 100, 100)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cell42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(50, 50, 50)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(cell60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, 0)
                                    .addComponent(cell70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, 0)
                                    .addComponent(cell80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cell68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cell63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 0, 0)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(cell73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, 0)
                                            .addComponent(cell83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(cell72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, 0)
                                            .addComponent(cell82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(cell78, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell77, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell76, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell75, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(cell88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(cell86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(cell71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(50, 50, 50)))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(cell74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, 0)
                                            .addComponent(cell84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        (new AboutDialog(this, true)).show();
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        // New game
        //(new NewGridTask()).execute();
        newGame();
    }//GEN-LAST:event_newMenuItemActionPerformed

    private void easyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_easyMenuItemActionPerformed
        setDifficulty(Difficulty.Easy);
    }//GEN-LAST:event_easyMenuItemActionPerformed

    private void mediumMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediumMenuItemActionPerformed
        setDifficulty(Difficulty.Medium);
    }//GEN-LAST:event_mediumMenuItemActionPerformed

    private void hardMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hardMenuItemActionPerformed
        setDifficulty(Difficulty.Hard);
    }//GEN-LAST:event_hardMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Instead of Nimbus look and feel, let's use System look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SudokuSolver.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SudokuSolver().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private mx.iteso.msc.sudokuSolver.Cell cell00;
    private mx.iteso.msc.sudokuSolver.Cell cell01;
    private mx.iteso.msc.sudokuSolver.Cell cell02;
    private mx.iteso.msc.sudokuSolver.Cell cell03;
    private mx.iteso.msc.sudokuSolver.Cell cell04;
    private mx.iteso.msc.sudokuSolver.Cell cell05;
    private mx.iteso.msc.sudokuSolver.Cell cell06;
    private mx.iteso.msc.sudokuSolver.Cell cell07;
    private mx.iteso.msc.sudokuSolver.Cell cell08;
    private mx.iteso.msc.sudokuSolver.Cell cell10;
    private mx.iteso.msc.sudokuSolver.Cell cell11;
    private mx.iteso.msc.sudokuSolver.Cell cell12;
    private mx.iteso.msc.sudokuSolver.Cell cell13;
    private mx.iteso.msc.sudokuSolver.Cell cell14;
    private mx.iteso.msc.sudokuSolver.Cell cell15;
    private mx.iteso.msc.sudokuSolver.Cell cell16;
    private mx.iteso.msc.sudokuSolver.Cell cell17;
    private mx.iteso.msc.sudokuSolver.Cell cell18;
    private mx.iteso.msc.sudokuSolver.Cell cell20;
    private mx.iteso.msc.sudokuSolver.Cell cell21;
    private mx.iteso.msc.sudokuSolver.Cell cell22;
    private mx.iteso.msc.sudokuSolver.Cell cell23;
    private mx.iteso.msc.sudokuSolver.Cell cell24;
    private mx.iteso.msc.sudokuSolver.Cell cell25;
    private mx.iteso.msc.sudokuSolver.Cell cell26;
    private mx.iteso.msc.sudokuSolver.Cell cell27;
    private mx.iteso.msc.sudokuSolver.Cell cell28;
    private mx.iteso.msc.sudokuSolver.Cell cell30;
    private mx.iteso.msc.sudokuSolver.Cell cell31;
    private mx.iteso.msc.sudokuSolver.Cell cell32;
    private mx.iteso.msc.sudokuSolver.Cell cell33;
    private mx.iteso.msc.sudokuSolver.Cell cell34;
    private mx.iteso.msc.sudokuSolver.Cell cell35;
    private mx.iteso.msc.sudokuSolver.Cell cell36;
    private mx.iteso.msc.sudokuSolver.Cell cell37;
    private mx.iteso.msc.sudokuSolver.Cell cell38;
    private mx.iteso.msc.sudokuSolver.Cell cell40;
    private mx.iteso.msc.sudokuSolver.Cell cell41;
    private mx.iteso.msc.sudokuSolver.Cell cell42;
    private mx.iteso.msc.sudokuSolver.Cell cell43;
    private mx.iteso.msc.sudokuSolver.Cell cell44;
    private mx.iteso.msc.sudokuSolver.Cell cell45;
    private mx.iteso.msc.sudokuSolver.Cell cell46;
    private mx.iteso.msc.sudokuSolver.Cell cell47;
    private mx.iteso.msc.sudokuSolver.Cell cell48;
    private mx.iteso.msc.sudokuSolver.Cell cell50;
    private mx.iteso.msc.sudokuSolver.Cell cell51;
    private mx.iteso.msc.sudokuSolver.Cell cell52;
    private mx.iteso.msc.sudokuSolver.Cell cell53;
    private mx.iteso.msc.sudokuSolver.Cell cell54;
    private mx.iteso.msc.sudokuSolver.Cell cell55;
    private mx.iteso.msc.sudokuSolver.Cell cell56;
    private mx.iteso.msc.sudokuSolver.Cell cell57;
    private mx.iteso.msc.sudokuSolver.Cell cell58;
    private mx.iteso.msc.sudokuSolver.Cell cell60;
    private mx.iteso.msc.sudokuSolver.Cell cell61;
    private mx.iteso.msc.sudokuSolver.Cell cell62;
    private mx.iteso.msc.sudokuSolver.Cell cell63;
    private mx.iteso.msc.sudokuSolver.Cell cell64;
    private mx.iteso.msc.sudokuSolver.Cell cell65;
    private mx.iteso.msc.sudokuSolver.Cell cell66;
    private mx.iteso.msc.sudokuSolver.Cell cell67;
    private mx.iteso.msc.sudokuSolver.Cell cell68;
    private mx.iteso.msc.sudokuSolver.Cell cell70;
    private mx.iteso.msc.sudokuSolver.Cell cell71;
    private mx.iteso.msc.sudokuSolver.Cell cell72;
    private mx.iteso.msc.sudokuSolver.Cell cell73;
    private mx.iteso.msc.sudokuSolver.Cell cell74;
    private mx.iteso.msc.sudokuSolver.Cell cell75;
    private mx.iteso.msc.sudokuSolver.Cell cell76;
    private mx.iteso.msc.sudokuSolver.Cell cell77;
    private mx.iteso.msc.sudokuSolver.Cell cell78;
    private mx.iteso.msc.sudokuSolver.Cell cell80;
    private mx.iteso.msc.sudokuSolver.Cell cell81;
    private mx.iteso.msc.sudokuSolver.Cell cell82;
    private mx.iteso.msc.sudokuSolver.Cell cell83;
    private mx.iteso.msc.sudokuSolver.Cell cell84;
    private mx.iteso.msc.sudokuSolver.Cell cell85;
    private mx.iteso.msc.sudokuSolver.Cell cell86;
    private mx.iteso.msc.sudokuSolver.Cell cell87;
    private mx.iteso.msc.sudokuSolver.Cell cell88;
    private javax.swing.JMenu difficultyMenu;
    private javax.swing.JRadioButtonMenuItem easyMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu gameMenu;
    private javax.swing.JRadioButtonMenuItem hardMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JRadioButtonMenuItem mediumMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newMenuItem;
    // End of variables declaration//GEN-END:variables
}

//EOF
