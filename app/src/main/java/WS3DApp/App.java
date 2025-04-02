package WS3DApp;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;
import ws3dproxy.model.Thing;
import ws3dproxy.model.Leaflet;

public class App {
    public Creature selectedCreature;
    public List<Creature> creatures = new ArrayList<>();
    public World w;
    public int width;
    public int height;
    public ConcurrentLinkedQueue<Integer> movementQueue = new ConcurrentLinkedQueue<>();
    public List<Leaflet> leaflets;

    public App() {
        WS3DProxy proxy = new WS3DProxy();
        try {
            w = World.getInstance();
            width = w.getEnvironmentWidth();
            height = w.getEnvironmentHeight();
            w.reset();
            creatures.add(proxy.createCreature(300, 300, 0));
            selectedCreature = creatures.get(0);
            selectedCreature.start();
            leaflets = selectedCreature.getLeaflets();
            createRandomItems();
            
        } catch (Exception e) {
            System.out.println("Error during initialization");
        }
    }

    private void createRandomItems() {
        createRandomCrystals();
        createRandomWalls();
        createRandomFoods();
    }

    private void createRandomCrystals() {
        // 2 Red Crystals
        createRandomCrystal(0, 2);

        // 2 Green Crystals
        createRandomCrystal(1, 2);

        // 3 Blue Crystals
        createRandomCrystal(2, 3);

        // 1 Yellow Crystal
        createRandomCrystal(3, 1);

        // 1 Magenta Crystal
        createRandomCrystal(4, 1);

        // 3 White Crystals
        createRandomCrystal(5, 3);
    }

    private void createRandomCrystal(int type, int quantity) {
        for (int i = 0; i < quantity; i++) {
            double x = Math.random() * width;
            double y = Math.random() * height;
            try {
                w.createJewel(type, x, y);
            } catch (Exception e) {
                System.out.println("Error creating crystal: " + e);
            }
        }
    }

    private void createRandomWalls() {
        int wallCount = 2;
        for (int i = 0; i < wallCount; i++) {
            double x1 = Math.random() * width;
            double y1 = Math.random() * height;
            double x2 = x1 + 50 + Math.random() * 100;
            double y2 = y1 + 50 + Math.random() * 100;
            try {
                w.createBrick(0, x1, y1, x2, y2);
            } catch (Exception e) {
                System.out.println("Error creating wall: " + e);
            }
        }
    }

    private void createRandomFoods() {
        int foodCount = 5;
        for (int i = 0; i < foodCount; i++) {
            double x = Math.random() * width;
            double y = Math.random() * height;
            int foodType = (int) (Math.random() * 2);  // Tipo de comida aleatório (0 ou 1)
            try {
                w.createFood(foodType, x, y);
            } catch (Exception e) {
                System.out.println("Error creating food: " + e);
            }
        }
    }

    public static void main(String[] args) {
        App app = new App();
        
        new Thread(new UpdateCreature(app)).start();
        new GUICreatureScore(app);
        new GUIItens(app);
        new GUIMovement(app);
    }
}

class UpdateCreature implements Runnable {
    private App app;

    public UpdateCreature(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        while (true) {
            try {
                app.selectedCreature.updateState();
                app.selectedCreature.updateBag();
                checkThingsInVision();
                Integer action = app.movementQueue.poll();
                if (action != null) {
                    switch (action) {
                        case KeyEvent.VK_UP:
                        app.selectedCreature.move(50, 50, 0);
                        break;
                    case KeyEvent.VK_DOWN:
                        app.selectedCreature.move(-50, -50, 0);
                        break;
                    case KeyEvent.VK_LEFT:
                        app.selectedCreature.rotate(5);
                        break;
                    case KeyEvent.VK_RIGHT:
                        app.selectedCreature.rotate(-5);
                        break;
                    }
                }
                Thread.sleep(10);
                app.selectedCreature.move(0, 0, 0);       
            } catch (Exception e) {
                System.out.println("MovementProcessor Error: " + e);
            }
   
        }
    }

    private void checkThingsInVision() {
        for (Thing thing : app.selectedCreature.getThingsInVision()) {
            try {
                System.out.println("ThingsInVision: " + thing.getName());
                if (thing.getName().startsWith("Brick")){
                    continue;
                }
                app.selectedCreature.putInSack(thing.getName());        
            } catch (Exception e) {
                e.printStackTrace();
            }            

        }
    }
}

class CreatureKeyListener implements KeyListener {
    private App app;

    public CreatureKeyListener(App app){
        this.app = app;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        System.out.println("Tecla pressionada: " + KeyEvent.getKeyText(keyCode));
        app.movementQueue.add(keyCode);
    }
}

class GUIItens extends JFrame {
    private App app;
    private JTextField xField, yField;
    private JPanel ButtonPanel;
    
