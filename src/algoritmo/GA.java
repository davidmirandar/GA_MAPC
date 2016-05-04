package algoritmo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;



import javax.swing.JOptionPane;

//import massim.competition2014.scenario.GraphNode;
//import salvarObjetoArquivo.Pessoa;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.comparators.ObjectiveComparator;

public class GA extends Algorithm{
	private static final String URLSOLUTIONSIMULATION = "//Users//david//Desktop//solutionSimulation";
	private static final String URLPOPULATIONSIMULATION = "//Users//david//Desktop//populationSimulation";
	public GA(Problem problem) {
		super(problem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		//Parametrização inicial
		int tamPop;
		int maxEvaluations;
		int evaluations;
		
		//cria a população e a população de filhos sendo o conjunto de soluções
		SolutionSet population;
		SolutionSet offspringPopulation;
		
		//Cria os operadores do GA
		Operator mutationOperator;
		Operator crossoverOperator;
		Operator selectionOperator;
		
		//Comparador do valor da função objetivo
		Comparator<ObjectiveComparator> comparator;
		comparator = new ObjectiveComparator(0,true); //TRUE - ordem decrescente   FALSE - Ordem crescente
		
		//Leitura dos parametros por setInputParameter()
	    tamPop = ((Integer)this.getInputParameter("tamPop")).intValue();
	    maxEvaluations = ((Integer)this.getInputParameter("maxEvaluations")).intValue(); 
		
	    //Inicializa as populações
	    population        = new SolutionSet(tamPop);   
	    offspringPopulation = new SolutionSet(tamPop);
	    
	    //Seta o valor  inicial para o número de evoluções
	    evaluations = 0;
	    
	    //Leitura dos operadores para o GA
	    mutationOperator  = this.operators_.get("mutation");
	    crossoverOperator = this.operators_.get("crossover");
	    selectionOperator = this.operators_.get("selection");  
	    
	    //Criação de uma população inicial
	    Solution newIndividual;
	    int ind = 0;
	    while(ind < tamPop){
	       newIndividual = new Solution(this.problem_); //gera individuo                   
	       problem_.evaluate(newIndividual); 
	     
	       //Verificar restrição
	       //problem_.evaluateConstraints(newIndividual);
	       //if (newIndividual.getNumberOfViolatedConstraint() == 0){
	    	   evaluations++;
	    	  	
	    	   population.add(newIndividual);   //armazena individuo na população
	    	   ind++;
	    	   if(ind > 1)//Controle de individuos
	    	  	    break;
	      //}
	    }
	    //------------------
        try {
        		System.out.println("Salvando populacao");
			//salvaRegistroSimulacao("//Users//david//Desktop//solutionSimulation");
        		salvaRegistroSimulacao(URLSOLUTIONSIMULATION);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //-------------------
	    //Ordena a populacao
	    population.sort(comparator);
	    
	    //loop de evoluções 
	    while (evaluations < 3/*maxEvaluations*/){
	    		//A cada 10 evaluations escreve uma msg como a melhor solução atual
	    		if ((evaluations % 10) == 0) {
	            System.out.println(evaluations + ": " + population.get(0).getObjective(0)) ;
	            } 

	    		//Copy the best two individuals to the offspring population
	      //  offspringPopulation.add(new Solution(population.get(0))) ;	
	      //  offspringPopulation.add(new Solution(population.get(1))) ;	
	        
	        //Reproductive cycle
	        int ind2 = 0;
	        while ( ind2 < (tamPop/* - 2*/)) {
	        		//Seleção dos pais para o cruzamento
	        		Solution[] parents = new Solution[2];
	        		
	        		parents[0] = (Solution)selectionOperator.execute(population);
	        		parents[1] = (Solution)selectionOperator.execute(population);
	        	    
	        		
	        		//JOptionPane.showMessageDialog(null, parents[0]==null?"Nulo":"Não Nulo" +" -- "+parents[1]==null?"Nulo":"Não Nulo");
	            //Crossover
	            	Solution[] offspring = (Solution[]) crossoverOperator.execute(parents);
	            	//JOptionPane.showMessageDialog(null, "Filhos: "+ offspring);
	        	
	            	//Mutation
	            mutationOperator.execute(offspring[0]);
	            mutationOperator.execute(offspring[1]);
	            
	            // Avalia os filhos gerados
	            problem_.evaluate(offspring[0]);
	            //problem_.evaluateConstraints(offspring[0]);
	            
	            problem_.evaluate(offspring[1]);
	            //problem_.evaluateConstraints(offspring[1]);
	            
	            //Incrementa o número de avaliações
	            evaluations += 2;
	            
	            // Replacement: the two new individuals are inserted in the offspring
	            //              population
	            if (/*(offspring[0].getNumberOfViolatedConstraint() == 0)&&*/(ind2 < (tamPop))){
	            		offspringPopulation.add(offspring[0]) ;
	            		ind2++;
	            }
	            
	            if (/*(offspring[1].getNumberOfViolatedConstraint() == 0)&&*/(ind2 < (tamPop))){
	            	    offspringPopulation.add(offspring[1]) ;
	             	    ind2++;
	            }
	        } 
	        // The offspring population becomes the new current population
	        population.clear();
	        for (int i = 0; i < tamPop; i++) {
	          population.add(offspringPopulation.get(i));
	        }
	        //------------------
	        try {
	        		System.out.println("Salvando populacao");
				//salvaRegistroSimulacao("//Users//david//Desktop//solutionSimulation");
	        		salvaRegistroSimulacao(URLSOLUTIONSIMULATION);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //-------------------
	        offspringPopulation.clear();
	        population.sort(comparator) ; //ordena a população
	    }
	    
	    //Return a population with the best individual
	    //SolutionSet resultPopulation = new SolutionSet(1);
	    //resultPopulation.add(population.get(0));
	    
	    System.out.println("Evaluations: " + evaluations);
	    
	    return population;
	}
	
	public void salvaRegistroSimulacao(String arquivo) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(arquivo)));
		//HashMap<int[],String> evaluationSimulation = (HashMap<int[], String>) in.readObject();
		HashMap<Solution,String> evaluationSimulation = (HashMap<Solution, String>) in.readObject();
		in.close();
		//JOptionPane.showMessageDialog(null,evaluationSimulation.size());
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(URLPOPULATIONSIMULATION)));
		out.writeObject(evaluationSimulation);
		out.close();
		
		File arq = new File(arquivo);
		arq.delete();
		/*if (arq.delete())
			JOptionPane.showMessageDialog(null, "Apagou o arquivo!");*/
	}

}
