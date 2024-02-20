package main;

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

import br.ufpa.linc.optimization.GeneticAlgorithm.GeneticAlgorithm;
import static br.ufpa.linc.optimization.GeneticAlgorithm.GeneticAlgorithm.writeToFile;
import br.ufpa.linc.util.arff.EscritorArff;
import br.ufpa.linc.util.arff.InformacoesArff;
import br.ufpa.linc.util.arff.LeitorArff;
import br.ufpa.linc.util.imputation.MissingValue;
import br.ufpa.linc.util.imputation.InfoDadosComVA;
import br.ufpa.linc.util.keel.algorithms.ConceptMostCommonValue;
import br.ufpa.linc.util.keel.algorithms.EventCovering;
import br.ufpa.linc.util.keel.algorithms.KMeansImpute;
import br.ufpa.linc.util.keel.algorithms.KNNImpute;
import br.ufpa.linc.util.keel.algorithms.MostCommonValue;
import br.ufpa.linc.util.keel.algorithms.WKNNImpute;
import br.ufpa.linc.util.mulan.Classificadores;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
/**
 * Class main
 * @author Fabrício Almeida (fabrycio30@hotmail.com), Antonio Jacob Jr. (antoniojunior@professor.uema.br)
 * @version 1.0
     * @param args none
 */
    
    public static void main(String[] args) {
        
         int qtd_test = 5;
         String item = "benchmarking";
         switch (item) {
          case "benchmarking": 
             /**
              * Incializa o EvoImp para todos os classificadores multirrótulos.
              */
            File directoryBenchCompleto = new File("dataset-amputed");
             /**
              * Inicializa o benchmarking com todos os métodos de imputação utilizados, com excessão do evoImp, para todos os classificadores multirrótulos.
              */
             for (String ClassificadoresMultirrotulo : Properties.ClassificadoresMultirrotulo) {
                for (String imputation : Properties.imputations) {
                 bench(directoryBenchCompleto, Properties.MULTI_LABEL_CLASSIFICATION, imputation, ClassificadoresMultirrotulo);
                }
             }
           break; 
 
          case "amputacao":
              /**
               * Inicialazação do método que irá inserir Valores Ausentes na base de dados. Para isso é necessário especificar o caminho do dataset
               * e o percentual de valores ausentes a ser inserido.
               */
            File datasetsOriginais = new File("dataset-amp");
            File datasets[] = datasetsOriginais.listFiles();
             for (File dataset : datasets) {
                 for (int i = 0; i < Properties.Percentuais_Amputation.length; i++) {
                     inserirMV(dataset, Properties.Percentuais_Amputation[i]);
                 }
             }
              break;

           default:
               System.err.println("....erro");
          
     }
 
    }

   /**
    * Inicialização do processo de execução do algoritmo EVOIMP. 
    * @param directory Caminho do arquivo (.arff) com a base utilizada.
    * @param tipoDeClassificacao - Especifica o tipo de Classificação (nesse caso, multirrótulo).
    * @param classsificador  - Nome do Classificador utilizado.
    */
    private static void startEvoImp( File directory, String tipoDeClassificacao, String classsificador){
      
        EscritorArff escritorArff = new EscritorArff();
        String output = " ";
        File files[] = directory.listFiles();
        Arrays.sort(files);      
        for(File file : files){    
           if(!file.getName().endsWith(".arff")){
                continue;
            }
                
            System.err.println("\n\n\n" + file.getName());
            LeitorArff leitor = new LeitorArff(file);
            InformacoesArff info = leitor.lerArff();
            InfoDadosComVA solutionPool = new InfoDadosComVA(info);
           
           /*ALGORÍTMO GENÉTICO */
          long startTime = System.currentTimeMillis();

          GeneticAlgorithm GA = new GeneticAlgorithm(solutionPool,tipoDeClassificacao);
            output += GA.runMulti(file.getName().substring(0,file.getName().length()-5),classsificador);
            long endTime = System.currentTimeMillis();
            output += "\n"+((endTime - startTime)/60000);

        
               try {
            writeToFile(output, "summary/methods.csv", Boolean.TRUE);
            output = "";
            } catch (IOException ex) {
            Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
        
    }
  
    /**
     * O método inserirMV realiza a inserção de uma determinada quantidade de valores ausentes, na base de dados, de maneira aleatória.
     * @param dataset - Arquivo com a base de dados.
     * @param percentualMV  - Percentual de Valores Ausentes a ser inserido.
     */
    private static void inserirMV(File dataset, double percentualMV){
   
        LeitorArff lerDataset = new LeitorArff(dataset);
        InformacoesArff info = lerDataset.lerArff();
        int i = 0, j = 0;
        ArrayList<MissingValue> posicoesMV = new ArrayList<>();
        ArrayList<String> amp = new ArrayList<>();
        
        System.out.println("Name: "+ info.getnomeBase());
        
/**
 *  percorre os dados da base para verificar a quantidade de dados.
 */
        InformacoesArff in = info;
    for(String line : in.getData()){
            if(line.contains(",")){
                String token[] = line.split(",");
                j = 0;
                for(String element : token){
                j++;
                }
            }
        i++;
        }
        int tamanhoDaBase = i*j;
/**
 *   Calcula a quantidade de valores ausentes que serão inseridos na base.
 */     
        System.out.println("Tamanho da Base: "+ tamanhoDaBase);
        double percentMV = (tamanhoDaBase *percentualMV);
        int quantidadeMV = (int) percentMV;
        System.out.println("Missing Values adicionados :"+ quantidadeMV +"  ["+percentualMV+"]");
    
/**
 * Escolha de posições, de forma aleatória, na base que, posteriormente, serão lacunas com Valores Ausentes.
 * A posição (a,b) representa o indice escolhido aleatoriamente.
 */
         int index = 0;
         while(index < quantidadeMV){
             /** 
              * linha - escolhe-se uma determinada linha da base de dados aleatóriamente.
              * coluna - escolhe-se a coluna da base de dados aleatóriamente.
              * 
              */
         Random linha = new Random();
         int a = linha.nextInt(i);
         Random coluna = new Random();
         int b = coluna.nextInt(j);
           
        if (posicoesMV.size()>1) {
            for (int k = 0; k < posicoesMV.size(); k++) {
                if ((a == posicoesMV.get(k).getLine())&&(b==posicoesMV.get(k).getColumn())) {
                    
                    while ((a == posicoesMV.get(k).getLine())&&(b==posicoesMV.get(k).getColumn())) {                        
                        linha = new Random();
                        a = linha.nextInt(i);
                        
                        coluna = new Random();
                        coluna.nextInt(j);
                    
                    }
                    
                }
            }
        }
         posicoesMV.add(new MissingValue(a, b));
        index++;
    }
         int count =0;
         for (MissingValue posicoesMV1 : posicoesMV) {
             System.out.println(count+" ["+posicoesMV1.getLine()+", "+ posicoesMV1.getColumn()+"]");
        count++;
         }

/**
 * Inserção de valores ausentes nas posições geradas anteriormente.
 * Percorre novamenta a base e, onde são encontrados os indices, é adicionado(?). 
 */
    
         String linha = "";
         int indeceDatalist =0;
         for (int d = 0; d < info.getData().size(); d++) {
            String instanciaAtual = info.getData().get(d);
             if (instanciaAtual.contains(",")) {
                 String token[] = instanciaAtual.split(",");
                  for (int d1 = 0; d1 < info.getNomeAtributo().size(); d1++) {
                      String tokenAtual = token[d1].trim();
                      for (MissingValue pMV : posicoesMV) {
                        if (d == pMV.getLine() && d1==pMV.getColumn() ) {
                             tokenAtual="?";
                             indeceDatalist++;
                             posicoesMV.remove(pMV);
                            break;
                         }
                        
                      }
                         linha+= ","+tokenAtual;
                         
                     }
                  amp.add(linha.substring(1));
                  linha= "";
                  
             }else{
            amp.add(instanciaAtual);
             }
        }
         
    /**
     * Quantidade de instancias com Valores Ausentes.
     */
         int countVA=0;
         for (String li : amp) {
             if (li.contains("?")) {
                 countVA++;
             }
        }
         int quantDeInstComVAs = countVA;
         
         
//         Quantidade de Instancias
         int quantdeInstancias = info.getData().size();
        
         
         
/**
 * Gerar a base de dados com os valores ausentes adicionados.
 */
         EscritorArff montarBase = new EscritorArff();
         montarBase.EscritorArffMissing(new File("bases-amp/sg_amp_"+info.getnomeBase()+"_"+percentualMV+".arff"), info, amp);
    
/**
 * Escrever as informações em um arquivo (.txt)
 */
               String estatistica ="    "+info.getnomeBase()+    " ||   VA(%): "+percentualMV+"   ||    QuantVA:    "+ quantidadeMV+"   || quantAtributos: "+info.getNomeAtributo().size()+ " || inst: "+quantdeInstancias+"  ||  instComVA: "+quantDeInstComVAs+"  \n";
         try {
             writeToFile(estatistica, "bases-amp-info/info.txt", Boolean.TRUE);
        } catch (IOException e) {
        }
         
    }
         
  
/**
 * O método realiza a imputação e, em seguida, a classificação.
 * @param pool - Informações sobre a base de dados (com Valores Ausentes).
 * @param metodo - Método Utilizado para a imputação.
 * @param file - Nome do arquivo da base de dados.
 * @param classifier - Classificador Multirrótulo utilizado para a classificação dos dados.
 * @return  - uma string com as medidas de avaliação do classificador (EM, ACC e HL, respectivamente).
 */    
    private static String outrosMetodos (InfoDadosComVA pool, String metodo, String file, String classifier){
        
        ArrayList imputedData = new ArrayList();
         switch (metodo) {
                case "CMC":
                    ConceptMostCommonValue CMC = new ConceptMostCommonValue(pool.getInfo());
                    imputedData = CMC.process();
                    break;
                case "EC":
                    EventCovering EC = new EventCovering(pool.getInfo(), Properties.EC_T, Properties.EC_Min_Change, Properties.EC_Cfactor);
                    imputedData = EC.process();
                    break;
                case "KMI":
                    KMeansImpute KMI = new KMeansImpute(pool.getInfo(), Properties.KMI_Seed, Properties.KMI_K, Properties.KMI_Error, Properties.KMI_Max_It);
                    imputedData = KMI.process();
                    break;
                case "KNNI":
                    KNNImpute KNNI = new KNNImpute(pool.getInfo(), Properties.KNNI_K);
                    imputedData = KNNI.process();
                    break;
                case "MC":
                    MostCommonValue MC = new MostCommonValue(pool.getInfo());
                    imputedData = MC.process();
                    break;
                case "WKNNI":
                    WKNNImpute WKNNI = new WKNNImpute(pool.getInfo(), Properties.WKNNI_K);
                    imputedData = WKNNI.process();
                    break;
                default:
                    System.err.println("Unknowed Imputation Method: " + metodo);
                    System.err.println("Check your properties file.");
                    System.exit(1);
            }
        
/**
 * Geração do arquivo contendo a base de dados imputada.
 */
         EscritorArff ea = new EscritorArff();
          ea.EscritorArffMissing(new File("result/outrosMetodos/_"+metodo+"_"+file+"_.arff"), pool.getInfo(), imputedData);
             double acc,hl,em; 
             Double vals[]= new Double[3];
           switch(classifier){
            
               case "BR":
                    vals = Classificadores.BR(pool.getInfo(), imputedData, pool.getInfo().getTipoAtributo().size() - 1);
                   break;
                case "HOMER":
                    vals = Classificadores.HOMER(pool.getInfo(), imputedData, pool.getInfo().getTipoAtributo().size() - 1);
                   break;
                case "MLKNN":
                    vals = Classificadores.MLKNN(pool.getInfo(), imputedData, pool.getInfo().getTipoAtributo().size() - 1);
                    break;
                case "ECC": 
                    vals = Classificadores.ECC(pool.getInfo(), imputedData, pool.getInfo().getTipoAtributo().size() - 1);
                    break;
                case "CC": 
                    vals = Classificadores.CC(pool.getInfo(), imputedData, pool.getInfo().getTipoAtributo().size() - 1);
                    break;
                    default:
                        System.err.println("error: no option!");
           }
                       
                 em = vals[2]; acc = vals[1];  hl = vals[0];
                 String out = "EM ["+ em+"], ACC ["+acc+"], HL ["+ hl+"]\n";
                    return  out;
    }
  
    /**
     * O metodo bench roda o experimento para todos os métodos imputação simples, com exceção do EvoImp, em todos os cenários de classificação multirrótulo.
     * @param dir  - Caminho do arquivo da base dados.
     * @param tipoDeClassificacao  - Tipo de Classificação (Nesse caso, Multirrótulo).
     * @param metodo - Método de Imputação simples utilizado.
     * @param classifier - Algoritmo de classificação utilizado.
     */
    public static void bench(File dir, String tipoDeClassificacao, String metodo, String classifier){
    
       EscritorArff escritorArff = new EscritorArff();
          String methods = "";

        File files[] = dir.listFiles();
        Arrays.sort(files);   
        
        for(int i=0;i<files.length;i++){

            if(!files[i].getName().endsWith(".arff")){
                continue;
            }

            LeitorArff leitor = new LeitorArff(files[i]);
            InformacoesArff info = leitor.lerArff();
            InfoDadosComVA solutionPool = new InfoDadosComVA(info);
            String filename = files[i].getName().substring(0,files[i].getName().length()-5);
           
            methods += "dataset: "+files[i].getName().substring(0,files[i].getName().length()-5)+", "
                    + " classificador: "+ classifier
                    + ",  metodo: "+metodo+ " --> "+ outrosMetodos(solutionPool, metodo, filename,classifier);
      
               try {
            writeToFile(methods, "summary/methods.csv", Boolean.TRUE);
            } catch (IOException ex) {
            Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
            methods = "";

    }
    }
    
  
}
