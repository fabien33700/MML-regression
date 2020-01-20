# Ingénierie Dirigée par les Modèles

## Travaux Pratiques n°6 - Multi-Machine Learning

_Guillaume Collet – Valentin Duval – Yoann Even – Fabien Le Houëdec_

---

# Rapport de synthèse

## Plan du rapport : 

*   Emplacement du code du projet
*   Note sur le code mise en oeuvre : l’abstraction autour des compilateurs
*   Démarche d’implémentation : le langage R
*   **Le classement des différents algorithmes**
*   Emplacement des livrables attendus


## 

---



## Emplacement des livrables attendus 

Dépôt Github de notre projet : [https://github.com/fabien33700/MML-regression](https://github.com/fabien33700/MML-regression)

Le code à prendre en compte se trouve sur la branche **master**. Le code des compilateurs et des tests associés se situe dans le projet<code><em> org.xtext.example.mml.tests</em></code>.

Les classes `RCompilerTest `et `ScikitLearnCompilerTest` permettent de tester respectivement nos compilateurs pour le langage R et pour Scikit-learn/Python.

**Installation sous Eclipse :**



1. Récupérer le projet avec **git**.
2. Sous Eclipse, importer le projet via le menu _File > Import…_, puis sélectionner _General > Existing projects into workspace_
3. Sélectionner les 5 projets disponibles pour importation depuis le dépôt
4. Se rendre dans le projet <code><em>org.xtext.example.mml</em></code>, dans le dossier <em>src</em>/, package <code><em>org.xtext.example.mydsl</em></code>, puis clic droit sur le fichier de grammaire<code><em> Mml.xtext</em></code>, puis en cliquant sur <em>Run As > Generate Xtext Artifacts</em>




## Note sur le code mise en oeuvre : l’abstraction autour des compilateurs

Étant donné la structure relativement commune des programmes pour les différentes cibles, nous avons réalisé une abstraction nous permettant de définir plus facilement des compilateurs, en déportant le code commun dans des interfaces et des classes abstraites.

Cette abstraction s’articule autour de 3 grands axes : 



*   La définition d’une structure commune aux programmes générés
*   Le déport du code commun à tous compilateurs dans une classe abstraite `AbstractMmlCompiler`
*   La mise en place du PC Façade, pour fournir un jeu de classes du modèle de données plus aisément manipulable que les classes de modèles générées par Xtext, notamment avec : 
    *   utilisation d’énumération Java plutôt que l’opérateur `instanceof `pour la différenciation des algorithmes, des frameworks, des métriques, …
    *   accès aisé à la liste des features et du label, respectivement grâce aux méthode `getPredictors()` et `getPredictive(`). La méthode` hasAllPredictors()` indique la sélection de toutes les colonnes.

Les programmes générées par nos compilateurs possèdent la structure suivante : 



*   la liste des imports des bibliothèques et autres dépendances
*   le chargement des données
*   le mélange (shuffling)
*   l’extraction et la sélection des colonnes, en fonction de la formule R éventuellement fournie

    dans le programme MML

*   la mise en oeuvre de la stratification (training/test, cross-validation)
*   l’appel de la fonction correspondant à l’algorithme sélectionné
*   l’appel aux métriques et l’écriture des résultats

Cette structure est définie dans la couche d’abstraction par l'interface `ProgramStructure`. L’interface Compiler hérite de `ProgramStructure` et des opérations utilitaires pour :



*   récupérer le modèle associée au compilateur
*   récupérer d’un seul coup l’intégralité du programme 
*   indiquer que le compilateur supporte certaines métriques ou algorithmes et cible certains framework

La classe abstraite `AbstractMmlCompiler` implémente cette interface et fournit des méthodes utilitaires pour la manipulation de chaîne de caractères ainsi que le mécanisme pour stocker le code généré. Elle fournit aussi une méthode` isCompatibleWithModel()` qui vérifie que le compilateur : 



*   cible le framework indiqué dans le programme MML via la clause `mlframework`
*   prenne en charge l'algorithme spécifié
*   prenne en charge les métriques spécifiées

La fabrique `CompilerFactory` possède une méthode `findTargetCompiler()` qui accepte une instance du modèle généré par Xtext en argument, et va automatiquement : 



*   Appeler la façade pour adapter le modèle
*   Trouver, à partir de tous les compilateurs disponibles, un compilateur compatible avec le programme MML fourni en entrée
*   Retourner l’instance du bon compilateur associé au modèle de données préalablement adapté par la façade.

