package WS3DApp;

import java.util.HashMap;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import ws3dproxy.CommandUtility;
import ws3dproxy.model.Bag;
import ws3dproxy.util.Constants;

public class GUICreatureScore extends javax.swing.JFrame {

    private Timer timer;
    
    public GUICreatureScore(App app) {
        initComponents(app);
        updateLeafletData();
        startAutoUpdate();
        setVisible(true);
    }

    private void updateData() {
        try {

            score = CommandUtility.getCreatureState(app.selectedCreature.getName()).getScore();
            Bag bag = app.selectedCreature.getBag();

            DefaultTableModel model1 = (DefaultTableModel) jTable1.getModel();            
            model1.setValueAt(bag.getNumberCrystalPerType(Constants.colorRED), 0, 1);
            model1.setValueAt(bag.getNumberCrystalPerType(Constants.colorGREEN), 1, 1);
            model1.setValueAt(bag.getNumberCrystalPerType(Constants.colorBLUE), 2, 1);
            model1.setValueAt(bag.getNumberCrystalPerType(Constants.colorYELLOW), 3, 1);
            model1.setValueAt(bag.getNumberCrystalPerType(Constants.colorMAGENTA), 4, 1);
            model1.setValueAt(bag.getNumberCrystalPerType(Constants.colorWHITE), 5, 1);
            model1.setValueAt(bag.getTotalNumberCrystals(), 6, 1);
            
            DefaultTableModel model2 = (DefaultTableModel) jTable2.getModel();
            model2.setValueAt(app.selectedCreature.getPosition().getX(), 0, 1);
            model2.setValueAt(app.selectedCreature.getPosition().getY(), 1, 1);
            model2.setValueAt(app.selectedCreature.getPosition().getAngle(), 2, 1);
            model2.setValueAt(app.selectedCreature.getFuel(), 3, 1);
            model2.setValueAt(score, 4, 1);

            model1.fireTableDataChanged();
            model2.fireTableDataChanged();
    
        } catch (Exception e) {
            System.out.println("Error updating data: " + e.getMessage());
        }
    }
    
    private void updateLeafletData() {
        try {
            HashMap<String, Integer[]> items_l1 = app.leaflets.get(0).getItems();
            HashMap<String, Integer[]> items_l2 = app.leaflets.get(1).getItems();
            HashMap<String, Integer[]> items_l3 = app.leaflets.get(2).getItems();
            
            DefaultTableModel model1 = (DefaultTableModel) jTable1.getModel();
            for (int i = 0; i < 6; i++) {
                String item = (String) model1.getValueAt(i, 0);
                
                model1.setValueAt(items_l1.getOrDefault(item, new Integer[]{0})[0], i, 2);
                model1.setValueAt(items_l2.getOrDefault(item, new Integer[]{0})[0], i, 3);
                model1.setValueAt(items_l3.getOrDefault(item, new Integer[]{0})[0], i, 4);
            }
            model1.setValueAt(app.leaflets.get(0).getPayment(), 7, 2);
            model1.setValueAt(app.leaflets.get(1).getPayment(), 7, 3);
            model1.setValueAt(app.leaflets.get(2).getPayment(), 7, 4);
            
            model1.fireTableDataChanged();
        } catch (Exception e) {
            System.out.println("Error updating leaflet data: " + e.getMessage());
        }
    }
    
    private void initComponents(App app) {
        this.app = app;
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
    
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    
        jTable1.setModel(new DefaultTableModel(
            new Object [][] {
                {"Red", 0, 0, 0, 0},
                {"Green", 0, 0, 0, 0},
                {"Blue", 0, 0, 0, 0},
                {"Yellow", 0, 0, 0, 0},
                {"Magenta", 0, 0, 0, 0},
                {"White", 0, 0, 0, 0},
                {"Completed", 0, 0, 0, 0},
                {"Score", 0, 0, 0, 0}
            },
            new String [] {
                "Item", "Collected", "L1", "L2", "L3"
            }
        ));
    
        jTable2.setModel(new DefaultTableModel(
            new Object [][] {
                {"Position X:", 0},
                {"Position Y:", 0},
                {"Orientation:", 0},
                {"Fuel:", 0},
                {"Score:", 0}
            },
            new String [] {"Status", "Value"}
        ));
    
        jScrollPane1.setViewportView(jTable1);
        jScrollPane2.setViewportView(jTable2);
    
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addContainerGap())
        );
    
        pack();
    }

    private void startAutoUpdate() {
        timer = new Timer(1000, e -> updateData());
        timer.start();
    }

    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private App app;
    private double score;
}