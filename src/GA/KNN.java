package GA;

public class KNN {
	// Feedforward-Neuronales Netz variabler Anzahl an Hiddenschichten

	private int m; 				// Anzahl Schichten
	private int n; 				// Anzahl Knoten (insgesamt ueber alle Schichten)
	public int[][] netz; 		// Enthaelt pro Schicht netz[i] die enthaltenen Knotennummern
	public double[][] w; 		// Gewichte

	// Knoteninformationen, jeweils durch ein Array gespeichert, Index =
	// Knotennummer
	public boolean[] bias; // true: Knoten ist Bias
	private double[] in;
	private double[] a;
	private double[] delta;
	
	private int batchSize = 1000;
	private double[][] gradient;

	// Parameter für Backprobagation
	private double alpha  = 0.5;    // Fehlerrate fuer Backprobagation
	private int maxIter   = 1;      // Anzahl Iterationen bei Fehlerminimierung
	private int maxEpoche = 2000;// Anzahl Iterationen bei Fehlerminimierung

	private int kleinsterFehler = 10000;
	
	public KNN(int anzahlEingabewerte, int[] anzahlKnotenProHiddenSchicht) {

		this.m = anzahlKnotenProHiddenSchicht.length + 2;// Anzahl Hiddenschichte + Eingabeschicht + Ausgabeschicht
		netz = new int[m][];
		int knotenNr = 0;

		// Eingabeschicht
		netz[0] = new int[anzahlEingabewerte + 1];// der erste Knoten der ersten Schicht ist der Bias, deshalb plus 1

		// Ausgabeschicht
		netz[m - 1] = new int[1];

		// Hiddenschichten
		for (int l = 0; l < anzahlKnotenProHiddenSchicht.length; l++) {
			netz[l + 1] = new int[anzahlKnotenProHiddenSchicht[l]];
		}

		for (int l = 0; l < m; l++) {// alle Schichten werden mit fortlaufenden Knotennummern gefüllt
			for (int i = 0; i < netz[l].length; i++) {
				netz[l][i] = knotenNr;
				knotenNr++;
			}
		}

		this.n 			= knotenNr;
		this.w 			= new double[this.n][this.n];
		this.bias 		= new boolean[this.n];
		this.in 		= new double[this.n];
		this.a 			= new double[this.n];
		this.delta 		= new double[this.n];
		this.gradient   = new double[this.n][this.n];

		//bias
		for (int l = 0; l < m; l++) {
			for (int i = 0; i < netz[l].length; i++) {
				knotenNr = netz[l][i];
				if (i == 0 && l < m - 1)
					bias[knotenNr] = true;// der erste Knoten einer Schicht wird bias (aussnahme in der ausgabeschicht)
				else
					bias[knotenNr] = false;
			}
		}

	}

/*
 * Trainieren mit Backpropagation Algorithmus
 */
	
	public int kleinsterFehler() {
		// TODO Auto-generated method stub
		return kleinsterFehler;
	}

	
	public void trainieren(double[][] liste) {
		double[][] optAnzGewichte = new double[n][n];//bestes Ergebnis bzgl. anzFehler
		double[][] optFehGewichte = new double[n][n];//bestes Ergebnis bzgl. fehler
		double[] fehlerVektor;
		
		double klasse;
		double fehler;
		int anzFehler =100000;
		
		
		
		gewichteInitialisieren();

		int intervall    = 50;
		int minAnzFehler = Integer.MAX_VALUE;
		double minFehler = Double.MAX_VALUE;
		boolean goBack 	 = false;
		boolean stop  	 = false;
		int epoche    	 = 0;
		int anzVb		 = 0;
		
		while (!stop) {
			epoche++;
			// s = x1 x2 y
			for (int s = 0; s < liste.length; s++) {	
				eingabeSchichtInitialisieren(liste[s]); // daten x1 x2 y an eingabeschicht anhängen
				klasse = liste[s][liste[s].length - 1];  // 0 oder 1 = y
				forward();
				backward(klasse);
				// hier könnte man aktualisieren
			}

			fehlerVektor	= fehler3(liste);
			fehler    		= fehlerVektor[0];
			anzFehler 		= (int)fehlerVektor[1];
			
			//System.out.println("-Epoche: " + epoche + " " + anzFehler + " " + fehler + " minAnzFehler " + minAnzFehler + " minFehler " + minFehler +" " + goBack + " " + alpha);
			if (epoche >= maxEpoche || anzFehler == 0)	stop = true;
			
			if(anzFehler < kleinsterFehler) {
				kleinsterFehler = anzFehler;
			}
		}
	}


