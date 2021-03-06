package operators;


import java.util.Arrays;
import java.util.HashMap;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.operators.crossover.Crossover;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XInt;

public class SinglePointArrayIntCrossover extends Crossover{

	 /* Valid solution types to apply this operator 
	   */
	  private static final java.util.List VALID_TYPES = Arrays.asList(ArrayIntSolutionType.class) ;

	  private Double crossoverProbability_ = null;

	  /**
	   * Constructor
	   * Creates a new instance of the single point crossover operator
	   */
	  public SinglePointArrayIntCrossover(HashMap<String, Object> parameters) {
	  	super(parameters) ;
	  	if (parameters.get("probability") != null)
	  		crossoverProbability_ = (Double) parameters.get("probability") ;  		
	  } // SinglePointCrossover


	  /**
	   * Constructor
	   * Creates a new instance of the single point crossover operator
	   */
	  //public SinglePointCrossover(Properties properties) {
	  //    this();
	  //} // SinglePointCrossover

	  /**
	   * Perform the crossover operation.
	   * @param probability Crossover probability
	   * @param parent1 The first parent
	   * @param parent2 The second parent   
	   * @return An array containig the two offsprings
	   * @throws JMException
	   */
	  public Solution[] doCrossover(double probability,
			  						Solution parent1,
			  						Solution parent2) throws JMException {
	 
		Solution[] offSpring = new Solution[2];
	    offSpring[0] = new Solution(parent1);
	    offSpring[1] = new Solution(parent2);
	    XInt vars1 = new XInt(parent1);
	    XInt vars2 = new XInt(parent2);
	    try {
	      if (PseudoRandom.randDouble() < probability) {
	          //1. Compute the total number of values
	          int totalNumberOfBits = vars1.getNumberOfDecisionVariables(); //weight
	          
	          //2. Calculate the point to make the crossover
	          int crossoverPoint = PseudoRandom.randInt(0, totalNumberOfBits - 1);

	          //3. Compute the encodings.variable containing the crossoverPoint bit
	          
	          //4. Compute the bit into the selected encodings.variable
	          
	          //5. Make the crossover into the gene;
	         // XInt offSpring1 = new XInt(offSpring[0]);
	         // XInt offSpring2 = new XInt(offSpring[1]);
	          ArrayInt offSpringOne = new ArrayInt(vars1.getNumberOfDecisionVariables());
	          ArrayInt offSpringTwo = new ArrayInt(vars1.getNumberOfDecisionVariables());
	         
	          offSpringOne = (ArrayInt) offSpring[0].getDecisionVariables()[0];
	          offSpringTwo = (ArrayInt) offSpring[1].getDecisionVariables()[0];
	          for (int i = crossoverPoint; i < vars1.getNumberOfDecisionVariables(); i++){
	        	  	
	        	  	
	        	  //	offSpring1.setValue(i, vars2.getValue(i));
	        	  //	offSpring2.setValue(i, vars1.getValue(i));
	        	  	
	        	 	offSpringOne.setValue(i, vars2.getValue(i));
	        	 	offSpringTwo.setValue(i, vars1.getValue(i));
	          }
	          offSpring[0].getDecisionVariables()[0] = offSpringOne;
	          offSpring[1].getDecisionVariables()[0] = offSpringTwo;  
	      }
	    } catch (ClassCastException e1) {
	      Configuration.logger_.severe("SinglePointCrossover.doCrossover: Cannot perfom " +
	              "SinglePointCrossover");
	      Class cls = java.lang.String.class;
	      String name = cls.getName();
	      throw new JMException("Exception in " + name + ".doCrossover()");
	    }
	    return offSpring;
	  } // doCrossover

	  /**
	   * Executes the operation
	   * @param object An object containing an array of two solutions
	   * @return An object containing an array with the offSprings
	   * @throws JMException
	   */
	  public Object execute(Object object) throws JMException {
	    Solution[] parents = (Solution[]) object;

	    if (!(VALID_TYPES.contains(parents[0].getType().getClass())  &&
	        VALID_TYPES.contains(parents[1].getType().getClass())) ) {

	      Configuration.logger_.severe("SinglePointCrossover.execute: the solutions " +
	              "are not of the right type. The type should be 'Binary' or 'Int', but " +
	              parents[0].getType() + " and " +
	              parents[1].getType() + " are obtained");

	      Class cls = java.lang.String.class;
	      String name = cls.getName();
	      throw new JMException("Exception in " + name + ".execute()");
	    } // if

	    if (parents.length < 2) {
	      Configuration.logger_.severe("SinglePointCrossover.execute: operator " +
	              "needs two parents");
	      Class cls = java.lang.String.class;
	      String name = cls.getName();
	      throw new JMException("Exception in " + name + ".execute()");
	    } 
	    
	    Solution[] offSpring;
	    offSpring = doCrossover(crossoverProbability_,
	            parents[0],
	            parents[1]);

	    //-> Update the offSpring solutions
	    for (int i = 0; i < offSpring.length; i++) {
	      offSpring[i].setCrowdingDistance(0.0);
	      offSpring[i].setRank(0);
	    }
	    return offSpring;
	  } // execute


}
