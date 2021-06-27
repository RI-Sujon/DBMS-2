package decision_tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class DecisionTree {
    File file = new File("example2.txt") ;
    private String arrMain[][] ;
    private int totalRow = 0, numberOfColumn = 0, numberOfTrainingData = 0;
    private String targetAttribute ;
    private ArrayList<String> targetAttrValue = new ArrayList<>() ;
    private String shortTable [][][] ;
    private int countShortTable=0 ;
    int TP = 0, TN = 0, FP = 0, FN = 0 ;

    public DecisionTree(){
        readDataSet();
        Node root = createNode(arrMain, numberOfTrainingData, numberOfColumn) ;
        createTree(root, arrMain, numberOfTrainingData, numberOfColumn);
        System.out.println("===================================");
        print(root);
        System.out.println("===================================");
        accuracy(root);
    }

    public void readDataSet(){
        try{
            Scanner scanner = new Scanner(file) ;

            totalRow = Integer.valueOf(scanner.next()) ;
            numberOfColumn = Integer.valueOf(scanner.next());

            numberOfTrainingData = totalRow*4/5 ;

            targetAttribute = scanner.next() ;

            arrMain = new String[totalRow][numberOfColumn] ;

            for(int i=0; i<totalRow ; i++){
                for(int j=0; j<numberOfColumn ; j++){
                    arrMain[i][j] = scanner.next() ;
                }
            }

            for(int j=0 ; j<numberOfColumn ; j++){
                if(arrMain[0][j].equals(targetAttribute)){
                    for(int i=1; i<totalRow ; i++){
                        int flag = -1 ;
                        for(String value: targetAttrValue){
                            if(value.equals(arrMain[i][j])){
                                flag = 1 ;
                                break;
                            }
                        }

                        if(flag==-1){
                            targetAttrValue.add(arrMain[i][j]) ;
                        }
                    }
                }
            }
        }catch (Exception e){

        }
    }

    public double calculateEntropyS(String arrayTable[][], int row, int column){
        int p=0, n=0 ;
        for(int i=1; i<row ; i++){
            if(arrayTable[i][column-1].equals(targetAttrValue.get(0))){
                p++ ;
            }
            else n++ ;
        }

        System.out.println(p + "\t" + n + "\t" + calculateEntropy(p, n)) ;
        return calculateEntropy(p, n) ;
    }

    public double calculateEntropyI(String arrayTable[][], int index, int row, int column){
        countShortTable = index ;
        double I = 0 ;
        int k = -1 , kTotal = 0 ;

        for (int i=0 ; i<row-1 ; i++){
            for (int j=1 ; j<3 ; j++){
                shortTable[countShortTable][i][j] = "0" ;
            }
        }

        for(int i=1; i<row ; i++){
            k = -1 ;
            for(int l=0 ; l<kTotal ; l++){
                if(arrayTable[i][index].equals(shortTable[countShortTable][l][0])){
                    k = l ;
                    break;
                }
            }
            if(k==-1){
                k = kTotal ;
                kTotal++ ;
                shortTable[countShortTable][k][0] = arrayTable[i][index] ;
            }
            if(arrayTable[i][column-1].equals(targetAttrValue.get(0))){
                shortTable[countShortTable][k][1] = String.valueOf(Integer.valueOf(shortTable[countShortTable][k][1]) + 1) ;
            }
            else if(arrayTable[i][column-1].equals(targetAttrValue.get(1))){
                shortTable[countShortTable][k][2] = String.valueOf(Integer.valueOf(shortTable[countShortTable][k][2]) + 1) ;
            }
        }

        for (int i =0 ; i<kTotal ; i++){
            for (int j=0 ; j<3 ; j++){
                System.out.print(shortTable[countShortTable][i][j] +  "\t");
            }
            I = I + calculateEntropy(Integer.valueOf(shortTable[countShortTable][i][1]), Integer.valueOf(shortTable[countShortTable][i][2]))*(Integer.valueOf(shortTable[countShortTable][i][1])+Integer.valueOf(shortTable[countShortTable][i][2]))*1.0/(row-1);
            System.out.println();
        }

        System.out.println("I:" + I);
        return I ;
    }

    public Node createNode(String arrayTable[][], int row, int column){
        shortTable = new String[numberOfColumn-1][numberOfTrainingData-1][3] ;
        double gain = 0.0 , maxGain = 0.0 ;
        int maxGainIndex = 0 ;
        for(int i=0; i<column-1 ; i++){
            gain = calculateEntropyS(arrayTable,row, column) - calculateEntropyI(arrayTable, i, row, column) ;
            System.out.println("Gain:" + gain);
            if(gain>maxGain){
                maxGain = gain ;
                maxGainIndex = i ;
            }
        }

        System.out.println(maxGainIndex + "\t\t" + arrayTable[0][maxGainIndex]);

        Node node = new Node() ;
        ArrayList<Edge> edges = new ArrayList<>() ;
        for(int i=0 ; i<row-1 ; i++){
            if(shortTable[maxGainIndex][i][0]==null) break;

            Edge edge = new Edge() ;
            edge.edgeName = shortTable[maxGainIndex][i][0] ;
            edge.nextNode = null ;
            edges.add(edge) ;
        }

        node.attrName = arrayTable[0][maxGainIndex] ;
        node.edges = edges ;
        return node ;
    }

    public String[][] createNewTable(String arrayTable[][],int row, int column, String attrName, String valueName){
        String newArray[][] = new String[row][column-1] ;
        int indexAttr = 0 ;
        for(int i=0 ; i<column-1 ; i++){
            if(arrayTable[0][i].equals(attrName)){
                indexAttr = i ;
                break;
            }
        }

        int k=1 ;
        for(int j=0, jn=0 ; j<column ; j++){
            if(arrayTable[0][j].equals(attrName)) continue;
            newArray[0][jn] = arrayTable[0][j] ;
            jn++ ;
        }

        for(int i=1 ; i<row ; i++){
            if(arrayTable[i][indexAttr].equals(valueName)){
                for(int j=0, jn = 0 ; j<column ; j++){
                    if(j==indexAttr) continue;
                    newArray[k][jn] = arrayTable[i][j] ;
                    jn++ ;
                }
                k++ ;
            }
        }

        String newArray2[][] = new String[k][column-1] ;
        for(int i=0; i<k ; i++){
            for(int j=0 ; j<column-1 ; j++){
                newArray2[i][j] = newArray[i][j] ;
                System.out.print(newArray2[i][j] + "\t");
            }
            System.out.println();
        }

        return newArray2 ;
    }
  
    public void createTree(Node node, String arrTable[][], int row, int column){
        String arrTable2[][] ;
        for(int i=0 ; i<node.edges.size() ;i++){
            arrTable2 = createNewTable(arrTable,row,column,node.attrName,node.edges.get(i).edgeName) ;

            int flagP = 0, flagN = 0 ;
            for(int k=1; k<arrTable2.length ; k++){
                if(arrTable2[k][column-2].equals(targetAttrValue.get(0))){
                    flagP = 1 ;
                }
                else {
                    flagN = 1 ;
                }
            }

            if(flagP==1 && flagN==0) {
                Node node1 = new Node() ;
                node1.attrName = targetAttrValue.get(0) ;
                node.edges.get(i).nextNode = node1 ;
                continue;
            }else if(flagP==0 && flagN==1){
                Node node1 = new Node() ;
                node1.attrName = targetAttrValue.get(1) ;
                node.edges.get(i).nextNode = node1 ;
                continue;
            }

            node.edges.get(i).nextNode = createNode(arrTable2, arrTable2.length, column-1) ;

            if(column>2) {
                createTree(node.edges.get(i).nextNode, arrTable2, arrTable2.length, column - 1);
            }
            else return;
        }
    }

    public double log2(double value){
        return Math.log(value)/Math.log(2) ;
    }

    public double calculateEntropy(int p, int n){
        if(p==0 || n==0){
            return 0 ;
        }
        else {
            return -p*1.0/(p+n)*log2(p*1.0/(p+n)) - n*1.0/(p+n)*log2(n*1.0/(p+n)) ;
        }
    }

    public void print(Node node){
        System.out.println(node.attrName);
        if(node.edges!=null){
            for(int i=0 ; i<node.edges.size() ; i++){
                print(node.edges.get(i).nextNode);
            }
        }
    }

    public void checkingValue(Node parent, int rowIndex){
        int index=0 ;

        if(parent.edges==null){
            if(parent.attrName.equals(arrMain[rowIndex][numberOfColumn-1])){
                if(parent.attrName.equals(targetAttrValue.get(1))){
                    TP++ ;
                }
                else if(parent.attrName.equals(targetAttrValue.get(0))){
                    TN++ ;
                }
            }
            else {
                if(parent.attrName.equals(targetAttrValue.get(1))){
                    FP++ ;
                }
                else {
                    FN++ ;
                }
            }
            return;
        }

        for(int j=0; j<numberOfColumn-1 ; j++) {
            if(parent.attrName.equals(arrMain[0][j])){
                index = j ;
            }
        }

        for(int i=0; i<parent.edges.size() ; i++){
            if(parent.edges.get(i).edgeName.equals(arrMain[rowIndex][index])){
                checkingValue(parent.edges.get(i).nextNode, rowIndex);
            }
        }
    }
    public void accuracy(Node root){
        for(int i=numberOfTrainingData ; i<totalRow ; i++){
            checkingValue(root, i);
        }

        System.out.println("TP:" + TP + "\tTN:" + TN + "\tFP:" + FP + "\tFN:" + FN);
        System.out.println("Accuracy:" + (TP+TN)*1.0/(TP+TN+FP+FN));
    }

    class Node{
        String attrName ;
        ArrayList<Edge> edges ;
    }

    class Edge{
        String edgeName ;
        Node nextNode ;
    }
}
