import java.awt.*;
import java.applet.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;

/*<applet code="snake2" width=298 height=298>
 <param name="img" value="hit1.png">
 </applet>*/

public class snake2 extends JApplet implements KeyListener, Runnable {
    
    final char START_THE_GAME='j';
    final char PAUSE_THE_GAME='p';
    final char RESTART='r';
    final char MUTE='m';
    final char INCREASE_LEVEL='.';
    final char DECREASE_LEVEL=',';
    final char PASS_CHAR='*';
    String delmt=" == ";
    String delmt1=" :::: ";
    
    
    
    int passwordIndex = 0, cheatCodeIndex = 0;
    int level = 0, level1 = 0,levelDisplay = 1;
    int  score = 0, inBonus = -1, speed = 40;
    int noOfTurns = 0,direction = 4;
    int headX = 170, headY = 50;
    int tailX = 50, tailY = 50;
    int foodX = 350, foodY = 350;
    int bonusFoodX = 350, bonusFoodY = 350;
    int temp1, temp2, bonusTime = 0;
    boolean authenticated=false,firstTime=true;
    boolean mute=false, inBonusFlg = false;
    boolean gameOver = false,foodEaten=true;
    int ptx = 49, pty = 49;
    int hit = 0, capslk = 0;
    // int sz = 120; 
    
    double bonusIncr = 1;
    char operation = ' ', startGame = ' ', pause = ' ';
    String str = " ", showPassword = " ";
    char password[] = new char[64];
    int turningPointX[] = new int[512];
    int turningPointY[] = new int[512];
    Image img, img1, img2;
    AudioClip clip1, clip2, clip3, clip4;
    Random rd = new Random();
    
    public void init() {
        img = getImage(getDocumentBase(), getParameter("img"));
        img1 = getImage(getDocumentBase(), "TBALL.gif");
        clip1 = getAudioClip(getDocumentBase(), "s1.wav");
        clip2 = getAudioClip(getDocumentBase(), "s2.wav");
        clip3 = getAudioClip(getDocumentBase(), "enter.wav");
        clip4 = getAudioClip(getDocumentBase(), "tada.wav");
        addKeyListener(this);
        requestFocus();
    }
    
    public void keyPressed(KeyEvent ke) {
        int kc = ke.getKeyCode();
        if (kc == KeyEvent.VK_CAPS_LOCK) {
            if (capslk == 0)
                capslk = 2;
            if (capslk == 1)
                capslk = 3;
            showStatus("CapsLk OFF");
        }
        
        if (kc == KeyEvent.VK_ENTER) {
            showPassword = " ";
            passwordIndex = 0;
        }
        repaint();
    }
    
    public void keyReleased(KeyEvent ke) {
    }
    
    public void keyTyped(KeyEvent ke) {
        if (!authenticate()) {
            
            try {
                password[passwordIndex++] = ke.getKeyChar();
            } catch (Exception e) {
            }
            if (password[0] >= 'A' && password[0] <= 'Z') {
                showStatus("CapsLk ON");
                if (password[passwordIndex - 1] >= 'a' && password[passwordIndex - 1] <= 'z') {
                    password[0] = password[passwordIndex - 1];
                }
                passwordIndex = 1;
            }
            if (password[passwordIndex - 1] >= 'a' && password[passwordIndex - 1] <= 'z') {
                showPassword = showPassword + PASS_CHAR;
                if (passwordIndex == 0)
                    showPassword = " ";
                showStatus(showPassword);
            }
            passwordIndex%=5;
            cheatCodeIndex++;
            if (cheatCodeIndex == 49) { // cheat code ::::: if 49 incorrect charcters are typed , give authentication
                authenticated=true;
                passwordIndex = 0;
            }
        }
        if (authenticate()) {
            operation = ke.getKeyChar();
            
            // if(s3=='x'&&s!=RESTART)s='x';
            if (startGame == START_THE_GAME
                    & (operation == 'w' | operation == 'a' | operation == 's' | operation == 'd' | operation == PAUSE_THE_GAME))
                pause = ke.getKeyChar();
            if (startGame != START_THE_GAME) {
                startGame = ke.getKeyChar();
                if (startGame == DECREASE_LEVEL) {
                    levelDisplay--;
                    if (levelDisplay == 0)
                        levelDisplay = 5;
                }
                if (startGame == INCREASE_LEVEL) {
                    levelDisplay++;
                    if (levelDisplay == 6)
                        levelDisplay = 1;
                }
                if (operation == MUTE) {
                    mute=!mute;
                }
            }
            if (startGame == START_THE_GAME) {
                if (operation == MUTE) {
                    mute=!mute;
                }
                if (!gameOver) {
                    if (operation == 'w')
                        if (direction != 3 & direction != 1) {
                        direction = 1;
                        createNewTurn();
                    }
                    if (operation == 'a')
                        if (direction != 4 & direction != 2) {
                        direction = 2;
                        createNewTurn();
                    }
                    if (operation == 's')
                        if (direction != 1 & direction != 3) {
                        direction = 3;
                        createNewTurn();
                    }
                    if (operation == 'd')
                        if (direction != 2 & direction != 4) {
                        direction = 4;
                        createNewTurn();
                    }
                }
            }
        }
    }
    