	/*
	 * backward-Pass
	 */
	
	private void deltaAusgabeSchicht(double klasse) {
		int ausgabeSchicht = netz.length - 1;
		for (int nrj = 0; nrj < netz[ausgabeSchicht].length; nrj++) {
			int j = netz[ausgabeSchicht][nrj]; // knotennummer der Ausgabeschicht
			double yj = klasse;
			delta[j]  = ableitungAktivierungsFunktion(in[j]) * (a[j]-yj);
		}
	}
	
	

	private void backward(double klasse) {
		// deltas für Ausgabeschicht
		deltaAusgabeSchicht(klasse);

		// deltas für Hiddenschicht
		int ausgabeSchicht = netz.length - 1;
		
		
		for (int l = ausgabeSchicht - 1; l >= 0; l--) {
			for (int nri = 0; nri < netz[l].length; nri++) {
				int i = netz[l][nri];
				delta[i] = 0.0;
				if (!bias[i]) {
					double sum = 0;
					for (int nrj = 0; nrj < netz[l + 1].length; nrj++) {
						int j = netz[l + 1][nrj];
						sum += w[i][j] * delta[j];
					}
					delta[i] = ableitungAktivierungsFunktion(in[i]) * sum;
				}
			}
		}

		// aktualisierung der Gewichte (kann man rausziehen)
		for (int l = 0; l < netz.length - 1; l++) {
			for (int nri = 0; nri < netz[l].length; nri++) {
				int i = netz[l][nri];

				for (int nrj = 0; nrj < netz[l + 1].length; nrj++) {
					int j = netz[l + 1][nrj];
					if (!bias[j]) {
						double delt = alpha * a[i] * delta[j];
						//w[i][j] += delt;
						w[i][j] = w[i][j] - delt;//Gradientenabstieg
					}
				}
			}
		}

	}


	
	
	/*
	 * Forward-Pass
	 */
	
	private void forward() {
		
			for (int l = 1; l < netz.length; l++) {// alle Schichten l ab erster Hiddenschicht
				for (int nrj = 0; nrj < netz[l].length; nrj++) {// alle Knoten in Schicht l
					int j = netz[l][nrj];// knotennummer in der Schicht l
					if (!bias[j]) {
						in[j] = 0.0;
						for (int nri = 0; nri < netz[l - 1].length; nri++) {// alle Knoten in Schicht l-1
							int i = netz[l - 1][nri];// knotennummer in der Schich l-1
							in[j] += w[i][j] * a[i];
						}
						a[j] = aktivierungsFunktion(in[j]);
					}
				}
			}
		}

	/*
	 * Aktivierungsfunktion und deren Ableitung
	 */
	
	private double aktivierungsFunktion(double x) {
		return (1.0 / (1.0 + Math.exp(-x)));
	}

	private double ableitungAktivierungsFunktion(double x) {
		return (aktivierungsFunktion(x) * (1 - aktivierungsFunktion(x)));
	}

	
	/*
	 * Initialisierung
	 */
	
