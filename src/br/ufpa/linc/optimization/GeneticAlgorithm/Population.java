package br.ufpa.linc.optimization.GeneticAlgorithm;

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

import br.ufpa.linc.util.imputation.MissingValue;
import main.Properties;
import br.ufpa.linc.util.imputation.InfoDadosComVA;
import br.ufpa.linc.util.keel.algorithms.ConceptMostCommonValue;
import br.ufpa.linc.util.keel.algorithms.EventCovering;
import br.ufpa.linc.util.keel.algorithms.KMeansImpute;
import br.ufpa.linc.util.keel.algorithms.KNNImpute;
import br.ufpa.linc.util.keel.algorithms.MostCommonValue;
import br.ufpa.linc.util.keel.algorithms.WKNNImpute;
import br.ufpa.linc.util.mulan.Classificadores;
import br.ufpa.linc.util.mulan.MultiClassification;
import java.util.ArrayList;
import java.util.List;

public class Population {
/**
 * Class Population 
 * Manages the population of the genetic algorithm
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */

    private final InfoDadosComVA pool;
    private final ArrayList<Chromosome> individuals;
        ArrayList<Double> tFit = new ArrayList<>();
        ArrayList<Double> tFitS = new ArrayList<>();
        
   
   

    public Population(InfoDadosComVA pool) {
        this.pool = pool;
        this.individuals = new ArrayList<>();
    }

    public ArrayList<Chromosome> getIndividuals() {
        return individuals;
    }