    public GUIItens(App app) {
        this.app = app;

        // X Y Field
        JPanel movePanel = new JPanel(new GridLayout(2, 2));
        JLabel xLabel = new JLabel("X:");
        xField = new JTextField("300");
        JLabel yLabel = new JLabel("Y:");
        yField = new JTextField("300");
        movePanel.add(xLabel);
        movePanel.add(xField);
        movePanel.add(yLabel);
        movePanel.add(yField);

        // Buttons
        ButtonPanel = new JPanel(new GridLayout(3, 3));
        JButton appleButton = createItemButton("Apple", 0);
        JButton nutButton = createItemButton("Nut", 1);
        JButton wallButton = createItemButton("Wall", 2);
        ButtonPanel.add(appleButton);
        ButtonPanel.add(nutButton);
        ButtonPanel.add(wallButton);
        JButton crystalRedButton = createItemButton("Red Crystal", 3);
        JButton crystalGreenButton = createItemButton("Green Crystal", 4);
        JButton crystalBlueButton = createItemButton("Blue Crystal", 5);
        ButtonPanel.add(crystalRedButton);
        ButtonPanel.add(crystalGreenButton);
        ButtonPanel.add(crystalBlueButton);
        JButton crystalYellowButton = createItemButton("Yellow Crystal", 6);
        JButton crystalMagentaButton = createItemButton("Magenta Crystal", 7);
        JButton crystalWhiteButton = createItemButton("White Crystal", 8);
        ButtonPanel.add(crystalYellowButton);
        ButtonPanel.add(crystalMagentaButton);
        ButtonPanel.add(crystalWhiteButton);

        // Frame settings
        setTitle("Creature and Environment Editor");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(movePanel, BorderLayout.NORTH);
        add(ButtonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createItemButton(final String name, final int type) {
        JButton button = new JButton(name);
        button.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                double x = Integer.parseInt(xField.getText());
                double y = Integer.parseInt(yField.getText());
                if (type == 0) 
                {
                    try {
                        app.w.createFood(0, x, y);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                } 
                else if (type == 1) 
                {
                    try {
                        app.w.createFood(1, x, y);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                }
                else if (type == 2) 
                {
                    try {
                        app.w.createBrick(0, x, y, x+50, y+50);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                }
                else if (type == 3) 
                {
                    try {
                        app.w.createJewel(0, x, y);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                } 
                else if (type == 4) 
                {
                    try {
                        app.w.createJewel(1, x, y);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                }
                else if (type == 5) 
                {
                    try {
                        app.w.createJewel(2, x, y);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                }
                else if (type == 6) 
                {
                    try {
                        app.w.createJewel(3, x, y);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                }
                else if (type == 7) 
                {
                    try {
                        app.w.createJewel(4, x, y);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                }
                else if (type == 8) 
                {
                    try {
                        app.w.createJewel(5, x, y);
                    } catch (Exception ex) {
                        System.out.println("Invalid input.");
                    }
                } 
            }
        });
        return button;
    }
}

class GUIMovement extends JFrame {
    private App app;
    private JComboBox<String> creaturesComboBox;
    private JTextField xField, yField;
    private JPanel ButtonPanel;
    private JPanel topPanel;
    private JPanel movePanel;

    public GUIMovement(App app) {
        this.app = app;

        // Creatures selection
        topPanel = new JPanel(new FlowLayout());
        JLabel creatureLabel = new JLabel("Select Creature:");
        creaturesComboBox = new JComboBox<>();
        for (int i = 0; i < app.creatures.size(); i++) {
            creaturesComboBox.addItem("Creature " + i);
        }
        creaturesComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                app.selectedCreature = app.creatures.get(creaturesComboBox.getSelectedIndex());
            }
        });
        topPanel.add(creatureLabel);
        topPanel.add(creaturesComboBox);

        // Manual move controls
        movePanel = new JPanel(new GridLayout(2, 2));
        JLabel xLabel = new JLabel("X:");
        xField = new JTextField("300");
        JLabel yLabel = new JLabel("Y:");
        yField = new JTextField("300");
        movePanel.add(xLabel);
        movePanel.add(xField);
        movePanel.add(yLabel);
        movePanel.add(yField);
        ButtonPanel = new JPanel(new FlowLayout());
        JButton moveButton = createItemButton("Move Creature");
        ButtonPanel.add(moveButton);

        // KeyListener
        addKeyListener(new CreatureKeyListener(app));

        // Frame settings
        setTitle("Creature Movement");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(movePanel, BorderLayout.CENTER);
        add(ButtonPanel, BorderLayout.SOUTH);
        setFocusable(true);
        requestFocusInWindow();
        setVisible(true);
    }

    private JButton createItemButton(final String name) {
        JButton button = new JButton(name);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double x = Integer.parseInt(xField.getText());
                double y = Integer.parseInt(yField.getText());
                try {
                    app.selectedCreature.moveto(4, x, y);
                } catch (Exception ex) {
                    System.out.println("Invalid input.");
                }
            }
        });
        return button;
    }
}
