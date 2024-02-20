package util.arff;

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

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class EscritorArff {
/**
 * Class EscritorArff
 * Create an Arff file
 * @author Fábio Lobato (fabio.lobato@ufopa.edu.br)
 * @version 1.0
 */    

    public void EscritorArffMissing(File arquivo, InformacoesArff info, ArrayList<String> dataMissing) {
        try {
            PrintWriter pw;
            pw = new PrintWriter(arquivo);
            for (Iterator<String> it = info.getCabecalho().iterator(); it.hasNext();) {
                String string = it.next();
                pw.println(string);
             }
            pw.println("@RELATION "+ info.getnomeBase());
             for (Iterator<String> it = info.getLinhaAtributo().iterator(); it.hasNext();) {
                String string = it.next();
                pw.println(string);
            }
            pw.println("@data");
            for (Iterator<String> it = dataMissing.iterator(); it.hasNext();) {
                String string = it.next();
                pw.println(string);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}