Les implémentations des compilateurs pour les différentes cibles se situent dans le package <code><em>core.compilers</em></code> du projet<code> <em>org.xtext.example.mml.tests</em></code>.



---



## Implémentation en langage R

Decision Tree est un algorithme de machine learning utilisé tant pour les tâches de classification que pour les tâches de régression.

En ce qui concerne l'écriture du compilateur MML vers R, et face à la difficulté d'apprentissage de ce dernier, le parti pris a été de s'appuyer sur un exemple tiers expliquant les étapes à implémenter en R, en vue de traiter un jeu de données à l'aide de l'algorithme du _Decision Tree_ notamment.

Cet exemple s'appuie sur le jeu de données "_titanic_" qui recense une partie des passagers du tristement célèbre paquebot qui fit naufrage en 1912. Le dataset utilisé est à cette adresse :

**[https://raw.githubusercontent.com/guru99-edu/R-Programming/master/titanic_data.csv](https://raw.githubusercontent.com/guru99-edu/R-Programming/master/titanic_data.csv)**

Ce jeu de données s'appuie sur 1309 observations (entrées) avec 13 variables ou attributs pour chacune d'elles. Les entrées sont classées selon l'attribut x (id). Dans le détail, les attributs sont les suivants :


| ***x***                               | Un id unique au format entier (numéro de ligne de l'entrée). Ici, les valeurs vont de 1 à 1309.                                                       |
|---------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| ***pclass***                          | La "classe" de la cabine dans laquelle le passager en question logeait lors de cette croisière. Les valeurs vont de 1 à 3.                            |
| ***survived***                        | Au format entier de valeur 0 ou 1. Cet attribut indique si la personne concernée à survécu au naufrage (1) ou pas (0).                                |
| ***name***                            | Il s'agit de la concaténation, dans l'ordre suivant, du nom de la personne, de son titre, de son ou ses prénom(s).                                    |
| ***sex***                             | Cette chaîne de caractères prend comme valeur male ou female.                                                                                         |
| ***age***                             | Cet entier indique l'âge de la personne.                                                                                                              |
| ***sibsp*** (siblings/spouses aboard) | Cet entier indique combien de frères, soeurs ou conjoint de la personne en question étaient aussi à bord du Titanic.                                  |
| ***parch*** (parents/children aboard) | Cet entier indique combien de parents ou enfants de la personne en question étaient aussi à bord du Titanic.                                          |
| ***ticket***                          | Indique le numéro du billet de la personne qui lui a permis de monter à bord.                                                                         |
| ***fare***                            | Ce décimal indique le prix payé par la personne pour monter à bord (en livres sterling).                                                              |
| ***cabin***                           | Cette chaîne de caractères correspond au numéro de la cabine de la personne.                                                                          |
| ***embarked***                        | Cette lettre indique le port d'embarquement de la personne (C = Cherbourg; Q = Queenstown; S = Southampton).                                          |
| ***home.dest***                       | Cette chaîne de caractères indique pour chaque personne, la localisation de son lieu d'habitation et sa destination prévue à l'issue de la croisière. |

Le programme à écrire a pour but de traiter les données en 7 étapes :

1. **Importer et mélanger les données**
```
library(caret)
library(e1071)
library(rpart)
library(rpart.plot)
library(dplyr) 
set.seed(678)
path <- './titanic.csv'
titanic <-read.csv(path)
shuffle_index <- sample(1:nrow(titanic))
titanic <- titanic[shuffle_index, ]
```

2. **Nettoyer le jeu de données**

```
clean_titanic <- titanic % > %
select(-c(home.dest, cabin, name, X, ticket)) % > %
   	mutate(pclass = factor(pclass, levels = c(1, 2, 3), labels = c('Upper', 'Middle', 'Lower')),
   	survived = factor(survived, levels = c(0, 1), labels = c('No', 'Yes'))) % > %
na.omit()
glimpse(clean_titanic)
```

3. **Créer un jeu d'entraînement et un jeu de test**

```
create_train_test <- function(data, size = 0.8, train = TRUE) {
	n_row = nrow(data)
	total_row = size * n_row
	train_sample <- 1: total_row
	if (train == TRUE) {
    	return (data[train_sample, ])
	} else {
    	return (data[-train_sample, ])
	}
}
data_train <- create_train_test(titanic, 0.8, train = TRUE)
data_test <- create_train_test(titanic, 0.8, train = FALSE)
```

4. **Construire le modèle de prédiction**

```
fit <- rpart(survived~., data = data_train, method = 'class')
rpart.plot(fit, extra = 106)
```

5. **Réaliser la prédiction**

```
predict_unseen <- predict(fit, data_test, type = 'class')
table_mat <- table(data_test$survived, predict_unseen)
```

6. **Évaluer la performance du modèle**

```
accuracy_Test <- sum(diag(table_mat)) / sum(table_mat)
print(paste('Accuracy for test', accuracy_Test))
```

7. **Affiner les hyper-paramètres _(étape non prise en compte dans le test du compilateur R)_**

```
accuracy_tune <- function(fit) {
	predict_unseen <- predict(fit, data_test, type = 'class')
	table_mat <- table(data_test$survived, predict_unseen)
	accuracy_Test <- sum(diag(table_mat)) / sum(table_mat)
	accuracy_Test
}
control <- rpart.control(minsplit = 4,
	minbucket = round(5 / 3),
	maxdepth = 3,
	cp = 0)
tune_fit <- rpart(survived~., data = data_train, method = 'class', control = control)
accuracy_tune(tune_fit)
```



 

Ce sont à partir de ces fragments de codes que nous écrivons nos compilateurs. Nous nous appuyons sur l’abstraction décrite plus haut pour simplifier ce processus et pour n’avoir qu’à développer la partie génération de code spécifique à la cible. 

Pour chaque classe de compilateur, nous créons une classe de test associée qui contient plusieurs cas de tests : ces cas consistent à vérifier que pour un programme MML en entrée, le compilateur produise bien le code attendu. Nous avons choisi de stocker ces programmes attendues dans un dossier _resources/_ du projet de les coder en dur dans le code Java des classes de tests, ceci pour éviter les erreurs liées aux concaténations, à l’échappement de caractères, etc. 

Nous aurions pu utiliser le langage Xtend pour coder nos classes de tests et utiliser ainsi la fonctionnalité des chaînes multi-lignes. Nous avons cependant choisi, pour des raisons de simplicité, de ne pas mélanger les langages pour notre code de génération et de test.

Les classes de test héritent elles-mêmes d’une classe abstraite `AbstractCompilerTest`, qui propose les opérations communes pour ces classes : 

*   Une méthode pour compiler le code MML en un méta-modèle
*   Une méthode pour lire les programmes attendus à partir du dossier _resources/_

---

## Le classement des différents algorithmes

**Q.1: Ecrire un programme qui, étant donné un programme MML, retourne le classement des frameworks+algorithmes de machine learning.**

Avec le jeu de données Boston et en cherchant à calculer le “mdev” en fonction de toutes les autres variables, en utilisant la _cross-validation_ et utilisant _Scikit-learn_ : 


|                                 | *Gradient Boosting* | *Random Forest* | *Decision Tree* |
|---------------------------------|-----------------|-----------------|---------------------|
| **M**ean **S**quared **E**rror  | `-22`           | `-25`           | `-36`               |
| **M**ean **A**bsolute **E**rror | `-3,0`          | `-3,4`          | `-4,1`              |


**Q.2: Utiliser d’autres jeux de données et d’autres frameworks/algorithmes pour tester votre solution ?**

Afin de comparer avec un autre jeu de donnée nous avons utilisé une approximation de la MAPE, nous donc aussi utilisé le jeu de données titanic en cherchant à déterminer l’attribut survived. Ici pour chaque algorithme nous avons utilisé la méthode avec les jeux de données séparés en données de test et d’entraînement (30% de test et 70% d’entraînement).


|              | ***Decision Tree*** | ***Random Forest*** | ***Gradient Boosting*** |
|--------------|---------------------|---------------------|-------------------------|
| MAPE Boston  | `4,7%`              | `5,5%`              | `7,2%`                  |
| MAPE Titanic | `40,1%`             | `40,2%`             | `42,3%`                 |


L’ordre reste bien le même, on peut remarquer que pour _titanic, _la MAPE est globalement supérieure à 40%, cela s’explique par le fait que nous n’avons pas réussi à intégrer les données sous formes de chaînes de caractères dans les calculs (car nous ne connaissions pas bien les fonctionnalités de Scikit-learn). Certaines informations pouvant être cruciales sont donc manquantes (sexe, destination, etc.).

Nous n’avons pas pu utiliser un autre framework car nos avancées en R n’ont pas été suffisantes pour faire tourner plus d’un programme, et que nos tentatives d’apprentissage de _TensorFlow _et de Weka se sont avérées infructueuses malgré le temps passé à travailler sur ces cibles.

**Q.3: Adapter/paramétrer votre programme de la question Q1 pour pouvoir établir un classement en fonction du temps d’exécution**

Les différents temps d'exécutions en secondes (avec *Scikit-learn*) : 

|              | *Gradient Boosting* | *Random Forest* | *Decision Tree* |
|--------------|---------------------|-----------------|-----------------|
| MSE + MAE    | `1.646`             | `1.537`         | `1.233`         |
| MAPE Boston  | `1.291`             | `1.250`         | `1.224`         |
| MAPE Titanic | `1.338`             | `1.311`         | `1.302`         |


**Q.4: Conduire une évaluation rigoureuse des solutions de machine learning grâce à votre infrastructure MML.**


_**a.** Sur vos jeux de données, quel framework + algorithme est le mieux classé (en comparaison d’autres frameworks) en termes de temps d’exécution? de précision?_


En utilisant la cross-validation, nous avons testé les performances des algorithmes suivants : _Decision Tree_, _Gradient Boosting_ et _Random Forest_. Nous avons pu uniquement traiter ce problème avec Scikit-learn donc nous ne pouvons pas émettre de résultats concernant les frameworks, cependant grâce au temps de calcul, à la _mean squared error_ (MSE) et à la _mean absolute error_ (MAE) nous avons pu constater le résultat suivant : 

GradientBoosting est le plus lent mais est aussi le plus efficace (MSE et MAE plus faibles) alors qu’au contraire le DecisionTree, bien que le plus rapide est aussi le moins fiable (MSE et MAE la plus haute). Le _RandomForest _quand à lui se situe entre les deux en terme de temps et d’efficacité.

On retrouve les même résultats en utilisant le calcul de la MAPE avec les jeux de données _Boston_ et _Titanic_, le classement reste le même en efficacité et en temps d’exécution, même si dans le deuxième cas de figure, les temps d’exécutions pour un même jeu de données reste très proches.

___


_**b.** Parmi les frameworks et algorithmes de machine learning, y a-t-il des implémentations significativement plus lentes/précises que d’autres?_

_DecisionTree_ est significativement plus rapide et moins précis que les deux autre (d’après les calculs de MSE et de MAE en _cross-validation_)

La *cross-validation* est plus lente que la séparation entraînement/test : cela est dû au fait qu’elle consiste à séparer les données en **_n_** sets puis à effectuer les calculs avec entraînement et test **_n_** fois (**_n_** étant égal à 6 dans notre cas).

---

_**c.** Etant donné un algorithme de machine learning (e.g., decision tree), est-ce qu’on observe des différences (temps d’exécution/précision) entre les frameworks?_

N’étant pas parvenus à créer suffisamment de tests en langage R, nous ne pouvons malheureusement pas répondre à cette question.

___

_**d.** Y a-t-il des jeux de données plus difficiles à traiter (en termes de précision/temps d’exécution) ?_ 

Le jeu de données _Titanic_ est plus long à traiter : pour un même algorithme, en utilisant Scikit-learn et en calculant la MAPE les programmes sont plus long à s’exécuter avec _Titanic_ qu’avec _Boston_.

Les résultats donnés par les algorithmes sur Titanic sont médiocres (MAPE de 40%)  mais cependant à nuancer avec notre manque de maîtrise du Machine Learning. Un meilleur paramétrage et un meilleur traitement préliminaire des données (“_shuffling_”) auraient sans doute permis de tirer meilleur parti de cet algorithme.

---

 _**e.** Au vu des résultats, quel framework de machine learning recommanderiez-vous ?_

Difficile de statuer étant donné que Scikit-learn est le seul framework que nous ayons véritablement réussi à manipuler. Il s’agit d’un framework Python donc la syntaxe est plus compréhensible pour nous que celle du langage R qui est sensiblement différente car il s’agit d’un langage davantage orienté statistiques et mathématiques. 

La documentation de Scikit-learn est abordable et bien construite, même pour des personnes ne pratiquant pas dans le domaine du machine learning comme c’est notre cas. Nous avons facilement trouvé des exemples à partir desquels construire nos compilateurs.
