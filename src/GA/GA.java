package GA;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
//import java.math.*;
import java.util.concurrent.TimeUnit;

class GA implements ICollector {
	static int anz = 6;// Anzahl Individuen: muss eine gerade Zahl sein!
	static int gene = 20;// Anzahl Gene pro Individuum
	static double pm = 1.0 / (double) gene;
	static int seed = 20;

	int[][] eltern = new int[anz][gene];
	int[][] nachkommen = new int[anz][gene];
	int[] besteLsg = new int[gene];
	float[] fitness = new float[anz];
	float besteFitness = Float.MAX_VALUE;

	double[][] daten = Einlesen.einlesenDiabetes(new File("diabetes.csv"), true);
	int minFehler = Integer.MAX_VALUE;
	int dimension = daten[0].length - 1;
	KNN bestKNN;

	Random r = new Random();

	public KNN getbestKNN() {
		return bestKNN;
	}

	GA() {
		for (int i = 0; i < anz; i++) {
			for (int j = 0; j < gene; j++) {
				eltern[i][j] = Math.abs(r.nextInt()) % 2;
			}
			fitness[i] = berechneFitness(i);

			if (fitness[i] < besteFitness) {
				for (int j = 0; j < gene; j++) {
					besteLsg[j] = eltern[i][j];
				}
				besteFitness = fitness[i];
				System.out.println("Beste Fitness= " + besteFitness);
			}
		}
	}

	void selektierenRekombinieren() {
		int elter1, elter2, indi1, indi2, trennstelle;

		for (int i = 0; i < anz; i = i + 2) {
			indi1 = Math.abs(r.nextInt()) % anz;
			indi2 = Math.abs(r.nextInt()) % anz;
			if (fitness[indi1] < fitness[indi2])
				elter1 = indi1;
			else
				elter1 = indi2;

			indi1 = Math.abs(r.nextInt()) % anz;
			indi2 = Math.abs(r.nextInt()) % anz;
			if (fitness[indi1] < fitness[indi2])
				elter2 = indi1;
			else
				elter2 = indi2;

			trennstelle = Math.abs(r.nextInt()) % gene;

			for (int j = 0; j < trennstelle; j++) {
				nachkommen[i][j] = eltern[elter1][j];
				nachkommen[i + 1][j] = eltern[elter2][j];
			}

			for (int j = trennstelle; j < gene; j++) {
				nachkommen[i][j] = eltern[elter2][j];
				nachkommen[i + 1][j] = eltern[elter1][j];
			}
		}
	}

	void mutieren() {
		double zz;
		for (int i = 0; i < anz; i++) {
			for (int j = 0; j < gene; j++) {
				zz = Math.random();
				if (zz < pm) {
					System.out.println("DRINNE");
					if (nachkommen[i][j] == 0)
						nachkommen[i][j] = 1;
					else
						nachkommen[i][j] = 0;
				}
			}
		}
	}

	void dekodierenBewertenErsetzen() { // Thread?
		List<Future> threads = new ArrayList<Future>();
		ExecutorService vExecutor = Executors.newFixedThreadPool(anz);
		for (int i = 0; i < anz; i++) {
			DecodeThread task = new DecodeThread(i, this);
			threads.add(vExecutor.submit(task));
		}
		vExecutor.shutdown();
		while (!vExecutor.isTerminated()) {
		}
		System.out.println("Beste Loesung: " + besteFitness + " Aktuelle Loesung: " + fitness[0]);
	}

	/*
	 * problemspezifisch ist die Fitnessberechnung festzulegen!
	 */

	float berechneFitness(int individuum) {

		// nach jedem 4 Gen müssen wir konvertieren in Dezimalzahlen => Ergebnis 10 | 8
		// | 3 | 1 | 0
		// Dieses Ergebnis müssen wir Hauptprogramm mitgeben. Und zwar Variable
		// StrukturNN definieren
		// Daraus resultiert Lösung => Fehler, welcher möglichst klein werden soll
		// Return fitness, welcher der Fehler von KNN ist.

		float deciwert = -1.0f;
		float dualwert = 0.0f;
		float fitness;

		int versteckteSchichten[] = { 0, 0, 0, 0, 0 };

		int exponent = 3;
		int schicht = 0;

		// Gene in Dezimalzahlen konvertieren
		for (int j = 0; j < 5; j++) { // 5 Schichten
			for (int k = 0; k < 4; k++) { // je 4 bits || links ist höchster Bit
				// System.out.println(k + (j * 4) + ", pow: " + Math.pow(2, exponent));
				versteckteSchichten[schicht] += (int) (eltern[individuum][k + (j * 4)] * Math.pow(2, exponent));
				exponent -= 1;

				if (k == 3) { // damit bei nächster Schicht wieder der Exponent bei 3 ist
					exponent = 3;
				}
			}
			schicht += 1;
		}

		// System.out.println(Arrays.toString(versteckteSchichten));

		// Damit die Schichten immer mehr als 0 Knoten haben
		int[] removedZero = Arrays.stream(versteckteSchichten).filter(value -> value != 0).toArray();

		fitness = trainieren(removedZero);

		return fitness;
	}

	public int trainieren(int schichten[]) {

		KNN netz = new KNN(dimension, schichten);

		netz.trainieren(daten);// Verlustfunktion min
		int kleinsterFehler = netz.kleinsterFehler();

		if (kleinsterFehler < minFehler) {
			minFehler = kleinsterFehler;
			bestKNN = netz;
		}

		System.out.println("KleinsterFehler: " + kleinsterFehler + ", Schicht:" + Arrays.toString(schichten));

		return kleinsterFehler;
	}

	public void auswerten() {
		daten = Einlesen.einlesenDiabetes(new File("diabetes.csv"), false);
		System.out.println("KNN für die Evaluation (Fehler): " + bestKNN.kleinsterFehler());
		bestKNN.evaluieren(daten);
	}

	@Override
	synchronized public void sendResult(float fitness) {
		// TODO Auto-generated method stub
		if (fitness < besteFitness) {
			besteFitness = fitness;
			for (int j = 0; j < gene; j++) {
				besteLsg[j] = eltern[anz][j];
			}
		}
	}

	class DecodeThread implements Runnable {

		private ICollector myCollector;
		public int anz;

		DecodeThread(int anz, ICollector myCollector) {
			this.myCollector = myCollector;
			this.anz = anz;
		}

		@Override
		public void run() {
			for (int j = 0; j < gene; j++) {
				eltern[anz][j] = nachkommen[anz][j];
			}
			fitness[anz] = berechneFitness(anz);
			
			myCollector.sendResult(fitness[anz]);
		}

	}
}