    public void start() {
        Thread nt = new Thread(this);
        nt.start();
    }
    
    public void run() {
        try {
            Thread.sleep(1);
        } catch (Exception e) {
        }
    }
    
    private void createNewTurn(){
        noOfTurns += 1;
        for (int j = noOfTurns - 1; j > 0; j--) {
            turningPointX[j + 1] = turningPointX[j];
            turningPointY[j + 1] = turningPointY[j];
        }
        turningPointX[1] = headX;
        turningPointY[1] = headY;
    }
    
    private void initialise(Graphics h)
    {
        if (score == 0)
            showStatus("a -left d -right w -up s -down p -pause r -restart");
        if (operation >= 'A' && operation <= 'Z') {
            showStatus("CapsLk ON");
        }
        if (startGame != START_THE_GAME) {
            h.setColor(Color.white);
            h.drawString("CLICK HERE", 0, 290);
            h.fillRect(0, 0, 297, 297);
            showStatus("LEVEL:" + levelDisplay + "          "
                           + "PRESS '<' '>'to change level ,j to START");
            
            if (operation >= 'A' && operation <= 'Z') {
                showStatus("CapsLk ON");
            }
            if (capslk == 2) {
                showStatus("CapsLk ON");
                capslk = 1;
            }
            if (capslk == 3) {
                showStatus("CapsLk OFF");
                capslk = 0;
            }
        }
    }
    private boolean authenticate(){
        if(!authenticated)
            if(password[0] == 'h' & password[1] == 'i' & password[2] == 't' & password[3] == 'k' & password[4] == 'u')
            authenticated=true;
        return authenticated;
    }
    private void setSpeed(){
        switch (levelDisplay) {
            case 1:
                bonusIncr = 1;
                speed = 30;
                break;
            case 2:
                bonusIncr = 1.15;
                speed = 25;
                break;
            case 3:
                bonusIncr = 1.25;
                speed = 20;
                break;
            case 4:
                bonusIncr = 1.50;
                speed = 15;
                break;
            case 5:
                bonusIncr = 1.75;
                speed = 10;
        }
    }
    