 /**
  * O método initPopulation realiza a inicialização da população inicial do AG. na população inicial, cada indivíduo é gerado a partir 
  * da utilização de um método de imoutação simples. Ao todo, cinco indivíduos são formados utlizando os seguintes métodos: 
  * KMI, KNNI, MC, CMC, WKNNI.
  */
    public void initPopulation() {
        for (String method : Properties.imputedMethods) {
            ArrayList<String> imputedData;
            switch (method) {
                case "CMC":
                    ConceptMostCommonValue CMC = new ConceptMostCommonValue(pool.getInfo());
                    imputedData = CMC.process();
                    individuals.add(new Chromosome(extractChromosome(imputedData)));
                    break;
                case "EC":
                    EventCovering EC = new EventCovering(pool.getInfo(), Properties.EC_T, Properties.EC_Min_Change, Properties.EC_Cfactor);
                    imputedData = EC.process();
                    individuals.add(new Chromosome(extractChromosome(imputedData)));
                    break;
                case "KMI":
                    KMeansImpute KMI = new KMeansImpute(pool.getInfo(), Properties.KMI_Seed, Properties.KMI_K, Properties.KMI_Error, Properties.KMI_Max_It);
                    imputedData = KMI.process();
                    individuals.add(new Chromosome(extractChromosome(imputedData)));
                    break;
                case "KNNI":
                    KNNImpute KNNI = new KNNImpute(pool.getInfo(), Properties.KNNI_K);
                    imputedData = KNNI.process();
                    individuals.add(new Chromosome(extractChromosome(imputedData)));
                    
                    break;
                case "MC":
                    MostCommonValue MC = new MostCommonValue(pool.getInfo());
                    imputedData = MC.process();
                    individuals.add(new Chromosome(extractChromosome(imputedData)));
                    break;
                case "WKNNI":
                    WKNNImpute WKNNI = new WKNNImpute(pool.getInfo(), Properties.WKNNI_K);
                    imputedData = WKNNI.process();
                    individuals.add(new Chromosome(extractChromosome(imputedData)));
                    break;
                default:
                    System.err.println("Unknowed Imputation Method: " + method);
                    System.err.println("Check your properties file.");
                    System.exit(1);
            }

        }

    }
   
/**
* O metodo evaluate realiza o processo de avaliação dos indivíduos. 
* Para cada classificador, passado como parametro, as três medidas de 
* avaliação são verificadas (EM, ACC e HL, respectivamente) para o indivduo corrente.
* @param classificador classificador multirrótulo utilizado.
* 
* Nesse casos, os seguintes são adotados, nos experimentos: BR, HOMER, CC, MLKNN, ECC.
*/
    public void evaluate(String classificador){
    if ("BR".equals(classificador)) { 
      for (int i = 0; i < individuals.size(); i++) {
                    Double values[] = Classificadores.BR(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);
                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]); } 
    }
     if ("HOMER".equals(classificador)) {
         for (int i = 0; i < individuals.size(); i++) {
                    Double values[] = Classificadores.HOMER(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);
                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]); } 
     }  
    if ("CC".equals(classificador)) {
        for (int i = 0; i < individuals.size(); i++) {
                    Double values[] = Classificadores.CC(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);
                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]); } 
    }
    if ("MLKNN".equals(classificador)) { 
        for (int i = 0; i < individuals.size(); i++) {
                    Double values[] = Classificadores.MLKNN(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);
                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]); } 
    }
    if ("ECC".equals(classificador)) { 
        for (int i = 0; i < individuals.size(); i++) {
                    Double values[] = Classificadores.ECC(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);
                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]); } 
    }
    
    }
    
    
    /**
      * O método getBestIndividual() busca pelo melhor indivíduo na lista de indivíduos da população atual.
      * @return  o melhor individuo da população.
    */
    public Chromosome getBestIndividual() {
        if(getIndividuals().size()>0){
        Chromosome best = getIndividuals().get(0);
        for (Chromosome ind : getIndividuals()) {
            
            if(ind.getEM() > best.getEM()) {
                best = ind;
           
            }else if(ind.getEM() == best.getEM()){
                    if (ind.getACC() > best.getACC()) {
                        best = ind;
                    }else if (ind.getACC() == best.getACC()){
                         if(ind.getHL()<best.getHL()){
                            best=ind;
                         }else{
                        best=best;
                         }
                    }else{
                     best=best;
                    }
            
            }else {
                 best = best;
            }
           
        }
        return best;
    }return null;
    }    
    /**
      * O metodo getMelhorIndividual é utilizado para auxiliar o metodo de ordenação da população.
      *
      * 
    */
    public Chromosome getMelhorIndividual(List<Chromosome> in) {
        if (!in.isEmpty()) {
            int i = 0;  
            Chromosome best = getPiorIndividualdaGeracao(in);
          
            for (Chromosome ind : in) {
                if (ind.getEM()!=0) {
                    if(ind.getEM() > best.getEM()) {
                        best = ind;
                    }else if(ind.getEM() == best.getEM()){
                        if (ind.getACC() > best.getACC()) {
                            best = ind;
                        }else if (ind.getACC() == best.getACC()){
                            if(ind.getHL()<best.getHL()){
                                best=ind;
                            }else{
                                best=best;
                            }
                        }else{
                            best=best;
                        }
                    }else {
                        best = best;
                    }
                }
            }
            return best;
        }
        return null;
    }      
/**
 * O método getPiorIndividual() retorna o pior indiviiduo baseado na avaliações feitas (via EM, ACC e HL)
 * 
*/
    public Chromosome getPiorIndividual() {
         Chromosome pior = individuals.get(0);
        
        for (Chromosome ind : individuals) {
            
           if(ind.getEM() < pior.getEM()) {
                pior = ind;
           
            }else if(ind.getEM() == pior.getEM()){
                    if (ind.getACC() < pior.getACC()) {
                        pior = ind;
                    }else if (ind.getACC() == pior.getACC()){
                         if(ind.getHL()>pior.getHL()){
                            pior=ind;
                         }else{
                        pior=pior;
                         }
                    }else{
                     pior=pior;
                    }
            
            }else {
               /* Random rand = new Random();
                int randomNum = rand.nextInt(2);
                if(randomNum > 0)
                    best = ind;*/
                 pior = pior;
            }
           
        }
        return pior;
    }
