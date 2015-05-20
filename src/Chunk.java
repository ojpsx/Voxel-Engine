public class Chunk {
    
    static final int chunkSize = 16;
    private static final int[][][] chunk = new int[chunkSize][chunkSize][chunkSize];
    static final int vertexSize = 3;
    static final int amountOfVertices = 4;
    static final int quadFaces =6;
    static final int colorSize = 3;
    
   
   public Chunk() {
   for (int X=0; X<Chunk.chunkSize; X++){
        for (int Y=0; Y<Chunk.chunkSize; Y++){
                for (int Z=0; Z<Chunk.chunkSize; Z++){
                                        if ((Chunk.chunkSize-Y)+(SimplexNoise.noise(X/3,Z/3)*5)>0){
                        chunk[X][Y][Z] = 1;}
                }
            }
        }
    
        for (int X=0; X<Chunk.chunkSize; X++){
            for (int Y=0; Y<Chunk.chunkSize; Y++){
                for (int Z=0; Z<Chunk.chunkSize; Z++){
                      if (chunk[X][Y][Z] == 1){
                                            int nY;
                        
                        if (Y>2){nY = Y-2;}
                        else {nY =0;}
                        chunk[X][nY][Z] = 2;
                    if(SimplexNoise.noise(X/3,Y/3,Z/3)>0.6){chunk[X][nY][Z] = 1;}
                  
                                                }
                   }
                }
            }

}
    public static int getChunkContent(int x, int y, int z) {
        return chunk[x][y][z];
        
    
    }
    public static int getchunkSize(){
     
        return chunkSize * chunkSize * chunkSize;
}
    public static float[] gettexCoord(int ID){
        int totaltextures = 7;
        float texatlaspixelwidth = 512.0f;
        float texatlaspixelheight = 512.0f;
        
        float sU[] = new float[totaltextures]; //column in atlas start
        float eU[] = new float[totaltextures]; //column in atlas end
        float sV[] = new float[totaltextures]; //row in atlas start
        float eV[] = new float[totaltextures]; //row in atlas start
                
                sU[1] = 1f/texatlaspixelwidth; //column in atlas start
                eU[1]=65.0f/texatlaspixelwidth; //column in atlas end
                sV[1]=1f/texatlaspixelheight; //row in atlas start
                eV[1]=65.0f/texatlaspixelheight; //row in atlas end
                
                sU[2] = 67f/texatlaspixelwidth; //column in atlas start
                eU[2]=131.0f/texatlaspixelwidth; //column in atlas end
                sV[2]=1f/texatlaspixelheight; //row in atlas start
                eV[2]=65.0f/texatlaspixelheight; //row in atlas end
                
                sU[3] = 133f/texatlaspixelwidth; //column in atlas start
                eU[3]=197.0f/texatlaspixelwidth; //column in atlas end
                sV[3]=1f/texatlaspixelheight; //row in atlas start
                eV[3]=65.0f/texatlaspixelheight; //row in atlas end
                
                sU[4] = 199f/texatlaspixelwidth; //column in atlas start
                eU[4]=263.0f/texatlaspixelwidth; //column in atlas end
                sV[4]=1f/texatlaspixelheight; //row in atlas start
                eV[4]=65.0f/texatlaspixelheight; //row in atlas end
                

                
                                
                sU[5] = 265f/texatlaspixelwidth; //column in atlas start
                eU[5]=328.0f/texatlaspixelwidth; //column in atlas end
                sV[5]=1f/texatlaspixelheight; //row in atlas start
                eV[5]=65.0f/texatlaspixelheight; //row in atlas end
                
                sU[6] = 1f/texatlaspixelwidth; //column in atlas start
                eU[6]=129.0f/texatlaspixelwidth; //column in atlas end
                sV[6]=67f/texatlaspixelheight; //row in atlas start
                eV[6]=195.0f/texatlaspixelheight; //row in atlas end
        

        return new float[] {
            eU[ID], sV[ID],
            sU[ID], sV[ID],
            sU[ID], eV[ID], 
            eU[ID], eV[ID], 
            
            eU[ID], sV[ID],
            sU[ID], sV[ID],
            sU[ID], eV[ID],
            eU[ID], eV[ID],
            
            eU[ID], sV[ID],
            sU[ID], sV[ID],
            sU[ID], eV[ID],
            eU[ID], eV[ID],
            
            eU[ID], sV[ID],
            sU[ID], sV[ID],
            sU[ID], eV[ID],
            eU[ID], eV[ID],
            
            eU[ID], sV[ID],
            sU[ID], sV[ID],
            sU[ID], eV[ID],
            eU[ID], eV[ID],
            
            eU[ID], sV[ID],
            sU[ID], sV[ID],
            sU[ID], eV[ID],
            eU[ID], eV[ID]
            };
    }
    public static float[] CreateCube(float x, float y, float z) {
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
       public float[] CallChunkVertexs(){ 
   float[] vertarray = null; 
    for (int X=0; X<chunkSize+1; X++){
        for (int Y=0; Y<chunkSize+1; Y++){
                for (int Z=0; Z<chunkSize+1; Z++){
                    if (chunk[X][Y][Z] > 0) {
                        vertarray = CreateCube((float) X, (float) Y, (float) Z);


                }
                }
        }
   }
       return vertarray;}


       public float[] CallChunkTex(){ 
   float[] texarray = null; 
    for (int X=0; X<chunkSize+1; X++){
        for (int Y=0; Y<chunkSize+1; Y++){
                for (int Z=0; Z<chunkSize+1; Z++){
                    if (chunk[X][Y][Z] > 0) {
                        texarray = gettexCoord(chunk[X][Y][Z]);

                }
                }
        }
   }
       return texarray;}
       
       public static float[] CreateSkyBox(float x, float y, float z, float size) {
		return new float[] {
				// Reversed order to show inside of box due to culling
				
				
				x ,y-size, z-size,
                                x,y-size , z,
                                x ,y, z,
                                x, y ,z-size,
                                
                                x -size,y-size, z,
                                x-size,y-size , z-size,
                                x - size,y, z-size,
                                x-size, y ,z,
                                
                                x ,y, z-size,
                                x-size,y , z-size,
                                x - size,y-size, z-size,
                                x , y-size ,z-size,
                                
                                x ,y -size, z,
                                x-size,y-size , z,
                                x - size,y, z,
                                x , y ,z,
                                
                                x ,y-size , z-size,
                                x-size,y-size , z-size,
                                x - size,y-size, z,
                                x , y-size ,z,
                                
                                x ,y , z,
                                x-size,y , z,
                                x - size,y, z-size,
                                x , y ,z-size,
                
                };
    }
}
