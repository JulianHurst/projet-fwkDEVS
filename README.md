# A propos
Le	cadriciel	fwkDEVS	est	un	simulateur	ad-hoc	DEVS	développé	sous	Java.	Il	permet	l'implémentation	de	
modèles	DEVS	et	leur	simulation.	Afin	de	réaliser	de	telles	simulations,	le	développeur	doit	avoir	des	compétences	en	
programmation	objet	pour	produire	le	code	nécessaire	à	la	simulation.	
Le	but	de	ce	projet	est	de	développer	un	éditeur	graphique	pour	automatiser	partiellement	la	phase	de	codage	et	par	
conséquent	faciliter	l'implémentation	des	modèles	DEVS	auprès	des	utilisateurs.	

# Requis
 - [jdk 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (testé)
 - Eclipse NEON 2
 - Linux ou Windows (testé)  

# Projet Industriel fwkDEVS
Pensez à inclure le [jar de fwkdevs](http://www.lsis.org/hamria/fwkdevs-v0.7.jar) et le [jar de codemodel](http://central.maven.org/maven2/com/sun/codemodel/codemodel/2.6/codemodel-2.6.jar) dans le build path du projet sinon le programme ne compilera pas.  

# Arborescence
projet-fwkDEVS  
└── projet-fwkDEVS  
    ├── bin  
    │   ├── codegen  
    │   ├── devs  
    │   ├── gate  
    │   ├── main  
    │   ├── models  
    │   └── util  
    ├── crash  
    ├── doc  
    │   ├── codegen  
    │   ├── devs  
    │   ├── main  
    │   ├── models  
    │   └── util  
    ├── input  
    ├── lib (IMPORTANT : .jar a importer dans le build path du projet)  
    └── src  
        ├── codegen  
        ├── couples  
        ├── devs  
        ├── gate  
        ├── main  
        ├── models  
        └── util  

25 directories

# Javadoc
[Docs](https://rawgit.com/JulianHurst/projet-fwkDEVS/master/projet-fwkDEVS/doc/index.html)