    public void paint(Graphics h) {
        
        
        
        System.out.println(
         
             "headX"+delmt+headX +delmt1+
             "headY"+delmt+headY +delmt1+
             "tailX"+delmt+tailX+delmt1+
            "tailY" +delmt+tailY
             
        );
        
  /*      "score"+delmt+score +delmt1+
             "inBonus"+delmt+ inBonus+delmt1+
            "speed" +delmt+speed+delmt1+
             "noOfTurns"+delmt+noOfTurns +delmt1+
             "direction"+delmt+direction +delmt1+
        "foodX"+delmt+foodX+delmt1+
             "foodY"+delmt+foodY+delmt1+
             "bonusFoodX"+delmt+ bonusFoodX +delmt1+
             "bonusFoodY"+delmt+bonusFoodY +delmt1+
             "bonusTime"+delmt+bonusTime+delmt1+
             "authenticated"+delmt+authenticated+delmt1+
             "firstTime"+delmt+firstTime+delmt1+
             "mute"+delmt+mute+delmt1+
             "inBonusFlg"+delmt+inBonusFlg+delmt1+
             "gameOver"+delmt+ gameOver +delmt1+
             "foodEaten"+delmt+foodEaten+delmt1+
             "ptx"+delmt+ptx +delmt1+
             "pty"+delmt+ pty+delmt1+*/
        
        if (authenticate()) {
            initialise(h);
            setSpeed();
            
            if (pause == PAUSE_THE_GAME)
                showStatus("SCORE: " + String.valueOf(score));
            if (startGame == START_THE_GAME & pause != PAUSE_THE_GAME) {
                if (firstTime) {
                    if (!mute)
                        clip3.play();
                    firstTime = false;
                }
                {
                    h.setColor(Color.black);
                    h.drawRect(0, 0, 297, 297);
                }
                
                if (noOfTurns != 0) {
                    h.setColor(Color.white);
                    if (turningPointY[noOfTurns] != tailY) {
                        h.drawLine(turningPointX[noOfTurns] - 1, tailY, tailX - 1, tailY);
                        h.drawLine(turningPointX[noOfTurns] + 1, tailY, tailX + 1, tailY);
                        if (turningPointY[noOfTurns] > tailY)
                            h.drawLine(turningPointX[noOfTurns], tailY - 1, tailX, tailY++);
                        if (turningPointY[noOfTurns] < tailY)
                            h.drawLine(turningPointX[noOfTurns], tailY + 1, tailX, tailY--);
                    }
                    if (turningPointX[noOfTurns] != tailX) {
                        h.drawLine(tailX, turningPointY[noOfTurns] - 1, tailX, tailY - 1);
                        h.drawLine(tailX, turningPointY[noOfTurns] + 1, tailX, tailY + 1);
                        if (turningPointX[noOfTurns] > tailX)
                            h.drawLine(tailX - 1, turningPointY[noOfTurns], tailX++, tailY);
                        if (turningPointX[noOfTurns] < tailX)
                            h.drawLine(tailX + 1, turningPointY[noOfTurns], tailX--, tailY);
                    }
                }
                
                try {
                    Thread.sleep(speed);
                } catch (Exception e) {
                }
                
                
                switch (direction) {
                    case 1:
                        if (headY == 0) {
                        headY = 297;
                        noOfTurns+=2;
                        for (int j = noOfTurns - 2; j > 0; j--) {
                            turningPointX[j + 2] = turningPointX[j];
                            turningPointY[j + 2] = turningPointY[j];
                        }
                        turningPointX[2] = headX;
                        turningPointY[2] = 0;
                        turningPointX[1] = headX;
                        turningPointY[1] = 297;
                    }
                        if (noOfTurns == 0) {
                            h.setColor(Color.white);
                            h.drawLine(headX - 1, tailY, tailX - 1, tailY);
                            h.drawLine(headX + 1, tailY, tailX + 1, tailY);
                            h.drawLine(headX, tailY, tailX, tailY--);
                            h.setColor(Color.black);
                            if (headY % 2 == 0)
                                h.drawLine(headX, headY --, tailX, tailY);
                            else
                                headY --;
                            h.drawLine(headX - 1, headY, tailX - 1, tailY);
                            h.drawLine(headX + 1, headY, tailX + 1, tailY);
                        } else {
                            h.setColor(Color.black);
                            if (headY % 2 == 0)
                                h.drawLine(headX, headY--, turningPointX[1], turningPointY[1]);
                            else
                                headY--;
                            h.drawLine(headX - 1, headY, turningPointX[1] - 1, turningPointY[1]);
                            h.drawLine(headX + 1, headY, turningPointX[1] + 1, turningPointY[1]);
                        }
                        break;
                        
                    case 2:
                        if (headX == 0) {
                        headX = 297;
                        
                        noOfTurns+=2;
                        for (int j = noOfTurns - 2; j > 0; j--) {
                            turningPointX[j + 2] = turningPointX[j];
                            turningPointY[j + 2] = turningPointY[j];
                        }
                        turningPointX[2] = 0;
                        turningPointY[2] = headY;
                        turningPointX[1] = 297;
                        turningPointY[1] = headY;
                        
                    }
                        
                        if (noOfTurns == 0) {
                            h.setColor(Color.white);
                            h.drawLine(tailX, headY - 1, tailX, tailY - 1);
                            h.drawLine(tailX, headY + 1, tailX, tailY + 1);
                            h.drawLine(tailX, headY, tailX--, tailY);
                            h.setColor(Color.black);
                            if (headX % 2 == 0)
                                h.drawLine(headX--, headY, tailX, tailY);
                            else
                                headX--;
                            h.drawLine(headX, headY - 1, tailX, tailY - 1);
                            h.drawLine(headX, headY + 1, tailX, tailY + 1);
                        } else {
                            h.setColor(Color.black);
                            if (headX % 2 == 0)
                                h.drawLine(headX--, headY, turningPointX[1], turningPointY[1]);
                            else
                                headX--;
                            h.drawLine(headX, headY - 1, turningPointX[1], turningPointY[1] - 1);
                            h.drawLine(headX, headY + 1, turningPointX[1], turningPointY[1] + 1);
                        }
                        break;
                        
                    case 3:
                        if (headY == 297) {
                        headY = 0;
                        noOfTurns+=2;
                        for (int j = noOfTurns - 2; j > 0; j--) {
                            turningPointX[j + 2] = turningPointX[j];
                            turningPointY[j + 2] = turningPointY[j];
                        }
                        turningPointX[2] = headX;
                        turningPointY[2] = 297;
                        turningPointX[1] = headX;
                        turningPointY[1] = 0;
                    }
                        
                        if (noOfTurns == 0) {
                            h.setColor(Color.white);
                            h.drawLine(headX - 1, tailY, tailX - 1, tailY);
                            h.drawLine(headX + 1, tailY, tailX + 1, tailY);
                            h.drawLine(headX, tailY, tailX, tailY++);
                            h.setColor(Color.black);
                            if (headY % 2 == 0)
                                h.drawLine(headX, headY++, tailX, tailY);
                            else
                                headY++;
                            h.drawLine(headX - 1, headY, tailX - 1, tailY);
                            h.drawLine(headX + 1, headY, tailX + 1, tailY);
                        } else {
                            h.setColor(Color.black);
                            if (headY % 2 == 0)
                                h.drawLine(headX, headY++, turningPointX[1], turningPointY[1]);
                            else
                                headY++;
                            h.drawLine(headX - 1, headY, turningPointX[1] - 1, turningPointY[1]);
                            h.drawLine(headX + 1, headY, turningPointX[1] + 1, turningPointY[1]);
                        }
                        
                        break;
                    case 4:
                        if (headX == 297) {
                        headX = 0;
                        
                        for (int j = noOfTurns - 2; j > 0; j--) {
                            turningPointX[j + 2] = turningPointX[j];
                            turningPointY[j + 2] = turningPointY[j];
                        }
                        turningPointX[2] = 297;
                        turningPointY[2] = headY;
                        turningPointX[1] = 0;
                        turningPointY[1] = headY;
                        
                    }
                        
                        if (noOfTurns == 0) {
                            h.setColor(Color.white);
                            h.drawLine(tailX, headY - 1, tailX, tailY - 1);
                            h.drawLine(tailX, headY + 1, tailX, tailY + 1);
                            h.drawLine(tailX, headY, tailX++, tailY);
                            h.setColor(Color.black);
                            if (headX % 2 == 0)
                                h.drawLine(headX++, headY, tailX, tailY);
                            else
                                headX++;
                            h.drawLine(headX, headY - 1, tailX, tailY - 1);
                            h.drawLine(headX, headY + 1, tailX, tailY + 1);
                        } else {
                            h.setColor(Color.black);
                            h.drawLine(headX++, headY, turningPointX[1], turningPointY[1]);
                            h.drawLine(headX, headY - 1, turningPointX[1], turningPointY[1] - 1);
                            h.drawLine(headX, headY + 1, turningPointX[1], turningPointY[1] + 1);
                        }
                }
                
                if (tailX - ptx == 1 | tailY - pty == 1 | ptx - tailX == 1
                        | pty - tailY == 1) {
                    ptx = tailX;
                    pty = tailY;
                }
                
                if (noOfTurns > 0) {
                    if (turningPointX[noOfTurns] == tailX & turningPointY[noOfTurns] == tailY) {
                        if ((turningPointX[noOfTurns - 1] == 0 && turningPointX[noOfTurns] == 297)
                                | (turningPointX[noOfTurns] == 0 && turningPointX[noOfTurns - 1] == 297)
                                | (turningPointY[noOfTurns - 1] == 0 && turningPointY[noOfTurns] == 297)
                                | (turningPointY[noOfTurns] == 0 && turningPointY[noOfTurns - 1] == 297)) {
                            tailX = turningPointX[noOfTurns - 1];
                            tailY = turningPointY[noOfTurns - 1];
                            noOfTurns -= 2;
                        } else
                            noOfTurns--;
                    }
                }
                
                /*
                 * for(i=0;i<noOfTurns;i++) { h.setColor(Color.white);
                 * h.drawLine(hx,hy,turningPointX[i],turningPointY[i]);
                 * h.drawLine(turningPointX[i],turningPointY[i],turningPointX[i+1],turningPointY[i+1]); }
                 */
                
                ptx = tailX;
                pty = tailY;
                
                turningPointX[noOfTurns + 1] = ptx;
                turningPointY[noOfTurns + 1] = pty;
                // if(turningPointY[3]==turningPointY[4])j=3;else j=4;
                if (direction == 1 | direction == 3) {
                    for (int j = 3; j <= noOfTurns; j += 2) {
                        if (turningPointX[j] > turningPointX[j + 1]) {
                            temp1 = turningPointX[j + 1];
                            temp2 = turningPointX[j];
                        } else {
                            temp1 = turningPointX[j];
                            temp2 = turningPointX[j + 1];
                        }
                        if ((temp1 <= headX && temp2 >= headX) && (turningPointY[j] + 1 == headY | turningPointY[j] - 1 == headY)) {
                            if ((turningPointY[j] != 0 & turningPointY[j + 1] != 297)
                                    | (turningPointY[j] != 297 & turningPointY[j + 1] != 0))
                                gameOver = true;
                        }
                    }
                }
                
                // if(turningPointX[3]==turningPointX[4])j=3;else j=4;
                if (direction == 2 | direction == 4) {
                    for (int j = 3; j <= noOfTurns; j += 2) {
                        if (turningPointY[j] > turningPointY[j + 1]) {
                            temp1 = turningPointY[j + 1];
                            temp2 = turningPointY[j];
                        } else {
                            temp1 = turningPointY[j];
                            temp2 = turningPointY[j + 1];
                        }
                        if ((temp1 <= headY && temp2 >= headY)  && (turningPointX[j] + 1 == headX | turningPointX[j] - 1 == headX)) {
                            if ((turningPointX[j] != 0 & turningPointX[j + 1] != 297)
                                    | (turningPointX[j] != 297 & turningPointX[j + 1] != 0))
                                gameOver = true;
                        }
                    }
                }
                
                if (gameOver & hit == 0) {
                    h.setColor(Color.white);
                    h.fillRect(0, 0, 350, 350);
                    direction = 0;
                    showStatus("GAME OVER!!!!" + " " + "SCORE:" + " " + score
                                   + " " + "       press r -restart");
                    if (!mute) {
                        clip2.stop();
                        clip3.stop();
                        clip1.stop();
                        clip4.play();
                    }
                    // s3 = 'x';
                    hit = 1;
                }
                
                if ((headX > foodX - 4 & headX < foodX + 4) & (headY > foodY - 4 & headY < foodY + 4)) {
                    foodEaten=true;
                    score += 4 + levelDisplay;
                    if (!mute) {
                        clip2.stop();
                        clip1.stop();
                        clip3.stop();
                        clip1.play();
                    }
                }
                if (!foodEaten) {
                    h.setColor(Color.black);
                    h.fillOval(foodX - 3, foodY - 3, 6, 6);
                }
                
                if (inBonusFlg) {
                    if ((headX > bonusFoodX - 7 & headX < bonusFoodX + 7)
                            & (headY > bonusFoodY - 7 & headY < bonusFoodY + 7)) {
                        h.setColor(Color.white);
                        h.fillOval(bonusFoodX - 7, bonusFoodY - 7, 14, 14);
                        score += (int) ((200 - ((bonusTime / 3) * 2)) * bonusIncr);
                        if (!mute) {
                            clip2.stop();
                            clip1.stop();
                            clip2.play();
                        }
                        
                        for (int z = 1; z < noOfTurns; z++) {
                            if ((turningPointX[z] != 0 & turningPointX[z + 1] != 297))
                                if (turningPointX[z + 1] != 0 & turningPointX[z] != 297)
                                if (turningPointY[z] != 0 & turningPointY[z + 1] != 297)
                                if ((turningPointY[z + 1] != 0 & turningPointY[z] != 297)) {
                                h.setColor(Color.black);
                                h.drawLine(turningPointX[z], turningPointY[z],
                                           turningPointX[z + 1], turningPointY[z + 1]);
                                if (turningPointX[z] == turningPointX[z + 1]) {
                                    h.drawLine(turningPointX[z] - 1,
                                               turningPointY[z],
                                               turningPointX[z + 1] - 1,
                                               turningPointY[z + 1]);
                                    h.drawLine(turningPointX[z] + 1,
                                               turningPointY[z],
                                               turningPointX[z + 1] + 1,
                                               turningPointY[z + 1]);
                                } else {
                                    h.drawLine(turningPointX[z],
                                               turningPointY[z] + 1,
                                               turningPointX[z + 1],
                                               turningPointY[z + 1] + 1);
                                    h.drawLine(turningPointX[z],
                                               turningPointY[z] - 1,
                                               turningPointX[z + 1],
                                               turningPointY[z + 1] - 1);
                                }
                            }
                        }
                        
                        if (turningPointX[noOfTurns] == tailX) {
                            if (turningPointY[noOfTurns] > tailY) {
                                tailY = tailY - 20;
                                //sz += 20;
                                h.setColor(Color.black);
                                h.drawLine(tailX, tailY, tailX, tailY + 20);
                                h.drawLine(tailX + 1, tailY, tailX + 1, tailY + 20);
                                h.drawLine(tailX - 1, tailY, tailX - 1, tailY + 20);
                            } else if (turningPointY[noOfTurns] < tailY) {
                                tailY = tailY + 20;
                                //sz += 20;
                                h.setColor(Color.black);
                                h.drawLine(tailX, tailY, tailX, tailY - 20);
                                h.drawLine(tailX + 1, tailY, tailX + 1, tailY - 20);
                                h.drawLine(tailX - 1, tailY, tailX - 1, tailY - 20);
                            }
                        }
                        
                        if (turningPointY[noOfTurns] == tailY) {
                            if (turningPointX[noOfTurns] > tailX) {
                                tailX = tailX - 20;
                                //sz += 20;
                                h.setColor(Color.black);
                                h.drawLine(tailX, tailY, tailX + 20, tailY);
                                h.drawLine(tailX, tailY + 1, tailX + 20, tailY + 1);
                                h.drawLine(tailX, tailY - 1, tailX + 20, tailY - 1);
                            } else if (turningPointX[noOfTurns] < tailX) {
                                tailX = tailX + 20;
                                //sz += 20;
                                h.setColor(Color.black);
                                h.drawLine(tailX, tailY, tailX - 20, tailY);
                                h.drawLine(tailX, tailY + 1, tailX - 20, tailY + 1);
                                h.drawLine(tailX, tailY - 1, tailX - 20, tailY - 1);
                            }
                        }
                        
                        inBonus = 0;
                        inBonusFlg=false;
                        bonusTime = 0;
                        showStatus("SCORE:" + score);
                        
                    }
                    else{
                        bonusTime += 1;
                        if (!gameOver)
                            showStatus("SCORE:"
                                           + " "
                                           + String.valueOf(score
                                                                + "                                            "
                                                                + (100 - bonusTime / 3)));
                        if (bonusTime == 300) {
                            bonusTime = 0;
                            inBonus = 0;
                            inBonusFlg=false;
                            h.setColor(Color.white);
                            h.fillOval(bonusFoodX - 7, bonusFoodY - 7, 14, 14);
                            showStatus("SCORE:" + score);
                            for (int z = 1; z < noOfTurns; z++) {
                                if ((turningPointX[z] != 0 & turningPointX[z + 1] != 297))
                                    if (turningPointX[z + 1] != 0 & turningPointX[z] != 297)
                                    if (turningPointY[z] != 0 & turningPointY[z + 1] != 297)
                                    if ((turningPointY[z + 1] != 0 & turningPointY[z] != 297)) {
                                    h.setColor(Color.black);
                                    h.drawLine(turningPointX[z], turningPointY[z], turningPointX[z + 1],
                                               turningPointY[z + 1]);
                                    if (turningPointX[z] == turningPointX[z + 1]) {
                                        h.drawLine(turningPointX[z] - 1, turningPointY[z],
                                                   turningPointX[z + 1] - 1, turningPointY[z + 1]);
                                        h.drawLine(turningPointX[z] + 1, turningPointY[z],
                                                   turningPointX[z + 1] + 1, turningPointY[z + 1]);
                                    } else {
                                        h.drawLine(turningPointX[z], turningPointY[z] + 1,
                                                   turningPointX[z + 1], turningPointY[z + 1] + 1);
                                        h.drawLine(turningPointX[z], turningPointY[z] - 1,
                                                   turningPointX[z + 1], turningPointY[z + 1] - 1);
                                    }
                                }
                            }
                            
                        }
                        else{
                            h.setColor(Color.green);
                            h.fillOval(bonusFoodX - 5, bonusFoodY - 5, 10, 10);
                            h.drawImage(img1, bonusFoodX - 7, bonusFoodY - 7, this);
                        }  
                    }
                }
                
                
                
                if (foodEaten) {
                    h.setColor(Color.white);
                    h.fillOval(foodX - 3, foodY - 3, 6, 6);
                    foodEaten=false;
                        inBonus++;
                    
                    for (int z = 1; z < noOfTurns; z++) {
                        if ((turningPointX[z] != 0 & turningPointX[z + 1] != 297))
                            if (turningPointX[z + 1] != 0 & turningPointX[z] != 297)
                            if (turningPointY[z] != 0 & turningPointY[z + 1] != 297)
                            if ((turningPointY[z + 1] != 0 & turningPointY[z] != 297)) {
                            h.setColor(Color.black);
                            h.drawLine(turningPointX[z], turningPointY[z], turningPointX[z + 1],
                                       turningPointY[z + 1]);
                            if (turningPointX[z] == turningPointX[z + 1]) {
                                h.drawLine(turningPointX[z] - 1, turningPointY[z],
                                           turningPointX[z + 1] - 1, turningPointY[z + 1]);
                                h.drawLine(turningPointX[z] + 1, turningPointY[z],
                                           turningPointX[z + 1] + 1, turningPointY[z + 1]);
                            } else {
                                h.drawLine(turningPointX[z], turningPointY[z] + 1,
                                           turningPointX[z + 1], turningPointY[z + 1] + 1);
                                h.drawLine(turningPointX[z], turningPointY[z] - 1,
                                           turningPointX[z + 1], turningPointY[z + 1] - 1);
                            }
                        }
                    }
                    
                    if (turningPointX[noOfTurns] == tailX) {
                        if (turningPointY[noOfTurns] > tailY) {
                            tailY = tailY - 20;
                            //sz += 20;
                            h.setColor(Color.black);
                            h.drawLine(tailX, tailY, tailX, tailY + 20);
                            h.drawLine(tailX + 1, tailY, tailX + 1, tailY + 20);
                            h.drawLine(tailX - 1, tailY, tailX - 1, tailY + 20);
                        } else if (turningPointY[noOfTurns] < tailY) {
                            tailY = tailY + 20;
                            //sz += 20;
                            h.setColor(Color.black);
                            h.drawLine(tailX, tailY, tailX, tailY - 20);
                            h.drawLine(tailX + 1, tailY, tailX + 1, tailY - 20);
                            h.drawLine(tailX - 1, tailY, tailX - 1, tailY - 20);
                        }
                    }
                    
                    if (turningPointY[noOfTurns] == tailY) {
                        if (turningPointX[noOfTurns] > tailX) {
                            tailX = tailX - 20;
                            //sz += 20;
                            h.setColor(Color.black);
                            h.drawLine(tailX, tailY, tailX + 20, tailY);
                            h.drawLine(tailX, tailY + 1, tailX + 20, tailY + 1);
                            h.drawLine(tailX, tailY - 1, tailX + 20, tailY - 1);
                        } else if (turningPointX[noOfTurns] < tailX) {
                            tailX = tailX + 20;
                            //sz += 20;
                            h.setColor(Color.black);
                            h.drawLine(tailX, tailY, tailX - 20, tailY);
                            h.drawLine(tailX, tailY + 1, tailX - 20, tailY + 1);
                            h.drawLine(tailX, tailY - 1, tailX - 20, tailY - 1);
                        }
                    }
                    
                    
                    if (inBonus == 5) {
                        bonusFoodX = rd.nextInt(280) + 11;
                        bonusFoodY =  rd.nextInt(280) + 11;
                        for (int z = 0; z <= noOfTurns; z++) {
                            if (turningPointX[z] > turningPointX[z + 1]) {
                                if (bonusFoodX > turningPointX[z + 1]
                                        & bonusFoodX < turningPointX[z]
                                        & (bonusFoodY > turningPointY[z] - 8 & bonusFoodY < turningPointY[z] + 8)) {
                                    bonusFoodX = tailX;
                                    bonusFoodY = tailY;
                                }
                            } else if (turningPointX[z] < turningPointX[z + 1]) {
                                if (bonusFoodX < turningPointX[z + 1]
                                        & bonusFoodX > turningPointX[z]
                                        & (bonusFoodY > turningPointY[z] - 8 & bonusFoodY < turningPointY[z] + 8)) {
                                    bonusFoodX = tailX;
                                    bonusFoodY = tailY;
                                }
                            } else if (turningPointY[z] > turningPointY[z + 1]) {
                                if (bonusFoodY > turningPointY[z + 1]
                                        & bonusFoodY < turningPointY[z]
                                        & (bonusFoodX > turningPointX[z] - 8 & bonusFoodX < turningPointX[z] + 8)) {
                                    bonusFoodX = tailX;
                                    bonusFoodY = tailY;
                                }
                            } else if (turningPointY[z] < turningPointY[z + 1]) {
                                if (bonusFoodY < turningPointY[z + 1]
                                        & bonusFoodY > turningPointY[z]
                                        & (bonusFoodX > turningPointX[z] - 8 & bonusFoodX < turningPointX[z] + 8)) {
                                    bonusFoodX = tailX;
                                    bonusFoodY = tailY;
                                }
                            }
                        }
                         inBonusFlg=true;
                        h.setColor(Color.green);
                        h.fillOval(bonusFoodX - 5, bonusFoodY - 5, 10, 10);
                        h.drawImage(img1, bonusFoodX - 7, bonusFoodY - 7, this);
                    }
                    else{
                        if (score % 5 == 0)
                            showStatus("SCORE: " + score
                                           + "    PRESS p for PAUSE   r for RESTART");
                        else
                            showStatus("SCORE: " + score);
                    }
                    
                    foodX = rd.nextInt(284) + 8;
                    foodY = rd.nextInt(284) + 8;
                    
                    turningPointX[0] = headX;
                    turningPointY[0] = headY;
                    turningPointX[noOfTurns + 1] = tailX;
                    turningPointY[noOfTurns + 1] = tailY;
                    
                    for (int z = 0; z <= noOfTurns; z++) {
                        if (turningPointX[z] > turningPointX[z + 1]) {
                            if (foodX > turningPointX[z + 1] & foodY < turningPointX[z]
                                    & (foodY > turningPointY[z] - 5 & foodY < turningPointY[z] + 5)) {
                                foodX = tailX;
                                foodY = tailY;
                            }
                        } else if (turningPointX[z] < turningPointX[z + 1]) {
                            if (foodX < turningPointX[z + 1] & foodX > turningPointX[z]
                                    & (foodY > turningPointY[z] - 5 & foodY < turningPointY[z] + 5)) {
                                foodX = tailX;
                                foodY = tailY;
                            }
                        } else if (turningPointY[z] > turningPointY[z + 1]) {
                            if (foodY > turningPointY[z + 1] & foodY < turningPointY[z]
                                    & (foodX > turningPointX[z] - 5 & foodX < turningPointX[z] + 5)) {
                                foodX = tailX;
                                foodY = tailY;
                            }
                        } else if (turningPointY[z] < turningPointY[z + 1]) {
                            if (foodY < turningPointY[z + 1] & foodY > turningPointY[z]
                                    & (foodX > turningPointX[z] - 5 & foodX < turningPointX[z] + 5)) {
                                foodX = tailX;
                                foodY = tailY;
                            }
                        }
                    }
                    
                    h.setColor(Color.black);
                    h.fillOval(foodX - 3, foodY - 3, 6, 6);// h.drawImage(img2,fx-5,fy-5,this);
                    
                }
                
                level = score / 100;
                if (speed > 10) {
                    if (level != level1) {
                        int fcr = level - level1;
                        speed -= fcr;
                    }
                    level1 = level;
                }
                
                if (capslk == 2) {
                    showStatus("CapsLk ON");
                    capslk = 1;
                }
                if (capslk == 3) {
                    showStatus("CapsLk OFF");
                    capslk = 0;
                }
                
                if (operation == RESTART) {
                    passwordIndex = 0;
                    gameOver=false;
                    foodEaten=true;
                    headX = 170;
                    headY = 50;
                    //sz = 120;
                    tailX = 50;
                    tailY = 50;
                    ptx = 50;
                    pty = 50;
                    temp1 = 0;
                    temp2 = 0;
                    noOfTurns = 0;
                    direction = 4;
                    foodX = 350;
                    foodY = 350;
                    score = 0;
                    inBonus = -1;
                    inBonusFlg=false;
                    bonusTime = 0;
                    operation = ' ';
                    startGame = ' ';
                    pause = ' ';
                    bonusFoodX = 150;
                    bonusFoodY = 150;
                    level = 0;
                    level1 = 0;
                    speed = 30;
                    firstTime = true;
                    hit = 0;
                    h.setColor(Color.white);
                    h.fillRect(-10, -10, 400, 400);
                }
                if (!gameOver)
                    repaint();
            }
        } else if (passwordIndex == 0) {
            h.drawString("CLICK HERE", 0, 290);
            showStatus("Enter password");
            h.drawImage(img, 1, 1, this);
            if (capslk == 2) {
                showStatus("CapsLk ON");
                capslk = 1;
            }
            if (capslk == 3) {
                showStatus("CapsLk OFF");
                capslk = 0;
            }
            
        }
    }
}