
import java.awt.Font;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.ARBBufferObject.glDeleteBuffersARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.opengl.*;
import static org.newdawn.slick.opengl.renderer.SGL.GL_TEXTURE_2D;
import static org.newdawn.slick.opengl.renderer.SGL.GL_TEXTURE_WRAP_S;

public class Game {
              
    private Vector3f    position    = null; //3d vector to store the camera's position in
    private float       yaw         = 0.0f; //the rotation around the Y axis of the camera
    private float       pitch       = 0.0f; //the rotation around the X axis of the camera

    float[] coord; //get coordinates from camera
    
    private Texture atlas;
          
     int vertexSize = 3;
     int amountOfVertices = 4;
     int quadFaces =6;
     int colorSize = 3;
    
     boolean DebugMode = false;
       boolean ChatMode = false;
       String chatInput = "";
       float ChatTimer = 0f;
    TrueTypeFont font;
    
    public static final boolean VSYNC = true;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    
	long lastFrame;
	int fps;
	long lastFPS;
        long finalFPS;
	float xRotation = 0f;
	float yRotation = 0f;

    public static final boolean FULLSCREEN = false; // Whether to use fullscreen mode
    protected boolean running = false; // Whether our game loop is running

    public static void main(String[] args) throws LWJGLException {
        new Game().start();
    }
    public int vboVertexHandle;
    public int vboColorHandle;
    public int vbo2dVertexHandle;
    public int vbo2dColorHandle;
    private int vbo2dTexCoordHandle;
    private int vbo3dTexCoordHandle;

    // Start our game
    public void start() {
        // Set up our display 
        try {
        Display.setTitle("Voxel Engine"); //title of our window
        Display.setResizable(true); //whether our window is resizable
        Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT)); //resolution of our display
        Display.setVSyncEnabled(VSYNC); //whether hardware VSync is enabled
        Display.setFullscreen(FULLSCREEN); //whether fullscreen is enabled

        //create and show our display
        Display.create();
       } catch (LWJGLException e) {
	    e.printStackTrace();
	    System.exit(0);
	}
        
        Font awtFont = new Font("Times New Roman", Font.BOLD, 12) {};
    font = new TrueTypeFont(awtFont, false);

        resize();
    getDelta();
    lastFPS = getTime();// call once before loop to initialise lastFrame
    running = true;
    FPCameraController camera = new FPCameraController(0, 0, 0);
    float dx                 = 0.0f;
    float dy                 = 0.0f;
    float mouseSensitivity   = 0.05f;
    float movementSpeed      = 0.005f; //move 0.010 units per second

    initTexture();

    int chunkSize = 16;

