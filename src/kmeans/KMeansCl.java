
package kmeans;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;



public class KMeansCl {
    
    int R[], G[], B[], r[], g[], b[];
    BufferedImage image;
    File file = new File("sujon.jpg");
    File outputFile = new File("kmeans-output.jpg");
    int height , width;
    int totCount[] ;
    int K ;
    double arr[] ;
        
    public KMeansCl(int K) throws IOException {
        this.K = K ;
        init() ;
        middle() ;
        printImage() ;
       
    }
    
    
    public void init() throws IOException{
        R = new int[K];
        G = new int[K];
        B = new int[K];

        r = new int[K];
        g = new int[K];
        b = new int[K];
        
        for(int i = 0 ; i<K ; i++){
            r[i] = 0 ;
            g[i] = 0 ;
            b[i] = 0 ;
        }
        
        arr = new double[K] ;
        totCount = new int[K] ;
        
        int y[]=new int[K];
        int x[]=new int[K];

        image = ImageIO.read(file);

        width=image.getWidth();
        height=image.getHeight();


        for(int i=0;i<K;i++){
            Random rand=new Random();

            y[i]=rand.nextInt(width);
            x[i]=rand.nextInt(height);

            int pixel = image.getRGB(y[i], x[i]);

            int red = (pixel >> 16) & 0xff;
            int green = (pixel >> 8) & 0xff;
            int blue = (pixel) & 0xff;

            R[i]=red;
            G[i]=green;
            B[i]=blue;

            System.out.println("i:"+i+"\tR:"+R[i]+"\tG:"+G[i] +"\tB:"+B[i]);
        }
    } 
    
    public double getDistance(double x,double y,double x1,double y1,double x2,double y2){
        double distance=Math.pow((x-y),2)+Math.pow((x1-y1),2)+Math.pow((x1-y1),2);
        distance = Math.sqrt(distance);
        
        return distance;
    }
    
    public void middle(){
        while(true){
            
            for(int i = 0 ; i<K ; i++){
                totCount[i] = 0 ;
            }
            
            for (int i = 0; i < height ; i++){
                for (int j = 0; j < width ; j++){
                    for(int k=0;k<K;k++){
                        arr[k]=0;
                    }

                    int pixel = image.getRGB(j, i);

                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;

                    for(int k=0;k<K;k++){
                        arr[k]=getDistance(red,R[k],green,G[k],blue,B[k]);
                    }
                    
                    double min= arr[0];
                    int index=0;

                    for(int k=0;k < K;k++){
                        if(min>arr[k]){
                            min=(int) arr[k];
                            index=k;
                        }
                    }
                   
                    r[index] = r[index] + red ;
                    g[index] = g[index] + green ;
                    b[index] = b[index] + blue ;
                    
                    totCount[index]++ ;                 
                }
            }
            
            for(int i=0;i < K;i++){
                if(totCount[i]!=0){
                    r[i] = r[i]/totCount[i] ;
                    g[i] = g[i]/totCount[i] ;
                    b[i] = b[i]/totCount[i] ;
                }
                else {
                    r[i] = 0 ;
                    g[i] = 0 ;
                    b[i] = 0 ;
                }
                
                System.out.println("i:"+i+"\tr:"+r[i]+"\tg:"+g[i] +"\tb:"+b[i] + "\n");
            }
            int count = 0 ;
            for(int i = 0 ; i<K ; i++){
                if(getDistance(r[i],R[i],g[i],G[i],b[i],B[i])<0.000000001){
                    count++ ;
                }
                
                R[i] = r[i] ;
                G[i] = g[i] ;
                B[i] = b[i] ;
            }
            
            if(count==K) break ;
            
       }
    }
    
    public void printImage() throws IOException{
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                int pixel = image.getRGB(j, i);

                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                for(int k=0 ; k<K ; k++){

                    arr[k]=getDistance(red,R[k],green,G[k],blue,B[k]);
                }

                int min=(int) arr[0];

                int index = 0;
                for(int k=0 ; k<K ;k++){
                    if(min>arr[k]){
                        min = (int) arr[k];
                        index = k;
                    }
                }

                image.setRGB(j,i,(new Color(R[index],G[index],B[index])).getRGB());
            }
        }
        
        ImageIO.write(image, "jpg", outputFile) ;
    }
}

