# GAMultImp
Multiple Imputation of Multi-label Classification Data With a Genetic Algorithm

***
#### **About the paper**
> Article submitted to the journal XXXXXX, in the year 2023. This work proposed an method in multi-label learning and evaluated its performance using six synthetic databases, considering various missing values distribution scenarios.


#### **Authors (original paper)**
<table>
  <tr>
    <td align="center"><a href="http://lattes.cnpq.br/4510520291728075"><img style="width: 99px; height:auto;" src="http://servicosweb.cnpq.br/wspessoa/servletrecuperafoto?tipo=1&id=K4125840Z1" width="100px;" alt=""/><br /><sub><b>Antonio F. L. Jacob Jr.</b></sub></a><br /><sub><b>UEMA | UFMA</b></sub></a></td>
    <td align="center"><a href="http://lattes.cnpq.br/5415578583738595"><img style="width: 99px; height:auto;" src="https://servicosweb.cnpq.br/wspessoa/servletrecuperafoto?tipo=1&id=K4367095H1" width="100px;" alt=""/><br /><sub><b>Fabrício A. do Carmo</b></sub></a><br /><sub><b>UEMA</b></sub></a></td>
    <td align="center"><a href="http://lattes.cnpq.br/4073088744952858"><img style="width: 100px; height:auto;" src="https://lincproguema.com/misc/adamo.png" width="100px;" alt=""/><br /><sub><b>Ádamo L. Santana</b></sub></a><br /><sub><b> Fuji Electric Co., Japan</b></sub></a></td>
    <td align="center"><a href="http://lattes.cnpq.br/0660692009750374"><img style="width: 100px; height:auto;" src="http://servicosweb.cnpq.br/wspessoa/servletrecuperafoto?tipo=1&id=K4509325E9" width="110px;" alt=""/><br /><sub><b>Ewaldo Santana</b></sub></a><br /><sub><b>UEMA | UFMA</b></sub></a></td>
    <td align="center"><a href="http://lattes.cnpq.br/8320014491229434"><img style="width: 108px; height:auto;" src="http://servicosweb.cnpq.br/wspessoa/servletrecuperafoto?tipo=1&id=K4450672H1" width="100px;" alt=""/><br /><sub><b>Fábio M. F. Lobato</b></sub></a><br /><sub><b>UFOPA | UEMA</b></sub></a></td>
  </tr>
<table>
  
  
<br>

# Abstract

The massive amount of available data has aroused the interest of companies and academia in exploiting these data to gain competitive advantages. In this context, missing data is a problem that deserves attention due to its prevalence, and most data analysis techniques cannot deal with it, harming the results' reliability. Despite the relevance of this problem, only a few studies investigating Multilabel Classifications (MLC) with missing data are observed. Multilabel learning is considered an emerging and promising research topic because of the growing number of new applications, such as the semantic classification of videos and images, music categorization, and medical diagnostics. One of the most usual missing data treatment methods is data imputation, which seeks plausible values to fill in the missing ones. In this scenario, we propose a novel imputation method based on a multi-objective genetic algorithm for optimizing multiple data imputations called EvoImp. We applied the proposed method in multilabel learning and evaluated its performance using six synthetic databases, considering various missing values distribution scenarios. The method was compared with other state-of-the-art imputation strategies. The results proved that the proposed method outperformed the baseline in all the scenarios, representing a feasible solution to missing data treatment for multilabel learning. Following open-science principles, the source codes and datasets are publicly available in a GitHub repository.

~~~

------------------------------------------
***Directory description***
------------------------------------------
~~~

- datasets      --> It contains the databases used in the experiments presented in the article.
- src          --> It contains the Java code of GAMultImp and the libraries used.
- supp          --> It has additional data cited in the article, such as baseline tests.
  
~~~
  
  
---
