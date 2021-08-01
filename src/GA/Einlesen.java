package GA;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Einlesen {

		
		public static void auslesen(double[][] daten) {
			for(int i=0;i<daten.length;i++){
	        	System.out.print(i + " ");
	        	for(int j=0;j<daten[i].length;j++){
	        		System.out.print(daten[i][j] + " ");
	        	}
	        	System.out.println();
	        }
		}
		
	
		
		public static double[][] einlesenBankdaten(File file) {
			double[][] koord = null;
			int          dim = 0;
	        try{
	        	//Anzahl Zeilen ermitteln
	            Scanner scanner  = new Scanner(file);
	            String x;
		        while(scanner.hasNext()) {
	                x = scanner.next();
	                dim++;
	            }
		        dim = dim-1;//erste Zeile ist hier dummy
	            System.out.println("Anzahl Samples: " + dim);
	            scanner.close();
	            
	            //Zeilen einlesen
	            scanner    = new Scanner(file);
		        koord      = new double[dim][10];
		        int index  = 0;
		        int nr     = 0;
		        while(scanner.hasNext()) {
	                x = scanner.next();
	                StringTokenizer tokenizer = new StringTokenizer(x,",", false);
	                ArrayList<String> tokens  = new ArrayList<String>();
	                while (tokenizer.hasMoreTokens()){
	                    tokens.add(tokenizer.nextToken());
	                }
	                if(nr != 0){
		                for(int i=0;i<tokens.size();i++){
		                	String s = tokens.get(i);
		                	koord[index][i] = Double.valueOf(s);
		                }
		                index++;
	                }
	                nr++;
	                if(index >= dim)break;
	            }    
		        scanner.close();		        
	        }
	        catch(FileNotFoundException v){
	        	
	        }
	        return koord;
	    }	
		
		public static double[][] einlesenDiabetes(File file, boolean train) {
			double[][] koord = null;
			int          dim = 0;
			int     dimTrain = 0;
			int     dimEvalu = 0;
		    double prozent   = 0.5;    
		    int anzahlMerk   = 9;//inclusive der Klasse!!

	        try{
	        	//Anzahl Zeilen ermitteln
	            Scanner scanner  = new Scanner(file);
	            String x;
		        while(scanner.hasNext()) {
	                x = scanner.next();
	                dim++;
	            }
		        
		        dim = dim-1;//erste Zeile ist hier dummy
		        
		        dimTrain = (int)(dim*prozent);
		        dimEvalu = dim-dimTrain;
		        
	          //if(train)System.out.println("Anzahl Samples Original: " + dim + " Anzahl Samples Auswahl: " + dimTrain);
	          //else     System.out.println("Anzahl Samples Original: " + dim + " Anzahl Samples Auswahl: " + dimEvalu);
	            scanner.close();
	            
	            //Zeilen einlesen
	            scanner        = new Scanner(file);
	            if(train)koord = new double[dimTrain][anzahlMerk]; 
	            else     koord = new double[dimEvalu][anzahlMerk];
		        int index      = 0;//Nummer des Musters in der Datei
		        int indexA     = 0;//Nummer des Musters in der Auswahl
		        int nr         = 0;//Attribute bzw Merkmale
		        
		        while(scanner.hasNext()) {
	                x = scanner.next();
	                StringTokenizer tokenizer = new StringTokenizer(x,",", false);
	                ArrayList<String> tokens  = new ArrayList<String>();
	                while (tokenizer.hasMoreTokens()){
	                    tokens.add(tokenizer.nextToken());
	                }
	                
	                if( (!train && index < dimTrain)||(train && index >= dimTrain) ){
//	                	System.out.println(train + " index 0");
	                	index++;
	                	continue;
	                }
	                
	                if(nr != 0){
		                for(int i=0;i<tokens.size();i++){
		                	String s = tokens.get(i);
		                	if(i==0){
		                		koord[indexA][i] = Double.valueOf(s)/17.;
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else if(i==1){
		                		koord[indexA][i] = Double.valueOf(s)/199.;
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else if(i==2){
		                		koord[indexA][i] = Double.valueOf(s)/122.;
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else if(i==3){
		                		koord[indexA][i] = Double.valueOf(s)/99.;
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else if(i==4){
		                		koord[indexA][i] = Double.valueOf(s)/846.;
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else if(i==5){
		                		koord[indexA][i] = Double.valueOf(s)/67.1;
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else if(i==6){
		                		koord[indexA][i] = Double.valueOf(s)/2.42;
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else if(i==7){
		                		koord[indexA][i] = Double.valueOf(s)/81.;
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else if(i==8){
		                		koord[indexA][i] = Double.valueOf(s);
		                		if(koord[indexA][i]> 1.0)System.out.println("Fehler in Dateiwert " + index + " " + i);
		                	}
		                	else{
		                		System.out.println("Fehler in Dateiauf");
		                	}	
		                }
		                index++;
		                indexA++;
	                }
	                nr++;
	                //if(index >= dim)break;
	            }    
		        scanner.close();		        
	        }
	        catch(FileNotFoundException v){
	        	
	        }
	        return koord;
	    }	
		
		
		public static double[][] einlesenVorlesungsbeispiele(File file) {
			//Es wird angenommen, dass alle Eingabedaten im Intervall [0, 100] liegen
			double[][] koord = null;
			int          dim = 0;	        
			
			try{
	    		Scanner scanner      = new Scanner(file);            
	            while(scanner.hasNext()) {
	            	double x1 = Double.valueOf (scanner.next());
	                double x2 = Double.valueOf (scanner.next());
	            	int y     = Integer.valueOf(scanner.next());
	            	//hier koennte man die minimalen und maximalen Eingabewerte ermitteln
	            	//um sie beim Einlesen auf den Bereich [0, 1] zu skalieren
	            	dim++;
	            } 
	            scanner.close();
	            koord   = new double[dim][3];
	            scanner = new Scanner(file);
	            int nr  = 0;
	            while(scanner.hasNext()) {
	            	double x1 = Double.valueOf (scanner.next());
	                double x2 = Double.valueOf (scanner.next());
	            	double y  = Double.valueOf(scanner.next());
	            	koord[nr][0] = x1/100.;
	            	koord[nr][1] = x2/100.;
	            	koord[nr][2] = y;
	            	nr++;
	            } 
	            scanner.close();	            
	        }
	        catch(FileNotFoundException e){
				System.out.println(e.getMessage());
	        }
	        return koord;
		}
	
		
		
	}