/**
 * metodo auxilar utilizado no metodo getMelhorIndividual como contributo para encontrar o melhor individuo. 
 * Ambos os métodos são auxiliares de (ordenaGeracao()).
 */
    public Chromosome getPiorIndividualdaGeracao(List<Chromosome> in) {
        if (in.size()>0) {
             Chromosome pior = in.get(0);
        
        for (Chromosome ind : in) {
            
           if(ind.getEM() < pior.getEM()) {
                pior = ind;
           
            }else if(ind.getEM() == pior.getEM()){
                    if (ind.getACC() < pior.getACC()) {
                        pior = ind;
                    }else if (ind.getACC() == pior.getACC()){
                         if(ind.getHL()>pior.getHL()){
                            pior=ind;
                         }else{
                        pior=pior;
                         }
                    }else{
                     pior=pior;
                    }
            
            }else {
               /* Random rand = new Random();
                int randomNum = rand.nextInt(2);
                if(randomNum > 0)
                    best = ind;*/
                 pior = pior;
            }
           
        }
        return pior;
        }
       return null;
    }
/**
   * Captura o melhor indíduo da população passada (individualsSort) para iniciar o processo 
   * de comparação.
   * Adiciona na lista dos ordenados.
   * Remove esse individuo da lista da população passada e repete-se o processo
   * até ordenar todos.
   * Para cada indivíduo adicionado na lista é atribuido um valor fitness 
   * de acordo com sua avaliação. Tal valor será utilizado para comparação 
   * no processo de seleção.
   * 
   */
    protected ArrayList<Chromosome> ordenaGeracao(int tamanhoDaGeracao, ArrayList<Chromosome> populacao){
        ArrayList<Chromosome>  individualsSort = populacao;
        
        Chromosome[]  sort = new Chromosome[tamanhoDaGeracao];
        
        ArrayList<Chromosome> ordenado = new ArrayList();
        
            if (individualsSort.isEmpty()) {
              System.out.println("Lista Vazia");
            }else{
               
                switch(tamanhoDaGeracao){
                    case 5:
                        double valorDoFitness5 = 5.0;
                        for (int i = 0; i <tamanhoDaGeracao; i++) {
                      sort[i] = getMelhorIndividual(individualsSort);
                      sort[i].setFitness(valorDoFitness5);
                      ordenado.add(i, sort[i]);
                      individualsSort.remove(getMelhorIndividual(individualsSort));
                      valorDoFitness5 -= 1.0;

              }
                    break;
                    case 10:
                        double valorDoFitness10 = 5.0;
                        for (int i = 0; i <tamanhoDaGeracao; i++) {
                      sort[i] = getMelhorIndividual(individualsSort);
                      sort[i].setFitness(valorDoFitness10);
                      ordenado.add(i, sort[i]);
                      individualsSort.remove(getMelhorIndividual(individualsSort));
                      valorDoFitness10 -= 0.5;
              }
                    break;
                    case 25:
                        double valorDoFitness25 = 5.0;
                        for (int i = 0; i <tamanhoDaGeracao; i++) {
                      sort[i] = getMelhorIndividual(individualsSort);
                      sort[i].setFitness(valorDoFitness25);
                      ordenado.add(i, sort[i]);
                      individualsSort.remove(getMelhorIndividual(individualsSort));
                      valorDoFitness25 -= 0.2;
              }
                    break;
                    case 50:
                        double valorDoFitness50 = 5.0;
                        for (int i = 0; i <tamanhoDaGeracao; i++) {
                      sort[i] = getMelhorIndividual(individualsSort);
                      sort[i].setFitness(valorDoFitness50);
                      ordenado.add(i, sort[i]);
                      individualsSort.remove(getMelhorIndividual(individualsSort));
                      valorDoFitness50 -= 0.1;

              }
                    break;
                        default:
                            System.err.println("Error!! ");
                        
                }
              
          
          }
         
          return ordenado;
      }
 
    public ArrayList<String> extractChromosome(ArrayList<String> imputedData) {
        ArrayList<String> data = new ArrayList<>();
        for (MissingValue mv : pool.getMV()) {
            data.add(imputedData.get(mv.getLine()).split(",")[mv.getColumn()]);
        }
        return data;
    }

    public ArrayList<String> imputeChromosomeData(Chromosome individual) {
        ArrayList<String> data = new ArrayList<>();
        int index = 0; int ins=0;
        for (String instance : pool.getInfo().getData()) {
            
            if (instance.contains("?")) {
//                System.err.println("numInst: "+ins+" {"+instance+"}");
                String token[] = instance.split(",");
                String value = "";
                for (int i = 0; i < token.length-1; i++) {
                    if (token[i].trim().equals("?")) {
                        
                        value += individual.getGenes().get(index++) + ",";
                        
                    } else {
                        value += token[i] + ",";
                    }
                }
                if (token[token.length - 1].trim().equals("?")) {
                    value += individual.getGenes().get(index++);
                } else {
                    value += token[token.length - 1];
                }
                data.add(value);
            } else {
                data.add(instance);
            }
            ins++;
        }
        return data;
    }
 

     /**
      * método não utilizado (Somente para testes)
      * 
      */
    public void evaluateTestes(String classificador) {
        
        if (classificador=="BR") {  
            /**
             * Para cada geração aplica-se um valor fitness para cada indivíduo. Quanto maior 
             */
            double val;
       switch(individuals.size()){ 
           case 5:
                        val = 5.0;
                   for (int i = 0; i < individuals.size(); i++) {
                    tFit.add(i, val);
                    Double values[] = Classificadores.BR(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]);
                    

                    System.out.println(""+val);
                    System.out.println("individuo ["+i+"] fitness ["+ tFit.get(i)+"]");
                    individuals.get(i).setFitness(tFit.get(i));

                         val -= 1.0;
                } 
        break;
        
           case 10:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = MultiClassification.BR(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);

                        individuals.get(i).setFitness(tFit.get(i));
 System.out.println(""+val);
                             val -= 0.5;
                    } 
                    break;

                   
           case 25:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = MultiClassification.BR(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                        individuals.get(i).setFitness(tFit.get(i));

                             val -= 0.2;
                    } 
                    break;
               
            case 50:
                               val = 5.0;
                           for (int i = 0; i < individuals.size(); i++) {
                            tFit.add(i, val);

                            Double values[] = MultiClassification.BR(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                            individuals.get(i).setHL(values[0]);
                            individuals.get(i).setACC(values[1]);
                            individuals.get(i).setEM(values[2]);

                //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                            individuals.get(i).setFitness(tFit.get(i));

                                 val -= 0.1;
                        } 
                        break;
               
       }
            
            
            
        }else if(classificador=="HOMER"){
        
                double val;
       switch(individuals.size()){ 
           case 5:
                        val = 5.0;
                   for (int i = 0; i < individuals.size(); i++) {
                    tFit.add(i, val);

                    Double values[] = Classificadores.HOMER(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]);

        //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
        System.out.println(""+val);
                    individuals.get(i).setFitness(tFit.get(i));

                         val -= 1.0;
                } 
        break;
        
           case 10:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = Classificadores.HOMER(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);

                        individuals.get(i).setFitness(tFit.get(i));
 System.out.println(""+val);
                             val -= 0.5;
                    } 
                    break;

                   
           case 25:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = Classificadores.HOMER(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                        individuals.get(i).setFitness(tFit.get(i));

                             val -= 0.2;
                    } 
                    break;
               
            case 50:
                               val = 5.0;
                           for (int i = 0; i < individuals.size(); i++) {
                            tFit.add(i, val);

                            Double values[] = Classificadores.HOMER(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                            individuals.get(i).setHL(values[0]);
                            individuals.get(i).setACC(values[1]);
                            individuals.get(i).setEM(values[2]);

                //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                            individuals.get(i).setFitness(tFit.get(i));

                                 val -= 0.1;
                        } 
                        break;
               
       }
        
        }else if(classificador=="MLKNN"){
        
                double val;
       switch(individuals.size()){ 
           case 5:
                        val = 5.0;
                   for (int i = 0; i < individuals.size(); i++) {
                    tFit.add(i, val);

                    Double values[] = Classificadores.MLKNN(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]);

        //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
        System.out.println(""+val);
                    individuals.get(i).setFitness(tFit.get(i));

                         val -= 1.0;
                } 
        break;
        
           case 10:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = Classificadores.MLKNN(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);

                        individuals.get(i).setFitness(tFit.get(i));
 System.out.println(""+val);
                             val -= 0.5;
                    } 
                    break;

                   
           case 25:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = Classificadores.MLKNN(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                        individuals.get(i).setFitness(tFit.get(i));

                             val -= 0.2;
                    } 
                    break;
               
            case 50:
                               val = 5.0;
                           for (int i = 0; i < individuals.size(); i++) {
                            tFit.add(i, val);

                            Double values[] = Classificadores.MLKNN(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                            individuals.get(i).setHL(values[0]);
                            individuals.get(i).setACC(values[1]);
                            individuals.get(i).setEM(values[2]);

                //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                            individuals.get(i).setFitness(tFit.get(i));

                                 val -= 0.1;
                        } 
                        break;
               
       }
        
        
        
        }else if(classificador=="ECC"){
        
                double val;
       switch(individuals.size()){ 
           case 5:
                        val = 5.0;
                   for (int i = 0; i < individuals.size(); i++) {
                    tFit.add(i, val);

                    Double values[] = Classificadores.ECC (pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]);

        //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
        System.out.println(""+val);
                    individuals.get(i).setFitness(tFit.get(i));

                         val -= 1.0;
                } 
        break;
        
           case 10:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = Classificadores.ECC(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);

                        individuals.get(i).setFitness(tFit.get(i));
 System.out.println(""+val);
                             val -= 0.5;
                    } 
                    break;

                   
           case 25:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = Classificadores.ECC(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                        individuals.get(i).setFitness(tFit.get(i));

                             val -= 0.2;
                    } 
                    break;
               
            case 50:
                               val = 5.0;
                           for (int i = 0; i < individuals.size(); i++) {
                            tFit.add(i, val);

                            Double values[] = Classificadores.ECC(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                            individuals.get(i).setHL(values[0]);
                            individuals.get(i).setACC(values[1]);
                            individuals.get(i).setEM(values[2]);

                //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                            individuals.get(i).setFitness(tFit.get(i));

                                 val -= 0.1;
                        } 
                        break;
               
       }
        
        
        }else if(classificador=="CC"){
        
                        double val;
       switch(individuals.size()){ 
           case 5:
                        val = 5.0;
                   for (int i = 0; i < individuals.size(); i++) {
                    tFit.add(i, val);

                    Double values[] = Classificadores.CC (pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                    individuals.get(i).setHL(values[0]);
                    individuals.get(i).setACC(values[1]);
                    individuals.get(i).setEM(values[2]);

        //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
        System.out.println(""+val);
                    individuals.get(i).setFitness(tFit.get(i));

                         val -= 1.0;
                } 
        break;
        
           case 10:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = Classificadores.CC(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);

                        individuals.get(i).setFitness(tFit.get(i));
 System.out.println(""+val);
                             val -= 0.5;
                    } 
                    break;

                   
           case 25:
                           val = 5.0;
                       for (int i = 0; i < individuals.size(); i++) {
                        tFit.add(i, val);

                        Double values[] = Classificadores.CC(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                        individuals.get(i).setHL(values[0]);
                        individuals.get(i).setACC(values[1]);
                        individuals.get(i).setEM(values[2]);

            //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                        individuals.get(i).setFitness(tFit.get(i));

                             val -= 0.2;
                    } 
                    break;
               
            case 50:
                               val = 5.0;
                           for (int i = 0; i < individuals.size(); i++) {
                            tFit.add(i, val);

                            Double values[] = Classificadores.CC(pool.getInfo(), imputeChromosomeData(individuals.get(i)), pool.getInfo().getTipoAtributo().size() - 1);

                            individuals.get(i).setHL(values[0]);
                            individuals.get(i).setACC(values[1]);
                            individuals.get(i).setEM(values[2]);

                //         individuals.get(i).setFitness(((values[2]*5.0)+values[1]+((1-values[0])*0.5))/3);
 System.out.println(""+val);
                            individuals.get(i).setFitness(tFit.get(i));

                                 val -= 0.1;
                        } 
                        break;
               
       }
        
        
        
        
        }
         
        
    }
    public Chromosome getbestIndividualSingle (){
     if(getIndividuals().size()>0){
       Chromosome best = getIndividuals().get(0);
        
        for (Chromosome ind : getIndividuals()) {
            
           if(ind.getPrecision()> best.getPrecision()) {
                best = ind;
           
            }else if(ind.getPrecision()== best.getPrecision()){
                    if (ind.getF_measure() > best.getF_measure()) {
                        best = ind;
                    }else if (ind.getF_measure() == best.getF_measure()){
                         if(ind.getACC()>best.getACC()){
                            best=ind;
                         }else{
                        best=best;
                         }
                    }else{
                     best=best;
                    }
            
            }else {

                 best = best;
            }
           
        }
        return best;
    }return null;
    
    }
    public Chromosome getPiorIndividualSingle(){
     Chromosome pior = individuals.get(0);
        
        for (Chromosome ind : individuals) {
            
           if(ind.getPrecision() < pior.getPrecision()) {
                pior = ind;
           
            }else if(ind.getPrecision() == pior.getPrecision()){
                    if (ind.getF_measure() < pior.getF_measure()) {
                        pior = ind;
                    }else if (ind.getF_measure() == pior.getF_measure()){
                         if(ind.getACC()<pior.getACC()){
                            pior=ind;
                         }else{
                        pior=pior;
                         }
                    }else{
                     pior=pior;
                    }
            
            }else {

                 pior = pior;
            }
           
        }
        return pior;
    }
    public Chromosome getPiorIndividualdaGeracaoSingleSort(List<Chromosome> in) {

    if (in.size()>0) {
             Chromosome pior = in.get(0);
        
        for (Chromosome ind : in) {
            
           if(ind.getPrecision() < pior.getPrecision()) {
                pior = ind;
           
            }else if(ind.getPrecision() == pior.getPrecision()){
                    if (ind.getF_measure() < pior.getF_measure()) {
                        pior = ind;
                    }else if (ind.getF_measure() == pior.getF_measure()){
                         if(ind.getACC()<pior.getACC()){
                            pior=ind;
                         }else{
                        pior=pior;
                         }
                    }else{
                     pior=pior;
                    }
            
            }else {
              
                 pior = pior;
            }
           
        }
        return pior;
        }
    
    return null;
} 
    public Chromosome getMelhorIndividualdaGeracaoSingleSort(List<Chromosome> in) {
    
     if (!in.isEmpty()) {
                    int i = 0;  
                Chromosome best = getPiorIndividualdaGeracaoSingleSort(in);
          
        for (Chromosome ind : in) {
             if (ind.getPrecision()!=0) {
                       
           if(ind.getPrecision() > best.getPrecision()) {
                best = ind;
           
            }else if(ind.getPrecision() == best.getPrecision()){
                    if (ind.getF_measure() > best.getF_measure()) {
                        best = ind;
                    }else if (ind.getF_measure() == best.getF_measure()){
                         if(ind.getACC()>best.getACC()){
                            best=ind;
                         }else{
                        best=best;
                         }
                    }else{
                     best=best;
                    }
            
            }else {
              
                 best = best;
            }

        }}
        
//        }
        return best;
    }
    
return null;
} 
    protected ArrayList<Chromosome> ordenaGeracaoSingle(int tamanhoDaGeracao, ArrayList<Chromosome> ordena){

         ArrayList<Chromosome>  individualsSort = ordena;
        
        Chromosome[]  sort = new Chromosome[tamanhoDaGeracao];
        
        ArrayList<Chromosome> s = new ArrayList();
        
            if (individualsSort.isEmpty()) {
              System.out.println("Lista Vazia");
            }else{
              for (int i = 0; i <tamanhoDaGeracao; i++) {
                      sort[i] = getMelhorIndividualdaGeracaoSingleSort(individualsSort);
                      s.add(i, sort[i]);
//                    System.out.println("Indivíduo Adicionado: EM "+s.get(i).getEM()+" ACC "+s.get(i).getACC()+" HL "+s.get(i).getHL());     
                      individualsSort.remove(getMelhorIndividualdaGeracaoSingleSort(individualsSort));

              }
          
          }
    
    return s;
}
   
    
    
    
}