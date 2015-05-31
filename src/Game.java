
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.lwjgl.opengl.GL11;
import org.lwjgl.Sys;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.ARBBufferObject.glDeleteBuffersARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.opengl.*;
import static org.newdawn.slick.opengl.renderer.SGL.GL_TEXTURE_2D;
import static org.newdawn.slick.opengl.renderer.SGL.GL_TEXTURE_WRAP_S;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import org.lwjgl.util.WaveData;

public class Game {
              
    float[] coord; //get coordinates from camera
    
    private Texture atlas;
          
 /** Buffers hold sound data. */
  IntBuffer buffer = BufferUtils.createIntBuffer(1);
 
  /** Sources are points emitting sound. */
  IntBuffer source = BufferUtils.createIntBuffer(1);
 
  /** Position of the source sound. */
  FloatBuffer sourcePos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
 
  /** Velocity of the source sound. */
  FloatBuffer sourceVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.1f }).rewind();
 
  /** Position of the listener. */
  FloatBuffer listenerPos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
 
  /** Velocity of the listener. */
  FloatBuffer listenerVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
 
  /** Orientation of the listener. (first 3 elements are "at", second 3 are "up") */
  FloatBuffer listenerOri = (FloatBuffer)BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f }).rewind();
 
 
    
     //move to a interface class???
//     boolean DebugMode = false;
//       boolean ChatMode = false;
//       String chatInput = "";
//       float ChatTimer = 0f;
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
        int AudioMoving =0;
        int ShaderIndex;

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
        
     try{
      AL.create();
    } catch (LWJGLException le) {
      le.printStackTrace();
      
    }
    AL10.alGetError();
        // Set up our display 
        try {
        Display.setTitle("Voxel Engine"); //title of our window
        Display.setResizable(true); //whether our window is resizable
        Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT)); //resolution of our display
        Display.setVSyncEnabled(VSYNC); //whether hardware VSync is enabled
        Display.setFullscreen(FULLSCREEN); //whether fullscreen is enabled


 
    // Load the wav data.
    if(loadALData() == AL10.AL_FALSE) {
      System.out.println("Error loading data.");
      return;
    }
 
    setListenerValues();
        //create and show our display
        Display.create();
       } catch (LWJGLException e) {
	    e.printStackTrace();
	    System.exit(0);
	}
        
        Font awtFont = new Font("Times New Roman", Font.BOLD, 12) {};
    font = new TrueTypeFont(awtFont, false);

    getDelta();
    lastFPS = getTime();// call once before loop to initialise lastFrame
    running = true;
    
    FPCameraController camera = new FPCameraController(0, 0, 0);
    float dx                 = 0.0f;
    float dy                 = 0.0f;
    float mouseSensitivity   = 0.05f;
    float movementSpeedWalk = 0.001f;
    float movementSpeedRun = 0.005f;

   // compile and link vertex and fragment shaders into
		// a "program" that resides in the OpenGL driver
		ShaderProgram shader = new ShaderProgram();
                ShaderIndex = shader.getProgramId();
 
		// do the heavy lifting of loading, compiling and linking
		// the two shaders into a usable shader program
		shader.init("src/Shaders/simple.vertex", "src/Shaders/simple.fragment");    
    initTexture();

       Chunk chunk = new Chunk();
       chunk.CallChunkVertexs();       
    
    //hide the mouse
    Mouse.setGrabbed(true);
    
        FloatBuffer vertex2dData = BufferUtils.createFloatBuffer(Chunk.vertexSize*Chunk.amountOfVertices);
        vertex2dData.put(CreateSquare(-0.06f,-0.03f,-0.1f,0.01f));
        vertex2dData.flip();
            
                        float xU = 1f/512f; //column in atlas start
                float xU2=65.0f/512.0f; //column in atlas end
                float xV=1f/512f; //row in atlas start
                float xV2=65.0f/512.0f; //row in atlas end
        
            FloatBuffer texture2dData = BufferUtils
            .createFloatBuffer(Chunk.amountOfVertices * 2);
    texture2dData.put(new float[] { xU, xV, }); // Texture Coordinate
    texture2dData.put(new float[] { xU, xV2, }); // Texture Coordinate
    texture2dData.put(new float[] { xU2, xV2, }); // Texture Coordinate
    texture2dData.put(new float[] { xU2, xV }); // Texture Coordinate
        texture2dData.flip();

        vboVertexHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, chunk.vertexData, GL_STATIC_DRAW);
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
    glBufferData(GL_ARRAY_BUFFER, chunk.texture3dData, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    int PreAudioMoving = 0;
        // While we're still running and the user hasn't closed the window... 
        while (running && !Display.isCloseRequested()) {
            int delta = getDelta();
            updateFPS();

        dx = Mouse.getDX();
        dy = Mouse.getDY();
        camera.yaw(dx * mouseSensitivity);
        camera.pitch(dy * mouseSensitivity);

        coord = camera.getPosition();
                
        if (Keyboard.isKeyDown(Keyboard.KEY_W)){//move forward
        AudioMoving += 1;
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))//toggle sprint
                {camera.walkForward(movementSpeedRun*delta);}
        else{camera.walkForward(movementSpeedWalk*delta);}
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
        {AudioMoving += 1;
             if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))//toggle sprint
                {camera.walkBackwards(movementSpeedRun*delta);}
                else{camera.walkBackwards(movementSpeedWalk*delta);}
        }
      
        if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left
               {AudioMoving += 1;
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))//toggle sprint
                {camera.strafeLeft(movementSpeedRun*delta);}
                else{camera.strafeLeft(movementSpeedWalk*delta);} }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right
               {AudioMoving += 1;
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))//toggle sprint
                {camera.strafeRight(movementSpeedRun*delta);}
                else{camera.strafeRight(movementSpeedWalk*delta);} }
        
        
                    if (AudioMoving-PreAudioMoving > 0){
              AL10.alSourcePlay(source.get(0));}
                    else if (AudioMoving <= 0){AL10.alSourcePause(source.get(0));}   
            //AL10.alSourceStop(source.get(0));
                    PreAudioMoving = AudioMoving;
            AudioMoving = 0;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
