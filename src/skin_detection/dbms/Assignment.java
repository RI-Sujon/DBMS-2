package skin_detection.dbms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class Assignment {
    double finalArray[][][] = new double[256][256][256] ;
    int count=00 , fileCount = 0;
    int fileCountStartTest = 0 , fileCountEndTest = 0;
    DecimalFormat df = new DecimalFormat("#.##") ;
    
    Map<File,File>map ;
    
    File folder1 = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\skin_detection\\training\\mainImage");
    File folder2 = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\skin_detection\\training\\maskImage");
        
    File [] file1 = folder1.listFiles();
    File [] file2 = folder2.listFiles();
    double [] finalAcc = new double[5] ;
    double [] finalPre = new double[5] ;
    double [] finalRecall = new double[5] ;
    double [] finalFM = new double[5] ;
    int counting=0 ;
    
    public Assignment() throws IOException{
        map = new HashMap<File,File>() ;
        
        for(int i=0 ; i<file1.length ; i++){
            map.put(file2[i], file1[i]) ;
        }
        
        List keys = new ArrayList(map.keySet());
        Collections.shuffle(keys) ;
        Collections.shuffle(keys) ;
        
        for (Object o : keys) {
            map.get(o);
        }
        
        for(int i=0 ; i<5 ; i++){
            fileCountEndTest = fileCountEndTest + 111 ;
            System.out.print(i+1 + ":");
            training() ;
            testing() ;
            accuracy2() ;
            
            fileCountStartTest = fileCountStartTest + 111 ;
        }
        double avAcc = 0,avPre=0,avRe=0,avFM = 0 ;
        for(int i=0 ; i<5 ; i++){
            avAcc = avAcc + finalAcc[i] ;
            
            avFM = avFM + finalFM[i] ;
        }
        
        System.out.println("Average Accuracy: " + df.format(avAcc/5) + "%");
        
        System.out.println("Average F-measure: " + df.format(avFM/5) + "%");
        
      
        
    }
    
    public void training() throws IOException{
       
        double skinArray[][][] = new double[256][256][256] ;
        double nonSkinArray[][][] = new double[256][256][256] ;
       
        int totalOfSkin=0 , totalOfNonSkin=0 ,f = -1 ;
        
        for(Map.Entry m:map.entrySet()){
            
            f++ ;
            
            if(f >= fileCountStartTest && f < fileCountEndTest){
                continue ;
            }
            
            if(f >= 555) break ;
            
            BufferedImage img = ImageIO.read((File)m.getValue()) ;
            BufferedImage mask = ImageIO.read((File)m.getKey()) ;
            
            for(int i=0 ; i<img.getHeight() ; i++){
                for(int j=0 ; j<img.getWidth() ; j++){
                    int iR=0,iG=0,iB=0,mR=0,mG=0,mB=0 ;
                    
                    int imgRGB = img.getRGB(j,i) ;
                    iB = imgRGB & 0xff ;
                    iG = (imgRGB & 0xff00) >> 8 ;
                    iR = (imgRGB & 0xff0000) >> 16 ;
                  
                    int maskRGB = mask.getRGB(j,i) ;
                    mB = maskRGB & 0xff ;
                    mG = (maskRGB & 0xff00) >> 8 ;
                    mR = (maskRGB & 0xff0000) >> 16 ;
                    
                    if(mB>=220 && mG>=220 && mR>=220){
                        nonSkinArray[iR][iG][iB]++ ;
                        totalOfNonSkin++ ;
                    }
                    
                    else{
                        skinArray[iR][iG][iB]++ ;
                        totalOfSkin++ ;
                    }
                }
            }
        }
        
        for(int i=0 ; i<256 ; i++){
            for(int j=0 ; j<256 ; j++){
                for(int k=0 ; k<256 ; k++){
                    skinArray[i][j][k] = skinArray[i][j][k]/totalOfSkin ;
                    nonSkinArray[i][j][k] = nonSkinArray[i][j][k]/totalOfNonSkin ;
                    
                    if(nonSkinArray[i][j][k]==0){
                        finalArray[i][j][k] = 0.5 ;
                    }
                    else 
                        finalArray[i][j][k] = skinArray[i][j][k]/nonSkinArray[i][j][k] ; 
                }
            }
        } 
    }
    
    public void testing() throws IOException{
        
        fileCount = 0 ;
        int f = 0 ;
        for(Map.Entry m1:map.entrySet()){
            
            f++ ;
            if(f >= 555){ 
                break ;
            } 
            
            if(f >= fileCountStartTest && f < fileCountEndTest){
                
                BufferedImage image = ImageIO.read((File)m1.getValue()) ;

                for(int i=0 ; i<image.getHeight() ; i++){
                    for(int j=0 ; j<image.getWidth() ; j++){
                        int iR=0, iG=0, iB=0 ;

                        int imgRGB = image.getRGB(j,i) ;
                        iB = imgRGB & 0xff ;
                        iG = (imgRGB & 0xff00) >> 8 ;
                        iR = (imgRGB & 0xff0000) >> 16 ;

                        if(finalArray[iR][iG][iB]<0.8){
                            image.setRGB(j,i,0xffffff);
                            
                        }
                        
                        else{
                            image.setRGB(j, i, image.getRGB(j, i));
                        }
                    }
                }

                File newMask = new File("") ;

                if(count<10){
                    newMask = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\skin_detection\\training\\myMask\\0000" + count + ".jpg") ;
                }

                else if(count<100){
                    newMask = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\skin_detection\\training\\myMask\\000" + count + ".jpg") ;
                }

                else if(count<1000){
                    newMask = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\skin_detection\\training\\myMask\\00" + count + ".jpg") ;
                }

                ImageIO.write(image, "jpg", newMask) ;
               
                count++ ;
                map.replace((File) m1.getKey(),newMask ) ;
                
            }
            else continue ;
        }
    }
    
    
    public void accuracy2() throws IOException{
        int TP[] = new int[555] ;
        int FP[] = new int[555] ;
        int TN[] = new int[555] ;
        int FN[] = new int[555] ;
        double accuracy[] = new double[555] ;
        double F1_Score[] = new double[555] ;
        double Precission[] = new double[555] ;
        double Recall[] = new double[555] ;
        double totalAccuracy = 0 ,totalPrecission=0 , totalRecall=0, totalF1_Score = 0;
        
        for(int i=0 ; i<555 ; i++){
            TP[i] = 0 ;
            TN[i] = 0 ;
            FP[i] = 0 ;
            FN[i] = 0 ;
        }
        
        int f=0 ;
        fileCount = 0 ;
      
        for(Map.Entry m:map.entrySet()){
            
            fileCount++ ;
            if(f < fileCountStartTest){
                f++ ;
                continue ;
            }
            
            if(f>=fileCountEndTest) break ;
           
            BufferedImage myMask = ImageIO.read((File)m.getValue()) ;
            BufferedImage sMask = ImageIO.read((File)m.getKey()) ;
            
            for(int i=0 ; i<myMask.getHeight() ; i++){
                for(int j=0 ; j<myMask.getWidth() ; j++){
                    int tR=0,tG=0,tB=0,mR=0,mG=0,mB=0 ;
                    
                    int myRGB = myMask.getRGB(j,i) ;
                    int sRGB = sMask.getRGB(j,i) ;
                            
                    String hex = "#"+Integer.toHexString(myRGB).substring(2);
                    String hex1 = "#"+Integer.toHexString(sRGB).substring(2);
                    
                    /*
                    
                    if( hex.equals("#ffffff") && hex1.equals("#ffffff")){
                        TP[f]++ ;
                    }
                    
                    else if(!hex.equals("#ffffff") && !hex1.equals("#ffffff")){
                        TN[f]++ ;
                    }
                    
                    
                    
                    else if(!hex.equals("#ffffff") && hex1.equals("#ffffff")){
                        FN[f]++ ;
                    }
                    
                    else{
                        FP[f]++;
                    }/*/
                    
                     mB = myRGB & 0xff ;
                        mG = (myRGB & 0xff00) >> 8 ;
                        mR = (myRGB & 0xff0000) >> 16 ;
                    
                        tB = sRGB & 0xff ;
                        tG = (sRGB & 0xff00) >> 8 ;
                        tR = (sRGB & 0xff0000) >> 16 ;
                    
                    if( mB>240 && mG>240 && mR>240 && tB>240 && tG>240 && tR>240 ){
                        TP[f]++ ;
                    }
                    
                    else if((mB<240 || mG<240 || mR<240) && (tB<240 || tG<240 || tR<240)){
                        TN[f]++ ;
                    }
                    
                    
                    
                    else if(mB>240 && mG>240 && mR>240 && (tB<240 || tG<240 || tR<240 )){
                        FP[f]++ ;
                    }
                    
                    else{
                        FN[f]++;
                    }
                    
                }
            }
            
            
            if(FP[f]==0) FP[f] = 1 ;
            if(FN[f]==0) FN[f] = 1 ;
            
             
            accuracy[f] = (TP[f]+TN[f])*1.0/(TP[f]+TN[f]+FN[f]+FP[f]) ;
            
            F1_Score[f] = 2.0*TP[f] /(2.0*TP[f] + FN[f] + FP[f]) ;

            f++ ;
        }   
       
        for(int i=0 ; i<111 ; i++){
            totalAccuracy = totalAccuracy + accuracy[i+fileCountStartTest] ;
            
            totalF1_Score = totalF1_Score + F1_Score[i+fileCountStartTest] ;
        }
        
        totalAccuracy = totalAccuracy*1.0 / 111 ;
        
        totalF1_Score = totalF1_Score*1.0 / 111 ;
        
        finalAcc[counting] = totalAccuracy*100 ;
        
        finalFM[counting] = totalF1_Score*100 ;
        counting++ ;
        
        System.out.println("\tAccuracy: " + df.format(totalAccuracy*100) + "%");
       
        System.out.println("\tF-measure: " + df.format(totalF1_Score*100) + "%");
      
    } 
}
















































