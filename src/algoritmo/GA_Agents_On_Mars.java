package algoritmo;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.variable.ArrayInt;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import jmetal.util.wrapper.XInt;

public class GA_Agents_On_Mars {

	public static void main(String[] args) throws JMException, ClassNotFoundException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		Problem problem;
		Algorithm ga;
		Crossover crossover;
		Selection selection;
		Mutation mutation;
		
		//Operador de Parametros
		HashMap parametros;
		
		//Criando a instancia do problema
		int weigth_of_vertices = 30;
		problem = new PadraoAmbiental(weigth_of_vertices);
		
		//instancia o algoritmo
		ga = new GA(problem);
		//Passagem de parametros ao algoritmo
		ga.setInputParameter("tamPop", 2);
		ga.setInputParameter("maxEvaluations", 2);
		
		// Mutation and Crossover for Binary codification 
	    parametros = new HashMap() ;
	    parametros.put("probability", 0.9) ;
	    crossover = CrossoverFactory.getCrossoverOperator("SinglePointArrayIntCrossover", parametros);                   

	    parametros = new HashMap() ;
	    parametros.put("probability", 0.2) ;
	    mutation = MutationFactory.getMutationOperator("BitFlipArrayIntMutation", parametros); 
			
	    /* Selection Operator */
	    parametros = null ;
	    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parametros) ;
	    
	    /* Add the operators to the algorithm*/
	    ga.addOperator("crossover",crossover);
	    ga.addOperator("mutation",mutation);
	    ga.addOperator("selection",selection);
	    
	    /* Execute the Algorithm */
	    long initTime = System.currentTimeMillis(); //medir o tempo
	    SolutionSet population = ga.execute();
	    long estimatedTime = System.currentTimeMillis() - initTime;//medir o tempo
	    System.out.println("Total execution time: " + estimatedTime);
	    
	    /* Log messages */
	    System.out.println("Objectives values have been writen to file FUN");
	    population.printObjectivesToFile("FUN");
	    System.out.println("Variables values have been writen to file VAR");
	    population.printVariablesToFile("VAR");  
	    
	    /* Limpando arquivos*/
	  
	    ObjectInputStream in3 = new ObjectInputStream(new BufferedInputStream(new FileInputStream("//Users//david//Desktop//populationSimulation")));
		//HashMap<int[],String> copia3 = (HashMap<int[],String>) in3.readObject();
		HashMap<Solution,String> popSim = (HashMap<Solution,String>) in3.readObject();
		in3.close();
		String lista="";
		//System.out.println(copia3.size());
		for(Solution sol : popSim.keySet()){
			//System.out.println("------------------");
			//XInt solution = new XInt(sol);
			//ArrayInt sol2 = (ArrayInt)sol.getDecisionVariables()[0];
			//System.out.println(Arrays.toString(sol2.array_)+" : "+copia3.get(sol)+"\n");
			lista += popSim.get(sol).substring(0, 16)+"\n";
		}
		//--------------------
		//SALVANDO LISTA DE ARQUIVOS
		BufferedWriter buffer = new BufferedWriter(new FileWriter(new File("//Users//david//Desktop//arquivosSim")));
		buffer.write(lista);
		buffer.flush();
		buffer.close();
		//DELETANDO ARQUIVOS DESNECESSARIOS
		Runtime run = Runtime.getRuntime();
		Process p = run.exec("./src/scripts/deleteFile.sh");	
	}

}
