package myapplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class Snakegame extends JPanel implements ActionListener {

    static final int BOX = 24, COLS = 22, ROWS = 22;
    static final int W = COLS * BOX, H = ROWS * BOX;

    static final int INITIAL_DELAY = 100;
    static final int MIN_DELAY     = 45;
    static final int DELAY_STEP    = 9;
    static final int SPEED_EVERY   = 5;
    static final int FOOD_COUNT    = 3;

    static final Color CLR_BG         = new Color(10, 12, 15);
    static final Color CLR_GRID       = new Color(255, 255, 255, 8);
    static final Color CLR_BORDER     = new Color(255, 255, 255, 30);
    static final Color CLR_HEAD       = new Color(34, 197, 94);
    static final Color CLR_HEAD_DARK  = new Color(21, 128, 61);
    static final Color CLR_FOOD       = new Color(239, 68, 68);
    static final Color CLR_SPECIAL    = new Color(245, 158, 11);
    static final Color CLR_HUD_BG     = new Color(17, 24, 39, 220);
    static final Color CLR_PANEL      = new Color(13, 17, 23);
    static final Color CLR_TEXT       = new Color(226, 232, 240);
    static final Color CLR_MUTED      = new Color(100, 116, 139);
    static final Color CLR_GOLD       = new Color(251, 191, 36);

    enum GameState { PLAYING, PAUSED, GAME_OVER, MENU }

    LinkedList<Point>  snake    = new LinkedList<>();
    java.util.List<FoodItem> foods = new ArrayList<>();
    java.util.List<Particle> particles = new ArrayList<>();

    String    dir      = "RIGHT";
    String    nextDir  = "RIGHT";
    GameState state    = GameState.MENU;
    boolean   wrapWalls = false;
    boolean   showGrid = true;
    boolean   soundOn = true;

    public javax.swing.Timer timer = new javax.swing.Timer(INITIAL_DELAY, this);
    int score      = 0;
    int highScore  = 0;
    int level      = 1;
    int eatCount   = 0;

    float animTick = 0f;
    private final Random rng = new Random();

    // interpolation / rendering helpers
    java.util.List<Point> prevSnake = new ArrayList<>();
    float renderT = 1f;
    float bgPhase = 0f;

    static class FoodItem {
        int x, y;
        boolean special;
        float pulse = 0f;
        FoodItem(int x, int y, boolean special) { this.x = x; this.y = y; this.special = special; }
    }

    static class Particle {
        float x, y, vx, vy, life, r;
        Color color;
        Particle(int px, int py, Color c, Random rng) {
            x = px + 12; y = py + 12;
            vx = (rng.nextFloat() - 0.5f) * 6f;
            vy = (rng.nextFloat() - 0.5f) * 6f - 2f;
            life = 1f; r = 2f + rng.nextFloat() * 3f;
            color = c;
        }
        boolean update() {
            x += vx; y += vy; vy += 0.18f;
            vx *= 0.97f; life -= 0.045f; r *= 0.96f;
            return life > 0;
        }
    }

    public Snakegame() {
        setPreferredSize(new Dimension(W, H + 60));
        setBackground(CLR_BG);
        setFocusable(true);
        setDoubleBuffered(true);

        javax.swing.Timer renderTimer = new javax.swing.Timer(16, e -> {
            renderT = Math.min(1f, renderT + 0.06f);
            bgPhase += 0.0025f;
            if (bgPhase > 1f) bgPhase -= 1f;
            repaint();
        });
        renderTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();
                switch (k) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        if (!dir.equals("RIGHT")) nextDir = "LEFT";
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        if (!dir.equals("LEFT")) nextDir = "RIGHT";
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        if (!dir.equals("DOWN")) nextDir = "UP";
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        if (!dir.equals("UP")) nextDir = "DOWN";
                        break;
                    case KeyEvent.VK_P:
                        if (state == GameState.PLAYING) state = GameState.PAUSED;
                        else if (state == GameState.PAUSED) state = GameState.PLAYING;
                        break;
                    case KeyEvent.VK_T:
                        wrapWalls = !wrapWalls;
                        break;
                    case KeyEvent.VK_G:
                        showGrid = !showGrid;
                        break;
                    case KeyEvent.VK_M:
                        soundOn = !soundOn;
                        break;
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_ENTER:
                        if (state == GameState.GAME_OVER || state == GameState.MENU) {
                            resetGame(); timer.restart();
                        } else if (state == GameState.PAUSED) {
                            state = GameState.PLAYING;
                        }
                        break;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        resetGame();
    }

    public void resetGame() {
        snake.clear(); foods.clear(); particles.clear(); prevSnake.clear();
        int cx = COLS / 2, cy = ROWS / 2;
        for (int i = 0; i < 4; i++) snake.add(new Point((cx - i) * BOX, cy * BOX));
        dir = "RIGHT"; nextDir = "RIGHT";
        score = 0; level = 1; eatCount = 0;
        state = GameState.PLAYING;
        for (int i = 0; i < FOOD_COUNT; i++) spawnFood();
        timer.setDelay(INITIAL_DELAY);
        renderT = 1f;
        requestFocusInWindow();
    }

    void spawnFood() {
        Point f;
        boolean special = rng.nextFloat() < 0.15f;
        int tries = 0;
        do {
            f = new Point(rng.nextInt(COLS) * BOX, rng.nextInt(ROWS) * BOX);
            tries++;
        } while (tries < 300 && (isOnSnake(f) || isOnFood(f)));
        foods.add(new FoodItem(f.x, f.y, special));
    }

    boolean isOnSnake(Point p) { return snake.stream().anyMatch(s -> s.equals(p)); }
    boolean isOnFood(Point p)  { return foods.stream().anyMatch(f -> f.x == p.x && f.y == p.y); }

    void spawnParticles(int x, int y, Color c, int count) {
        for (int i = 0; i < count; i++) particles.add(new Particle(x, y, c, rng));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state != GameState.PLAYING) return;

        prevSnake.clear();
        for (Point p : snake) prevSnake.add(new Point(p));
        renderT = 0f;

        dir = nextDir;
        animTick = 0f;

        Point head = new Point(snake.getFirst());
        switch (dir) {
            case "LEFT":  head.x -= BOX; break;
            case "RIGHT": head.x += BOX; break;
            case "UP":    head.y -= BOX; break;
            case "DOWN":  head.y += BOX; break;
        }

        if (wrapWalls) {
            head.x = ((head.x / BOX + COLS) % COLS) * BOX;
            head.y = ((head.y / BOX + ROWS) % ROWS) * BOX;
        } else if (head.x < 0 || head.y < 0 || head.x >= W || head.y >= H) {
            endGame(); return;
        }

        if (isOnSnake(head)) { endGame(); return; }

        snake.addFirst(head);

        FoodItem eaten = foods.stream()
            .filter(f -> f.x == head.x && f.y == head.y)
            .findFirst().orElse(null);

        if (eaten != null) {
            int pts = eaten.special ? 3 : 1;
            score += pts;
            if (score > highScore) highScore = score;
            eatCount++;
            spawnParticles(head.x, head.y, eaten.special ? CLR_SPECIAL : CLR_FOOD, eaten.special ? 18 : 11);
            foods.remove(eaten);
            spawnFood();
            if (eatCount % SPEED_EVERY == 0) {
                level = Math.min(level + 1, 10);
                timer.setDelay(Math.max(MIN_DELAY, timer.getDelay() - DELAY_STEP));
            }
        } else {
            snake.removeLast();
        }
    }

    private void endGame() {
        timer.stop();
        state = GameState.GAME_OVER;
        if (score > highScore) highScore = score;
        spawnParticles(snake.getFirst().x, snake.getFirst().y, CLR_FOOD, 22);
        spawnParticles(snake.getFirst().x, snake.getFirst().y, Color.WHITE, 8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        particles.removeIf(p -> !p.update());
        animTick = Math.min(animTick + 0.05f, 1f);

        drawBackground(g2);
        if (showGrid) drawGrid(g2);
        drawParticles(g2);
        drawFoods(g2);
        drawSnake(g2);
        drawHUD(g2);

        if (state == GameState.PAUSED)    drawPauseOverlay(g2);
        if (state == GameState.GAME_OVER) drawGameOverOverlay(g2);
        if (state == GameState.MENU)      drawMenuOverlay(g2);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        float phase = bgPhase;
        Color c1 = Color.getHSBColor((phase * 0.08f) % 1f, 0.18f, 0.12f);
        Color c2 = Color.getHSBColor((phase * 0.08f + 0.06f) % 1f, 0.12f, 0.18f);
        GradientPaint gp = new GradientPaint(0, 0, c1, 0, H, c2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, W, H);

        g2.setColor(new Color(255,255,255,6));
        for (int i = 0; i < 6; i++) {
            int y = (int)(H * (i / 6.0));
            g2.fillRect(0, y, W, 1);
        }

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
        g2.setColor(new Color(0, 0, 0));
        int pad = 24;
        g2.fillRoundRect(-pad, -pad, W + pad*2, H + pad*2, 60, 60);
        g2.setComposite(old);
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(CLR_GRID);
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x <= W; x += BOX) g2.drawLine(x, 0, x, H);
        for (int y = 0; y <= H; y += BOX) g2.drawLine(0, y, W, y);
        g2.setColor(CLR_BORDER);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(0, 0, W - 1, H - 1);
    }

    private void drawParticles(Graphics2D g2) {
        for (Particle p : particles) {
            float alpha = Math.max(0, Math.min(1, p.life));
            g2.setColor(new Color(
                p.color.getRed(), p.color.getGreen(), p.color.getBlue(),
                (int)(alpha * 200)));
            g2.fillOval((int)(p.x - p.r), (int)(p.y - p.r), (int)(p.r * 2), (int)(p.r * 2));
        }
    }

    private void drawFoods(Graphics2D g2) {
        long now = System.currentTimeMillis();
        for (FoodItem f : foods) {
            f.pulse = (float)(Math.sin(now / 400.0 + f.x) * 0.5 + 0.5);
            float r = BOX / 2f - 3f + f.pulse * 2f;
            float cx = f.x + BOX / 2f, cy = f.y + BOX / 2f;

            if (f.special) {
                Composite old = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.setColor(CLR_SPECIAL);
                g2.fillOval((int)(cx - r*1.6f), (int)(cy - r*1.6f), (int)(r*3.2f), (int)(r*3.2f));
                g2.setComposite(old);

                g2.setColor(new Color(245, 158, 11, 40));
                g2.fillOval((int)(cx - BOX/2f), (int)(cy - BOX/2f), BOX, BOX);
                g2.setColor(CLR_SPECIAL);
                drawStar(g2, cx, cy, r, 5);
                g2.setColor(new Color(255, 255, 200, 160));
                g2.fillOval((int)(cx - r * 0.25f - 1), (int)(cy - r * 0.3f - 1), 4, 4);
            } else {
                RadialGradientPaint rp = new RadialGradientPaint(
                    cx - r * 0.25f, cy - r * 0.25f, r * 1.1f,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(252, 100, 100), CLR_FOOD, new Color(153, 27, 27)}
                );
                g2.setPaint(rp);
                g2.fillOval((int)(cx - r), (int)(cy - r), (int)(r * 2), (int)(r * 2));

                g2.setColor(new Color(255, 255, 255, 150));
                g2.fillOval((int)(cx - r * 0.45f), (int)(cy - r * 0.45f),
                            (int)(r * 0.45f), (int)(r * 0.35f));

                g2.setColor(new Color(34, 197, 94, 200));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new QuadCurve2D.Float(cx, cy - r, cx + r * 0.4f, cy - r * 1.35f, cx + r * 0.3f, cy - r * 0.7f));
                g2.setStroke(new BasicStroke(1f));
            }
        }
    }

    private void drawStar(Graphics2D g2, float cx, float cy, float r, int points) {
        GeneralPath path = new GeneralPath();
        float inner = r * 0.42f;
        for (int i = 0; i < points * 2; i++) {
            double ang = -Math.PI / 2 + (i * Math.PI / points);
            float rad = (i % 2 == 0) ? r : inner;
            float px = cx + (float)(Math.cos(ang) * rad);
            float py = cy + (float)(Math.sin(ang) * rad);
            if (i == 0) path.moveTo(px, py); else path.lineTo(px, py);
        }
        path.closePath();
        g2.fill(path);
    }

    private Point lerpPoint(Point a, Point b, float t) {
        int x = Math.round(a.x + (b.x - a.x) * t);
        int y = Math.round(a.y + (b.y - a.y) * t);
        return new Point(x, y);
    }

    private void drawSnake(Graphics2D g2) {
        if (snake.isEmpty()) return;
        int size = snake.size();
        for (int i = 0; i < size; i++) {
            Point cur = snake.get(i);
            Point prev = (i < prevSnake.size()) ? prevSnake.get(i) : cur;
            Point p = lerpPoint(prev, cur, renderT);
            float t = (float) i / size;
            if (i == 0) drawHeadInterpolated(g2, p);
            else drawBodySegment(g2, p, t, i);
        }
    }

    private void drawHeadInterpolated(Graphics2D g2, Point p) {
        if (state != GameState.GAME_OVER) {
            Composite old = g2.getComposite();
            for (int i = 0; i < 3; i++) {
                float a = 0.06f * (3 - i);
                int pad = 6 + i * 6;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
                g2.setColor(CLR_HEAD);
                g2.fillRoundRect(p.x - pad, p.y - pad, BOX + pad * 2, BOX + pad * 2, 20, 20);
            }
            g2.setComposite(old);
        }

        GradientPaint gp = new GradientPaint(p.x, p.y, CLR_HEAD, p.x + BOX, p.y + BOX, CLR_HEAD_DARK);
        g2.setPaint(gp);
        g2.fillRoundRect(p.x + 1, p.y + 1, BOX - 2, BOX - 2, 9, 9);

        g2.setColor(new Color(255, 255, 255, 60));
        g2.fillRoundRect(p.x + 3, p.y + 2, BOX - 8, (BOX - 4) / 2, 5, 5);

        g2.setColor(new Color(0, 0, 0, 100));
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(p.x + 1, p.y + 1, BOX - 2, BOX - 2, 9, 9);
        g2.setStroke(new BasicStroke(1f));

        g2.setColor(Color.WHITE);
        int[][] eyes = eyeOffsets(dir);
        g2.fillOval(p.x + eyes[0][0], p.y + eyes[0][1], 5, 5);
        g2.fillOval(p.x + eyes[1][0], p.y + eyes[1][1], 5, 5);

        g2.setColor(new Color(10, 10, 10));
        g2.fillOval(p.x + eyes[0][0] + 1, p.y + eyes[0][1] + 1, 3, 3);
        g2.fillOval(p.x + eyes[1][0] + 1, p.y + eyes[1][1] + 1, 3, 3);

        g2.setColor(Color.WHITE);
        g2.fillOval(p.x + eyes[0][0] + 1, p.y + eyes[0][1] + 1, 1, 1);
        g2.fillOval(p.x + eyes[1][0] + 1, p.y + eyes[1][1] + 1, 1, 1);
    }

    private void drawBodySegment(Graphics2D g2, Point p, float t, int idx) {
        int green = (int)(163 - t * 63);
        Color segColor = new Color(20, Math.max(60, green), 40);
        Color segDark  = new Color(15, Math.max(40, green - 40), 28);

        GradientPaint gp = new GradientPaint(
            p.x + 1, p.y + 1, segColor,
            p.x + BOX - 2, p.y + BOX - 2, segDark
        );
        g2.setPaint(gp);
        g2.fillRoundRect(p.x + 2, p.y + 2, BOX - 4, BOX - 4, 7, 7);

        if (idx % 2 == 0) {
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillOval(p.x + 5, p.y + 5, BOX - 12, BOX - 12);
        }

        g2.setColor(new Color(0, 0, 0, 80));
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(p.x + 2, p.y + 2, BOX - 4, BOX - 4, 7, 7);
        g2.setStroke(new BasicStroke(1f));
    }

    private int[][] eyeOffsets(String d) {
        int h = BOX / 2;
        switch (d) {
            case "RIGHT": return new int[][]{{h + 2, 3}, {h + 2, BOX - 9}};
            case "LEFT":  return new int[][]{{3,     3}, {3,     BOX - 9}};
            case "UP":    return new int[][]{{3,     3}, {BOX - 9, 3}};
            default:      return new int[][]{{3,     h + 3}, {BOX - 9, h + 3}};
        }
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(CLR_HUD_BG);
        g2.fillRoundRect(6, H - 6, W - 12, 66, 10, 10);
        g2.setColor(CLR_BORDER);
        g2.drawLine(0, H, W, H);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(CLR_MUTED);
        g2.drawString("SCORE", 18, H + 18);
        g2.setFont(new Font("Monospaced", Font.BOLD, 22));
        g2.setColor(CLR_TEXT);
        g2.drawString(String.valueOf(score), 18, H + 44);

        int lx = W / 2 - 20;
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(CLR_MUTED);
        g2.drawString("LEVEL", lx, H + 18);
        g2.setFont(new Font("Monospaced", Font.BOLD, 22));
        g2.setColor(CLR_HEAD);
        g2.drawString(String.valueOf(level), lx, H + 44);

        int bx = W / 2 + 20, bw = 100;
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(CLR_MUTED);
        g2.drawString("SPEED", bx, H + 18);
        g2.setColor(new Color(30, 40, 50));
        g2.fillRoundRect(bx, H + 26, bw, 8, 6, 6);
        float pct = (float)(level - 1) / 9f;
        Color barColor = pct < 0.5f ? CLR_HEAD : (pct < 0.8f ? CLR_SPECIAL : CLR_FOOD);
        g2.setColor(barColor);
        g2.fillRoundRect(bx, H + 26, (int)(bw * Math.max(0.08f, pct)), 8, 6, 6);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(CLR_MUTED);
        g2.drawString("BEST", W - 84, H + 18);
        g2.setFont(new Font("Monospaced", Font.BOLD, 22));
        g2.setColor(CLR_GOLD);
        g2.drawString(String.valueOf(highScore), W - 84, H + 44);

        if (wrapWalls) {
            g2.setFont(new Font("Monospaced", Font.ITALIC, 10));
            g2.setColor(new Color(34, 197, 94, 200));
            g2.drawString("WALLS: ON", W - 180, H + 44);
        } else {
            g2.setFont(new Font("Monospaced", Font.ITALIC, 10));
            g2.setColor(new Color(200, 200, 200, 120));
            g2.drawString("WALLS: OFF", W - 180, H + 44);
        }

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(new Color(200,200,200,160));
        g2.drawString("G = grid  T = wrap  M = sound", 18, H + 62);
    }

    private void drawMenuOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, W, H);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.setColor(CLR_HEAD);
        drawCentered(g2, "SNAKE", H / 2 - 60);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(CLR_MUTED);
        drawCentered(g2, "ARROW KEYS ", H / 2 - 20);
        drawCentered(g2, "", H / 2 - 6);
        drawCentered(g2, "", H / 2 + 10);
        drawPillButton(g2, "SPACE TO RESTART", W / 2, H / 2 + 60, CLR_HEAD);
    }

    private void drawPauseOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, W, H);
        g2.setFont(new Font("Monospaced", Font.BOLD, 34));
        g2.setColor(CLR_TEXT);
        drawCentered(g2, "PAUSED", H / 2 - 10);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(CLR_MUTED);
        drawCentered(g2, " TO RESUME", H / 2 + 18);
    }

    private void drawGameOverOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, W, H);
        int cx = W / 2, cy = H / 2;
        int bw = 300, bh = 200;
        g2.setColor(CLR_PANEL);
        g2.fillRoundRect(cx - bw/2, cy - bh/2, bw, bh, 18, 18);
        g2.setColor(new Color(239, 68, 68, 90));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cx - bw/2, cy - bh/2, bw, bh, 18, 18);
        g2.setStroke(new BasicStroke(1f));
        g2.setFont(new Font("Monospaced", Font.BOLD, 30));
        g2.setColor(CLR_FOOD);
        drawCentered(g2, "GAME OVER", cy - 50);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(CLR_MUTED);
        drawCentered(g2, "SCORE", cy - 18);
        g2.setFont(new Font("Monospaced", Font.BOLD, 28));
        g2.setColor(CLR_TEXT);
        drawCentered(g2, String.valueOf(score), cy + 8);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(CLR_GOLD);
        drawCentered(g2, "BEST: " + highScore, cy + 40);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(CLR_MUTED);
        drawCentered(g2, "ENJOYING???", cy + 70);
    }

    private void drawPillButton(Graphics2D g2, String text, int cx, int cy, Color c) {
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text);
        int bw = tw + 36, bh = 34;
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
        g2.fillRoundRect(cx - bw/2, cy - bh/2, bw, bh, bh, bh);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(cx - bw/2, cy - bh/2, bw, bh, bh, bh);
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.drawString(text, cx - tw/2, cy + fm.getAscent()/2 - 2);
    }

    private void drawCentered(Graphics2D g2, String txt, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(txt, (W - fm.stringWidth(txt)) / 2, y);
    }

    public static void launchFromMainframe() {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Snake");
            Snakegame game = new Snakegame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(game, BorderLayout.CENTER);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
            controls.setBackground(new Color(16, 18, 22));
            controls.setPreferredSize(new Dimension(W, 54));

            JButton restartBtn = makeBtn("↺ Restart");
            JButton pauseBtn   = makeBtn("⏸ Pause/Resume");
            JButton settingsBtn= makeBtn("⚙ Settings");
            JButton backBtn    = makeBtn("← Back");

            controls.add(restartBtn);
            controls.add(pauseBtn);
            controls.add(settingsBtn);
            controls.add(backBtn);
            f.add(controls, BorderLayout.SOUTH);

            restartBtn.addActionListener(e -> {
                game.resetGame();
                game.timer.restart();
                game.requestFocusInWindow();
            });

            pauseBtn.addActionListener(e -> {
                if (game.state == GameState.PLAYING) game.state = GameState.PAUSED;
                else if (game.state == GameState.PAUSED) game.state = GameState.PLAYING;
                game.repaint();
                game.requestFocusInWindow();
            });

            settingsBtn.addActionListener(e -> {
                JDialog dlg = new JDialog(f, "Settings", true);
                dlg.setLayout(new BorderLayout());
                JPanel p = new JPanel(new GridLayout(0,1,6,6));
                p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
                JCheckBox gridCb = new JCheckBox("Show grid", game.showGrid);
                JCheckBox wrapCb = new JCheckBox("Wrap walls", game.wrapWalls);
                JCheckBox soundCb= new JCheckBox("Sound", game.soundOn);
                JSlider speed = new JSlider(45, 200, game.timer.getDelay());
                speed.setMajorTickSpacing(50);
                speed.setPaintTicks(true);
                speed.setPaintLabels(true);
                p.add(gridCb); p.add(wrapCb); p.add(soundCb);
                p.add(new JLabel("Tick delay (lower = faster)"));
                p.add(speed);
                dlg.add(p, BorderLayout.CENTER);
                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton ok = new JButton("OK");
                ok.addActionListener(a -> {
                    game.showGrid = gridCb.isSelected();
                    game.wrapWalls = wrapCb.isSelected();
                    game.soundOn = soundCb.isSelected();
                    game.timer.setDelay(speed.getValue());
                    dlg.dispose();
                    game.requestFocusInWindow();
                });
                btns.add(ok);
                dlg.add(btns, BorderLayout.SOUTH);
                dlg.pack();
                dlg.setLocationRelativeTo(f);
                dlg.setVisible(true);
            });

            backBtn.addActionListener(e -> {
                f.dispose();
                new Mainframe().setVisible(true);
            });

            f.pack();
            f.setLocationRelativeTo(null);
            f.setResizable(false);
            f.setVisible(true);
            game.requestFocusInWindow();
        });
    }

    private static JButton makeBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Monospaced", Font.PLAIN, 12));
        b.setBackground(new Color(28, 32, 38));
        b.setForeground(new Color(200, 210, 220));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 66, 74), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setForeground(new Color(245,245,245));
                b.setBackground(new Color(36, 42, 50));
            }
            public void mouseExited(MouseEvent e) {
                b.setForeground(new Color(200, 210, 220));
                b.setBackground(new Color(28, 32, 38));
            }
        });
        return b;
    }
}