	private void gewichteInitialisieren() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				w[i][j] = 0;
			}
		}

		for (int l = 0; l < m - 1; l++) {
			for (int i = 0; i < netz[l].length; i++) {
				int indexi = netz[l][i];
				for (int j = 0; j < netz[l + 1].length; j++) {
					int indexj = netz[l + 1][j];
					if (!bias[indexj]) {
						w[indexi][indexj] = Math.random();
						if (Math.random() < 0.5)
							w[indexi][indexj] = -w[indexi][indexj];
					}
				}
			}
		}

	}
	
	private void eingabeSchichtInitialisieren(double[] input) {
		// Alle Bias-Knoten initialisieren
		for (int i = 0; i < netz.length - 1; i++) {// über alle Schichten
			int knoten = netz[i][0]; // der erste Knoten einer Schicht ist Bias!
			if (!bias[knoten])
				System.out.println("ups, nicht-Bias-Knoten als bias initialisiert");
			in[knoten] = 1.0;
			a[knoten]  = 1.0;
		}

		// s = x1 | x2 | y <= Eine Zeile
		// Alle Knoten der Eingabeschicht ab dem 2. Knoten mit Eingabe belegen
		for (int i = 0; i < input.length - 1; i++) {// der letzte Wert in input ist der output und gehört nicht zur
													// Eingabe
			in[i + 1] = input[i]; // in[0] ist Bias, deshalb i.ten Input bei bei in[i+1] speichern
			a[i + 1] = input[i];
		}

	}

	
	
	
	

	/*
	 * Hilfsmethoden zur Evaluation
	 */
	
	private int fehler(double[][] liste) {
		int anzFehler = 0;
		double klasse;
		for (int s = 0; s < liste.length; s++) {
			eingabeSchichtInitialisieren(liste[s]);
			klasse = liste[s][liste[s].length - 1];
			forward();
			if ((a[n - 1] < 0.5 && (int) klasse == 1) || (a[n - 1] >= 0.5 && (int) klasse == 0))anzFehler++;
			//System.out.println(s + " " + liste[s][0] + " " + liste[s][1] + " " + liste[s][2] + " " + a[n-1]);
		}		
		return anzFehler;
	}
	
	private double fehler2(double[][] liste) {
		double fehler = 0.;
		double klasse;
		for (int s = 0; s < liste.length; s++) {
			eingabeSchichtInitialisieren(liste[s]);
			klasse = liste[s][liste[s].length - 1];
			forward();
			fehler += Math.pow(klasse - a[n - 1], 2);
		}		
		return fehler;
	}
	private double[] fehler3(double[][] liste) {
		double[] fehler = new double[2];
		fehler[0] = 0.0;
		fehler[1] = 0.0;
		
		double klasse;
		for (int s = 0; s < liste.length; s++) {
			eingabeSchichtInitialisieren(liste[s]);
			klasse = liste[s][liste[s].length - 1];
			forward();
			fehler[0] += Math.pow(klasse - a[n - 1], 2);
			if ((a[n - 1] < 0.5 && (int) klasse == 1) || (a[n - 1] >= 0.5 && (int) klasse == 0))fehler[1]++;
		}		
		return fehler;
	}
	
	/*
	 * Methoden zur Evaluierung
	 */
	
	public double[] evaluieren(double[][] liste) {
		//fuer Bankenbeispiel
		double output;
		int falschPositiv  = 0;
		int falschNegativ  = 0;
		int richtigPositiv = 0;
		int richtigNegativ = 0;
		int anzahlPositiv  = 0;
		int anzahlNegativ  = 0;
		
		double[] ergebnis = new double[12];

		for (int s = 0; s < liste.length; s++) {
			eingabeSchichtInitialisieren(liste[s]);
			output = liste[s][liste[s].length - 1];
			forward();
			if (a[n - 1] < 0.5 && (int) output == 1) {
				falschNegativ++;
				anzahlPositiv++;
			}
			else if(a[n - 1] >= 0.5 && (int) output == 1) {
				richtigPositiv++;
				anzahlPositiv++;
			} 
			else if(a[n - 1] >= 0.5 && (int) output == 0) {
				falschPositiv++;
				anzahlNegativ++;
			}
			else  if(a[n - 1] < 0.5 && (int) output == 0) {
				richtigNegativ++;
				anzahlNegativ++;
			}
			else {
				System.out.println("Error0 in Auswertung");
			}
		}
		if(anzahlPositiv != richtigPositiv+falschNegativ)System.out.println("Error1 in Auswertung");
		if(anzahlNegativ != richtigNegativ+falschPositiv)System.out.println("Error2 in Auswertung");
		if(anzahlPositiv+anzahlNegativ != liste.length)System.out.println("Error3 in Auswertung");
	
		
		ergebnis[0] = liste.length;
		ergebnis[1] = anzahlPositiv; 
		ergebnis[2] = anzahlNegativ;
		ergebnis[3] = (double)anzahlPositiv/(double)liste.length; 
		ergebnis[4] = (double)anzahlNegativ/(double)liste.length;
		ergebnis[5] = (double)(richtigPositiv+richtigNegativ)/(double)liste.length;
		ergebnis[6] = richtigPositiv; 
		ergebnis[7] = falschPositiv;
		ergebnis[8] = richtigNegativ;
		ergebnis[9] = falschNegativ;
		ergebnis[10] = (double)richtigPositiv / (double)(richtigPositiv+falschNegativ); 
		ergebnis[11] = (double)falschPositiv  / (double)(richtigNegativ+falschPositiv);

		System.out.println("Anzahl Muster:  \t" + ergebnis[0]);
		System.out.println("Anzahl Positiv: \t" + ergebnis[1]);
		System.out.println("Anzahl Negativ: \t" + ergebnis[2]);
		System.out.println("Anteil Positiv: \t" + ergebnis[3]);
		System.out.println("Anteil Negativ: \t" + ergebnis[4]);
		
		System.out.println("Genauigkeit  :  \t" + ergebnis[5]);
		System.out.println("Trefferquote:   \t" + ergebnis[10]);
		System.out.println("Ausfallrate :   \t" + ergebnis[11]);

		System.out.println("richtigPositiv: \t" + ergebnis[6]);
		System.out.println("falsch Negativ: \t" + ergebnis[9]);
		System.out.println("richtigNegativ: \t" + ergebnis[8]);
		System.out.println("falsch Positiv: \t" + ergebnis[7]);
		
		return ergebnis;
	}	
	 
	 public int output(double[] x) {
			double[] input = new double[3];
			input[0] = x[0]/100.;
			input[1] = x[1]/100.;
			input[2] = 1.;
			
		
			
			eingabeSchichtInitialisieren(input);
			forward();	
	
			double u = a[n-1];
			int out;
			
			if (u <  0.5) out = 0;
			else     	  out = +1;
			
			return out;
		}

