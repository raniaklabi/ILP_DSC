
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;
public class Formulation {
	 static final int NC=500;
	 static final int NT=200;
	int N; // Nombre N de capteurs
	int M; // Nombre M de zones a surveiller 
	int nbreDeTypeDeSur;
	int area=500;
	int rangeenergie=10;
	int prix=5;
	int range=200;
	int ok1=0, ok2=0,s=0;
	float R[] =new float[NC];
	float XT[] =new float[NT];
	float YT[] =new float[NT];
	float XS[] =new float[NC];
	float YS[] =new float[NC];
	float RS[] =new float[NC];
	float E[] =new float[NC];
	float P[] =new float[NC];
	float T[] = new float[NC]; // duree de vie de chaque capteur separee par un espace
	int Z[][] = new int[NC][NT]; // numero de zones couvertes par le capteurs
	int delta[][] = new int[NC][NT];
	double Distance[][] = new double[NC][NT];
	int K1;//K-Coverage:par exemple: chaque cible est au moins couvert par 3 capteurs
	int K2=1;// chaque capteurs couvre au moins k2 cibles
	int k, nbreCS,CT;
	float min;
	public void Read_Data_v1(String fichier)
	{
	    // Role:
	    //cette fonction permet de lire toutes  les donnees necessaires pour la simulation a partir d'un fichier S
	   String []survTarget=new String[NT];
	   int i,j,k,L;
	   
	   String line; /* or other suitable maximum line size */
	   String S;
	   
	   try {
		  

		    InputStream ips=new FileInputStream(fichier); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			ligne = br.readLine();
			ligne = br.readLine();
	        N = Integer.parseInt(ligne);
	  	   	ligne = br.readLine();
	  	   	ligne = br.readLine();
      
	  	   	M = Integer.parseInt(ligne);
	  	   	ligne = br.readLine();
	  	   	ligne = br.readLine();
  
	  	   	nbreDeTypeDeSur= Integer.parseInt(ligne);
	
	  	   	ligne = br.readLine();
	  	   	ligne = br.readLine();
	  	   	String[] parts;
	  	  	for( L = 0; L < N; L++)
	  	   	{
	  	   		ligne = br.readLine();
	  	   		parts= ligne.split(" ");
	  	   		R[L]= Float.parseFloat(parts[0]);
	  	   		
	  	   		T[L]= Float.parseFloat(parts[1]);
				 for(i = 4; i< parts.length;i++)
				 { Distance[L][i-4] = Float.parseFloat(parts[i]);
				 System.out.print(Distance[L][i-4]+ "|");
				 }
				 System.out.println("");
			
	      }
	  	  System.out.print("rayon");
	  	  for( L = 0; L < N; L++)
	  	  {System.out.print(R[L] + "| ");
	  	  }
			//System.out.print(T[i] + " ");
		      ligne = br.readLine();
		  	  ligne = br.readLine();
		  	 parts = ligne.split(" ");
		  	for( i =0; i < M; i++)
			{
		  		 survTarget[i] = parts[i];
				System.out.print(survTarget[i] + " ");
			
			} 
	      br.close();
	   }
	   catch (Exception e){
			System.out.println(e.toString());
		}	

	}
	public void Construct_Z()
	{
		 for(int i=0;i<N;i++)
		    {
		        for(int j=0;j<M;j++)
		        {

		            if(delta[i][j]!=0)
		            {
		            	Z[i][j]=j+1;
		            }
		        }

		    }
	}
	public void Read_Data(String fichier)
	{
		//lecture du fichier texte	
		try{
			InputStream ips=new FileInputStream(fichier); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			ligne = br.readLine();
			N = Integer.parseInt(ligne);
			//System.out.println("N = " + N);
					
			ligne = br.readLine();
			M = Integer.parseInt(ligne);
		   // System.out.println("M = " + M);
					
		    ligne = br.readLine();
			String[] parts = ligne.split(" ");
			for(int i =0; i < N; i++)
			{
				T[i] =  Float.parseFloat(parts[i]);
				//System.out.print(T[i] + " ");
			}
					
			//System.out.println("");
					
			for(int i = 0; i< N; i++)
			{
				ligne =br.readLine();
				parts = ligne.split(" ");
				for(int j = 0; j < M; j++)
				{
					if (j < parts.length)
					{
						Z[i][j] =  Integer.parseInt(parts[j]);
						//System.out.print(Z[i][j] + " ");
					}
					else
					{
						Z[i][j] = 0;
						//System.out.print(Z[i][j] + " ");
					}
							
				}
				//System.out.println("");
			}
			
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}	
		
	}
	
	public void Print_Data()
	{
		System.out.println("Nombre de capteurs N = " + N);
		System.out.println("Nombre de cibles M = " + M);
		System.out.print("Durée de vie de chaque capteur: ");
		for(int i =0; i < N; i++)
		{
			System.out.print("E"+(i+1)+"="+T[i] + " ");
	    }
					
		System.out.println("");
		System.out.println("Les cibles couverts par chaque capteur : ");
		Calculer_delta();
		Construct_Z();
		for(int i = 0; i< N; i++)
		{System.out.print("S"+(i+1)+":");
			for(int j = 0; j<M; j++)
			{if(Z[i][j]!=0) {
				System.out.print("t"+Z[i][j] +" ");
			}
			}
			System.out.println("");
		}	
	}
	public void Calculer_delta()
	{
	    //Role:
	    //A partir de les diffrentes distances d'un capteur particulier on peut construire une ligne de delta.
	    //Retourne la matrice "delta" .
	    //Les lignes de la matrice presente les capteurs.
	    //les colonnes de la matrice presente les targets.
	    //losqu'on met par exemple 1 dans la case(1,1)  c'est_a_dire le capteur un couvre la target 1 .

	    int i,k,j,test;
	    System.out.println("Distance: ");
	    for(i=0;i<N;i++)
	    {
	        for(j=0;j<M;j++)
	        	System.out.print(Distance[i][j] +" |");
			System.out.println("");
	    }
	    System.out.println("Rayon: ");
	    for(i=0;i<N;i++)
	    {
	 
	        	System.out.print(R[i] +"| ");
			
	    }

	    for(i=0;i<N;i++)
	    {
	        for(j=0;j<M;j++)
	        {

	            delta[i][j]=0;
	        }

	    }

	    for(i=0;i<N;i++)
	    {
	        for(j=0;j<M;j++)
	        {
	            if(Distance[i][j]<=R[i])
	            {
	                delta[i][j]=1;
	            }
	            else
	            {
	                delta[i][j]=0;
	            }

	        }

	    }
	System.out.println("Delta: ");
	    for(i=0;i<N;i++)
	    {
	        for(j=0;j<M;j++)
	        	System.out.print(delta[i][j] +" ");
			System.out.println("");
	    }
	}
	
	public void CriticalTarget(int delta[][])
	{float s;
	min=0;
	 for(int h=0;h<N;h++)
		 min=min+delta[h][0]*T[h];
	 	 CT=1;
	    for(int i=1;i<M;i++)
	    {
	    	s=0;
	        for(int j=0;j<N;j++)
	        	s=s+delta[j][i]*T[j];
	        if (s<min) 
	        {
	        	min=s;
	        	CT=i+1;		
	        }
	        	
	    }
	    for(int h=0;h<N;h++) 
	    if(delta[h][CT-1]!=0)
	    {
		nbreCS=nbreCS+1;
	    }
	    System.out.println("upper bound of wsn lifeitme :"+min);
	    System.out.println("critical target t"+CT);
		System.out.println("nbre de critical sensors:"+nbreCS);
	}
	
}
