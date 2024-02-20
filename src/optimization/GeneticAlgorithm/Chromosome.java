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

import java.util.ArrayList;

public class Chromosome {
/**
 * Class Chromosome 
 * Genetic algorithm chromosome structure
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */
    
    private ArrayList<String> genes;
    private double fitness;
    private double em;
    private double hl;
    private double acc;
    
    private double auc; /*Area sob a curva ROC*/
    private double rmse; /*root mean squa*/
    private double mae; /*Absolute mean error*/
    
    private double precision;
    private double f_measure;

    public Chromosome(ArrayList<String> genes) {
        this.genes = genes;
        this.fitness = 0.0;
        this.hl = 0.0;
        this.em = 0.0;
        this.acc = 0.0;
        
        this.auc = 0.0;
        this.mae = 0.0;
        this.rmse = 0.0;
        
        this.precision = 0.0;
        this.f_measure = 0.0;

    }

    public ArrayList<String> getGenes() {
        return genes;
    }

    public void setGenes(ArrayList<String> genes) {
        this.genes = genes;
    }

    public double getFitness() {
        return fitness;
    }
    
    public void setEM(double em) {
        this.em = em;
    }

    public double getEM() {
        return em;
    }
    
    public void setHL(double hl) {
        this.hl = hl;
    }

    public double getHL() {
        return hl;
    }
    
    public void setACC(double acc) {
        this.acc = acc;
    }

    public double getACC() {
        return acc;
    }
    
//    public double getObjectiveA() {
//        return objectiveA;
//    }
//    
//    public double getObjectiveB() {
//        return objectiveB;
//    }

    public void setFitness(double fitness) {

      
        this.fitness = fitness;
    }
    
//    public void setFitness(double objectiveA, double objectiveB, double tie) {
//        if(objectiveA > objectiveB)
//            this.fitness = objectiveA;
//        else if(objectiveB > objectiveA)
//            this.fitness = objectiveB;
//        else
//            this.fitness = tie;
//    }
    
//    public void setObjectiveA(double objective) {
//        this.objectiveA= objective;
//    }
    
//        this.objectiveB= objective;
//    }

    public double getAuc() {
        return auc;
    }

    public void setAuc(double auc) {
        this.auc = auc;
    }

    public double getRmse() {
        return rmse;
    }

    public void setRmse(double rmse) {
        this.rmse = rmse;
    }

    public double getMae() {
        return mae;
    }

    public void setMae(double mae) {
        this.mae = mae;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getF_measure() {
        return f_measure;
    }

    public void setF_measure(double f_measure) {
        this.f_measure = f_measure;
    }
    
    
    
}
