package tests;
/**
 * Unit test to show that exiting full screen mode in JOGL issues one reshape too much.
 * The rendering resolution switches from windowed to full screen to windowed to full screen
 * while the JFrame stays windowed size.
 * 
 * The code to render the triangle was taken from http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html.
 * @author Andrei Borza
 */

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.BorderLayout;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import junit.framework.Assert;

import org.junit.Test;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;


public class JOGLFullscreenTest implements GLEventListener{
	
	private float angle = 0.0f;  // rotation angle of the triangle

	@Override
	public void display(GLAutoDrawable glad) {
		render(glad);
		update();
	}

	// Render a triangle
	private void render(GLAutoDrawable glad) {
		// Get the OpenGL graphics context
		GL2 gl = glad.getGL().getGL2();
		// Clear the color and the depth buffers
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Reset the view (x, y, z axes back to normal)
		gl.glLoadIdentity();   

		
		// Draw a triangle
		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);
		gl.glBegin(GL_TRIANGLES);
		gl.glColor3f(1.0f, 0.0f, 0.0f);   // Red
		gl.glVertex2d(-cos, -cos);
		gl.glColor3f(0.0f, 1.0f, 0.0f);   // Green
		gl.glVertex2d(0.0f, cos);
		gl.glColor3f(0.0f, 0.0f, 1.0f);   // Blue
		gl.glVertex2d(sin, -sin);
		gl.glEnd();
	}

	// Update the angle of the triangle after each frame
	private void update() {
		angle += 0.01f;
	}

	@Override
	public void reshape(GLAutoDrawable glad, int x, int y, int width, int height){
		System.out.println("Reshape issued by "+Thread.currentThread()+": "+width+"x"+height);
	}

	@Override
	public void init(GLAutoDrawable glad) {
	}

	@Override
	public void dispose(GLAutoDrawable glad) {
	}

	@Test
	public void FullscreenTest() throws InterruptedException, InvocationTargetException {
		GLCapabilities glCaps = new GLCapabilities(null);
		final GLWindow glWindow = GLWindow.create(glCaps);
		glWindow.setTitle("Fullscreen Test");
		glWindow.addGLEventListener(new JOGLFullscreenTest());
		final Animator animator = new Animator(glWindow);
		animator.start();
		final NewtCanvasAWT newtCanvasAWT = new NewtCanvasAWT(glWindow);
		final Container container = new Container();
		container.setLayout(new BorderLayout());
		container.add(newtCanvasAWT, BorderLayout.CENTER);
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(container, BorderLayout.CENTER);
        final JFrame frame = new JFrame("JOGL Fullscreen Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        SwingUtilities.invokeAndWait(new Runnable() {
           public void run() {
        	   frame.setSize(640, 480);
        	   frame.validate();
        	   frame.setVisible(true);
           }
        });
        Thread.sleep(1000);
        int windowedWidth = glWindow.getWidth();
        int windowedHeight = glWindow.getHeight();
        System.out.println("Pre-fullscreen dimension: "+windowedWidth+"x"+windowedHeight);
        glWindow.setFullscreen(true);
        System.out.println("Entered fullscreen.");
        Thread.sleep(1000);
        glWindow.setFullscreen(false);
        System.out.println("Exited fullscreen.");
        Assert.assertEquals(windowedWidth, glWindow.getWidth());
        Assert.assertEquals(windowedHeight, glWindow.getHeight());
        Thread.sleep(1000);
        Assert.assertEquals(windowedWidth, glWindow.getWidth());
        Assert.assertEquals(windowedHeight, glWindow.getHeight());
	}
}