/*
 * Methoden zur  Ausgabe der Netzparameter
 */
		
		public void ausgabeBias() {
			int i = 0;
			boolean ende = false;
			while (!ende) {
				ende = true;
				for (int l = 0; l < netz.length; l++) {
					if (i < netz[l].length) {
						int k = netz[l][i];
						System.out.print(bias[k] + "\t");
						ende = false;
					} else {
						System.out.print("\t");
					}
				}
				System.out.println();
				i++;
			}
		}

		public void ausgabeNetzStruktur() {
			int i = 0;
			boolean ende = false;
			while (!ende) {
				ende = true;
				for (int l = 0; l < netz.length; l++) {
					if (i < netz[l].length) {
						System.out.print(netz[l][i] + "\t\t");
						ende = false;
					} else {
						System.out.print("\t\t");
					}
				}
				System.out.println();
				i++;
			}
		}

		public void ausgabeKnotenwerte() {
			System.out.println("Ausgabe der Knoten-Ausgabewerte");

			int i = 0;
			boolean ende = false;
			while (!ende) {
				ende = true;
				for (int l = 0; l < netz.length; l++) {
					if (i < netz[l].length) {
						int k = netz[l][i];
						double preci = 1000;
						double round = ((int) (a[k] * preci)) / preci;
						System.out.print(round + "\t");
						ende = false;
					} else {
						System.out.print("\t");
					}
				}
				System.out.println();
				i++;
			}
		}

		public void ausgabeDelta() {
			System.out.println("Ausgabe der DELTA");
			int i = 0;
			boolean ende = false;
			while (!ende) {
				ende = true;
				for (int l = 0; l < netz.length; l++) {
					if (i < netz[l].length) {
						int k = netz[l][i];
						double preci = 100000;
						double round = ((int) (delta[k] * preci)) / preci;
						System.out.print(round + "\t");
						ende = false;
					} else {
						System.out.print("\t");
					}
				}
				System.out.println();
				i++;
			}
		}

		public void ausgabeInputwerte() {
			System.out.println("Ausgabe der Inputwerte");
			int i = 0;
			boolean ende = false;
			while (!ende) {
				ende = true;
				for (int l = 0; l < netz.length; l++) {
					if (i < netz[l].length) {
						int k = netz[l][i];
						double preci = 1000;
						double round = ((int) (in[k] * preci)) / preci;
						System.out.print(round + "\t");
						ende = false;
					} else {
						System.out.print("\t");
					}
				}
				System.out.println();
				i++;
			}
		}

		public void ausgabeEingabeSchicht() {
			System.out.println("Ausgabe der Eingabeschicht");

			for (int i = 0; i < netz[0].length; i++) {
				int knoten = netz[0][i];
				System.out.print(a[knoten] + " ");
			}
			System.out.println();
		}

		public void ausgabeW() {
			System.out.println("Ausgabe der Gewichte");

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					double preci = 1000;
					double round = ((int) (w[i][j] * preci)) / preci;
					if (round != 0)
						System.out.println(i + " " + j + " " + round);
				}
				// System.out.println();
			}
			System.out.println();
		}

		
}
