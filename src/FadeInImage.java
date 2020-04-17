import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class FadeInImage extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	private BufferedImage animated;
	private BufferedImage img;
	private String img_path = "";//Absolute path to the image;
	
	private String TITLE = "Fade in image";
	private int MAX_WIDTH=800,MAX_HEIGHT=700;
	private int width,height;
	
	private boolean loaded = true;
	
	public FadeInImage() {
		createImages();
		if(loaded) {
			createFrame(width,height);
			
			for(double i=0;i<=1;i+=0.02)
				render(i);
		}else {
			createFrame(1,1);
		}
	}
	
	private void createFrame(int w,int h) {
		JFrame frame = new JFrame();
		frame.setTitle(TITLE);
		frame.setSize(w, h);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);
	}
	
	private void createImages() {
		try {
			img = ImageIO.read(new File(img_path));
		} catch (IOException e) {
			e.printStackTrace();
			loaded = false;
			return;
		}
		if(img.getWidth()  > MAX_WIDTH)
			width = MAX_WIDTH;
		else
			width = img.getWidth();
		if(img.getHeight() > MAX_HEIGHT)
			height = MAX_HEIGHT;
		else
			height = img.getHeight();
		
		animated = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	
	private void render(double k) {
		int[] img_pixels = getRaster();
		int[] animated_pixels = ((DataBufferInt)animated.getRaster().getDataBuffer()).getData();
		int color;
		
		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				color = img_pixels[x+y*width];
				animated_pixels[x+y*width] = 0;
				animated_pixels[x+y*width] += ((int)(k*(color>>16))<<16);
				color -= (color>>16)<<16;
				animated_pixels[x+y*width] += ((int)(k*(color>>8))<<8);
				color -= (color>>8)<<8;
				animated_pixels[x+y*width] += (int)(k*color);
			}
		}
		
		BufferStrategy bs;
		do {
			bs = this.getBufferStrategy();
			if(bs==null) {
				createBufferStrategy(2);
				continue;
			}
		}while(bs == null);
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(animated, 0, 0, null);
		g.dispose();
		bs.show();
	}
	
	private int[] getRaster() {
		int[] raster = new int[width*height];
		
		for(int y=0;y<(Math.min(img.getHeight(), height));y++) {
			for(int x=0;x<(Math.min(img.getWidth(), width));x++) {
				raster[x+y*width] = img.getRGB(x, y);
			}
		}
		
		return raster;
	}
	
	public static void main(String[] args) {
		Thread t = new Thread(new FadeInImage());
		t.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
