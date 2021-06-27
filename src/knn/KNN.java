package knn ;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class KNN{
    File file= new File("C:\\Users\\Asus\\Documents\\NetBeansProjects\\DBMS\\src\\knn\\iris.data");
    int K ;
    double a,b,c,d ;
    double [] distance = new double[160];
    double[] sortedDis ;
    Data [] data = new Data[160];
    int nTest;
    int N ;
    ArrayList<String>  ans  = new ArrayList<String>(160) ;
    int countAns = 0 ,count=0;
    int startIndex=150,endIndex=150 ;
    double [] acc ;
    
    public KNN(int K,int N) throws IOException{
        this.K = K ;
        this.N = N ;
        
        nTest = 150/N ;
        
        acc = new double[N] ;
        
        sortedDis = new double[150-nTest] ;
        
        init() ;
        
        for(int i=0 ; i< N ; i++){
            startIndex = startIndex - nTest ;
            for(int j=startIndex ; j< endIndex ; j++){
                distanceAll(data[j].a , data[j].b , data[j].c , data[j].d) ;
                //byDistance();
                byWeight();
            }
            accuracy();
            ans.clear();
            endIndex = endIndex - nTest ;
        }
        
        double av=0 ;
        for(int i=0 ; i<N ; i++){
            av = av + acc[i] ;
        }
        
        av = av/N ;
        
        System.out.println("Avarage Accuracy: " + av + "%");
        
        
    }
    
    public void init() throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(file)) ;
       
        String str;

        for(int i=0 ; i<150 ; i++){
            str = reader.readLine() ;
            String[] s = str.split(",");

            double a1 = Double.parseDouble(s[0]);
            double b1 = Double.parseDouble(s[1]);
            double c1 = Double.parseDouble(s[2]);
            double d1 = Double.parseDouble(s[3]);

            String s1=s[4];

            data[i]=new Data(a1,b1,c1,d1,s1);
        }
        
        Random rand = new Random();

        for (int i = 0; i < 150 ; i++) {
                int randomIndexToSwap = rand.nextInt(150);
                Data temp = data[randomIndexToSwap];
                data[randomIndexToSwap] = data[i];
                data[i] = temp;
        }   
    }
    
    public double getDistance(double a, double b, double c, double d, double a1, double b1, double c1, double d1){
        double distance=Math.pow((a-a1),2)+Math.pow((b-b1),2)+Math.pow((c-c1),2) + Math.pow((d-d1), 2);
        distance = Math.sqrt(distance);
        
        return distance;
    }
    
     public void distanceAll(double a, double b, double c, double d){
        for(int i=0 ; i < 150 ; i++){
            distance[i] = 0;
        }
        
        for(int i=0 ; i < 150-nTest ; i++){
            sortedDis[i] = -1 ;
        }
        
        for(int i=0 ; i < 150 ; i++){
            if(i>=startIndex && i<endIndex) continue ;
            
            distance[i] = getDistance(a, b, c, d, data[i].a , data[i].b , data[i].c , data[i].d) ;
        }
        
        int j = 0 ;
        for(int i=0 ; i < 150 ; i++){
            if(i>=startIndex && i<endIndex) continue ;
            
            sortedDis[j] = distance[i] ;
           
            j++ ;
        }
        Arrays.sort(sortedDis);
        
    }
     
    
    public void byWeight(){
        double[] weight = new double[K];
        double Iris_setosa=0,Iris_versicolor=0,Iris_virginica=0;
               
        for(int i=0 ; i < K ; i++){
            weight[i] = (sortedDis[150-nTest-1]-sortedDis[i])/(sortedDis[150-nTest-1]-sortedDis[0]) ;
        }
        
        for(int i=0 ; i<K ; i++){
            for(int j=0 ; j<150 ; j++){
                if(j>=startIndex && j<endIndex) continue ;
                if(sortedDis[i]==distance[j]){
                    if(data[j].s.equals("Iris-setosa")){
                        Iris_setosa = Iris_setosa + weight[i] ;
                        
                    }

                    else if(data[j].s.equals("Iris-versicolor")){
                        Iris_versicolor = Iris_versicolor + weight[i] ;
                    }

                    else if(data[j].s.equals("Iris-virginica")){
                        Iris_virginica = Iris_virginica + weight[i] ;
                    }
                   
                }
                
                //System.out.println("matha:" + sortedDis[i] + "   " + distance[j]);

            }
        }
       
        if( Iris_setosa >= Iris_versicolor && Iris_setosa >= Iris_virginica){
            ans.add("Iris-setosa") ;
        }
        
        else if (Iris_versicolor >= Iris_setosa && Iris_versicolor >= Iris_virginica){
            ans.add("Iris-versicolor") ;
        }
        else if(Iris_virginica >= Iris_versicolor && Iris_virginica >=  Iris_setosa){
            ans.add("Iris-virginica") ;
        }
        
        countAns++ ;
       
    }
    
    public void accuracy(){
        int yes=0, no=0;
    
        for(int i=0 ; i < ans.size() ; i++){
            
            if(ans.get(i).equals(data[startIndex+i].s)){
                yes++ ;
                //System.out.print("Matching: " + ans.get(i) );
                //System.out.println("   " + data[startIndex+i].s);
            }
            else{
                no++ ;
                  //System.out.print("Not Matching----------: " + ans.get(i) );
                  //System.out.println("   " + data[startIndex+i].s);
            }
        }
        
        double d = yes*1.0/(yes+no)*100 ;
        
        System.out.println("Accuracy: " + d + "%");
        
        acc[count] = d ;
        count++ ;
    }   
}



/*
     public void byDistance(){
        int Iris_setosa=0,Iris_versicolor=0,Iris_virginica=0;
        
        for(int i=0 ; i<K ; i++){
            for(int j=0 ; j<su ; j++){
                if(sortedDis[i]==distance[j]){

                    if(data[j].s.equals("Iris-setosa")){
                        Iris_setosa++ ;
                    }

                    else if(data[j].s.equals("Iris-versicolor")){
                        Iris_versicolor++ ;
                    }

                    else if(data[j].s.equals("Iris-virginica")){
                        Iris_virginica++ ;
                    }
                }
            }
        }
        
        if( Iris_setosa >= Iris_versicolor && Iris_setosa >= Iris_virginica){
            ans.add("Iris-setosa") ;
        }
        
        else if (Iris_versicolor >= Iris_setosa && Iris_versicolor >= Iris_virginica){
            ans.add("Iris-versicolor") ;
        }
        else if(Iris_virginica >= Iris_versicolor && Iris_virginica >=  Iris_setosa){
            ans.add("Iris-virginica") ;
        }
        
        else{
            System.out.println(Iris_setosa + "  " + Iris_versicolor + "  " + Iris_virginica);
        }
    }/*/