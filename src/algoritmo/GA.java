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
import java.net.URISyntaxException;
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
import jmetal.encodings.variable.ArrayInt;
import jmetal.operators.selection.BestSolutionSelection;
import jmetal.util.JMException;
import jmetal.util.comparators.ObjectiveComparator;

public class GA extends Algorithm{
	private static final String URLSOLUTIONSIMULATION = "massim-2014-2.0/massim/target/arquivosMAPC/solutionSimulation";//"//Users//david//Desktop//solutionSimulation";
	private static final String URLPOPULATIONSIMULATION = "massim-2014-2.0/massim/target/arquivosMAPC/populationSimulation";//"//Users//david//Desktop//populationSimulation";
	private SolutionSet offspringPopulation;
	private int tamPop;
	private String dir;
	public GA(Problem problem) {
		super(problem);
		// TODO Auto-generated constructor stub
		try {
			dir = PadraoAmbiental.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			dir = dir.substring(0, dir.lastIndexOf("bin"));
			dir = dir+"src"+System.getProperty("file.separator")+"simulador";
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		//Parametrização inicial
		//int tamPop;
		int maxEvaluations;
		int evaluations;
		
		//cria a população e a população de filhos sendo o conjunto de soluções
		SolutionSet population;
		//SolutionSet offspringPopulation;
		
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
	    	  // if(ind > 1)//Controle de individuos
	    	  //	    break;
	      //}
	    }
	    //------------------
        try {
        		System.out.println("Salvando populacao "+ evaluations);
			//salvaRegistroSimulacao("//Users//david//Desktop//solutionSimulation");
        		salvaRegistroSimulacao(dir+System.getProperty("file.separator")+URLSOLUTIONSIMULATION);
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
	   
	    //Mecanismo de Parada
	    double bestSolution = 0;
	    int limiteEstabilidade = 0;
	   
	    //loop de evoluções 
	    System.out.println("Iniciando Processo de Reprodução/Mutação");
	    while (limiteEstabilidade < 1/*evaluations < maxEvaluations*/){
	    		//A cada 10 evaluations escreve uma msg como a melhor solução atual
	    		//if ((evaluations % 10) == 0) {
	            System.out.println(evaluations + ": " + population.get(0).getObjective(0)) ;
	        //} 

	    		//Copy the best two individuals to the offspring population
	        /*offspringPopulation.add(new Solution(population.get(0))) ;	
	        offspringPopulation.add(new Solution(population.get(1))) ;	
	        */
	            
	        //Copy the best N% individuals to the offspring population
	        int bestIndividuals = (int)(tamPop * 0.5);    
	        for (int i = 0; i < bestIndividuals; i++){
	        		offspringPopulation.add(new Solution(population.get(i)));
	        }
	        //Mantém as melhores soluções na população
	        try {
				mantemMelhoresSolucoes();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        //Reproductive cycle
	        int qtdParents = 0;
	        while ( qtdParents < (tamPop - bestIndividuals)) {
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
	           // problem_.evaluate(offspring[0]);
	            //problem_.evaluateConstraints(offspring[0]);
	            
	           // problem_.evaluate(offspring[1]);
	            //problem_.evaluateConstraints(offspring[1]);
	            
	            
	            // Replacement: the two new individuals are inserted in the offspring
	            //              population
	            if (/*(offspring[0].getNumberOfViolatedConstraint() == 0)&&*/qtdParents < (tamPop-bestIndividuals)){
	            	  // Avalia o Primeiro filho gerado
		            problem_.evaluate(offspring[0]);
	            		offspringPopulation.add(offspring[0]) ;
	            		qtdParents++;
	            		 //Incrementa o número de avaliações
	    	            evaluations++;
	            }
	            
	            if (/*(offspring[1].getNumberOfViolatedConstraint() == 0)&&*/qtdParents < (tamPop-bestIndividuals)){
	            	 // Avalia o Segundo filho gerado
	            		problem_.evaluate(offspring[1]);
	            		offspringPopulation.add(offspring[1]) ;
	            	    qtdParents++;
	            		 //Incrementa o número de avaliações
	    	            evaluations++;
	            }
	        } 
	        
	        // The offspring population becomes the new current population
	        population.clear();
	        for (int i = 0; i < tamPop; i++) {
	          population.add(offspringPopulation.get(i));
	        }
	        //------------------
	        try {
	        		System.out.println("Salvando populacao "+evaluations);
				//salvaRegistroSimulacao("//Users//david//Desktop//solutionSimulation");
	        		salvaRegistroSimulacao(dir+System.getProperty("file.separator")+URLSOLUTIONSIMULATION);
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
	        if (population.get(0).getObjective(0) > bestSolution){
        			bestSolution = population.get(0).getObjective(0);
        			limiteEstabilidade = 0;
	        }else
        			limiteEstabilidade++;
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
		HashMap<Solution,String> evaluationSimulation = historicPopulation();
		evaluationSimulation.putAll((HashMap<Solution, String>) in.readObject());
		in.close();
		//JOptionPane.showMessageDialog(null,evaluationSimulation.size());
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dir+System.getProperty("file.separator")+URLPOPULATIONSIMULATION)));
		out.writeObject(evaluationSimulation);
		out.close();
		
		//Apaga arquivo temporário
		File arq = new File(arquivo);
		arq.delete();
		/*if (arq.delete())
			JOptionPane.showMessageDialog(null, "Apagou o arquivo!");*/
	}

	public void mantemMelhoresSolucoes() throws FileNotFoundException, IOException, ClassNotFoundException, JMException{
		//Abrindo o arquivo e atribuindo os dados a uma variável
		ObjectInputStream inPopulation = new ObjectInputStream(new BufferedInputStream(new FileInputStream(dir+System.getProperty("file.separator")+URLPOPULATIONSIMULATION)));
		HashMap<Solution,String> dadosPopulation = (HashMap<Solution,String>) inPopulation.readObject();
		inPopulation.close();
		//Criando objeto para escrita
		HashMap<Solution,String> evaluationSimulation = new HashMap<>();
		//Mecanismo de repetição para filtrar os permanentes
		for(int i = 0; i < ((int)(tamPop * 0.5)); i++){	
			for(Solution s : dadosPopulation.keySet()){
				if (verificaIgualdade(s,offspringPopulation.get(i))){
					System.out.println("Salvando filho "+ i);
					evaluationSimulation.put(s, dadosPopulation.get(s));
				}
			}
		}
		//Abrindo o arquivo para escrita
		ObjectOutputStream outPopulation = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dir+System.getProperty("file.separator")+URLPOPULATIONSIMULATION)));
		outPopulation.writeObject(evaluationSimulation);
		outPopulation.close();
	}
	
	public HashMap<Solution, String> historicPopulation() throws FileNotFoundException, IOException, ClassNotFoundException{
		File file = new File(dir+System.getProperty("file.separator")+URLPOPULATIONSIMULATION);
		
		if (file.exists()){
    			ObjectInputStream inPopulation = new ObjectInputStream(new BufferedInputStream(new FileInputStream(dir+System.getProperty("file.separator")+URLPOPULATIONSIMULATION)));
	    		HashMap<Solution,String> dados = (HashMap<Solution,String>) inPopulation.readObject();
	    		inPopulation.close();
	    		file.delete();
	    		return dados;
		}	
		return new HashMap<Solution,String>();
	}
	
	public boolean verificaIgualdade(Solution s1, Solution s2) throws JMException{
		ArrayInt sol1 = (ArrayInt)s1.getDecisionVariables()[0];		
		ArrayInt sol2 = (ArrayInt)s2.getDecisionVariables()[0];
		for (int i = 0; i < sol1.getLength(); i++){
			if (sol1.getValue(i) != sol2.getValue(i))
				return false;
		}
		
		return true;
	}
}
