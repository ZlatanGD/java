//package sokaban.visualisation;

//import sokaban.metier.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Sokoban {

	// PAUSE entre chaque coup du replay
	public static int PAUSEREPLAY = 250;
	// PAUSE entre chaque coup du solveur
	public static int PAUSESOLVER = 10;
	public static String PATH = "C://Users//Zlatan Gojak//Desktop//Faks//L2Informatique//Java//Project//metier//";
	public static String EASY = PATH+"microban.txt";
	public static String MEDIUM = PATH+"sokoriginal.txt";
	public static String HARD = PATH+"Novoban.txt";		

	public static void main (String[]args){
		Configuration sokoban = loadSokoban(EASY,2);
		Configuration sokoban2 = loadSokoban(EASY,2);//promena
		
		System.out.println("La taille de caisse: " + sokoban.caisse.size());
		System.out.println("ToString: \n" + sokoban.toString());
		replay(sokoban2,jouer(sokoban));
		//SolverInterface solver = new Solver();
		//replay(sokoban2,resoudre(sokoban,solver));
	}

	
	public static ArrayList<Direction> jouer(Configuration config){
			Configuration sokoban = new Configuration(config);
			Configuration sokoban2 = new Configuration(config);//promena
			SokobanUI sokoUI = new SokobanUI();
			sokoUI.show(sokoban);
			while(!sokoban.victoire()){
				Direction d = null;
				switch (sokoUI.waitKey()) {
					case 27:
						System.out.println("sokoUI.endGame(sokoban);");
						sokoUI.endGame(sokoban);
						sokoUI.dispose();
						return sokoban.getJoueur().getHisto();
					case 'z':
						System.out.println("Z");
						d = Direction.HAUT;
						break;
					case 's':
						System.out.println("S");
						d = Direction.BAS;
						break;
					case 'q':
						System.out.println("Q");
						d = Direction.GAUCHE;
						break;
					case 'd':
						System.out.println("D");
						d = Direction.DROITE;
						break;
					case 'r':
						System.out.println("R");
						sokoban= new Configuration(config);
					default:
						break;
				}
				if (d !=null)
					sokoban.bougerJoueurVers(d);
				sokoUI.show(sokoban);
				sokoUI.drawString("Touches : q,z,d,s,r,escape (gauche,haut,droite,bas,recommencer,sortir)", 0.1, 0.95,32, SokobanUI.BLACK);
			}
			sokoUI.endGame(sokoban);
			sokoUI.dispose();
			return sokoban.getJoueur().getHisto();
	}
	


	public static ArrayList<Direction> resoudre(Configuration sokoban, SolverInterface solver){
		SokobanUI sokoUI = new SokobanUI();
		solver.set(sokoban);
		while(!solver.stop()){
			solver.step();
			sokoUI.show(solver.getConfiguration());
			sokoUI.drawString("testées: "+solver.getTotalSteps(),0.05,0.05,32,SokobanUI.RED);
			sokoUI.drawString("Déplacements: "+solver.getConfiguration().getJoueur().getHisto().size(),0.05,0.08,32,SokobanUI.RED);
			sokoUI.drawString("Coups: "+solver.getConfiguration().getJoueur().getNbCoups(),0.05,0.11,32,SokobanUI.RED);
			//sokoUI.drawString("Info: "+solver.getInfo(),0.05,0.15,32,SokobanUI.RED);
					sokoUI.refresh();
			if (sokoUI.popKey()==27) {
				sokoUI.endGame(solver.getConfiguration());
				return solver.getConfiguration().getJoueur().getHisto();
			}
			SokobanUI.pause(PAUSESOLVER);	
		}
		System.out.println("return de l'histo");
		return solver.getConfiguration().getJoueur().getHisto();
	}


	public static void replay(Configuration sokoban, ArrayList<Direction> histo){
		SokobanUI sokoUI = new SokobanUI();
	//	while(true){
			Configuration jeu = new Configuration(sokoban);
			sokoUI.show(jeu);
			sokoUI.drawString("REPLAY", 0.45, 0.05,30, SokobanUI.BLUE);
			sokoUI.refresh();
			for (Direction d: histo){
					SimpleInterface.pause(PAUSEREPLAY);
					jeu.bougerJoueurVers(d);
					sokoUI.show(jeu);
					sokoUI.drawString("REPLAY", 0.45, 0.05,30, SokobanUI.BLUE);
					sokoUI.refresh();
					if (sokoUI.popKey()==27) {
						sokoUI.dispose();
						return;
					}
			}
			if (sokoUI.popKey()==27) {
				sokoUI.dispose();
				return;
			}
			System.out.println("replay");
			sokoUI.endGame(jeu);
	//	}
	}
	
	public static Configuration loadSokoban(String levelString){
		Niveau niveau = null;
		Configuration config = null;
		ArrayList<Position> murs= new ArrayList<Position>();
		ArrayList<Position> cibles = new ArrayList<Position>();
		ArrayList<Position> caisses = new ArrayList<Position>();
		Position posjoueur=null;
		int j=0;
		int x = 0;
		for (String line : levelString.split("\n")){
			for (int i=0;i<line.length();i++){
				switch (line.charAt(i)) {
				case '#':
					murs.add(new Position(i,j));
					break;
				case '@':
					posjoueur = new Position(i,j);
					break;
				case '.':
					cibles.add(new Position(i,j));
					break;
				case '$':
					caisses.add(new Position(i,j));
					System.out.println("ajout d'une caisse");
					break;
				case '*':
					caisses.add(new Position(i,j));
					cibles.add(new Position(i,j));
					break;
				default:
					break;
				}
			}
			if (x<line.length())
				x = line.length();
			j++;
		}
		niveau = new Niveau(x, j);
		config = new Configuration(niveau, posjoueur);
		for (Position position: murs)
			if (!niveau.addMur(position)) {
				System.err.println("Erreur : mur "+position+" impossible à poser");
				return null;
			}
		System.out.println("FIN AJOUT MURS");
		for (Position position: caisses)
			if (!config.addCaisse(position)){
				System.err.println("Erreur : caisse "+position+" impossible à poser");
				return null;
			}
		System.out.println("FIN AJOUT CAISSES");
		for (Position position: cibles)
			if (!niveau.addCible(position)){
				System.err.println("Erreur : cible "+position+" impossible à poser");
				return null;
			}
		System.out.println("FIN AJOUT POSITIONS");
		return config;
	}


	public static Configuration loadSokoban(String fn,int level){
		Configuration config = null;
		try{
			BufferedReader buffer=new BufferedReader(new InputStreamReader(new FileInputStream(fn)));
			String line="";
			String levelString="";
			int nblev=0;
			while ((nblev<level && (line=buffer.readLine())!=null))
				if (line.contains(";"))	nblev++;
			while (((line=buffer.readLine())!=null) && (!line.contains(";"))){
				levelString+=line+"\n";
		
			}
			config = loadSokoban(levelString);
		}
		catch (Exception e){
			e.printStackTrace();;
		}
		return config;
	}



}
