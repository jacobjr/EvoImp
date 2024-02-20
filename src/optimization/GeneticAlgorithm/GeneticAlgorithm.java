package optimization.GeneticAlgorithm;

/***********************************************************************

	This file is part of EvoImp: Multiple Imputation of Multi-label 
        Classification data with a genetic algorithm.

	Copyright (C) 2019-2024
	
        Antonio Jacob Jr. (antoniojunior@professor.uema.br)
        Fabrício Almeida (fabrycio30@hotmail.com)
        Adamo Santana (adamo-santana@fujielectric.com)
        Ewaldo Santana (ewaldoeder@gmail.com)
        Fábio Lobato (fabio.lobato@ufopa.edu.br)
        
        If you use any of the resources available here, to cite this work, 
        please use:
        Jacob Junior, A. F. L., do Carmo, F. A., de Santana, A. L., 
        Santana, E. E. C., & Lobato, F. M. F. (2024). EvoImp: Multiple 
        Imputation of Multi-label Classification data with a genetic algorithm.
        Plos one, 19(1), e0297147.
        
        
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

import util.arff.EscritorArff;
import util.imputation.InfoDadosComVA;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Properties;

public class GeneticAlgorithm {
/**
 * Class GeneticAlgorith
 * The genetic algorithm itself is implemented, mapped according to the problem.
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */
    
    private final InfoDadosComVA pool;

    private final double[] avaliacoesPorGeracaoMelhores = new double[3];
    private final double[] avaliacoesPorGeracaoMelhorEPiorEM = new double[3];
    private final double[] avaliacoesPorGeracaoMelhorEPiorACC = new double[3];
    private final double[] avaliacoesPorGeracaoMelhorEPiorHL = new double[3];
    
    private final double[] avaliacoesPorGeracaoMelhorEPiorAUC = new double[3];
    private final double[] avaliacoesPorGeracaoMelhorEPiorRMSE = new double[3];
    private final double[] avaliacoesPorGeracaoMelhorEPiorMAE = new double[3];
    
    private final double[] avaliacoesPorGeracaoMelhorEPiorPrecision = new double[3];
    private final double[] avaliacoesPorGeracaoMelhorEPiorFMeasure = new double[3];
    
    ArrayList<String> listaInd = new ArrayList<>();
    private String todosIndividuos= " ";
    private final String todosIndividuosSort= " ";
    private final String dadosGraficos[] = new String[3];
    private String tClassificacao = "";
   
    private final double tx_mutation = 0.4;
     
    public GeneticAlgorithm(InfoDadosComVA pool, String tClassificacao) {
        this.pool = pool;
        this.tClassificacao = tClassificacao;        
    }

     /**
      * 
      * @param file
      * @param classificadorMulti tipo de classificação (MULTIRRÓTULO, nesse caso)
      * @return String com as melhores avaliações para ACC, EM e HL apos o processo de execução.
      */
    public String runMulti(String file, String classificadorMulti){
        todosIndividuos+="======================================================================\n";
         todosIndividuos+="                      DATASET = "+file+ "\n";
         todosIndividuos+="======================================================================\n";
        int generation = 0;
         
        Population population = new Population(pool);
        
        /**
          * Cria e avalia a População Inicial.
          * 
          */
        population.initPopulation();
        population.evaluate(classificadorMulti);
         /*
           imprime  a população inicial
           */ 
        for (Chromosome i : population.getIndividuals()) {
              System.out.println("em: "+i.getEM()+"   acc: "+i.getACC()+"   hl: "+i.getHL());
        }
        
        String output = "";
        System.err.println("Generation: "+generation+" | Population Size: "+population.getIndividuals().size()+" | Best Fitness: "+population.getBestIndividual().getFitness());
       
        /**
          * Inicia-se o processo de execução.
        */
        for(int individualPerGeneration : Properties.Generations){
            
        /**
          * Cria a população da geração seguinte.
        */ 
        Population nextGeneration = new Population(pool);
        Population nextGenerationOrdenada = new Population(pool);
           
        /**
          * adiciona o melhor indivíduo na próxima geração (ELITISMO).
        */ 
        for(int i = 0; i < Properties.Elitism; i++){
            /* MODIFICACAO-JACOB-ACC - BEGIN */
            nextGeneration.getIndividuals().add(population.getBestIndividual());
            
            //todosIndividuos+= "elitista ["+population.getBestIndividual().getEM()+"]";
            population.getIndividuals().remove(population.getBestIndividual());
            
            
            //nextGeneration.getIndividuals().add(population.getBestIndividualACC());
            
            //todosIndividuos+= "elitista ["+population.getBestIndividual().getEM()+"]";
            //population.getIndividuals().remove(population.getBestIndividualACC());
            /* MODIFICACAO-JACOB-ACC - END */
            
            
        }
          
          
        int indo = 1;
        for(Chromosome ind : nextGeneration.getIndividuals()){
            population.getIndividuals().add(ind);
            indo++;
        }
          /**
           * Realiza o operardor de cruzamento. 
           * Nesse processo, dois individuos da população atual são escolhidos e é feito o cruzamento de 2-pontos.
           * Logo após, é feito a mutação em 20% dos genes de cada indivíduo gerado (filhos).
           * 
           */
          while(nextGeneration.getIndividuals().size() < individualPerGeneration){
              Chromosome parent[] = Operator.selection(population);
              Chromosome children[] = Operator.crossover(parent);
              Chromosome mutado1 = Operator.mutation2(nextGeneration,children[0], pool, tx_mutation);
              Chromosome mutado2 = Operator.mutation2(nextGeneration,children[1], pool, tx_mutation);
              
              nextGeneration.getIndividuals().add(mutado1);
              
              if(nextGeneration.getIndividuals().size() < individualPerGeneration){
                nextGeneration.getIndividuals().add(mutado2);
              }
          }
     
          
          /**
           * Avaliação da população criada. As avaliações compreedem em Exact match, Acurária e Hamming Loss, respetivamente (lexicografia).
           * Posteriormente, os indivíduos são ordenados de acordo tais avaliações (sobreditas).
          */    
          nextGeneration.evaluate(classificadorMulti);

          int oG = 0;
          
          /* MODIFICACAO-JACOB-ACC - BEGIN */ 
          ArrayList<Chromosome> ordenaG = nextGeneration.ordenaGeracao(individualPerGeneration,nextGeneration.getIndividuals());
          
          
          //ArrayList<Chromosome> ordenaG = nextGeneration.ordenaGeracaoACC(individualPerGeneration,nextGeneration.getIndividuals());          
          /* MODIFICACAO-JACOB-ACC - END */
          for (Chromosome g :ordenaG ) {       
            nextGenerationOrdenada.getIndividuals().add(g);
            oG++;
          }
    
          population = nextGenerationOrdenada; 
          generation++;
           
         /**
          * Acompanhamento na saida do sistema.
          */ 
          System.err.println("Generation: "+generation+" | Population Size: "+population.getIndividuals().size()+" | Best Individuo (EM): "+population.getBestIndividual().getEM()+" | Pior Individuo (EM): "+ population.getPiorIndividual().getEM());

          /**
          * Imprime as avaliações de todos os indivíduos, de todas a gerações, em um file (.CSV)
           */ 
          int count =1;
           for (Chromosome i : population.getIndividuals()) {
              System.out.println("Ger"+generation+",  ind"+count+",   em: "+i.getEM()+"   acc: "+i.getACC()+"   hl: "+i.getHL() +" Fitness: "+ i.getFitness());
              todosIndividuos+= "Ger"+generation+",  ind"+count+",   em: "+i.getEM()+"   acc: "+i.getACC()+"   hl: "+i.getHL()+" Fitness: "+ i.getFitness()+"\n";
                           
              count++;
          }
          todosIndividuos+="\n";
          int counnt =1;
          
          /**
           * Geração da melhor base de dados da geração atual. Para cada geração, a base gravada representa os reutados do melhor indivíduo.
           * 
          */
           
          EscritorArff escritorArff = new EscritorArff();
          escritorArff.EscritorArffMissing(new File("result/BRGeracoesAG/"+file+"_geration"+generation+".arff"), pool.getInfo(), population.imputeChromosomeData(population.getBestIndividual()));
        
          
          /**
           * Para cada geração as avaliações dos melhor individuo são registradas.
          */
          /* MODIFICACAO-JACOB-ACC - BEGIN */
          avaliacoesPorGeracaoMelhores[0] = population.getBestIndividual().getHL();
          avaliacoesPorGeracaoMelhores[1] = population.getBestIndividual().getACC();
          avaliacoesPorGeracaoMelhores[2] = population.getBestIndividual().getEM();
         
          /*
          avaliacoesPorGeracaoMelhores[0] = population.getBestIndividualACC().getHL();
          avaliacoesPorGeracaoMelhores[1] = population.getBestIndividualACC().getACC();
          avaliacoesPorGeracaoMelhores[2] = population.getBestIndividualACC().getEM();

          /* MODIFICACAO-JACOB-ACC - END */          
          
          
          String avalGeracao = "Generation: "+generation+",   dataset: "+file+",  EM: "+avaliacoesPorGeracaoMelhores[2]+",  ACC: "+ avaliacoesPorGeracaoMelhores[1]+",  HL: "+avaliacoesPorGeracaoMelhores[0]+ ",  best Fitness: "+population.getBestIndividual().getFitness()+"\n";
            try {
                writeToFile(avalGeracao, "summary/MelhoresIndividuosDeTodasAsGerações.csv", Boolean.TRUE);
            } catch (IOException ex) {
                Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            /**
             * Registro das melhores, das piores, e das médias das avaliações dos indivíduos durante cada geração.
             */
            
            /* MODIFICACAO-JACOB-ACC - BEGIN             
                Onde getBestIndividual() -> getBestIndividualACC() 
                     getPiorIndividual() -> getPiorIndividualACC()
            */
            
            double contEM = 0;
            for (Chromosome i : population.getIndividuals()) {contEM += i.getEM(); }
            double resultMediaEM = contEM/population.getIndividuals().size();
            avaliacoesPorGeracaoMelhorEPiorEM[0] = population.getBestIndividual().getEM(); //melhorEM
            avaliacoesPorGeracaoMelhorEPiorEM[1] = population.getPiorIndividual().getEM(); //piorEM
            avaliacoesPorGeracaoMelhorEPiorEM[2] = resultMediaEM;                          //mediaEM
            dadosGraficos[0] = avaliacoesPorGeracaoMelhorEPiorEM[0]+"  "+avaliacoesPorGeracaoMelhorEPiorEM[1]+"  "+avaliacoesPorGeracaoMelhorEPiorEM[2]+"\n";
            
            double contACC = 0;
            for (Chromosome i : population.getIndividuals()) {contACC += i.getACC(); }
            double resultMediaACC = contACC/population.getIndividuals().size();
            avaliacoesPorGeracaoMelhorEPiorACC[0] = population.getBestIndividual().getACC(); //melhorACC
            avaliacoesPorGeracaoMelhorEPiorACC[1] = population.getPiorIndividual().getACC(); //piorACC
            avaliacoesPorGeracaoMelhorEPiorACC[2] = resultMediaACC;                           //mediaACC
            dadosGraficos[1] = avaliacoesPorGeracaoMelhorEPiorACC[0]+"  "+avaliacoesPorGeracaoMelhorEPiorACC[1]+"  "+avaliacoesPorGeracaoMelhorEPiorACC[2]+"\n";
            
            double contHL = 0;
            for (Chromosome i : population.getIndividuals()) {contHL += i.getHL(); }
            double resultMediaHL = contHL/population.getIndividuals().size();
            avaliacoesPorGeracaoMelhorEPiorHL[0] = population.getBestIndividual().getHL(); //melhorHL
            avaliacoesPorGeracaoMelhorEPiorHL[1] = population.getPiorIndividual().getHL(); //piorHL
            avaliacoesPorGeracaoMelhorEPiorHL[2] = resultMediaHL;                          //mediaHL
            dadosGraficos[2] = avaliacoesPorGeracaoMelhorEPiorHL[0]+"  "+avaliacoesPorGeracaoMelhorEPiorHL[1]+"  "+avaliacoesPorGeracaoMelhorEPiorHL[2]+"\n";
            
            try {
               writeToFile(dadosGraficos[0], "summary/dados-graficos/"+file+"_EM.txt", Boolean.TRUE);
                writeToFile(dadosGraficos[1], "summary/dados-graficos/"+file+"_ACC.txt", Boolean.TRUE);
                writeToFile(dadosGraficos[2], "summary/dados-graficos/"+file+"_HL.txt", Boolean.TRUE);
                
            } catch (IOException ex) {
                Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }/*fim das geracoes*/
        
        
         /**
          *  Registra em file (.csv) todos os indivíduos (de todas as gerações).
          */
               try {
                writeToFile(todosIndividuos, "summary/todosOsIndividuosDeTodasAsGerações.csv", Boolean.TRUE);
                      } catch (IOException ex) {
                Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                      }
 
      
         /**
          * Formatação da String de Saída.
          */ 
        output = "\ndataset: " + file + ", classificador: "+classificadorMulti+",  metodo: EvoImp -->  EM: [" + population.getBestIndividual().getEM() + "[,   ACC: [" + population.getBestIndividual().getACC()
                  + "],    HL: [" + population.getBestIndividual().getHL()+"]";
         /**
          * Geração da base de dados FINAL. Melhor indivíduo de todas as gerações.
          */ 
        EscritorArff escritorArff = new EscritorArff();
        escritorArff.EscritorArffMissing(new File("result/BR/imputed_AG_"+file+".arff"), pool.getInfo(), population.imputeChromosomeData(population.getBestIndividual()));
       
        
        /* MODIFICACAO-JACOB-ACC - END
                Onde getBestIndividual() -> getBestIndividualACC() 
                     getPiorIndividual() -> getPiorIndividualACC()
        */        
        
        
        
        
        return output;
    }

    /**
     * Método escritor em arquivos, utilizado para gravar os resultados das avaliações realizadas anteriormente.
     * @param data conteúdo a ser gravado.
     * @param filename endereço da gravação.
     */
    public static void writeToFile(String data, String filename, Boolean flag) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, flag))) {
            writer.write(data);
            writer.flush(); 
            writer.close();
        }
    }
    
    
    
    
    
    
    
}
 