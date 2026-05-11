package myapplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// ═══════════════════════════════════════════════════════════════
//  RUNECLONE — Single-file RPG inspired by early RuneScape
//  Controls: Arrow keys / WASD to move, Click NPC to talk,
//            Click enemy to start combat, number keys for skills
// ═══════════════════════════════════════════════════════════════

public class RuneClone extends JPanel implements ActionListener {

    // ── sizing ─────────────────────────────────────────────────
    static final int TILE  = 32;
    static final int COLS  = 20;
    static final int ROWS  = 15;
    static final int W     = COLS * TILE;
    static final int H     = ROWS * TILE;
    static final int HUD_H = 160;

    // ── tile types ─────────────────────────────────────────────
    static final int T_GRASS = 0;
    static final int T_WATER = 1;
    static final int T_WALL  = 2;
    static final int T_PATH  = 3;
    static final int T_TREE  = 4;
    static final int T_SAND  = 5;

    // ── game state ─────────────────────────────────────────────
    enum State { EXPLORE, DIALOGUE, COMBAT, DEAD }
    State gameState = State.EXPLORE;

    // ── world map ──────────────────────────────────────────────
    int[][] world = {
        {4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},
        {4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
        {4,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,4},
        {4,0,0,0,3,0,0,3,0,0,0,1,1,1,1,0,0,0,0,4},
        {4,0,0,0,3,0,0,3,0,0,1,1,5,5,1,1,0,0,0,4},
        {4,0,0,0,3,3,0,3,0,0,1,5,5,5,5,1,0,0,0,4},
        {4,0,0,0,0,0,0,3,0,0,1,5,5,5,5,1,0,0,0,4},
        {4,0,0,0,0,0,0,3,3,3,3,3,3,3,3,3,0,0,0,4},
        {4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,4},
        {4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,4},
        {4,0,4,4,4,4,0,0,0,0,0,0,0,0,0,3,0,0,0,4},
        {4,0,4,0,0,4,0,0,0,0,0,0,0,0,0,3,3,3,0,4},
        {4,0,4,0,0,4,0,0,0,0,0,0,0,0,0,0,0,3,0,4},
        {4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,4},
        {4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},
    };

    // ── player ─────────────────────────────────────────────────
    int px = 2, py = 2; // tile position
    int php = 100, pmaxHp = 100;
    int pAtk = 15, pDef = 5, pMagic = 10;
    int pExp = 0, pLevel = 1;
    int expToNext = 100;
    List<String> inventory = new ArrayList<>(Arrays.asList("Health Potion", "Bronze Sword"));
    String equippedWeapon = "Bronze Sword";

    // ── skills ─────────────────────────────────────────────────
    String[] skillNames = {"Slash", "Block", "Fireball", "Heal"};
    int[]    skillMp    = {0, 0, 15, 10};
    int      pMp = 50, pMaxMp = 50;

    // ── NPCs ───────────────────────────────────────────────────
    static class NPC {
        int tx, ty;
        String name;
        String[] lines;
        int dialogIdx = 0;
        Color color;
        NPC(int tx, int ty, String name, Color color, String... lines) {
            this.tx = tx; this.ty = ty; this.name = name;
            this.color = color; this.lines = lines;
        }
    }

    List<NPC> npcs = new ArrayList<>(Arrays.asList(
        new NPC(5, 5, "Guard Bob", new Color(70, 130, 180),
            "Halt! Who goes there?",
            "Ah, an adventurer. Watch yourself out there.",
            "The goblins to the east have been restless.",
            "Stay sharp, traveller."),
        new NPC(3, 11, "Merchant", new Color(180, 130, 40),
            "Welcome to my shop! ...Oh wait, the shop system isn't built yet.",
            "But I can tell you — the sword you carry is decent.",
            "Upgrade when you can. The swamp monsters hit hard.",
            "Come back anytime!"),
        new NPC(14, 12, "Wizard", new Color(148, 0, 211),
            "Greetings, young one. Magic flows through all things.",
            "Your Fireball skill costs MP — keep an eye on your blue bar.",
            "Heal restores HP. Use it before you die, not after.",
            "Wisdom is knowing when to run.")
    ));
    NPC activeNPC = null;

    // ── enemies ────────────────────────────────────────────────
    static class Enemy {
        int tx, ty;
        String name;
        int hp, maxHp, atk, def, expReward;
        Color color;
        boolean alive = true;
        Enemy(int tx, int ty, String name, Color color, int hp, int atk, int def, int exp) {
            this.tx = tx; this.ty = ty; this.name = name; this.color = color;
            this.hp = hp; this.maxHp = hp; this.atk = atk; this.def = def;
            this.expReward = exp;
        }
    }

    List<Enemy> enemies = new ArrayList<>(Arrays.asList(
        new Enemy(8,  2,  "Goblin",     new Color(0, 160, 0),   30, 8,  2, 25),
        new Enemy(16, 3,  "Goblin",     new Color(0, 160, 0),   30, 8,  2, 25),
        new Enemy(12, 8,  "Orc",        new Color(100, 60, 0),  60, 14, 5, 60),
        new Enemy(18, 10, "Dark Mage",  new Color(100, 0, 150), 45, 18, 3, 80),
        new Enemy(6,  13, "Rat",        new Color(120, 120, 80),15, 5,  1, 10)
    ));
    Enemy activeEnemy = null;

    // ── combat log ─────────────────────────────────────────────
    List<String> combatLog = new ArrayList<>();
    int selectedSkill = 0;
    boolean playerTurn = true;
    javax.swing.Timer enemyTurnTimer;

    // ── dialogue ───────────────────────────────────────────────
    String dialogueLine = "";

    // ── message overlay ────────────────────────────────────────
    String flashMsg = "";
    int    flashTimer = 0;

    // ── input ──────────────────────────────────────────────────
    Set<Integer> keys = new HashSet<>();
    javax.swing.Timer moveTimer;
    javax.swing.Timer gameTimer;

    // ── fonts ──────────────────────────────────────────────────
    Font fontMono  = new Font("Monospaced", Font.PLAIN, 12);
    Font fontBold  = new Font("Monospaced", Font.BOLD,  13);
    Font fontTitle = new Font("Monospaced", Font.BOLD,  16);

    // ══════════════════════════════════════════════════════════
    public RuneClone() {
        setPreferredSize(new Dimension(W, H + HUD_H));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                keys.add(e.getKeyCode());
                handleKey(e.getKeyCode());
            }
            public void keyReleased(KeyEvent e) { keys.remove(e.getKeyCode()); }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { handleClick(e.getX(), e.getY()); }
        });

        // enemy turn delay
        enemyTurnTimer = new javax.swing.Timer(800, ev -> {
            enemyTurnTimer.stop();
            doEnemyTurn();
        });

        // movement repeat timer
        moveTimer = new javax.swing.Timer(150, ev -> handleMovement());

        gameTimer = new javax.swing.Timer(16, this);
    }

    public void startGameLoop() {
        gameTimer.start();
        moveTimer.start();
    }

    // ── key handler ────────────────────────────────────────────
    void handleKey(int k) {
        if (gameState == State.DIALOGUE) {
            if (k == KeyEvent.VK_SPACE || k == KeyEvent.VK_ENTER) advanceDialogue();
            return;
        }
        if (gameState == State.COMBAT) {
            if (k == KeyEvent.VK_1) selectedSkill = 0;
            if (k == KeyEvent.VK_2) selectedSkill = 1;
            if (k == KeyEvent.VK_3) selectedSkill = 2;
            if (k == KeyEvent.VK_4) selectedSkill = 3;
            if ((k == KeyEvent.VK_SPACE || k == KeyEvent.VK_ENTER) && playerTurn)
                playerAttack();
            if (k == KeyEvent.VK_ESCAPE) fleeCombat();
            return;
        }
        // inventory use
        if (k == KeyEvent.VK_H) usePotion();
    }

    void handleMovement() {
        if (gameState != State.EXPLORE) return;
        int nx = px, ny = py;
        if (keys.contains(KeyEvent.VK_LEFT)  || keys.contains(KeyEvent.VK_A)) nx--;
        if (keys.contains(KeyEvent.VK_RIGHT) || keys.contains(KeyEvent.VK_D)) nx++;
        if (keys.contains(KeyEvent.VK_UP)    || keys.contains(KeyEvent.VK_W)) ny--;
        if (keys.contains(KeyEvent.VK_DOWN)  || keys.contains(KeyEvent.VK_S)) ny++;
        tryMove(nx, ny);
    }

    void tryMove(int nx, int ny) {
        if (nx < 0 || ny < 0 || nx >= COLS || ny >= ROWS) return;
        if (!isTileWalkable(nx, ny)) return;
        for (NPC n : npcs) if (n.tx == nx && n.ty == ny) return;
        for (Enemy en : enemies) if (en.alive && en.tx == nx && en.ty == ny) return;
        px = nx; py = ny;
    }

    boolean isTileWalkable(int x, int y) {
        int t = world[y][x];
        return t == T_GRASS || t == T_PATH || t == T_SAND;
    }

    // ── click handler ──────────────────────────────────────────
    void handleClick(int mx, int my) {
        if (my > H) return; // clicked HUD
        int tx = mx / TILE, ty = my / TILE;

        if (gameState == State.DIALOGUE) { advanceDialogue(); return; }
        if (gameState == State.COMBAT)   return;

        // click NPC adjacent
        for (NPC n : npcs) {
            if (n.tx == tx && n.ty == ty && isAdjacent(px, py, tx, ty)) {
                startDialogue(n); return;
            }
        }
        // click enemy adjacent
        for (Enemy en : enemies) {
            if (en.alive && en.tx == tx && en.ty == ty && isAdjacent(px, py, tx, ty)) {
                startCombat(en); return;
            }
        }
    }

    boolean isAdjacent(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1;
    }

    // ── dialogue ───────────────────────────────────────────────
    void startDialogue(NPC n) {
        activeNPC = n;
        n.dialogIdx = 0;
        dialogueLine = n.lines[0];
        gameState = State.DIALOGUE;
    }

    void advanceDialogue() {
        if (activeNPC == null) return;
        activeNPC.dialogIdx++;
        if (activeNPC.dialogIdx >= activeNPC.lines.length) {
            gameState = State.EXPLORE;
            activeNPC = null;
        } else {
            dialogueLine = activeNPC.lines[activeNPC.dialogIdx];
        }
    }

    // ── combat ─────────────────────────────────────────────────
    void startCombat(Enemy en) {
        activeEnemy = en;
        combatLog.clear();
        combatLog.add("Combat started with " + en.name + "!");
        combatLog.add("Choose a skill and press SPACE.");
        playerTurn = true;
        selectedSkill = 0;
        gameState = State.COMBAT;
    }

    void playerAttack() {
        if (!playerTurn || activeEnemy == null) return;

        String skill = skillNames[selectedSkill];
        int mpCost = skillMp[selectedSkill];
        if (pMp < mpCost) { combatLog.add("Not enough MP!"); if (combatLog.size() > 6) combatLog.remove(0); return; }
        pMp -= mpCost;

        int dmg = 0;
        switch (selectedSkill) {
            case 0: // Slash
                dmg = Math.max(1, pAtk - activeEnemy.def + (int)(Math.random() * 5));
                combatLog.add("You slash for " + dmg + " dmg!");
                break;
            case 1: // Block
                pDef += 3;
                combatLog.add("You brace! DEF +3 this round.");
                dmg = 0;
                break;
            case 2: // Fireball
                dmg = Math.max(1, pMagic * 2 - activeEnemy.def + (int)(Math.random() * 8));
                combatLog.add("Fireball hits for " + dmg + " dmg!");
                break;
            case 3: // Heal
                int healed = 20 + (int)(Math.random() * 10);
                php = Math.min(pmaxHp, php + healed);
                combatLog.add("You heal " + healed + " HP!");
                break;
        }

        if (dmg > 0) activeEnemy.hp -= dmg;
        if (combatLog.size() > 6) combatLog.remove(0);

        if (activeEnemy.hp <= 0) { defeatEnemy(); return; }

        playerTurn = false;
        enemyTurnTimer.start();
    }

    void doEnemyTurn() {
        if (activeEnemy == null) return;
        int dmg = Math.max(1, activeEnemy.atk - pDef + (int)(Math.random() * 6));
        // reset temp defense bonus
        pDef = Math.max(5, pDef - 3);
        php -= dmg;
        combatLog.add(activeEnemy.name + " hits you for " + dmg + "!");
        if (combatLog.size() > 6) combatLog.remove(0);

        if (php <= 0) {
            php = 0;
            gameState = State.DEAD;
            combatLog.add("You have died...");
            return;
        }
        playerTurn = true;
    }

    void defeatEnemy() {
        combatLog.add(activeEnemy.name + " defeated! +" + activeEnemy.expReward + " EXP");
        pExp += activeEnemy.expReward;
        activeEnemy.alive = false;
        // check level up
        while (pExp >= expToNext) {
            pExp -= expToNext;
            pLevel++;
            expToNext = pLevel * 100;
            pmaxHp += 15; php = pmaxHp;
            pAtk += 3; pDef += 1; pMagic += 2;
            pMaxMp += 5; pMp = pMaxMp;
            setFlash("LEVEL UP! Now level " + pLevel + "!");
        }
        activeEnemy = null;
        gameState = State.EXPLORE;
    }

    void fleeCombat() {
        combatLog.add("You fled!");
        activeEnemy = null;
        gameState = State.EXPLORE;
    }

    // ── inventory ──────────────────────────────────────────────
    void usePotion() {
        if (inventory.contains("Health Potion")) {
            int healed = 30;
            php = Math.min(pmaxHp, php + healed);
            inventory.remove("Health Potion");
            setFlash("Used Health Potion! +" + healed + " HP");
        } else {
            setFlash("No potions left!");
        }
    }

    void setFlash(String msg) { flashMsg = msg; flashTimer = 120; }

    // ── game loop ──────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        if (flashTimer > 0) flashTimer--;
        repaint();
    }

    // ══════════════════════════════════════════════════════════
    //  RENDERING
    // ══════════════════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawWorld(g2);
        drawEntities(g2);
        drawHUD(g2);

        if (gameState == State.DIALOGUE) drawDialogueBox(g2);
        if (gameState == State.COMBAT)   drawCombatOverlay(g2);
        if (gameState == State.DEAD)     drawDeathScreen(g2);
        if (flashTimer > 0)              drawFlash(g2);

        g2.dispose();
    }

    // ── world ──────────────────────────────────────────────────
    void drawWorld(Graphics2D g2) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = col * TILE, y = row * TILE;
                drawTile(g2, world[row][col], x, y);
            }
        }
    }

    void drawTile(Graphics2D g2, int type, int x, int y) {
        switch (type) {
            case T_GRASS:
                g2.setColor(new Color(34, 120, 34));
                g2.fillRect(x, y, TILE, TILE);
                g2.setColor(new Color(28, 100, 28));
                g2.fillRect(x+5,  y+8,  2, 5);
                g2.fillRect(x+12, y+4,  2, 6);
                g2.fillRect(x+20, y+10, 2, 4);
                g2.fillRect(x+26, y+5,  2, 7);
                break;
            case T_WATER:
                g2.setColor(new Color(30, 80, 180));
                g2.fillRect(x, y, TILE, TILE);
                g2.setColor(new Color(60, 120, 220));
                g2.fillRect(x+2,  y+6,  10, 3);
                g2.fillRect(x+16, y+18, 12, 3);
                break;
            case T_WALL:
                g2.setColor(new Color(90, 90, 90));
                g2.fillRect(x, y, TILE, TILE);
                g2.setColor(new Color(60, 60, 60));
                g2.fillRect(x, y, TILE, 2);
                g2.fillRect(x, y, 2, TILE);
                g2.setColor(new Color(120, 120, 120));
                g2.fillRect(x+4,  y+4,  10, 8);
                g2.fillRect(x+18, y+16, 8,  8);
                break;
            case T_PATH:
                g2.setColor(new Color(180, 150, 100));
                g2.fillRect(x, y, TILE, TILE);
                g2.setColor(new Color(160, 130, 85));
                g2.fillRect(x+3,  y+12, 6, 2);
                g2.fillRect(x+20, y+5,  5, 2);
                break;
            case T_TREE:
                g2.setColor(new Color(34, 100, 34));
                g2.fillRect(x, y, TILE, TILE);
                // trunk
                g2.setColor(new Color(100, 60, 20));
                g2.fillRect(x+12, y+18, 8, 14);
                // canopy
                g2.setColor(new Color(0, 130, 0));
                g2.fillOval(x+4, y+2, 24, 20);
                g2.setColor(new Color(0, 160, 0));
                g2.fillOval(x+8, y+4, 16, 14);
                break;
            case T_SAND:
                g2.setColor(new Color(210, 190, 120));
                g2.fillRect(x, y, TILE, TILE);
                g2.setColor(new Color(190, 170, 100));
                g2.fillRect(x+6,  y+10, 4, 2);
                g2.fillRect(x+18, y+20, 3, 2);
                break;
        }
        // grid line
        g2.setColor(new Color(0, 0, 0, 30));
        g2.drawRect(x, y, TILE, TILE);
    }

    // ── entities ───────────────────────────────────────────────
    void drawEntities(Graphics2D g2) {
        // enemies
        for (Enemy en : enemies) {
            if (!en.alive) continue;
            int x = en.tx * TILE, y = en.ty * TILE;
            // shadow
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(x+6, y+26, 20, 6);
            // body
            g2.setColor(en.color);
            g2.fillOval(x+8, y+8, 16, 18);
            // eyes
            g2.setColor(Color.RED);
            g2.fillOval(x+11, y+12, 4, 4);
            g2.fillOval(x+17, y+12, 4, 4);
            // hp bar
            drawBar(g2, x+2, y+2, 28, 4, en.hp, en.maxHp, new Color(200,40,40), new Color(80,0,0));
            // name
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
            g2.setColor(Color.WHITE);
            g2.drawString(en.name, x+1, y+1);
        }

        // npcs
        for (NPC n : npcs) {
            int x = n.tx * TILE, y = n.ty * TILE;
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(x+6, y+26, 20, 6);
            // robe
            g2.setColor(n.color);
            g2.fillRect(x+10, y+14, 12, 16);
            // head
            g2.setColor(new Color(255, 220, 170));
            g2.fillOval(x+9, y+6, 14, 14);
            // eyes
            g2.setColor(Color.BLACK);
            g2.fillOval(x+12, y+11, 3, 3);
            g2.fillOval(x+17, y+11, 3, 3);
            // name tag
            g2.setFont(new Font("Monospaced", Font.BOLD, 9));
            g2.setColor(new Color(255, 220, 50));
            g2.drawString(n.name, x - 4, y);
        }

        // player
        {
            int x = px * TILE, y = py * TILE;
            // shadow
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillOval(x+6, y+26, 20, 6);
            // legs
            g2.setColor(new Color(60, 60, 140));
            g2.fillRect(x+10, y+20, 5, 10);
            g2.fillRect(x+17, y+20, 5, 10);
            // body / armor
            g2.setColor(new Color(160, 160, 160));
            g2.fillRect(x+8, y+13, 16, 10);
            // head
            g2.setColor(new Color(255, 220, 170));
            g2.fillOval(x+9, y+4, 14, 14);
            // helmet
            g2.setColor(new Color(130, 130, 130));
            g2.fillRect(x+9, y+4, 14, 6);
            // eyes
            g2.setColor(Color.BLACK);
            g2.fillOval(x+12, y+10, 3, 3);
            g2.fillOval(x+17, y+10, 3, 3);
            // sword
            g2.setColor(new Color(200, 200, 220));
            g2.fillRect(x+24, y+10, 3, 14);
            g2.setColor(new Color(180, 140, 20));
            g2.fillRect(x+22, y+16, 7, 3);
        }
    }

    // ── HUD ────────────────────────────────────────────────────
    void drawHUD(Graphics2D g2) {
        int hy = H;
        g2.setColor(new Color(20, 20, 30));
        g2.fillRect(0, hy, W, HUD_H);
        g2.setColor(new Color(60, 60, 80));
        g2.fillRect(0, hy, W, 2);

        // left: player stats
        g2.setFont(fontBold);
        g2.setColor(new Color(255, 215, 0));
        g2.drawString("⚔ " + "RuneClone", 10, hy + 20);

        g2.setFont(fontMono);
        g2.setColor(Color.WHITE);
        g2.drawString("LVL " + pLevel + "  EXP: " + pExp + "/" + expToNext, 10, hy + 38);

        drawBar(g2, 10, hy + 46, 140, 14, php, pmaxHp, new Color(200, 40, 40), new Color(80, 0, 0));
        g2.setColor(Color.WHITE);
        g2.setFont(fontMono);
        g2.drawString("HP: " + php + "/" + pmaxHp, 15, hy + 57);

        drawBar(g2, 10, hy + 66, 140, 14, pMp, pMaxMp, new Color(40, 80, 200), new Color(0, 0, 80));
        g2.setColor(Color.WHITE);
        g2.drawString("MP: " + pMp + "/" + pMaxMp, 15, hy + 77);

        g2.setColor(new Color(180, 180, 180));
        g2.drawString("ATK:" + pAtk + " DEF:" + pDef + " MGC:" + pMagic, 10, hy + 95);
        g2.drawString("Weapon: " + equippedWeapon, 10, hy + 110);

        // middle: skills
        g2.setFont(fontBold);
        g2.setColor(new Color(255, 215, 0));
        g2.drawString("SKILLS [1-4]", 175, hy + 20);
        for (int i = 0; i < skillNames.length; i++) {
            boolean sel = i == selectedSkill;
            g2.setColor(sel ? new Color(255, 200, 0) : new Color(120, 120, 140));
            g2.fillRoundRect(175, hy + 26 + i * 28, 150, 22, 6, 6);
            g2.setColor(sel ? Color.BLACK : Color.WHITE);
            g2.setFont(fontMono);
            String mp = skillMp[i] > 0 ? " (" + skillMp[i] + "mp)" : "";
            g2.drawString((i+1) + ". " + skillNames[i] + mp, 182, hy + 42 + i * 28);
        }

        // right: inventory + controls
        g2.setFont(fontBold);
        g2.setColor(new Color(255, 215, 0));
        g2.drawString("INVENTORY", 345, hy + 20);
        g2.setFont(fontMono);
        g2.setColor(Color.WHITE);
        if (inventory.isEmpty()) {
            g2.setColor(new Color(100,100,100));
            g2.drawString("(empty)", 345, hy + 38);
        } else {
            for (int i = 0; i < Math.min(inventory.size(), 4); i++)
                g2.drawString("• " + inventory.get(i), 345, hy + 38 + i * 16);
        }

        g2.setFont(fontMono);
        g2.setColor(new Color(160, 160, 180));
        g2.drawString("H: use potion  ESC: flee", 345, hy + 110);
        g2.drawString("Click NPC/Enemy (adjacent)", 345, hy + 126);
        g2.drawString("WASD / Arrows: move", 345, hy + 142);

        // state indicator
        String stateStr = gameState == State.COMBAT ? "⚔ COMBAT" :
                          gameState == State.DIALOGUE ? "💬 DIALOGUE" : "🌍 EXPLORE";
        g2.setFont(fontBold);
        g2.setColor(new Color(100, 200, 100));
        g2.drawString(stateStr, 175, hy + 150);
    }

    // ── dialogue box ───────────────────────────────────────────
    void drawDialogueBox(Graphics2D g2) {
        int bx = 20, by = H - 80, bw = W - 40, bh = 70;
        g2.setColor(new Color(10, 10, 40, 220));
        g2.fillRoundRect(bx, by, bw, bh, 12, 12);
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(bx, by, bw, bh, 12, 12);

        if (activeNPC != null) {
            g2.setFont(fontBold);
            g2.setColor(activeNPC.color);
            g2.drawString(activeNPC.name + ":", bx + 12, by + 22);
            g2.setFont(fontMono);
            g2.setColor(Color.WHITE);
            g2.drawString(dialogueLine, bx + 12, by + 42);
            g2.setFont(new Font("Monospaced", Font.ITALIC, 11));
            g2.setColor(new Color(180, 180, 180));
            g2.drawString("[ SPACE / click to continue ]", bx + 12, by + 60);
        }
    }

    // ── combat overlay ─────────────────────────────────────────
    void drawCombatOverlay(Graphics2D g2) {
        if (activeEnemy == null) return;
        // dim world
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, W, H);

        // combat panel
        int cx = W / 2 - 160, cy = 40, cw = 320, ch = H - 80;
        g2.setColor(new Color(15, 15, 35, 240));
        g2.fillRoundRect(cx, cy, cw, ch, 14, 14);
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(cx, cy, cw, ch, 14, 14);

        g2.setFont(fontTitle);
        g2.setColor(new Color(255, 80, 80));
        drawCentered(g2, "⚔ BATTLE ⚔", cy + 28);

        // enemy display
        int ex = cx + 80, ey = cy + 50;
        g2.setColor(activeEnemy.color);
        g2.fillOval(ex, ey, 60, 70);
        g2.setColor(Color.RED);
        g2.fillOval(ex+12, ey+18, 10, 10);
        g2.fillOval(ex+35, ey+18, 10, 10);

        g2.setFont(fontBold);
        g2.setColor(Color.WHITE);
        drawCentered(g2, activeEnemy.name, ey + 90);
        drawBar(g2, cx+40, ey+95, 240, 12, activeEnemy.hp, activeEnemy.maxHp,
                new Color(200,40,40), new Color(60,0,0));
        g2.setFont(fontMono);
        g2.setColor(Color.WHITE);
        drawCentered(g2, "HP: " + activeEnemy.hp + "/" + activeEnemy.maxHp, ey + 120);

        // combat log
        int ly = cy + 180;
        g2.setFont(fontMono);
        for (String line : combatLog) {
            g2.setColor(line.contains("You") ? new Color(120,200,255) :
                        line.contains("defeated") ? new Color(100,255,100) :
                        new Color(255, 140, 140));
            g2.drawString(line, cx + 14, ly);
            ly += 16;
        }

        // turn indicator
        g2.setFont(fontBold);
        g2.setColor(playerTurn ? new Color(100, 255, 100) : new Color(255, 100, 100));
        drawCentered(g2, playerTurn ? "YOUR TURN — SPACE to attack" : "Enemy is acting...", cy + ch - 18);
    }

    // ── death screen ───────────────────────────────────────────
    void drawDeathScreen(Graphics2D g2) {
        g2.setColor(new Color(0,0,0,180));
        g2.fillRect(0, 0, W, H);
        g2.setFont(new Font("Monospaced", Font.BOLD, 32));
        g2.setColor(new Color(200, 0, 0));
        drawCentered(g2, "YOU DIED", H/2 - 20);
        g2.setFont(fontMono);
        g2.setColor(Color.WHITE);
        drawCentered(g2, "Close and restart to play again", H/2 + 20);
    }

    // ── flash message ──────────────────────────────────────────
    void drawFlash(Graphics2D g2) {
        g2.setFont(fontBold);
        g2.setColor(new Color(255, 220, 50, Math.min(255, flashTimer * 4)));
        int fw = g2.getFontMetrics().stringWidth(flashMsg);
        g2.drawString(flashMsg, (W - fw) / 2, H / 2 - 60);
    }

    // ── helpers ────────────────────────────────────────────────
    void drawBar(Graphics2D g2, int x, int y, int w, int h, int val, int max, Color fill, Color bg) {
        g2.setColor(bg);
        g2.fillRoundRect(x, y, w, h, 4, 4);
        int filled = (int)((float)val / max * w);
        g2.setColor(fill);
        g2.fillRoundRect(x, y, filled, h, 4, 4);
        g2.setColor(new Color(255,255,255,60));
        g2.drawRoundRect(x, y, w, h, 4, 4);
    }

    void drawCentered(Graphics2D g2, String txt, int y) {
        int tw = g2.getFontMetrics().stringWidth(txt);
        g2.drawString(txt, (W - tw) / 2, y);
    }

    // ── launch ─────────────────────────────────────────────────
    public static void launchFromMainframe() {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("RuneClone");
            RuneClone game = new RuneClone();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(game);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setResizable(false);
            f.setVisible(true);
            game.startGameLoop();
        });
    }
}
