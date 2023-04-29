import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


class Edge { // Edge class
	int src, dest;

	Edge(int src, int dest) {
		this.src = src;
		this.dest = dest;
	}
}

class UnionFind{ // UnionFind class for Kruskal's algorithm to generate a maze with no cycles
	private int[] parent;

	UnionFind(int size){
		parent = new int[size];
		for(int i = 0; i < size; i++){
			parent[i] = i;
		}
	}

	int find(int x){ // Find the root of the tree
		if(parent[x] == x){
			return x;
		}
		return parent[x] = find(parent[x]);
	}

	boolean union(int x, int y){ // Union two nodes and their trees
		int rootX = find(x);
		int rootY = find(y);
		if(rootX == rootY){
			return false;
		}
		parent[rootX] = rootY;
		return true;
	}
}

class MazePanel extends JPanel{ // Panel to display the maze
	private static final int WALL_SIZE = 10;

	private char[][] maze;
	List<Integer> path;

	public MazePanel(char[][] maze, List<Integer> path){ // Constructor
		this.maze = maze;
		this.path = path;
		setPreferredSize(new Dimension(maze[0].length * WALL_SIZE, maze.length * WALL_SIZE));
	}

	public void repaint(char[][] newMaze, List<Integer> path){ // Repaint the maze
		this.maze = newMaze;
		this.path = path;
		this.repaint();
	}

	protected void paintComponent(Graphics g){
		super.paintComponent(g);

		for(int i = 0; i < maze.length; i++){ // Paint the maze
			for(int j = 0; j < maze[0].length; j++){
				if(i == 1 && j == 1){ // Start point
					g.setColor(Color.GREEN);
				}
				else if(i == maze.length - 2 && j == maze[0].length - 2){ // End point
					g.setColor(Color.RED);
				}
				else if(maze[i][j] == '#'){ // Wall
					g.setColor(Color.BLACK);
				}
				else{ // Empty space
					g.setColor(Color.WHITE);
				}
				g.fillRect(j * WALL_SIZE, i * WALL_SIZE, WALL_SIZE, WALL_SIZE); // Fill the rectangle
			}
		}
		for(int p = 0; p < path.size(); p += 2){ // Paint the path in blue
			int x = path.get(p);
			int y = path.get(p + 1);
			g.setColor(Color.BLUE);
			g.fillRect(x * WALL_SIZE, y * WALL_SIZE, WALL_SIZE, WALL_SIZE);
		}
	}
}

public class Maze{ // Main class
	private static final int[] dx = {-1, 0, 1, 0}; // X directions
	private static final int[] dy = {0, 1, 0, -1}; // Y directions
	private static final List<Integer> path = new ArrayList<Integer>(); // Path list

	public static void main(String[] args){
		int width = 30;
		int height = 30;
		char[][] maze = generateMaze(width, height);
		maze[maze.length - 2][maze[0].length - 2] = 'E'; // Set a default end point
		dfsPath(maze, 1, 1, path);

		displayMaze(maze);
	}

	public static char[][] generateMaze(int width, int height){ // Generate a maze
		char[][] maze = new char[height * 2 + 1][width * 2 + 1];
		for(int i = 0; i < height * 2 + 1; i++){
			for(int j = 0; j < width * 2 + 1; j++){
				maze[i][j] = (i % 2 == 0 || j % 2 == 0) ? '#' : ' '; // Set the walls and empty spaces
			}
		}

		List<Edge> edges = new ArrayList<>(); // List of edges
		for(int i = 0; i < height; i++){ // Add all edges to the list
			for(int j = 0; j < width; j++){
				int cell = i * width + j;
				for(int k = 0; k < 4; k++){
					int ni = i + dx[k];
					int nj = j + dy[k];
					if(ni >= 0 && ni < height && nj >= 0 && nj < width){ // Check if the cell is in the maze
						int dest = ni * width + nj;
						edges.add(new Edge(cell, dest));
					}
				}
			}
		}
		Collections.shuffle(edges); // Shuffle the edges
		UnionFind uf = new UnionFind(width * height); // UnionFind class

		for(Edge edge : edges){ // Kruskal's algorithm, union of all the edges to create a maze with no cycles
			if(uf.union(edge.src, edge.dest)){
				int x = edge.src % width * 2 + 1;
				int y = edge.src / width * 2 + 1;
				int nx = edge.dest % width * 2 + 1;
				int ny = edge.dest / width * 2 + 1;

				maze[(y + ny) / 2][(x + nx) / 2] = ' ';
			}
		}
		return maze;
	}