int[][][] chunk = new int[16][16][16];


   
    for (int X=0; X<chunkSize; X++){
        for (int Y=0; Y<chunkSize; Y++){
                for (int Z=0; Z<chunkSize; Z++){
                                        if ((chunkSize-Y)+(SimplexNoise.noise(X/3,Z/3)*5)>0){
                        chunk[X][Y][Z] = 1;}
                                                            }
                }
        }
    
        for (int X=0; X<chunkSize; X++){
        for (int Y=0; Y<chunkSize; Y++){
                for (int Z=0; Z<chunkSize; Z++){
                                        if (chunk[X][Y][Z] == 1){
                                            int nY;
                        
                        if (Y>2){
                            nY = Y-2;}
                        else {
                            nY =0;}
                        chunk[X][nY][Z] = 2;
                    if(SimplexNoise.noise(X/3,Y/3,Z/3)>0.6){
                      
                        chunk[X][nY][Z] = 1;}
                  
                                        }

                                        }
                }
        }
    
        
    //hide the mouse
    Mouse.setGrabbed(true);
                    FloatBuffer texture3dData = BufferUtils
            .createFloatBuffer(amountOfVertices * quadFaces * 2 * chunkSize*chunkSize*chunkSize*2);
                    
                          
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(vertexSize*amountOfVertices*quadFaces*chunkSize*chunkSize*chunkSize*2);
        
        
            for (int X=0; X<chunkSize; X++){
        for (int Y=0; Y<chunkSize; Y++){
                for (int Z=0; Z<chunkSize; Z++){
                    if (chunk[X][Y][Z] > 0){
                  vertexData.put(CreateCube((float) X,(float) Y,(float) Z));
                  texture3dData.put(Chunk.gettexCoord(chunk[X][Y][Z]));}
                }
        }
   }
            
            texture3dData.put(Chunk.gettexCoord(6));
            vertexData.put(Chunk.CreateSkyBox(100f,100f,100f,210f));
            texture3dData.flip();
            vertexData.flip();
            

        
        FloatBuffer vertex2dData = BufferUtils.createFloatBuffer(vertexSize*amountOfVertices);
        vertex2dData.put(CreateSquare(-0.06f,-0.03f,-0.1f,0.01f));
        vertex2dData.flip();
            
                        float xU = 1f/512f; //column in atlas start
                float xU2=65.0f/512.0f; //column in atlas end
                float xV=1f/512f; //row in atlas start
                float xV2=65.0f/512.0f; //row in atlas end
        
            FloatBuffer texture2dData = BufferUtils
            .createFloatBuffer(amountOfVertices * 2);
    texture2dData.put(new float[] { xU, xV, }); // Texture Coordinate
    texture2dData.put(new float[] { xU, xV2, }); // Texture Coordinate
    texture2dData.put(new float[] { xU2, xV2, }); // Texture Coordinate
    texture2dData.put(new float[] { xU2, xV }); // Texture Coordinate
        texture2dData.flip();

        vboVertexHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        vbo2dVertexHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo2dVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertex2dData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
    vbo2dTexCoordHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo2dTexCoordHandle);
    glBufferData(GL_ARRAY_BUFFER, texture2dData, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    
        vbo3dTexCoordHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo3dTexCoordHandle);
    glBufferData(GL_ARRAY_BUFFER, texture3dData, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

        // While we're still running and the user hasn't closed the window... 
        while (running && !Display.isCloseRequested()) {
            int delta = getDelta();
            update(delta); //write function from www.breadmilkbearcigarettes/bmbc/shelves/users/bbb/src/java/SpinningTextureCube.php
            // If the game was resized, we need to update our projection
            if (Display.wasResized())
                resize();

        dx = Mouse.getDX();
        dy = Mouse.getDY();
        camera.yaw(dx * mouseSensitivity);
        camera.pitch(dy * mouseSensitivity);

        coord = camera.getPosition();
                
        if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
            {if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))//toggle sprint
            {movementSpeed = 0.03f;}
            else{movementSpeed = 0.01f;}

            camera.walkForward(movementSpeed*delta);}
        
        if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
            {if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))//toggle sprint
            {movementSpeed = 0.03f;}
            else{movementSpeed = 0.01f;} 
            
            camera.walkBackwards(movementSpeed*delta);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left
            {if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))//toggle sprint
            {movementSpeed = 0.03f;}
            else{movementSpeed = 0.01f;}
            
            camera.strafeLeft(movementSpeed*delta);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right
            {if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))//toggle sprint
            {movementSpeed = 0.03f;}
            else{movementSpeed = 0.01f;}
            
            camera.strafeRight(movementSpeed*delta);
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
camera.jump(movementSpeed*delta);
        }
if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
camera.decend(movementSpeed*delta);
}

        //look through the camera before you draw anything
        
            // Render the game
GL11.glLoadIdentity(); 
camera.lookThrough();
     renderSetup3d();
     
     render3d();
     GL11.glLoadIdentity();
     render2d();
     renderText();
     
            // Flip the buffers and sync to 60 FPS
            Display.sync(120);
            Display.update();
            
        }

        // Dispose any resources and destroy our window
        dispose();
        Display.destroy();
    }

    // Exit our game loop and close the window
    public void exit() {
        running = false;
    }

    // Called to setup our game and context
    protected void renderSetup3d() {
     
        
        GL11.glPopAttrib();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);


