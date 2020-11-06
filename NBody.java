//Abhishek Ramesh
//N-Body Problem

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import java.nio.file.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.lang.Math;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class NBody extends JPanel implements ActionListener{

	public static String listType;
	public static double scale;
	public static double[][] newArray = {};
	public static double[][] planetArray;
	public static int numberPlanets;


	double gConstant = 6.674*Math.pow(10,-11);

	Timer tm = new Timer(5, this);

	//Get the list type and scale value
	public static void getScale() {
		//Get ListType: ArrayList/LinkedList
		int n = 0;
		try (BufferedReader br = new BufferedReader(new FileReader("nbody_input.txt"))) {
			for (int i = 0; i < n; i++)
				br.readLine();
			listType = br.readLine();

		}
		catch(IOException e){
			System.out.println("No value");
		}

		//Get scale value
		n = 1;
		try (BufferedReader br = new BufferedReader(new FileReader("nbody_input.txt"))) {
			for (int i = 0; i < n; i++)
				br.readLine();
			scale = Double.parseDouble(br.readLine());

		}
		catch(IOException e){
			System.out.println("No value");
		}
	}

	//Place Planet Values into list
	public static void planetValues() throws java.io.IOException{
		Path file = Paths.get("nbody_input.txt");
		long count = Files.lines(file).count();

		numberPlanets=(int)count;
		numberPlanets = numberPlanets-2;
		planetArray = Arrays.copyOf(newArray, numberPlanets);

		for (int i = 2; i < count; i++){
			try{
				String line = Files.readAllLines(Paths.get("nbody_input.txt")).get(i);

				List<String> al = new ArrayList<>(Arrays.asList(line.split(",")));
				al.remove(0);

				double[] arrDouble = new double[al.size()];
				for(int v=0; v<al.size(); v++) {
					arrDouble[v] = Double.parseDouble(al.get(v));
				}
				planetArray[i-2] = arrDouble;
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
	}

	//Painting the planets on JFrame
	public void paintComponent(Graphics gg){
			super.paintComponent(gg);

			//Using Graphics2D, since fillOval() only accepts integers
			Graphics2D g = (Graphics2D) gg;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

			//Draws shape based on x,y axis
			for (int i=0; i<numberPlanets; i++){
				Ellipse2D.Double shape = new Ellipse2D.Double(planetArray[i][1], planetArray[i][2], planetArray[i][5], planetArray[i][5]);
				Color myColor = new Color(255/(i+1), 255/(i+1), 255/(i+1));
				g.setColor(myColor);
				g.fill(shape);
			}

		tm.start();

	}

	//Caclulate distance between 2 planets
	static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) * 1); 
	}

	//Repainting the planet according to new position
	public void actionPerformed(ActionEvent e){
		for (int i=0; i<numberPlanets; i++){

			//Initial velocity change
			planetArray[i][1] = planetArray[i][1] + planetArray[i][3];
			planetArray[i][2] = planetArray[i][2] + planetArray[i][4];

			//Gravitational Velocity Change
			for (int j = 1; j<numberPlanets; j++){
				distance(planetArray[i][1], planetArray[i][2], planetArray[j][1], planetArray[j][2]);
				
				//scale^2 to take into account the radius  
				double force = ((gConstant*(planetArray[i][0]*planetArray[i][1]))/Math.pow(planetArray[j][5], 2))/Math.pow(scale, 2);

				// Move planet in relation to whether the other planet is greater/lesser in x,y axis
				if ((planetArray[i][1] > planetArray[j][1]) && (planetArray[i][2] > planetArray[j][2])){
					planetArray[i][1] = planetArray[i][1] + force;
					planetArray[i][2] = planetArray[i][2] + force;

					planetArray[j][1] = planetArray[j][1] - force;
					planetArray[j][2] = planetArray[j][2] - force;

				}

				else if ((planetArray[i][1] > planetArray[j][1]) && (planetArray[i][2] < planetArray[j][2])){
					planetArray[i][1] = planetArray[i][1] + force;
					planetArray[i][2] = planetArray[i][2] - force;

					planetArray[j][1] = planetArray[j][1] - force;
					planetArray[j][2] = planetArray[j][2] + force;

				}

				else if ((planetArray[i][1] < planetArray[j][1]) && (planetArray[i][2] > planetArray[j][2])){
					planetArray[i][1] = planetArray[i][1] - force;
					planetArray[i][2] = planetArray[i][2] + force;

					planetArray[j][1] = planetArray[j][1] + force;
					planetArray[j][2] = planetArray[j][2] - force;

				}

				else{
					planetArray[i][1] = planetArray[i][1] - force;
					planetArray[i][2] = planetArray[i][2] - force;

					planetArray[j][1] = planetArray[j][1] + force;
					planetArray[j][2] = planetArray[j][2] + force;
				}

			}

		}
		repaint();
	}

	public static void main(String[] args) throws java.io.IOException{
		//Get values for the scale and planets
		getScale();
		planetValues();

		//Frame and attributes are set
		JFrame jf = new JFrame();
		jf.setTitle("N-Body Problem");
		jf.setSize(768, 768);

		//Adding planets as new bodies
		NBody t = new NBody();
		jf.add(t);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}