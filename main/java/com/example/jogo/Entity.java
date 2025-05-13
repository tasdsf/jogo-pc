package pcm;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Entity
{
	int x,y,speed,width,height, centerX, centerY;
	boolean[] direction = new boolean[4];
	int life, firerate, count=0, skin = 13;

	public Entity(int x, int y, int s, int f, int w, int h)
	{
		this.x = x;
		this.y = y;
		speed = s;
		width = w;
		height = h;
		centerX = x + h/2;
		centerY = y + w/2;
		for(int i = 0; i<4; i++)
			direction[i]=false;
		firerate = f;
		life=5;
			
	}
	

	public Rectangle getBounds()
	{
		return new Rectangle(x,y,width,height);
	}

	public void move()
	{
		if (direction[0]) //up
			y -= speed;
		if (direction[1]) //down
			y += speed;
		if (direction[2]) //left
			x -= speed;
		if (direction[3]) //right
			x += speed;
		centerX = x + height/2;
		centerY = y + width/2;
		count++;
	}

	public boolean checkOutOfBounds(){
		boolean rv = false;
		if(y<0){
			y = 0;
			rv = true;
		}
		if(y>600-height-28){
			y = 600-height-28;
			rv = true;
		}
		if(x<0){
			x = 0;
			rv = true;
		}
		if(x>800-width-8){
			x = 800-width-8;
			rv = true;
		}
		return rv;
	}

	public void drawP(Graphics g, Entity player, BufferedImage[] imgs, int shipupgrade, ImageObserver Ob){
		//draw Player
		g.drawImage(imgs[shipupgrade], player.x, player.y, player.width, player.height, Ob);
	}
	public void drawE(Graphics g, BufferedImage[] imgs, ImageObserver Ob){
		//draw Enemy
		g.drawImage(imgs[this.skin], x, y, width, height, Ob);
	}
	public void drawBP(Graphics g, Entity[] bullet, BufferedImage[] imgs, int bulletupgrade, int i, ImageObserver Ob){
		//draw Bullet > Player
		g.drawImage(imgs[bulletupgrade], bullet[i].x, bullet[i].y, bullet[i].width, bullet[i].height, Ob);
	}
	public void drawBE(Graphics g, Entity[] bulletE, BufferedImage[] imgs, int bulletupgrade, int i, ImageObserver Ob){
		//draw Bullet > Enemy
		g.drawImage(imgs[bulletupgrade], bulletE[i].x, bulletE[i].y, bulletE[i].width, bulletE[i].height, Ob);
	}
	public void drawD(Graphics g, Entity[] drops, BufferedImage[] imgs, int goldtype, int i, ImageObserver Ob){
		//draw Drop
		g.drawImage(imgs[goldtype], drops[i].x, drops[i].y, drops[i].width, drops[i].height, Ob);
	}

	// side = true <=> respawn left
	public void respawn(boolean side){
		if (side)
			x = 0;
		else
			x = 800;
		y = (int)(Math.random()*600);
	}
}

