package br.ufpa.linc.util.amputation;

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
import br.ufpa.linc.util.arff.LeitorArff;
import java.io.File;
import java.security.SecureRandom;
import java.util.Hashtable;
import main.Properties;

public class Amputation {
/**
 * Class Amputation
 * Performs data amputation on a dataset
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */   
 
     private final InformacoesArff infoDataset;
     private final Hashtable<Integer,String> tokens = new Hashtable<Integer,String>();
      
     int atr = 0;
     
    public  Amputation(File dataset ){
        LeitorArff leitor = new LeitorArff(dataset);
        this.infoDataset = leitor.lerArff(); 
        int a =1;
        for(String line : this.infoDataset.getData()){
            if(line.contains(",")){
               String[] token = line.split(",");
               atr += token.length;
                for (String elemento : token) {
                    
                    tokens.put(a,elemento);
                    a++;
                }
               
                
            }
        }
    
   
    }
    public void printTokens(){
        int i = 1;
            for (int j = 0; j < tokens.size(); j++) {
                System.err.println(i+" "+tokens.get(j));
                i++;
        }
    }
    
    public int getQuantAtributos(){return atr;}
    
    public boolean insertMV(){
        double quantidade_5 = atr* Properties.Percentuais_Amputation[0];
       int amp_5 = (int)quantidade_5;
        SecureRandom r =  new SecureRandom();
       int numero = r.nextInt(atr);
       String n = ""+numero;
     boolean a = false;
       System.err.println("Numero Gerado: "+n);
        for (int i = 0; i < tokens.size(); i++) {
            if (n == tokens.get(i)) {
                tokens.replace(i, tokens.get(i), "aa");
                 a=true;
                return a ;
            }
        }
      return a;
    }
    
}
