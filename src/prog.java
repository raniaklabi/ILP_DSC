import java.util.ArrayList;

import ilog.concert.*;
import ilog.cplex.*;

public class prog {


	public static void main(String[] args) {
		int max=10000000;
		float min=-1;
		int nbreContrainte1=0;
		int nbreContrainte2=0;
		int nbreContrainte3=0;
		double avrerageDuree=0;
		double averageTime=0;
		Formulation  P1 = new Formulation(); // Creer un instance pour manipuler les donnees
		//P1.write_Data("ecrit.txt");
		//System.out.println("*************"+args[0]);
	//	for(int r=0;r<2;r++)
	//	{
		P1.Read_Data_v1("Exemple50_30_1.txt");
		System.out.println("******************************************");
		//P1.Read_Data("ecrit.txt");
		System.out.println("zone: "+P1.area);
		System.out.println("la lecture du fichier: ");
		P1.Print_Data();
		P1.Calculer_delta();
		P1.CriticalTarget(P1.delta);
		//System.out.println("******nombre de critical sensors*****"+P1.nbreCS );
		//System.out.println("******nombre de capteurs*****"+P1.N);
		//System.out.println("******upper bound lifetime*****"+P1.min);
		//System.out.println("******critical target t"+P1.CT);
		for (int i = 0; i <P1.N; i++)
		{
			if (P1.T[i]>min) {min=P1.T[i];}
			
		}
		System.out.println("******max d'energie*****"+min);
		try
		{
			IloCplex cplex1=new IloCplex();
			IloNumVar t [] = new IloNumVar[P1.nbreCS];
			IloNumVar X [][] = new IloNumVar[P1.N][];
			//variable t1....tk
			for ( int i = 0; i < P1.nbreCS; i++)
			{
				t[i] = cplex1.numVar(0, Double.MAX_VALUE);
			}
			 for(int i=0;i<P1.N;i++)
			    {
			        	X[i]=cplex1.boolVarArray(P1.N);
			    }

			//expression   t1+....+tk
			IloLinearNumExpr objective1=cplex1.linearNumExpr();
			for(int i = 0; i < P1.nbreCS; i++)
	    		objective1.addTerm(1, t[i]);
			//define objective 
			cplex1.addMaximize(objective1);
			//define constrains  60x+60y>=300
			for(int i = 0; i <P1.nbreCS ; i++)
			{
				
				for(int j = 0; j <P1.M; j++)
				{IloLinearNumExpr Assignement =cplex1.linearNumExpr();
					for(int h=0;h<P1.N;h++)
					if(P1.delta[h][j]!=0)
					{
						
						Assignement.addTerm(P1.T[h],X[h][i]);
					}
					cplex1.addGe(Assignement, t[i]);
					Assignement.clear();
					nbreContrainte1=nbreContrainte1+1;
				}
				
				
				
			}
			
			System.out.println("nbre de contrainte =" + nbreContrainte1);
			for(int i = 0; i <P1.N ; i++)
			{
				IloLinearNumExpr Assignement1 =cplex1.linearNumExpr();
				for(int j = 0; j <P1.nbreCS; j++)
				{
					
				
						Assignement1.addTerm(1,X[i][j]);
					
				}
				
				cplex1.addEq(Assignement1, 1);
				Assignement1.clear();
				//break;
				nbreContrainte2=nbreContrainte2+1;
			}
			System.out.println("nbre de contrainte =" + nbreContrainte2);
		
			for(int i = 0; i <P1.nbreCS ; i++)
			{
			
				for(int j = 0; j <P1.N; j++)
				{
					IloLinearNumExpr Assignement2 =cplex1.linearNumExpr();
				
					Assignement2.setConstant(max);
					
					Assignement2.addTerm(-max,X[j][i] );
					
					Assignement2.addTerm(P1.T[j],X[j][i]);
					
					cplex1.addGe(Assignement2, t[i]);
					Assignement2.clear();
					nbreContrainte3=nbreContrainte3+1;
				}
		
				//break;
			}
			System.out.println("nbre de contrainte =" + nbreContrainte3);
			/************************************************************/
			
			/*************************************************************/
			//solve
			if(cplex1.solve())
			{avrerageDuree=avrerageDuree+cplex1.getObjValue();
			averageTime=averageTime+cplex1.getCplexTime();
				System.out.println("obj1 =" + cplex1.getObjValue());
		
				System.out.println("Time =" + cplex1.getCplexTime() );
				for (int i = 0; i <P1.nbreCS; i++)
				{System.out.print("c"+(i+1) +"[" );
					
					for(int j=0;j<P1.N;j++)
					{
						if(cplex1.getValue(X[j][i])!=0) {System.out.print(j+1+" ");}
					}
					System.out.println("]:"+cplex1.getValue(t[i]) );	
				}
			
			}
			else {
				System.out.println("Model not solved");
			}
			
		}
		catch(IloException exc)
		{
			
			exc.fillInStackTrace();
		}
		
		
	//}
		//System.out.println("The average lifetime ="+avrerageDuree/2);
		//System.out.println("The average execution ="+averageTime/2);
	}
}
