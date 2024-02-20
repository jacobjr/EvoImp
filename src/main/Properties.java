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

import java.util.Random;


public class Properties {
/**
 * Class Properties 
 * Used to set the values of the parameters used
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */
    
    public static long Weka_Seed = new Random().nextLong();
    
    public static String Crossover = "N-Point";
    
    public static int N_Point = 2;
            
    public static String Selection = "Tournament";
    
    public static int Tournament_Size = 2;
    
    public static int Elitism = 1;
    
    public static int Generations[] = {5, 10, 25, 50, 25, 10, 5};
    
    public static String imputedMethods[] = {"KMI", "KNNI", "MC", "CMC","WKNNI"};
    
    public static String imputations[] = {"KMI", "KNNI", "MC", "CMC", "WKNNI"};
    
    public static String ClassificadoresMultirrotulo[] = {"BR", "HOMER","CC","MLKNN","ECC"};
    
    public static double EC_T = 0.05;
    
    public static int EC_Min_Change = 0;
    
    public static double EC_Cfactor = 1;
    
    public static long KMI_Seed = new Random().nextLong();
      
    public static int KMI_K = 10;
    
    public static double KMI_Error = 100;
    
    public static int KMI_Max_It = 100;
    
    public static int KNNI_K = 10;
    
    public static int WKNNI_K = 10;
    
    public static double Percentuais_Amputation[] = {0.05, 0.10, 0.15, 0.20, 0.25, 0.30};
    
    public final static String[] nomeClassificadorSingle = {"J48", "PART", "RIPPER", "LWL", "UmBk", "TresBk",
        "NAIVE", "CSVM", "nuSVM", "SMO", "Logistic","RBFNet", "MLP", "LBR"};
    
    public static String TipoClassificacao[] = {"single","multi"};
    public static String MULTI_LABEL_CLASSIFICATION = "multi";    
    
}
