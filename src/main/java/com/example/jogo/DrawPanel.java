package com.example.jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DrawPanel extends JPanel implements KeyListener {

    BufferedImage buffer;
    Entity player;
    Entity boss;
    int max_bullets = 40;
    int max_enemies = 3;
    int max_drops = 5;
    int max_life = 5;
    Entity[] bullet = new Entity[max_bullets / 2];
    Entity[] bulletE = new Entity[max_bullets / 2];
    Entity[] enemy = new Entity[max_enemies];
    Entity[] drops = new Entity[max_drops];
    int bn = 0, ebn = 0, en = 0, dn = 0;
    int enemy_counter = 0;
    int lives = 0, score = 0, money = 0, highscore = 0, level = 1, kills = 0;
    boolean pause = false, quit = false, menu = true;
    int model_level, speed_level, firerate_level, damage_level, enemy_type, bullet_level;
    BufferedImage[] imgs = new BufferedImage[31];
    File[] files = new File[6];
    AudioInputStream[] audioIn = new AudioInputStream[6];
    Clip[] clip = new Clip[6];
    boolean god_mode = false, mute = true;

    private static final int PLAYER_FIRERATE = 10;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 50;
    private static final int PLAYER_SPEED = 3;
    private static final int BOSS_LIFE = 50;
    private static final int BOSS_SKIN1 = 27;
    private static final int BOSS_SKIN2 = 28;
    private static final int BOSS_WIDTH = 100;
    private static final int BOSS_HEIGHT = 200;
    private static final int MAX_LEVEL = 8;
    private static final int MID_LEVEL = 5;

    public DrawPanel() {
        setIgnoreRepaint(true);
        addKeyListener(this);
        setFocusable(true);
        initGifs();
        initWav();
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        //movement
        if (key == KeyEvent.VK_UP) {
            player.direction[0] = true;
        }
        if (key == KeyEvent.VK_DOWN) {
            player.direction[1] = true;
        }
        if (key == KeyEvent.VK_LEFT) {
            player.direction[2] = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            player.direction[3] = true;
        }
        if (key == KeyEvent.VK_SPACE) {
            menu = false;
        }
        if ((key == KeyEvent.VK_CAPS_LOCK) && menu) {
            // toggle god mode
            god_mode = !god_mode;
            // TODO CLEAN trace GOD MODE
            System.out.println("GOD MODE =" + god_mode);
        }
        if ((key == KeyEvent.VK_Q) && (menu || pause)) {
            quit = true;
            pause = false;
            menu = false;
        }
        if (key == KeyEvent.VK_P && !menu) {
            pause = !pause;
        }
        if (key == KeyEvent.VK_X) {
            if (!pause) {
                shoot();
            } else {
                pause = false;
            }
        }
        if (key == KeyEvent.VK_M) {
            mute = !mute;
        }
        //upgrades
        if (key == KeyEvent.VK_1 && pause) {
            if (money >= model_level * 200 && model_level < 3) {
                money -= model_level * 200;
                model_level++;
                max_life += 5 * model_level;// change ship graphics
                player.life = max_life;
            } else if (!mute) {
                try {
                    clip[2].start();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
        if (key == KeyEvent.VK_2 && pause) {
            if (money >= speed_level * 300 && speed_level < 5) {
                player.speed++;
                money -= speed_level * 300;
                speed_level++;
            } else if (!mute) {
                try {
                    clip[2].start();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
        if (key == KeyEvent.VK_3 && pause) {
            if (money >= firerate_level * 500 && firerate_level < 5) {
                player.firerate -= 10;
                money -= firerate_level * 500;
                firerate_level++;
            } else if (!mute) {
                try {
                    clip[2].start();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
        if (key == KeyEvent.VK_4 && pause) {
            if (money >= damage_level * 700 && damage_level < 5) {
                money -= damage_level * 700;
                damage_level++;
                bullet_level++;
            } else if (!mute) {
                try {
                    clip[2].start();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
        if (key == KeyEvent.VK_5 && pause) {
            if (money >= 2000 && lives < 10) {
                money -= 2000;
                lives++;
            } else if (!mute) {
                try {
                    clip[2].start();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            player.direction[0] = false;
        }
        if (key == KeyEvent.VK_DOWN) {
            player.direction[1] = false;
        }
        if (key == KeyEvent.VK_LEFT) {
            player.direction[2] = false;
        }
        if (key == KeyEvent.VK_RIGHT) {
            player.direction[3] = false;
        }
    }

    public void initialize() {
        buffer = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        player = new Entity(0, 300, PLAYER_SPEED, PLAYER_FIRERATE, PLAYER_WIDTH, PLAYER_HEIGHT);
        boss = null;
        lives = 5;
        model_level = 1;
        speed_level = 1;
        firerate_level = 1;
        damage_level = 1;
        bullet_level = 9;
        max_life = 5;
        player.life = max_life;
        score = 0;
        bn = 0;
        ebn = 0;
        en = 0;
        dn = 0;
        enemy_counter = 0;
        level = 1;
        kills = 0;
        money = 0; // remove after test ends
        enemy_type = 13;
        for (int i = 0; i < max_enemies; i++) {
            enemy[i] = null;
        }

        enemy[en] = new Entity(800, 300, 1, 100, 50, 50);
        en = (en + 1) % max_enemies;
        for (int i = 0; i < max_bullets / 2; i++) {
            bullet[i] = null;
        }
        for (int i = 0; i < max_bullets / 2; i++) {
            bulletE[i] = null;
        }
        for (int i = 0; i < max_drops / 2; i++) {
            drops[i] = null;
        }
    }

    public void update() {
        if (player.life <= 0) {
            player.life = max_life;
            lives--;
            if (lives <= 0) {
                // Encerrar o jogo quando as vidas chegam a zero
                System.out.println("Game Over! Final Score: " + score);
                menu = true; // Retorna ao menu principal
                return; // Sai do método update()
            }
            player.respawn(true);
        }
        if (!mute) {
            for (int i = 1; i < 6; i++) {
                if (!clip[i].isRunning()) {
                    clip[i].setFramePosition(0);
                }
            }
        }
        player.move();
        player.checkOutOfBounds();
        if (boss != null) {
            if (boss.checkOutOfBounds()) {
                boss.direction[0] = !boss.direction[0];
                boss.direction[1] = !boss.direction[1];
            }
            boss.move();
            if (boss.life <= 0) {
                boss = null;
                if (!mute) {
                    try {
                        clip[3].start();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }         
                if (level == MAX_LEVEL) {
                    System.out.println("Level " + level + " completed!");
                    highscore = score;
                    menu = true;
                } else {
                    level++;
                    System.out.println("BOSS DEFEATED! Level " + level + " increased!");
                }
            }
            if (boss.count >= boss.firerate) {
                shootE(boss);
                boss.count = 0;
            }
        }
        for (int i = 0; i < max_enemies; i++) {
            if (enemy[i] == null) {
                continue;
            }
            follow_move(enemy[i], player);
            enemy[i].move();
            enemy[i].checkOutOfBounds();
            if (enemy[i].count >= enemy[i].firerate) {
                shootE(enemy[i]);
                enemy[i].count = 0;
            }
            if (enemy[i].life <= 0) {
                drops[dn] = new Entity(enemy[i].centerX - 10, enemy[i].centerY - 10, 1, 0, 20, 20);
                drops[dn].direction[2] = true;
                dn = (dn + 1) % max_drops;
                enemy[i] = null;
                kills++;
                if (!mute) {
                    try {
                        clip[3].start();
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                score += 500;
            }
        }

        for (int i = 0; i < max_bullets / 2; i++) {
            if (bullet[i] != null) {
                //follow_move(bullet[i]);
                bullet[i].move();
                if (bullet[i].checkOutOfBounds()) {
                    bullet[i] = null;
                }
            }
        }

        for (int i = 0; i < max_bullets / 2; i++) {
            if (bulletE[i] != null) {
                //follow_move(bullet[i]);
                bulletE[i].move();
                if (bulletE[i].checkOutOfBounds()) {
                    bulletE[i] = null;
                }
            }
        }
        
        for (int i = 0; i < max_drops; i++) {
            if (drops[i] != null) {
                //follow_move(bullet[i]);
                drops[i].move();
                if (drops[i].checkOutOfBounds()) {
                    drops[i] = null;
                }
            }
        }
    }

    public void checkCollisions() {
        if (boss != null && player.getBounds().intersects(boss.getBounds())) {
            boss.life--;
            player.life--;
            player.x -= 30;
        }
        for (int k = 0; k < max_bullets / 2; k++) {
            if (bullet[k] != null) {
                if (boss != null && bullet[k].getBounds().intersects(boss.getBounds())) {
                    bullet[k] = null;
                    boss.life -= damage_level;
                }
            }
        }
        for (int i = 0; i < max_enemies; i++) {
            if (enemy[i] == null) {
                continue;
            }
            if (player.getBounds().intersects(enemy[i].getBounds())) {
                if (!god_mode) {
                    player.life--;
                }
                enemy[i].life--;
                away_move(enemy[i]);
                if (!mute) {
                    try {
                        clip[5].start();
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                for (int j = 0; j < 30; j++) {
                    enemy[i].move();
                }
            }
            for (int k = 0; k < max_bullets / 2; k++) {
                if (bullet[k] != null) {
                    if (bullet[k].getBounds().intersects(enemy[i].getBounds())) {
                        bullet[k] = null;
                        enemy[i].life -= damage_level;
                    }
                }
            }
        }
        for (int i = 0; i < max_bullets / 2; i++) {
            if (bulletE[i] != null) {
                if (bulletE[i].getBounds().intersects(player.getBounds())) {
                    bulletE[i] = null;
                    if (!god_mode) {
                        player.life--;
                    }
                }
            }
        }
        for (int i = 0; i < max_drops; i++) {
            if (drops[i] != null) {
                if (drops[i].getBounds().intersects(player.getBounds())) {
                    drops[i] = null;
                    score += 100;
                    if (!mute) {
                        try {
                            clip[4].start();
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                    int p = (int) (Math.random() * 100);
                    if (p < 30) {
                        money += 50;
                    } else if (p < 50) {
                        money += 100;
                    } else if (p < 98) {
                        player.life = max_life;
                    } else if (lives < 10) {
                        lives++;
                    } else {
                        score += 1000;
                    }
                }
            }
        }
    }

    public void initGifs() {
      /*[0] background
        [1-3] naves
        [4-8] upgrades
        [9-12] balas
        [13-26] inimigos
        [27-298 bosses?*/

        try {
            imgs[0] = ImageIO.read(getClass().getResourceAsStream("/Imagens/background.png"));
            imgs[1] = ImageIO.read(getClass().getResourceAsStream("/Imagens/Nave1.png"));
            imgs[2] = ImageIO.read(getClass().getResourceAsStream("/Imagens/Nave3.png"));
            imgs[3] = ImageIO.read(getClass().getResourceAsStream("/Imagens/Nave2.png"));
            imgs[4] = ImageIO.read(getClass().getResourceAsStream("/Imagens/xtralife.png"));
            imgs[5] = ImageIO.read(getClass().getResourceAsStream("/Imagens/shipupgrade.png"));
            imgs[6] = ImageIO.read(getClass().getResourceAsStream("/Imagens/attackupgrade.png"));
            imgs[7] = ImageIO.read(getClass().getResourceAsStream("/Imagens/rateupgrade.png"));
            imgs[8] = ImageIO.read(getClass().getResourceAsStream("/Imagens/speedupgrade.png"));
            imgs[9] = ImageIO.read(getClass().getResourceAsStream("/Imagens/bullet1.png"));
            imgs[10] = ImageIO.read(getClass().getResourceAsStream("/Imagens/bullet2.png"));
            imgs[11] = ImageIO.read(getClass().getResourceAsStream("/Imagens/bullet3.png"));
            imgs[12] = ImageIO.read(getClass().getResourceAsStream("/Imagens/BBullet.png"));
            imgs[13] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy1.png"));
            imgs[14] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy2.png"));
            imgs[15] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy3.png"));
            imgs[16] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy4.png"));
            imgs[17] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy5.png"));
            imgs[18] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy6.png"));
            imgs[19] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy7.png"));
            imgs[20] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy8.png"));
            imgs[21] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy9.png"));
            imgs[22] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy10.png"));
            imgs[23] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy11.png"));
            imgs[24] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy12.png"));
            imgs[25] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy13.png"));
            imgs[26] = ImageIO.read(getClass().getResourceAsStream("/Imagens/enemy14.png"));
            imgs[27] = ImageIO.read(getClass().getResourceAsStream("/Imagens/boss1.png"));
            imgs[28] = ImageIO.read(getClass().getResourceAsStream("/Imagens/boss2.png"));
            imgs[29] = ImageIO.read(getClass().getResourceAsStream("/Imagens/background1.jpg"));
            imgs[30] = ImageIO.read(getClass().getResourceAsStream("/Imagens/background2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initWav() {
        try {
            for (int i = 0; i < 6; i++) {
                InputStream audioSrc = getClass().getResourceAsStream("/Sons/" + getFileName(i));
                if (audioSrc == null) {
                    throw new FileNotFoundException("Audio file not found: " + getFileName(i));
                }
                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                audioIn[i] = AudioSystem.getAudioInputStream(bufferedIn);
                clip[i] = AudioSystem.getClip();
                clip[i].open(audioIn[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileName(int index) {
        switch (index) {
            case 0: return "Funky_Drum_n_Bass.wav";
            case 1: return "laser.wav";
            case 2: return "lose.wav";
            case 3: return "missileblast.wav";
            case 4: return "plwing.wav";
            case 5: return "pst.wav";
            default: return null;
        }
    }

    public void drawBuffer() {
        System.out.println("Drawing buffer...");

        Graphics2D b = buffer.createGraphics();
        //b.setColor(Color.black);
       // b.fillRect(0, 0, buffer.getWidth(), buffer.getHeight()); // Clear the buffer
        BufferedImage backgroundImg ;
        if (level < 5) {
            backgroundImg = imgs[0];
        } else if (level == MID_LEVEL || level == MAX_LEVEL) {
            backgroundImg = imgs[29];
        } else {
            backgroundImg = imgs[30];
        }

        b.drawImage(backgroundImg, 0, 0, this);

        player.drawP(b, player, imgs, model_level, this);

        if (boss != null) {
            boss.drawE(b, imgs, this);
        }
        for (int i = 0; i < max_enemies; i++) {
            if (enemy[i] != null) {
                enemy[i].drawE(b, imgs, this);
            }

        }
        b.setColor(Color.PINK);
        for (int i = 0; i < max_bullets / 2; i++) {
            if (bullet[i] != null) {
                bullet[i].drawBP(b, bullet, imgs, bullet_level, i, this);
            }
        }
        b.setColor(Color.PINK);
        for (int i = 0; i < max_bullets / 2; i++) {
            if (bulletE[i] != null) {
                bulletE[i].drawBE(b, bulletE, imgs, 11, i, this);
            }
        }
        b.setColor(Color.yellow);
        for (int i = 0; i < max_drops; i++) {
            if (drops[i] != null) {
                drops[i].drawD(b, drops, imgs, 27, i, this);
            }
        }
        b.setColor(Color.white);
        String str = "Level: " + level + " Lives: " + lives + " Energy: " + player.life + "/" + max_life + " Score: " + score + " Money: " + money;
        b.drawString(str, 50, 585);
        b.dispose();
    }

    public void drawScreen() {
        Graphics2D g = (Graphics2D) this.getGraphics();
        if (g == null) {
            System.out.println("Graphics context is null!");
            return;
        }
        g.drawImage(buffer, 0, 0, this);
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void random_move(Entity e) {
        int dir = (int) (Math.round(8 * Math.random()) + 1);
        e.direction[0] = false;
        e.direction[1] = false;
        e.direction[2] = false;
        e.direction[3] = false;
        switch (dir) {
            case 1:
                e.direction[2] = true;
                e.direction[0] = true;
                break;
            case 2:
                e.direction[0] = true;
                break;
            case 3:
                e.direction[3] = true;
                e.direction[0] = true;
                break;
            case 4:
                e.direction[2] = true;
                break;
            case 5:
                e.direction[3] = true;
                break;
            case 6:
                e.direction[2] = true;
                e.direction[1] = true;
                break;
            case 7:
                e.direction[1] = true;
                break;
            case 8:
                e.direction[3] = true;
                e.direction[1] = true;
                break;
        }
    }

    // make e1 move towards e2
    public void follow_move(Entity moving, Entity target) {
        moving.direction[0] = false;
        moving.direction[1] = false;
        moving.direction[2] = false;
        moving.direction[3] = false;
        if (target.centerX < moving.centerX) {
            moving.direction[2] = true;
        }
        if (target.centerX > moving.centerX) {
            moving.direction[3] = true;
        }
        if (target.centerY < moving.centerY) {
            moving.direction[0] = true;
        }
        if (target.centerY > moving.centerY) {
            moving.direction[1] = true;
        }
    }

    public void shootE(Entity e) {
        if (bulletE[ebn] == null && !e.checkOutOfBounds()) {
            bulletE[ebn] = new Entity(e.centerX - 5 - e.width / 2, e.centerY - 5, 15, 0, 10, 10);
            bulletE[ebn].speed = 4;
            bulletE[ebn].height = 10;
            bulletE[ebn].width = 10;
            bulletE[ebn].direction[2] = true;
            ebn++;
            ebn %= max_bullets / 2;
            if (!mute) {
                try {
                    clip[1].start();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }

    public void shoot() {
        if (player.count < player.firerate) {
            return;
        }

        if (bullet[bn] == null) {
            bullet[bn] = new Entity(player.centerX - 5 + player.width / 2, player.centerY - 5, 15, 0, 10, 10);
            bullet[bn].speed = 4;
            bullet[bn].height = 10;
            bullet[bn].width = 10;
            bullet[bn].direction[3] = true;
            player.count = 0;
            if (!mute) {
                try {
                    clip[1].start();

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
        bn++;
        bn %= max_bullets / 2;
    }

    public void away_move(Entity e) {
        e.direction[2] = false;
        e.direction[3] = false;
        e.direction[0] = false;
        e.direction[1] = false;
        if (player.x > e.x) {
            e.direction[2] = true;
        }
        if (player.x < e.x) {
            e.direction[3] = true;
        }
        if (player.y > e.y) {
            e.direction[0] = true;
        }
        if (player.y < e.y) {
            e.direction[1] = true;
        }
    }

    public void drawMenu() {

        // Obter o contexto gráfico do buffer
        Graphics2D g = buffer.createGraphics();

        // Preencher o fundo com uma cor preta
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);

        // Configurar a cor do texto como branca
        g.setColor(Color.YELLOW);

        // Desenhar as mensagens do menu
        g.drawString("Press Spacebar to Play", 350, 250);
        g.drawString("Or Q to Quit", 380, 300);
        g.drawString("High Score: " + highscore, 350, 350);

        // Libertar os recursos gráficos
        g.dispose();

        // Desenhar o buffer na tela
        drawScreen();

        // Adicionar um pequeno atraso para evitar sobrecarga de CPU
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawPause() {
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 800, 600);
        g.setColor(Color.white);
        g.drawString("*** Paused ***", 350, 150);
        g.drawString("Press P or X to Resume or Q to Quit", 280, 200);
        g.drawString("Press Number Keys (1~5) to Upgrade", 270, 250);
        g.drawString("(Current Money = " + money + ")", 310, 270);
        String str1, str2;
        //ship model
        g.drawString("1-Ship Model", 50, 350);
        if (model_level < 3) {
            str1 = "" + model_level;
            str2 = "" + 200 * model_level;
        } else {
            str1 = "MAX";
            str2 = "---";
        }

        g.drawImage(imgs[5], 70, 370, 40, 50, this);
        g.drawString("Current Level: " + str1, 30, 450);
        g.drawString("Upgrade Cost: " + str2, 30, 470);
        //ship speed
        g.drawString("2-Ship Speed", 210, 350);
        if (speed_level < 5) {
            str1 = "" + speed_level;
            str2 = "" + 300 * speed_level;
        } else {
            str1 = "MAX";
            str2 = "---";
        }
        g.drawImage(imgs[8], 230, 370, 40, 50, this);
        g.drawString("Current Level: " + str1, 190, 450);
        g.drawString("Upgrade Cost: " + str2, 190, 470);
        //ship fire rate
        g.drawString("3-Fire Rate", 370, 350);
        if (firerate_level < 5) {
            str1 = "" + firerate_level;
            str2 = "" + 500 * firerate_level;
        } else {
            str1 = "MAX";
            str2 = "---";
        }
        g.drawImage(imgs[7], 390, 370, 40, 50, this);
        g.drawString("Current Level: " + str1, 350, 450);
        g.drawString("Upgrade Cost: " + str2, 350, 470);
        //bullet damage
        g.drawString("4-Bullet Damage", 530, 350);
        if (damage_level < 4) {
            str1 = "" + damage_level;
            str2 = "" + 700 * damage_level;
        } else {
            str1 = "MAX";
            str2 = "---";
        }
        g.drawImage(imgs[6], 550, 370, 40, 50, this);
        g.drawString("Current Level: " + str1, 510, 450);
        g.drawString("Upgrade Cost: " + str2, 510, 470);
        //extra life
        g.drawString("5-Extra Life", 690, 350);
        if (lives < 10) {
            str1 = "" + lives;
            str2 = "" + 2000;
        } else {
            str1 = " (MAX)";
            str2 = "---";
        }
        g.drawImage(imgs[4], 710, 370, 40, 50, this);
        g.drawString("Current Lives: " + str1, 670, 450);
        g.drawString("   Cost: " + str2, 670, 470);

        g.dispose();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        initialize();
        try {
            clip[0].loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (!quit) {
            gameLoop();
        }

        // thank you for playing! :)
    }

    private void createBoss(int level) {
        boss = new Entity(800, 300, 3, 10, BOSS_WIDTH, BOSS_HEIGHT);
        boss.direction[0] = true;
        boss.life = BOSS_LIFE;
        if (level == MID_LEVEL) {
            boss.skin = BOSS_SKIN1;
        } else if (level == MAX_LEVEL) {
            boss.skin = BOSS_SKIN2;
        }
    }

    public void gameLoop() {
        if (menu) {
            initialize();
            drawMenu();
            return; // Sai para evitar que outras partes do loop sejam executadas
        }
        if (pause) {
            //System.out.println("Pause");
            drawPause();
            drawScreen();
            return; // Sai para evitar que outras partes do loop sejam executadas
        }
        try {
            if (kills > level && level != MID_LEVEL && level != MAX_LEVEL) {
                kills = 0;
                level++;
            }
            System.out.println("Level: " + level);
            System.out.println("Enemy Counter: " + enemy_counter);
            System.out.println("Enemy (1000 / (level * 2)): " + (1000 / (level * 2)));
            enemy_counter++;
            if (enemy_counter > (1000 / (level * 2)) && level != MID_LEVEL && level != MAX_LEVEL) {
                if (enemy[en] == null) {
                    enemy[en] = new Entity(800, (int) (Math.random() * 600), 1, 100, 50, 50);
                    enemy[en].skin = 13 + (int) (Math.random() * Math.min(level * 2, 14));
                    enemy[en].life = level * 3;
                    enemy[en].speed = (int) (level / 4 + 1);
                    en++;
                    en %= max_enemies;
                    enemy_counter = 0;
                }
            }
            if ((level == MID_LEVEL || level == MAX_LEVEL) && boss == null) {
                createBoss(level);
            }
            update();
            checkCollisions();

            drawBuffer();
            drawScreen();
            Thread.sleep(15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