/*
    public void accuracy() throws IOException{
        
        int yes[] = new int[55] ;
        int no[] = new int[55] ;
        double accuracy[] = new double[55] ;
        double finalAccuracy = 0 ;
        
        for(int i=0 ; i<55 ; i++){
            yes[i] = 1 ;
            no[i] = 0 ;
        }
        
        int f=0 ;
        fileCount = 0 ;
        
        for(Map.Entry m:map.entrySet()){
            
            fileCount++ ;
            if(fileCount <= 500){
                continue ;
            }
            
            if(f>54) break ;
            
            BufferedImage img = ImageIO.read((File)m.getValue()) ;
            BufferedImage mask = ImageIO.read((File)m.getKey()) ;
            
            for(int i=0 ; i<img.getHeight() ; i++){
                for(int j=0 ; j<img.getWidth() ; j++){
                    int tR=0,tG=0,tB=0,mR=0,mG=0,mB=0 ;
                    
                    int testRGB = img.getRGB(j,i) ;
                    tB = testRGB & 0xff ;
                    tG = (testRGB & 0xff00) >> 8 ;
                    tR = (testRGB & 0xff0000) >> 16 ;
                    
                    int maskRGB = mask.getRGB(j,i) ;
                    
                    mB = maskRGB & 0xff ;
                    mG = (maskRGB & 0xff00) >> 8 ;
                    mR = (maskRGB & 0xff0000) >> 16 ;
                    
                    if(mB>240 && mG>240 && mR>240 && tB>240 && tG>240 && tR>240){
                        yes[f]++ ;
                    }
                    
                    else if(mB<240 && mG<240 && mR<240 && tB<240 && tG<240 && tR<240){
                        yes[f]++ ;
                    }        
                    
                    else{
                        no[f]++ ;
                    }
                }
            }
        
            accuracy[f] = yes[f]*1.0/(yes[f]+no[f]) ;
            
            f++ ;
        }   
    
        for(int i=0 ; i<55 ; i++){
            finalAccuracy = finalAccuracy + accuracy[i] ;
        }
        
        finalAccuracy = finalAccuracy*1.0 / 55 ;
        
        System.out.println("Average Accuracy: " + df.format(finalAccuracy*100) + "%");
    }













package dbms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class Assignment {
    double finalArray[][][] = new double[256][256][256] ;
    int count=00 , fileCount = 0;
    int fileCountStartTest = 0 , fileCountEndTest = 0;
    DecimalFormat df = new DecimalFormat("#.##") ;
    
    Map<File,File>map ;
    
    File folder1 = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\training\\mainImage");
    File folder2 = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\training\\maskImage");
        
    File [] file1 = folder1.listFiles();
    File [] file2 = folder2.listFiles();
    double [] finalAcc = new double[5] ;
    double [] finalPre = new double[5] ;
    double [] finalRecall = new double[5] ;
    double [] finalFM = new double[5] ;
    int counting=0 ;
    
    public Assignment() throws IOException{
        map = new HashMap<File,File>() ;
        
        for(int i=0 ; i<file1.length ; i++){
            map.put(file2[i], file1[i]) ;
        }
        
        List keys = new ArrayList(map.keySet());
        Collections.shuffle(keys) ;
        Collections.shuffle(keys) ;
        
        for (Object o : keys) {
            map.get(o);
        }
        
        for(int i=0 ; i<5 ; i++){
            fileCountEndTest = fileCountEndTest + 111 ;
            System.out.print(i+1 + ":");
            training() ;
            testing() ;
            accuracy2() ;
            
            fileCountStartTest = fileCountStartTest + 111 ;
        }
        double avAcc = 0,avPre=0,avRe=0,avFM = 0 ;
        for(int i=0 ; i<5 ; i++){
            avAcc = avAcc + finalAcc[i] ;
            avPre = avPre + finalPre[i] ;
            avRe = avRe + finalRecall[i] ;
            avFM = avFM + finalFM[i] ;
        }
        
        System.out.println("Average Accuracy: " + df.format(avAcc/5) + "%");
        System.out.println("Average Precission: " + df.format(avPre/5) + "%");
        System.out.println("Average Recall: " + df.format(avRe/5) + "%");
        System.out.println("Average F-measure: " + df.format(avFM/5) + "%");
        
        System.out.println("Final Average F-measure: " + df.format(2*avPre*avRe/(avPre+avRe)) + "%");
        
    }
    
    public void training() throws IOException{
       
        double skinArray[][][] = new double[256][256][256] ;
        double nonSkinArray[][][] = new double[256][256][256] ;
       
        int totalOfSkin=0 , totalOfNonSkin=0 ,f = -1 ;
        
        for(Map.Entry m:map.entrySet()){
            
            f++ ;
            
            if(f >= fileCountStartTest && f < fileCountEndTest){
                continue ;
            }
            
            if(f >= 555) break ;
            
            BufferedImage img = ImageIO.read((File)m.getValue()) ;
            BufferedImage mask = ImageIO.read((File)m.getKey()) ;
            
            for(int i=0 ; i<img.getHeight() ; i++){
                for(int j=0 ; j<img.getWidth() ; j++){
                    int iR=0,iG=0,iB=0,mR=0,mG=0,mB=0 ;
                    
                    int imgRGB = img.getRGB(j,i) ;
                    iB = imgRGB & 0xff ;
                    iG = (imgRGB & 0xff00) >> 8 ;
                    iR = (imgRGB & 0xff0000) >> 16 ;
                  
                    int maskRGB = mask.getRGB(j,i) ;
                    mB = maskRGB & 0xff ;
                    mG = (maskRGB & 0xff00) >> 8 ;
                    mR = (maskRGB & 0xff0000) >> 16 ;
                    
                    if(mB>=220 && mG>=220 && mR>=220){
                        nonSkinArray[iR][iG][iB]++ ;
                        totalOfNonSkin++ ;
                    }
                    
                    else{
                        skinArray[iR][iG][iB]++ ;
                        totalOfSkin++ ;
                    }
                }
            }
        }
        
        for(int i=0 ; i<256 ; i++){
            for(int j=0 ; j<256 ; j++){
                for(int k=0 ; k<256 ; k++){
                    skinArray[i][j][k] = skinArray[i][j][k]/totalOfSkin ;
                    nonSkinArray[i][j][k] = nonSkinArray[i][j][k]/totalOfNonSkin ;
                    
                    if(nonSkinArray[i][j][k]==0){
                        finalArray[i][j][k] = 0.5 ;
                    }
                    else 
                        finalArray[i][j][k] = skinArray[i][j][k]/nonSkinArray[i][j][k] ; 
                }
            }
        } 
    }
    
    public void testing() throws IOException{
        
        fileCount = 0 ;
        int f = 0 ;
        for(Map.Entry m1:map.entrySet()){
            
            f++ ;
            if(f >= 555){ 
                break ;
            } 
            
            if(f >= fileCountStartTest && f < fileCountEndTest){
                
                BufferedImage image = ImageIO.read((File)m1.getValue()) ;

                for(int i=0 ; i<image.getHeight() ; i++){
                    for(int j=0 ; j<image.getWidth() ; j++){
                        int iR=0, iG=0, iB=0 ;

                        int imgRGB = image.getRGB(j,i) ;
                        iB = imgRGB & 0xff ;
                        iG = (imgRGB & 0xff00) >> 8 ;
                        iR = (imgRGB & 0xff0000) >> 16 ;

                        if(finalArray[iR][iG][iB]<0.8){
                            image.setRGB(j,i,0xffffff);
                            
                        }
                        
                        else{
                            image.setRGB(j, i, image.getRGB(j, i));
                        }
                    }
                }

                File newMask = new File("") ;

                if(count<10){
                    newMask = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\training\\myMask\\0000" + count + ".jpg") ;
                }

                else if(count<100){
                    newMask = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\training\\myMask\\000" + count + ".jpg") ;
                }

                else if(count<1000){
                    newMask = new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\training\\myMask\\00" + count + ".jpg") ;
                }

                ImageIO.write(image, "jpg", newMask) ;
               
                count++ ;
                map.replace((File) m1.getKey(),newMask ) ;
                
            }
            else continue ;
        }
    }
    
    
    public void accuracy2() throws IOException{
        int TP[] = new int[555] ;
        int FP[] = new int[555] ;
        int TN[] = new int[555] ;
        int FN[] = new int[555] ;
        double accuracy[] = new double[555] ;
        double F1_Score[] = new double[555] ;
        double Precission[] = new double[555] ;
        double Recall[] = new double[555] ;
        double totalAccuracy = 0 ,totalPrecission=0 , totalRecall=0, totalF1_Score = 0;
        
        for(int i=0 ; i<555 ; i++){
            TP[i] = 0 ;
            TN[i] = 0 ;
            FP[i] = 0 ;
            FN[i] = 0 ;
        }
        
        int f=0 ;
        fileCount = 0 ;
      
        for(Map.Entry m:map.entrySet()){
            
            fileCount++ ;
            if(f < fileCountStartTest){
                f++ ;
                continue ;
            }
            
            if(f>=fileCountEndTest) break ;
           
            BufferedImage myMask = ImageIO.read((File)m.getValue()) ;
            BufferedImage sMask = ImageIO.read((File)m.getKey()) ;
            
            for(int i=0 ; i<myMask.getHeight() ; i++){
                for(int j=0 ; j<myMask.getWidth() ; j++){
                    int tR=0,tG=0,tB=0,mR=0,mG=0,mB=0 ;
                    
                    int myRGB = myMask.getRGB(j,i) ;
                    int sRGB = sMask.getRGB(j,i) ;
                            
                    String hex = "#"+Integer.toHexString(myRGB).substring(2);
                    String hex1 = "#"+Integer.toHexString(sRGB).substring(2);
                    
                    
                    
                    if( hex.equals("#ffffff") && hex1.equals("#ffffff")){
                        TP[f]++ ;
                    }
                    
                    else if(!hex.equals("#ffffff") && !hex1.equals("#ffffff")){
                        TN[f]++ ;
                    }
                    
                    
                    
                    else if(!hex.equals("#ffffff") && hex1.equals("#ffffff")){
                        FN[f]++ ;
                    }
                    
                    else{
                        FP[f]++;
                    }
                    
                }
            }
            
            
            if(FP[f]==0) FP[f] = 1 ;
            if(FN[f]==0) FN[f] = 1 ;
            
             
            accuracy[f] = (TP[f]+TN[f])*1.0/(TP[f]+TN[f]+FN[f]+FP[f]) ;
            Precission[f] = TP[f] / (TP[f] + FP[f]) ;
            Recall[f] = TP[f] / (TP[f] + FN[f]) ;
            F1_Score[f] = 2.0*TP[f] /(2.0*TP[f] + FN[f] + FP[f]) ;

            f++ ;
        }   
       
        for(int i=0 ; i<111 ; i++){
            totalAccuracy = totalAccuracy + accuracy[i+fileCountStartTest] ;
            totalPrecission = totalPrecission + Precission[i+fileCountStartTest] ;
            totalRecall = totalRecall + Recall[i+fileCountStartTest] ;
            totalF1_Score = totalF1_Score + F1_Score[i+fileCountStartTest] ;
        }
        
        totalAccuracy = totalAccuracy*1.0 / 111 ;
        totalPrecission = totalPrecission*1.0 / 111 ;
        totalRecall = totalRecall*1.0 / 111 ;
        totalF1_Score = totalF1_Score*1.0 / 111 ;
        
        finalAcc[counting] = totalAccuracy*100 ;
        finalPre[counting] = totalPrecission*100 ;
        finalRecall[counting] = totalRecall*100 ;
        finalFM[counting] = totalF1_Score*100 ;
        counting++ ;
        
        System.out.println("\tAccuracy: " + df.format(totalAccuracy*100) + "%");
        System.out.println("\tPrecission: " + df.format(totalPrecission*100) + "%");
        System.out.println("\tRecall: " + df.format(totalRecall*100) + "%");
        System.out.println("\tF-measure: " + df.format(totalF1_Score*100) + "%");
      
    } 
}
















































/*
    public void accuracy() throws IOException{
        
        int yes[] = new int[55] ;
        int no[] = new int[55] ;
        double accuracy[] = new double[55] ;
        double finalAccuracy = 0 ;
        
        for(int i=0 ; i<55 ; i++){
            yes[i] = 1 ;
            no[i] = 0 ;
        }
        
        int f=0 ;
        fileCount = 0 ;
        
        for(Map.Entry m:map.entrySet()){
            
            fileCount++ ;
            if(fileCount <= 500){
                continue ;
            }
            
            if(f>54) break ;
            
            BufferedImage img = ImageIO.read((File)m.getValue()) ;
            BufferedImage mask = ImageIO.read((File)m.getKey()) ;
            
            for(int i=0 ; i<img.getHeight() ; i++){
                for(int j=0 ; j<img.getWidth() ; j++){
                    int tR=0,tG=0,tB=0,mR=0,mG=0,mB=0 ;
                    
                    int testRGB = img.getRGB(j,i) ;
                    tB = testRGB & 0xff ;
                    tG = (testRGB & 0xff00) >> 8 ;
                    tR = (testRGB & 0xff0000) >> 16 ;
                    
                    int maskRGB = mask.getRGB(j,i) ;
                    
                    mB = maskRGB & 0xff ;
                    mG = (maskRGB & 0xff00) >> 8 ;
                    mR = (maskRGB & 0xff0000) >> 16 ;
                    
                    if(mB>240 && mG>240 && mR>240 && tB>240 && tG>240 && tR>240){
                        yes[f]++ ;
                    }
                    
                    else if(mB<240 && mG<240 && mR<240 && tB<240 && tG<240 && tR<240){
                        yes[f]++ ;
                    }        
                    
                    else{
                        no[f]++ ;
                    }
                }
            }
        
            accuracy[f] = yes[f]*1.0/(yes[f]+no[f]) ;
            
            f++ ;
        }   
    
        for(int i=0 ; i<55 ; i++){
            finalAccuracy = finalAccuracy + accuracy[i] ;
        }
        
        finalAccuracy = finalAccuracy*1.0 / 55 ;
        
        System.out.println("Average Accuracy: " + df.format(finalAccuracy*100) + "%");
    }
    */
    

    
    