camera.jump(movementSpeedWalk*delta);
        }
if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
camera.decend(movementSpeedWalk*delta);
}

        //look through the camera before you draw anything
        
            // Render the game
GL11.glLoadIdentity(); 
camera.lookThrough();

     renderSetup3d();
     GL20.glUseProgram(shader.getProgramId());
     render3d();
     GL11.glLoadIdentity();
     GL20.glUseProgram(0);
     render2d();
     renderText();
     
            // Flip the buffers and sync to 60 FPS
            Display.sync(60);
            Display.update();
            
        }

        // Dispose any resources and destroy our window
        dispose();
            AL10.alDeleteSources(source);
    AL10.alDeleteBuffers(buffer);
    AL.destroy();
        Display.destroy();
    }

    // Exit our game loop and close the window
    public void exit() {
        running = false;
    }

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
        GL20.glUseProgram(ShaderIndex);
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        GL11.glVertexPointer(Chunk.vertexSize, GL11.GL_FLOAT, 0, 0L);
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
        GL20.glUseProgram(0);
   }

    // Clean up on exit
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
        // if VSYNC is enabled FPS wont go above 60 regardless of max sync rate being set higher. 
        //VSYNC off does yield the expected performance increase
		if (getTime() - lastFPS > 1000) {
                    finalFPS = fps;
                    fps = 0;
                    lastFPS += 1000;
		}
		fps++;
}
    
    public void initTexture() {
 
try {glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);

atlas = TextureLoader.getTexture("{PNG", ResourceLoader.getResourceAsStream("res/Atlas.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
   public enum State {
	INTRO, GAME, MAIN_MENU;
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
        GL11.glVertexPointer(Chunk.vertexSize, GL11.GL_FLOAT, 0, 0L);
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
   
  int loadALData() {
    // Load wav data into a buffer.
    AL10.alGenBuffers(buffer);
 
    if(AL10.alGetError() != AL10.AL_NO_ERROR)
      return AL10.AL_FALSE;
 
    //Loads the wave file from your file system
    /*java.io.FileInputStream fin = null;
    try {
      fin = new java.io.FileInputStream("FancyPants.wav");
    } catch (java.io.FileNotFoundException ex) {
      ex.printStackTrace();
      return AL10.AL_FALSE;
    }
    WaveData waveFile = WaveData.create(fin);
    try {
      fin.close();
    } catch (java.io.IOException ex) {
    }*/
 
    //Loads the wave file from this class's package in your classpath
    WaveData waveFile = WaveData.create("res/WalkingGravel.wav");
 
    AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
    waveFile.dispose();
 
    // Bind the buffer with the source.
    AL10.alGenSources(source);
 
    if (AL10.alGetError() != AL10.AL_NO_ERROR)
      return AL10.AL_FALSE;
 
    AL10.alSourcei(source.get(0), AL10.AL_BUFFER,   buffer.get(0) );
    AL10.alSourcef(source.get(0), AL10.AL_PITCH,    1.0f          );
    AL10.alSourcef(source.get(0), AL10.AL_GAIN,     1.0f          );
    AL10.alSource (source.get(0), AL10.AL_POSITION, sourcePos     );
    AL10.alSource (source.get(0), AL10.AL_VELOCITY, sourceVel     );
    AL10.alSourcei(source.get(0), AL10.AL_LOOPING,  AL10.AL_TRUE  );
 
    // Do another error check and return.
    if (AL10.alGetError() == AL10.AL_NO_ERROR)
      return AL10.AL_TRUE;
 
    return AL10.AL_FALSE;
  }
 
  /**
   * void setListenerValues()
   *
   *  We already defined certain values for the Listener, but we need
   *  to tell OpenAL to use that data. This function does just that.
   */
  void setListenerValues() {
    AL10.alListener(AL10.AL_POSITION,    listenerPos);
    AL10.alListener(AL10.AL_VELOCITY,    listenerVel);
    AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
  }

}