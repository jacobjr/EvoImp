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

import br.ufpa.linc.util.arff.InformacoesArff;
import br.ufpa.linc.util.imputation.MissingValue;
import br.ufpa.linc.util.imputation.InfoDadosComVA;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import main.Properties;

public class Operator {
/**
 * Class Operator 
 * Implements genetic operators
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */
    
    
    
    /**
     * O método realiza a seleção dos pais via torneio. Dois individuos são sorteados
     * aleatoriamente e, o que possuir maior fitness, é selecionado como o pai1. repete-se o 
     * processo para encontrar o pai2.
     * @param population
     * @return 
     */
    public static Chromosome[] selection(Population population) {
        Chromosome parent[] = new Chromosome[2];
        switch (Properties.Selection) {
            case "Tournament":
                ArrayList<Chromosome> selections = new ArrayList<>();
                for (int i = 0; i < Properties.Tournament_Size; i++) {
                   
                        int n = new Random().nextInt(population.getIndividuals().size());
                      
                        selections.add(population.getIndividuals().get(n));
                        population.getIndividuals().remove(n);   
                }
                int best = 0;
                for (int i = 1; i < Properties.Tournament_Size; i++) {
                    if (selections.get(i).getFitness()>= selections.get(best).getFitness()) {
                        best = i;
                    }
                }
//               O pai recebe o maior selecionado via torneio
                parent[0] = selections.remove(best);
                
                
                for (Chromosome ind : selections) {
                    population.getIndividuals().add(ind);
                }
                selections.clear();
                
                
                for (int i = 0; i < Properties.Tournament_Size; i++) {
                    int n = new Random().nextInt(population.getIndividuals().size());
                    selections.add(population.getIndividuals().remove(n));
                }
                best = 0;
                for (int i = 1; i < Properties.Tournament_Size; i++) {
                    if (selections.get(i).getFitness() >= selections.get(best).getFitness()) {
                        best = i;
                    }
                }
                parent[1] = selections.remove(best);
                for (Chromosome ind : selections) {
                    population.getIndividuals().add(ind);
                }
                
                population.getIndividuals().add(parent[0]);
                population.getIndividuals().add(parent[1]);
                break;
                
            default:
                System.err.println("Unknowed Selection Operator: " + Properties.Selection);
                System.err.println("Check your properties file.");
                System.exit(1);
        }
        return parent;
    }
/**
 * O método crossover realiza o cruzamento de 2-pontos. Neste, dois pais escolhidos, de
 * maneira aleatória, e gerarão dois filhos através do cruzamento de seus genes. 
 * @return array com os dois filhos
 */
    static Chromosome[] crossover(Chromosome[] parent) {
        Chromosome chieldren[] = new Chromosome[2];
        switch (Properties.Crossover) {
            case "N-Point":
                ArrayList<String> gene1 = new ArrayList<>();
                ArrayList<String> gene2 = new ArrayList<>();
                int begin = 0;
                boolean flag = true;
                for (int i = 0; i < Properties.N_Point; i++) {
                int n = new Random().nextInt(parent[0].getGenes().size()- 
                        (Properties.N_Point - i - 1) - begin);
                    while (gene1.size() <= n) {
                        if (flag) {
                            gene1.add(parent[0].getGenes().get(gene1.size()));
                            gene2.add(parent[1].getGenes().get(gene2.size()));
                        } else {
                            gene1.add(parent[1].getGenes().get(gene1.size()));
                            gene2.add(parent[0].getGenes().get(gene2.size()));
                        } 
                    }
                    begin = n + 1;
                    flag = !flag;
                }
                while (gene1.size() < parent[0].getGenes().size()) {
                    if (flag) {
                        gene1.add(parent[0].getGenes().get(gene1.size()));
                        gene2.add(parent[1].getGenes().get(gene2.size()));
                    } else {
                        gene1.add(parent[1].getGenes().get(gene1.size()));
                        gene2.add(parent[0].getGenes().get(gene2.size()));
                    }
                }
                chieldren[0] = new Chromosome(gene1);
                chieldren[1] = new Chromosome(gene2);
                break;
            default:
                System.err.println("Unknowed Crossover Operator: " + Properties.Crossover);
                System.err.println("Check your properties file.");
                System.exit(1);
        }
        return chieldren;
    }
/**
 * Esse método realiza a mutação de 20% dos indivíduos da população passada. 
 * Em cada indivíduo escolhido, os genes que passaram pelo processo de imputação,
 * devido a ausencia de dados, serão substituidos por valores escolhidos a partir de um conjunto de 
 * valores candidatos, formados por todos os possiveis valores de cada atributo.
 * @param next população trabalhada
 * @param informacoesDoIndividuo informações da base de dados trabalhada (cada individuo é
 * uma base)
 */
    public static void mutation(Population next,InfoDadosComVA informacoesDoIndividuo){
              Chromosome c,mutado;
              ArrayList<String> data = new ArrayList<>();              
              /**
                * n escolhe-se um valor aleatoriamente. Tal valor será o indice do indivíduo para o qual será feita a mutação. 
                */ 
              int n = new Random().nextInt(next.getIndividuals().size());
               
               /**
                *  c é o indivíduo a ser mutado, escolhido de forma aleatória.
                */
               c =  next.getIndividuals().get(n);
                /**
                * testa se o indivíduo escolhido não é o melhor individuo da população (Elitista). Caso seja escolhe outro individuo. Lembrando 
                * o individuo elitista sempre é passado para a proxima geração por isso não pode ser multado. 
                * Após essa escolha o individuo é removido da população, voltando após o processo de mutação.
                */
                  if (c == next.getIndividuals().get(0)) {
                   while(c == next.getIndividuals().get(0)){ 
                       n = new Random().nextInt(next.getIndividuals().size());
                       c =  next.getIndividuals().get(n);}
                         }
               c = next.getIndividuals().remove(n);
               
               /**
                * informações do indivíduo. Lembrando que um individuo é um base de dados completa.
                */
                InformacoesArff info = informacoesDoIndividuo.getInfo();
                int dadosDaBase = info.getData().size();
                int atributes= info.getNomeAtributo().size();
                
                /**
                * Cria uma lista de conjunto de soluções. Cada atributo da base tem seu conjunto de soluções.
                * Percorre-se toda a base de dados. Na instancia que houver dados ausentes checa-se qual é o atributo e, feito isso, escolhe-se 
                * um valor aleatório do conjunto de soluções daquele determinado atributo. Feito isso adiciona-se o novo individuo formado na 
                * população.
                */
                List<String>[] solutions =  conjuntoSolucoes(informacoesDoIndividuo);
            for (int i = 0; i < dadosDaBase; i++) {
            String instance = info.getData().get(i);
            
                if(instance.contains("?")){
                    String tokens[] = instance.split(",");
                    String line = "";
                    for (int j = 0; j < atributes; j++) {
                     String value = tokens[j].trim();
                     if(value.equals("?")){
                         Random r = new Random();
//                         solutions[j].size();
                         int NumGerado = r.nextInt(solutions[j].size());
                       String correspondenteDoGerado = solutions[j].get(NumGerado);
                         if (correspondenteDoGerado.contains("?")) {
                             while (correspondenteDoGerado.contains("?")) {
                             r = new Random();
                             NumGerado = r.nextInt(solutions[j].size());
                             correspondenteDoGerado = solutions[j].get(NumGerado);
                         }
                         }
  
                    
                               value = correspondenteDoGerado;
//                               System.out.println("gerado: "+ NumGerado+", value: "+ correspondenteDoGerado);
                        }
                    line += ","+value;
                    }
                    data.add(line.substring(1));

            }else{
            data.add(instance);
            }
            
        }
               
     mutado = new Chromosome(next.extractChromosome(data));

     next.getIndividuals().add(n,mutado);
     
    }
    /**
     * O método realiza a mutação em 20% dos genes do individuo.
     * @param nG População da qual pertence o indivíduo que será mutado.
     * @param individuo individuo que sofrerá mutação
     * @param infosDaBase informações gerais da base (lembra-se que cada individuo é um base).
     * @return individuo mutado.
     */
    public static Chromosome mutation2 (Population nG, Chromosome individuo, InfoDadosComVA infosDaBase, double tx_mutation){
        ArrayList<String> data = new ArrayList<>();
              Chromosome ind = individuo;
              
              /**
               * Informações da base
               */
              InformacoesArff info = infosDaBase.getInfo();
              int dadosDaBase = info.getData().size();
              int atributes= info.getNomeAtributo().size();
              ArrayList<MissingValue> posicoesVA = new ArrayList<>();
              posicoesVA = infosDaBase.getMV();
              /**
               * informa a quantidade de mutações.
               */
              int quantidadeDeMutacoes = (int) ( posicoesVA.size()* tx_mutation);
              int indice = 0;
              int indx = 0;
              Random r = new Random();
              int numG = r.nextInt(posicoesVA.size());
                /**
                * Cria uma lista de conjuntos soluções. 
                * Cada atributo da base tem seu conjunto solução.
                * Percorre-se toda a base de dados. Na instancia que houver dados ausentes checa-se qual é o atributo e, feito isso, escolhe-se 
                * um valor aleatório do conjunto de soluções daquele determinado atributo. Feito isso adiciona-se o novo individuo formado na 
                * população.
                */
                List<String>[] solutions =  conjuntoSolucoes(infosDaBase);
                
            for (int i = 0; i < dadosDaBase; i++) {
            String instance = info.getData().get(i);
                if(instance.contains("?")){
                    String tokens[] = instance.split(",");
                    String line = "";
                    for (int j = 0; j < atributes; j++) {
                     String value = tokens[j].trim();
                     if(value.equals("?")){
                         if (indx==numG) {
                             Random ra = new Random();
//                         solutions[j].size();
                         int Gerado = ra.nextInt(solutions[j].size());
                       String correspondenteDoGerado = solutions[j].get(Gerado);
                         if (correspondenteDoGerado.contains("?")) {
                             while (correspondenteDoGerado.contains("?")) {
                             r = new Random();
                             Gerado = r.nextInt(solutions[j].size());
                        correspondenteDoGerado = solutions[j].get(Gerado);
                         }
                         }
                          value = correspondenteDoGerado;
                               indx++;
                         }else{
                         value = ind.getGenes().get(indx++);
                         }
                         
//                               System.out.println("gerado: "+ NumGerado+", value: "+ correspondenteDoGerado);
                        }
                    line += ","+value;
                    }
                    data.add(line.substring(1));

            }else{
            data.add(instance);
            }
            
                if (indice<quantidadeDeMutacoes-1) {                    
                    numG = r.nextInt(posicoesVA.size()); 
                }
                  
              
                  indice++;    
        }
     Chromosome mutado = new Chromosome(nG.extractChromosome(data));
       return mutado;
        }
        
              
    
    
   /**
    * conjunto de soluções
    * Cada atributo da base de dados tem um conjunto de soluções, formado por todas as variaveis aceitas para aquele 
    * determinado atributo.
    * @param info informações da base de dados que representa o individuo analisado.
    * @return uma List de arrayLists. Essa lista contem os conjuntos de soluções de todos os 
    * atributos da base de dados atual.
    */ 
    public static List<String>[] conjuntoSolucoes(InfoDadosComVA info){
        /**
         * @param quantvariaveis verifica a quantidade de atributos da base.
         * @param s é uma lista que vai conter todos os arrayLists de soluções, cada arrayList é um conjunto de solução de uma variavel.
         */
        int quantvariaveis = info.getInfo().getNomeAtributo().size();
        List<String>[] s = new List[quantvariaveis];
        for (int i = 0; i < quantvariaveis; i++) {
            s[i] = new ArrayList<>();
        }
        /**
         * Percorre toda a base, instancia por instancia. Para cada instancia é verificado a variavel correspondente e colocado no seu 
         * devido arrayList.
         */
        for (int i = 0; i < info.getInfo().getData().size(); i++) {
             String instance = info.getInfo().getData().get(i);
           
             if (instance.contains(",")) {
                 String tokens[] = instance.split(",");
                    for (int j = 0; j < quantvariaveis; j++) {
                        String val = tokens[j].trim();
                        s[j].add(val);
   
                 }
                    
                
            }
        }
        return s;}
}
