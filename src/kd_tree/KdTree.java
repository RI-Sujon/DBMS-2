package kd_tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class KdTree {
    File file = new File("exampleA.txt") ;
    public int dimension ;
    public int numberOfData ;
    public String dataArray[][] ;
    int searchingFound = -1 ;
    String searchingValue = "(8,11)" ;

    public KdTree(){
        readDataSet();
        /*for(int i=0; i<numberOfData ; i++){
            for(int j=0; j<dimension ; j++){
                System.out.print(dataArray[i][j]+"\t") ;
            }
            System.out.println();
        }*/

        ArrayList<String> values = new ArrayList<>() ;
        for (int j=0 ; j<dimension ; j++) {
            values.add(dataArray[0][j]) ;
        }

        Node root = createNode(values);

        for(int i=1 ; i<numberOfData ; i++){
            values = new ArrayList<>() ;
            for(int j=0 ; j<dimension ; j++){
                values.add(dataArray[i][j]) ;
            }
            Node node = createNode(values) ;
            insertNode(root, node, 0);
        }

        printTree(root, 0 , "root");

        searchingValue = searchingValue.substring(1,searchingValue.length()-1) ;

        String str [] = searchingValue.split(",") ;
        search(root,0,"root", str, 0);
    }

    public void readDataSet(){
        try{
            Scanner scanner = new Scanner(file) ;

            dimension = Integer.valueOf(scanner.next()) ;
            numberOfData = Integer.valueOf(scanner.next());

            dataArray = new String[numberOfData][dimension] ;

            String temp ;
            String tempArray[] ;

            System.out.println("Dimension:" + dimension + "\t\tNumberOfData:" + numberOfData);

            for(int i=0; i<numberOfData ; i++){
                temp = scanner.next() ;
                temp = temp.substring(1,temp.length()-1) ;
                tempArray = temp.split(",") ;
                for(int j=0; j<dimension ; j++){
                    dataArray[i][j] = tempArray[j] ;
                }
            }
        }catch (Exception e){
            System.out.println("Exception:" + e);
        }
    }

    public Node createNode(ArrayList<String> values){
        Node node = new Node() ;
        node.values = values ;

        return node ;
    }

    public void insertNode(Node parent, Node newNode, int currentDim){
        if(Integer.valueOf(newNode.values.get(currentDim))>=Integer.valueOf(parent.values.get(currentDim))){
            if(parent.rightChild==null){
                parent.rightChild = newNode ;
            }
            else {
                if(currentDim+1==dimension){
                    currentDim = -1 ;
                }
                insertNode(parent.rightChild, newNode, currentDim+1);
            }
        }
        else if(Integer.valueOf(newNode.values.get(currentDim)) < Integer.valueOf(parent.values.get(currentDim))){
            if(parent.leftChild==null){
                parent.leftChild = newNode ;
            }
            else {
                if(currentDim+1==dimension){
                    currentDim = -1 ;
                }
                insertNode(parent.leftChild, newNode, currentDim+1);
            }
        }
    }

    public void printTree(Node node, int depth, String side){
        if(node==null){
            return;
        }

        String str = null ;
        for (int i=0 ; i<dimension ; i++){
            if(i==0){
                str = node.values.get(i);
            }else {
                str = str + "," + node.values.get(i);
            }
        }

        System.out.println("depth:" + depth + "||" + side + " node:(" + str + ")\t");

        if(node.leftChild!=null){
            printTree(node.leftChild, depth+1, "left");
        }

        if(node.rightChild!=null){
            printTree(node.rightChild, depth+1, "right");
        }
    }

    public boolean matchingKey(Node node, String str[]){
        int foundFlag = 0 ;
        for (int i=0 ; i<dimension ; i++){
            if(str[i].equals(String.valueOf(node.values.get(i)))){
                foundFlag++ ;
            }
        }

        if(foundFlag==dimension){
            return true;
        }
        return false;
    }

    public void search(Node node, int depth, String side, String searchingKey[], int currentDim){
        if(matchingKey(node, searchingKey)){
            System.out.println("\n\nSearching result:\ndepth:" + depth + "||" + side + " node:(" + searchingValue + ")\t");
            return;
        }

        if(Integer.valueOf(searchingKey[currentDim])>=Integer.valueOf(node.values.get(currentDim))){

            if(node.rightChild==null){
                System.out.println("\n\nSearching result:\nNot Found");
            }
            else {
                if(currentDim+1==dimension){
                    currentDim = -1 ;
                }
                search(node.rightChild, depth+1, "right", searchingKey, currentDim+1);
            }
        }
        else if(Integer.valueOf(searchingKey[currentDim]) < Integer.valueOf(node.values.get(currentDim))){
            if(node.leftChild==null){
                System.out.println("\n\nSearching result:\nNot Found");
            }
            else {
                if(currentDim+1==dimension){
                    currentDim = -1 ;
                }
                search(node.leftChild, depth+1, "left", searchingKey, currentDim+1);
            }
        }
    }

    class Node{
        Node leftChild, rightChild ;
        ArrayList<String> values = new ArrayList<>() ;
    }
}
