import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


class Edge {
	int src, dest;

	Edge(int src, int dest) {
		this.src = src;
		this.dest = dest;
	}
}

class UnionFind{
	private int[] parent;

	UnionFind(int size){
		parent = new int[size];
		for(int i = 0; i < size; i++){
			parent[i] = i;
		}
	}

	int find(int x){
		if(parent[x] == x){
			return x;
		}
		return parent[x] = find(parent[x]);
	}

	boolean union(int x, int y){
		int rootX = find(x);
		int rootY = find(y);
		if(rootX == rootY){
			return false;
		}
		parent[rootX] = rootY;
		return true;
	}
}

class MazePanel extends JPanel{
	private static final int WALL_SIZE = 10;

	private final char[][] maze;

	public MazePanel(char[][] maze){
		this.maze = maze;
		setPreferredSize(new Dimension(maze[0].length * WALL_SIZE, maze.length * WALL_SIZE));
	}

	protected void paintComponent(Graphics g){
		super.paintComponent(g);

		for(int i = 0; i < maze.length; i++){
			for(int j = 0; j < maze[0].length; j++){
				if(maze[i][j] == '#'){
					g.setColor(Color.BLACK);
				}
				else{
					g.setColor(Color.WHITE);
				}
				g.fillRect(j * WALL_SIZE, i * WALL_SIZE, WALL_SIZE, WALL_SIZE);
			}
		}
	}
}

public class Maze{
	private static final int[] dx = {-1, 0, 1, 0};
	private static final int[] dy = {0, 1, 0, -1};

	public static void main(String[] args){
		int width = 30;
		int height = 30;
		char[][] maze = generateMaze(width, height);
		displayMaze(maze);
	}

	public static char[][] generateMaze(int width, int height){
		char[][] maze = new char[height * 2 + 1][width * 2 + 1];
		for(int i = 0; i < height * 2 + 1; i++){
			for(int j = 0; j < width * 2 + 1; j++){
				maze[i][j] = (i % 2 == 0 || j % 2 == 0) ? '#' : ' ';
			}
		}

		List<Edge> edges = new ArrayList<>();
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				int cell = i * width + j;
				for(int k = 0; k < 4; k++){
					int ni = i + dx[k];
					int nj = j + dy[k];
					if(ni >= 0 && ni < height && nj >= 0 && nj < width){
						int dest = ni * width + nj;
						edges.add(new Edge(cell, dest));
					}
				}
			}
		}
		Collections.shuffle(edges);
		UnionFind uf = new UnionFind(width * height);

		for(Edge edge : edges){
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
	public static void displayMaze(char[][] maze){
		JFrame frame = new JFrame("maze");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		MazePanel panel = new MazePanel(maze);
		frame.add(panel);

		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}