GL11.glEnable(GL11.GL_TEXTURE_2D);
         GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
        GL11.glClearDepth(1.0); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
 
        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix
        glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
          
         GLU.gluPerspective(45.0f,(float)Display.getWidth()/(float)Display.getHeight(),0.1f,200.0f);

         GL11.glMatrixMode(GL11.GL_MODELVIEW);
       GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

    }

    protected void render3d() {

glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        GL11.glVertexPointer(vertexSize, GL11.GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vbo3dTexCoordHandle);
        
        atlas.bind();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        
        
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        
        GL11.glDrawArrays(GL11.GL_QUADS, 0, 24 * Chunk.getchunkSize()*2);

        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
   }

//    // Called to resize our game
    protected void resize() {
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        // ... update our projection matrices here ...
    }
    // Called to destroy our game upon exiting
    protected void dispose() {
        glDeleteBuffersARB(vboVertexHandle);glDeleteBuffersARB(vboVertexHandle);
        glDeleteBuffersARB(vbo3dTexCoordHandle);glDeleteBuffersARB(vbo3dTexCoordHandle);
    }
    public int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		      
		return delta;
	}
    public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
    public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
                    finalFPS = fps;
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
}
    public void update(int delta){
       xRotation += 0.01 * delta;
       if (xRotation >360f)
           xRotation = 0;
       yRotation += 0.02 * delta;
        if (yRotation >360f)
           yRotation = 0;
        updateFPS();
        
    }
    
    public void initTexture() {
 
try {glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			// load texture from PNG file

atlas = TextureLoader.getTexture("{PNG", ResourceLoader.getResourceAsStream("res/Atlas.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
   public enum State {
	INTRO, GAME, MAIN_MENU;
}
   
public float[] CreateCube(float x, float y, float z) {
		return new float[] {
				// Top Quad
				x , y ,z-1,
				x - 1,y, z-1,
				x-1,y , z,
				x ,y , z,
				// Bottom
				x , y-1 ,z,
				x - 1,y-1, z,
				x-1,y-1 , z-1,
				x ,y-1 , z-1,
				// FRONT QUAD
				x , y ,z,
				x - 1,y, z,
				x-1,y-1 , z,
				x ,y -1, z,
				// BACK QUAD
				x , y-1 ,z-1,
				x - 1,y-1, z-1,
				x-1,y , z-1,
				x ,y, z-1,
				// LEFT QUAD
                                x-1, y ,z,
				x - 1,y, z-1,
				x-1,y-1 , z-1,
				x -1,y-1, z,
				
				// RIGHT QUAD
				  x, y ,z-1,
				x ,y, z,
				x,y-1 , z,
				x ,y-1, z-1,
                
                };
                               
}
public float[] CreateSquare(float x, float y, float z,float Size) {
		return new float[] {

				x , y ,z,
				x - Size,y, z,
				x-Size,y-Size ,z,
				x ,y -Size, z,
				
                
                };

}
   protected void renderSetup2d(){
    GL11.glEnable(GL11.GL_BLEND);
GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
GL11.glMatrixMode(GL_PROJECTION);
GL11.glLoadIdentity();
GL11.glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
GL11.glMatrixMode(GL_MODELVIEW);
    
}

   protected void render2d() {
     
        glBindBuffer(GL_ARRAY_BUFFER, vbo2dVertexHandle);
        GL11.glVertexPointer(vertexSize, GL11.GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vbo2dTexCoordHandle);
        
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
        
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        
        GL11.glDrawArrays(GL11.GL_QUADS, 0, 24);
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        
    }
   
   protected void renderText() {

            GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        GL11.glEnable(GL11.GL_BLEND);
GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(),0,-1,1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
       

               font.drawString(10, 10, "Debug mode on", Color.white);
font.drawString(10, 25, "FPS: " + finalFPS, Color.white);
font.drawString(10, 40, "Coord: " + Arrays.toString(coord), Color.white);

        
GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
        
         
    }
   
   public int PlayerVoxel(float coordIn)
           
              {   int output;
              output = (int) Math.ceil(coord[0]);
              return output;
       
   }
  
    }