	public static boolean dfsPath(char[][] maze, int x, int y, List<Integer> path) { // Find a path from the start to the end using a depth-first search
		if (x < 0 || y < 0 || x >= maze[0].length || y >= maze.length || maze[y][x] == '#' || maze[y][x] == 'v') { // Check if the cell is valid
			return false;
		}

		if (maze[y][x] == 'E') { // Check if the cell is the end point
			path.add(x);
			path.add(y);
			return true;
		}

		maze[y][x] = 'v';

		for (int i = 0; i < dx.length; i++) { // Check all directions
			if (dfsPath(maze, x + dx[i], y + dy[i], path)) {
				path.add(x);
				path.add(y);
				return true;
			}
		}

		return false;
	}

	public static String pathToDirections(List<Integer> path){ // Convert the path to the cardinal directions string printed above when you "show path"
		StringBuilder directions = new StringBuilder();
		for(int i = path.size() - 4; i >= 0; i -= 2){
			int dx = path.get(i + 2) - path.get(i);
			int dy = path.get(i + 3) - path.get(i + 1);

			if (dx == -1)
				directions.append("E");
			else if (dx == 1)
				directions.append("W");
			else if (dy == -1)
				directions.append("S");
			else if (dy == 1)
				directions.append("N");
		}
		return directions.toString();
	}

	public static void displayMaze(char[][] initialMaze){ // Display the maze a bunch of buttons and sliders, you get the point :D
		AtomicReference<char[][]> mazeRef = new AtomicReference<>(initialMaze);
		JFrame frame = new JFrame("maze");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);

		MazePanel panel = new MazePanel(initialMaze, path);
		frame.add(panel);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(3, 2));

		JLabel directionsLabel = new JLabel("");
		frame.add(directionsLabel, BorderLayout.NORTH);

		JLabel widthLabel = new JLabel("Width: ");
		controlPanel.add(widthLabel);

		JSlider widthSlider = new JSlider(10, 60, 30);
		widthLabel.setLabelFor(widthSlider);
		controlPanel.add(widthSlider);

		widthSlider.addChangeListener(e -> {
			int value = widthSlider.getValue();
			widthLabel.setText("Width: " + value);
		});

		JLabel heightLabel = new JLabel("Height: ");
		controlPanel.add(heightLabel);

		JSlider heightSlider = new JSlider(10, 60, 30);
		heightLabel.setLabelFor(heightSlider);
		controlPanel.add(heightSlider);

		heightSlider.addChangeListener(e -> {
			int value = heightSlider.getValue();
			heightLabel.setText("Height: " + value);
		});

		JButton regenerate = new JButton("Generate new maze");
		regenerate.addActionListener(e -> {
			int width = widthSlider.getValue();
			int height = heightSlider.getValue();
			char[][] newMaze = generateMaze(width, height);
			newMaze[newMaze.length - 2][newMaze[0].length - 2] = 'E';
			path.clear();
			panel.repaint(newMaze, path);
			mazeRef.set(newMaze);
		});
		controlPanel.add(regenerate);

		JButton showPathButton = new JButton("Show path");
		showPathButton.addActionListener(e -> {
			char[][] currentMaze = mazeRef.get();
			dfsPath(currentMaze, 1, 1, path);
			String directions = pathToDirections(path);
			directionsLabel.setText("Path: " + directions);
			panel.repaint(currentMaze, path);
		});
		controlPanel.add(showPathButton);

		frame.add(controlPanel, BorderLayout.SOUTH);

		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}
