package operators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.operators.mutation.Mutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XInt;

public class BitFlipArrayIntMutation extends Mutation{
	 /**
	   * Valid solution types to apply this operator 
	   */
	  private static final List VALID_TYPES = Arrays.asList(ArrayIntSolutionType.class) ;

	  private Double mutationProbability_ = null ;
	  
		/**
		 * Constructor
		 * Creates a new instance of the Bit Flip mutation operator
		 */
		public BitFlipArrayIntMutation(HashMap<String, Object> parameters) {
			super(parameters) ;
	  	if (parameters.get("probability") != null)
	  		mutationProbability_ = (Double) parameters.get("probability") ;  		
		} // BitFlipMutation

		/**
		 * Perform the mutation operation
		 * @param probability Mutation probability
		 * @param solution The solution to mutate
		 * @throws JMException
		 */
		public void doMutation(double probability, Solution solution) throws JMException {
			try {
				XInt vars = new XInt(solution);
				ArrayInt solutionMutation = new ArrayInt(vars.getNumberOfDecisionVariables());
				solutionMutation = (ArrayInt) solution.getDecisionVariables()[0];
			 // Integer representation
				for (int i = 0; i < vars.getNumberOfDecisionVariables(); i++)
					if (PseudoRandom.randDouble() < probability) {
						int value = PseudoRandom.randInt(
								(int)vars.getLowerBound(i),
								(int)vars.getUpperBound(i));
						solutionMutation.setValue(i, value);
					} // if
				solution.getDecisionVariables()[0] = solutionMutation;	
			} catch (ClassCastException e1) {
				Configuration.logger_.severe("BitFlipMutation.doMutation: " +
						"ClassCastException error" + e1.getMessage());
				Class cls = java.lang.String.class;
				String name = cls.getName();
				throw new JMException("Exception in " + name + ".doMutation()");
			}
		} // doMutation

		/**
		 * Executes the operation
		 * @param object An object containing a solution to mutate
		 * @return An object containing the mutated solution
		 * @throws JMException 
		 */
		public Object execute(Object object) throws JMException {
			Solution solution = (Solution) object;

			if (!VALID_TYPES.contains(solution.getType().getClass())) {
				Configuration.logger_.severe("BitFlipMutation.execute: the solution " +
						"is not of the right type. The type should be 'ArrayInt'" +
						", but " + solution.getType() + " is obtained");

				Class cls = java.lang.String.class;
				String name = cls.getName();
				throw new JMException("Exception in " + name + ".execute()");
			} // if 

			doMutation(mutationProbability_, solution);
			return solution;
		} // execute

}
