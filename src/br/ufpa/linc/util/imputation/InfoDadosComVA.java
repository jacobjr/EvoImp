package br.ufpa.linc.util.imputation;

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
import java.util.ArrayList;

public class InfoDadosComVA {
/**
 * Class InfoDadosComVA
 * Checks the amount of missing data in the dataset
 * @author Fabrício Almeida (fabrycio30@hotmail.com)
 * @version 1.0
 */
    
    private final InformacoesArff info;
    private final ArrayList<MissingValue> MV;
    int quantidadeAtributos = 0;

    public InfoDadosComVA(InformacoesArff info) {
        this.info = info;
        int i = 0, j = 0;
        MV = new ArrayList<>();
        for(String line : info.getData()){
            if(line.contains("?")){
                String token[] = line.split(",");
                j = 0;
                for(String element : token){
                    if(element.toString().equals("?")){
                        MV.add(new MissingValue(i, j));
                    }
                j++;
                }
            }
        i++;
        }
    }

    public ArrayList<MissingValue> getMV() {
        return MV;
    }

    public InformacoesArff getInfo() {
        return info;
    }
    
    public int getQuantidadeAtributos(){return quantidadeAtributos;}
}
