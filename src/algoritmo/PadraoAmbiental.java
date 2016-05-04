package algoritmo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XInt;

public class PadraoAmbiental extends Problem{
	private static final String URLSOLUTIONSIMULATION = "//Users//david//Desktop//solutionSimulation";
	private static final String URLMASSIMBACKUP = "/Users/david/Downloads/massim-2014-2.0/massim/scripts/backup/";
	private static final String URLARQPESOS = "//Users//david//Desktop//pesos.txt";
	private double scoresA[];
	private double scoresB[];
	//public HashMap<int[],String> evaluationFiles;
	public HashMap<Solution,String> evaluationFiles;
	
	public PadraoAmbiental( int qtdVertices) throws FileNotFoundException, ClassNotFoundException, IOException{
		solutionType_ = new ArrayIntSolutionType(this);
		numberOfConstraints_ = 0;
		numberOfObjectives_ = 1;
		numberOfVariables_ = 30;//qtdVertices;
		
		lowerLimit_ = new double[qtdVertices];
		upperLimit_ = new double[qtdVertices];
		
		for(int i = 0; i < numberOfVariables_; i++){
			lowerLimit_[i] = 1;
			upperLimit_[i] = 10;
		}
		
		//evaluationFiles = new HashMap<int[],String>();
		
	}
	
	@Override
	public void evaluate(Solution arg0) throws JMException {
		// TODO Auto-generated method stub
		try {
			evaluationFiles = historicSolution();
			
		} catch (ClassNotFoundException | IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//JOptionPane.showMessageDialog(null, arg0.getDecisionVariables().length);
		//Avaliação da solução
		XInt vars = new XInt(arg0);
		//JOptionPane.showMessageDialog(null, vars.getLowerBound(29)+"---"+vars.getUpperBound(29));
		//JOptionPane.showMessageDialog(null, arg0.getDecisionVariables()[0].);
		int weights[] = new int[numberOfVariables_];
		for (int i = 0; i < numberOfVariables_; i++){
			weights[i] = vars.getValue(i);
		}
		//Atividade 00 : criar arquivo de pesos para o simulador criar o mapa
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(URLARQPESOS)));
			//Escreve a primeira parte dos vertices
			for (int i = 0; i < numberOfVariables_; i++){
				//System.out.println(weights[i]);
				bw.write(String.valueOf(weights[i]));
				bw.newLine();
			}
			//Escreve a segunda parte (espelho dos vertices)
			for (int i = 0; i < numberOfVariables_; i++){
				//System.out.println(weights[i]);
				bw.write(String.valueOf(weights[i]));
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Atividade 01 : executar a simulação MAPC com (TimeAlvo, TimeAdversario, PadraoAmbiental)
		Runtime run = Runtime.getRuntime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		Date date = new Date();
		String arqResult = dateFormat.format(date)+"-mapc-result.xml"; 
		try {
			Process execServer = run.exec("./src/scripts/script.sh");
			Thread.sleep(5000);
			Process execTimeA = run.exec("./src/scripts/scriptAnt.sh");
			Process execTimeB = run.exec("./src/scripts/scriptAnt2.sh");
			
			//Pausa a execução até que a simulação termine
			while ((execTimeA.isAlive())||(execTimeB.isAlive()))
				Thread.sleep(10000);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Atividade 02 : resgatar o valor de pontuação dos times por rounds.
		
		File file = new File(URLMASSIMBACKUP+arqResult);
		try {
			lerArquivo(file);
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Associa arquivos da simulação com a solução
		try {
			//evaluationFiles.put(weights, arqResult);
			evaluationFiles.put(arg0, arqResult);
			salvaRegistroSimulacao(evaluationFiles);
			//JOptionPane.showMessageDialog(null, evaluationFiles.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Atividade 03 : calcular o resultado da função f(x) = Sum (score_TimeAlvo / score_TimeAdversario).
		double sumScoresA = scoresA[0] + scoresA[1] + scoresA[2] + scoresA[3];
		double sumScoresB = scoresB[0] + scoresB[1] + scoresB[2] + scoresB[3];
		//atribuição da função objetivo
		arg0.setObjective(0, (sumScoresA / sumScoresB));
	}

	
	//private static File file = new File("/Users/david/Downloads/massim-2014-2.0/massim/scripts/backup/2016-02-17_13:35-david-mac-wifi.ifce.edu.br-result.xml");
	public void lerArquivo(File file) throws JDOMException,IOException{
		//criar um objeto SAXBuilder
		//para ler o arquivo
		SAXBuilder sb = new SAXBuilder();
		//criar um objeto Document que
		//recebe o conteúdo do arquivo
		Document doc = sb.build(file);
		//criar um objeto Element que
		//recebe as tags do XML
		Element elements = doc.getRootElement();
		//List<Element> tag = elements.getChild("state").getChild("teams").getChildren();
		List<Element> tag = elements.getChildren("match");
		//Associar as tags às variáveis
		Iterator<Element> i = tag.iterator();
		scoresA = new double[4];
		scoresB = new double[4];
		
		int ind = 0;
		while (i.hasNext()){
			List<Element> simulations = i.next().getChildren();
			Iterator<Element> ie = simulations.iterator();
			while (ie.hasNext()){
				Element e = ie.next().getChild("result");
				

				scoresA[ind] = Double.parseDouble(e.getAttributeValue("A"));
			
				scoresB[ind] = Double.parseDouble(e.getAttributeValue("B"));
				ind++;
			}	
		}
		
		/*for (int j = 0; j < 4; j++){
		JOptionPane.showMessageDialog(null, "scoreA:"+scoresA[j]);
		JOptionPane.showMessageDialog(null, "scoreB:"+scoresB[j]);
		}*/
	}

	public Map<Solution, String> getEvaluationFiles() {
		return evaluationFiles;
	}
	
	public void salvaRegistroSimulacao(HashMap<Solution, String> dados) throws FileNotFoundException, IOException{
		//JOptionPane.showMessageDialog(null, " Sem Erros");
		
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(URLSOLUTIONSIMULATION)));
  
		out.writeObject(dados);
		out.flush();
		out.close();
		
	}
	
    public HashMap<Solution, String> historicSolution() throws FileNotFoundException, IOException, ClassNotFoundException{
    		File file = new File(URLSOLUTIONSIMULATION);
    		
    		if (file.exists()){
	    		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(URLSOLUTIONSIMULATION)));
	    		//HashMap<int[],String> dados = (HashMap<int[],String>) in.readObject();
	    		HashMap<Solution,String> dados = (HashMap<Solution,String>) in.readObject();
	    		in.close();
	    		//File arq = new File(arquivo);
	    		file.delete();
	    		/*if (file.delete())
	    			JOptionPane.showMessageDialog(null, "Apagou o arquivo!");
	 */
	    		return dados;
    		}	
    		return new HashMap<Solution,String>();
    }
}
