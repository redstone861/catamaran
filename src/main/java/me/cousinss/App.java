package me.cousinss;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.cousinss.geometry.*;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class App extends JPanel {

    public static final double SQRT_THREE = Math.sqrt(3);
    public static final double HALF_SQRT_THREE = SQRT_THREE / 2d;
    public static final double THREE_HALVES = (double)3/2;
    public static final double SIDE = 50;

    private static class Point extends Point2D.Double {
        public Point(double x, double y) {
            super(x, y);
        }
    }

    private static final Map<Resource, Color> RESOURCE_COLORS = new HashMap<>();
    private static final GeneralPath HEXAGON;
    private static final Shape RECTANGLE;
    private static final Shape VILLAGE;
    private static final GeneralPath CITY;
    private static final Shape DIE;
    private static final double DIE_PADDING = 1D/4D;
    private static final Shape DIE_DOT;
    private static final double DIE_SIDE = SIDE;
    private static final Point[] DIE_DOT_POSITIONS;
    static {
        RESOURCE_COLORS.put(Resource.BRICK, Color.decode("#9c4300"));
        RESOURCE_COLORS.put(Resource.WOOD, Color.decode("#517d19"));
        RESOURCE_COLORS.put(Resource.SHEEP, Color.decode("#70b919"));
        RESOURCE_COLORS.put(Resource.WHEAT, Color.decode("#f0bb35"));
        RESOURCE_COLORS.put(Resource.ORE, Color.decode("#7b6f83"));
        RESOURCE_COLORS.put(null, Color.decode("#c9b383"));

        HEXAGON = new GeneralPath();
        HEXAGON.moveTo(0, SIDE); // First vertex
        for (int i = 1; i < 6; i++) {
            // Calculate subsequent vertices
            HEXAGON.lineTo(SIDE * Math.sin(i * 2 * Math.PI / 6), SIDE * Math.cos(i * 2 * Math.PI / 6));
        }
        HEXAGON.closePath();

        double road_width = SIDE/5d;
        double road_height = SIDE * 0.6d;
        RECTANGLE = new Rectangle2D.Double(HALF_SQRT_THREE * SIDE - road_width/2, -road_height/2d, road_width, road_height);

        double village_side = SIDE/3d;
        VILLAGE = new Rectangle2D.Double(-village_side/2d, -village_side/2d, village_side, village_side);

        CITY = new GeneralPath();

        // Calculate arm dimensions
        final double city_side = village_side;  // Overall width of the cross
        // Overall height of the cross
        final double ARM_WIDTH_RATIO = 0.4f; // Ratio of arm width to overall width
        // Ratio of arm height (vertical part of arm) to overall height
        double armWidth = city_side * ARM_WIDTH_RATIO;
        double armHeight = city_side * ARM_WIDTH_RATIO;
        double horizontalArmLength = (city_side - armWidth) / 2;
        double verticalArmLength = (city_side - armHeight) / 2;

        // Define the cross shape using line segments
        // Start at the top-left corner of the top arm
        CITY.moveTo(horizontalArmLength, 0);
        // Move to the top-right corner of the top arm
        CITY.lineTo(horizontalArmLength + armWidth, 0);
        // Move to the right side of the top horizontal section
        CITY.lineTo(horizontalArmLength + armWidth, verticalArmLength);
        // Move to the right side of the horizontal arm
        CITY.lineTo(city_side, verticalArmLength);
        // Move to the bottom-right corner of the horizontal arm
        CITY.lineTo(city_side, verticalArmLength + armHeight);
        // Move to the right side of the bottom horizontal section
        CITY.lineTo(horizontalArmLength + armWidth, verticalArmLength + armHeight);
        // Move to the bottom of the bottom vertical section
        CITY.lineTo(horizontalArmLength + armWidth, city_side);
        // Move to the left side of the bottom vertical section
        CITY.lineTo(horizontalArmLength, city_side);
        // Move to the left side of the bottom horizontal section
        CITY.lineTo(horizontalArmLength, verticalArmLength + armHeight);
        // Move to the left side of the horizontal arm
        CITY.lineTo(0, verticalArmLength + armHeight);
        // Move to the top-left corner of the horizontal arm
        CITY.lineTo(0, verticalArmLength);
        // Move to the left side of the top horizontal section
        CITY.lineTo(horizontalArmLength, verticalArmLength);

        CITY.closePath();
        CITY.transform(AffineTransform.getTranslateInstance(-city_side/2, -city_side /2));

        DIE = new RoundRectangle2D.Double(DIE_SIDE * DIE_PADDING, DIE_SIDE * DIE_PADDING, DIE_SIDE, DIE_SIDE, DIE_SIDE/6D, DIE_SIDE/6D); //TODO
        double dot_rad = DIE_SIDE / 12D;
        DIE_DOT = new Ellipse2D.Double(-dot_rad, -dot_rad, 2*dot_rad, 2*dot_rad);

        final double offset = DIE_SIDE / 4;
        DIE_DOT_POSITIONS = new Point[] {
            new Point(0, 0),
                    new Point( - offset,  - offset),       // top-left
                    new Point( + offset,  + offset),       // bottom-right
                    new Point( + offset,  - offset),       // top-right
                    new Point( - offset,  + offset),       // bottom-left
                    new Point( - offset, 0),                // middle-left
                    new Point( + offset, 0)                 // middle-right
        };
    }

    private final Simulator simulator;

    public App() {
        setPreferredSize(new Dimension(800, 600)); // Set preferred size for the panel
        simulator = new Simulator();
        App app = this;

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true); // Or KeyStroke.getKeyStroke(KeyEvent.VK_A, 0);
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW); // Or WHEN_FOCUSED
        inputMap.put(keyStroke, "advance");
        ActionMap actionMap = this.getActionMap();
        actionMap.put("advance", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                simulator.advance();
                app.repaint();
            }
        });
    }

    private static Point coordinateValues2P(double q, double r) {
        double x = (SQRT_THREE * q  +  HALF_SQRT_THREE * r);
        double y = (                         THREE_HALVES * r);
        // scale cartesian coordinates
        x = x * SIDE;
        y = y * SIDE;
        return new Point(x, y);
    }

    private static Point c2p(TileCoordinate coordinate) {
        return coordinateValues2P(coordinate.q(), coordinate.r());
    }

    private Point v2p(VertexCoordinate coordinate) {
        List<TileCoordinate> vectors = coordinate.getDirection().getAdjacentEdges().stream()
                .map(EdgeDirection::getCoordinateVector).toList();
        Point vec = coordinateValues2P((vectors.get(0).q() + vectors.get(1).q()) * 1d/3d, (vectors.get(0).r() + vectors.get(1).r()) * 1d/3d);
        Point base = c2p(coordinate.getTile());
        return new Point(base.x + vec.x, base.y + vec.y);
    }

    private Color c2c(me.cousinss.Color color) {
        switch(color) {
            case BLUE:
                return Color.BLUE;
            case RED:
                return Color.RED;
            case GREEN:
                return Color.GREEN;
            case BROWN:
                return Color.decode("#8C5E1A");
            case WHITE:
                return Color.WHITE;
            case ORANGE:
                return Color.ORANGE;
            default:
                return Color.BLACK;
        }
    }

    private void drawTile(Tile tile, TileCoordinate coordinate, Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate((double) this.getWidth()/2d + c2p(coordinate).x, (double) this.getHeight()/2d  + c2p(coordinate).y);
        g2d.setColor(RESOURCE_COLORS.get(tile.resource()));
        g2d.fill(HEXAGON);
        g2d.setColor(tile.rollValue() == 6 || tile.rollValue() == 8 ? Color.RED : Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, (int) (SIDE/1.4)));
        FontMetrics fm = g2d.getFontMetrics();
        int fontHeight = fm.getAscent() + fm.getDescent();
        float y = - fontHeight / 2f + fm.getAscent();
        int fontWidth = fm.stringWidth(""+tile.rollValue());
        g2d.drawString(""+tile.rollValue(), -fontWidth/2f, y);
        double dotGap = SIDE/5;
        double dotWidth = dotGap/1.7D;
        int numDots = Dice.getRollDots(tile.rollValue());
        double dotsWidth = dotGap * (numDots-1);
        for(int i = 0; i < numDots; i++) {
            Shape circle = new Ellipse2D.Double(-dotsWidth/2D + i*dotGap, y*1.3D, dotWidth , dotWidth );
            g2d.fill(circle);
        }
        g2d.setFont(new Font("Arial", Font.BOLD, (int) (SIDE/3)));
        g2d.drawString(coordinate.q() + ", " + coordinate.r(), 0, (int) (-1.5D*y));
        g2d.setTransform(originalTransform);
    }

    private void drawRoad(EdgeCoordinate coordinate, me.cousinss.Color color, Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
//        VertexCoordinate[] vertices = coordinate.getVertices().toArray(new VertexCoordinate[2]);
        g2d.translate((double) this.getWidth()/2d + c2p(coordinate.getTile()).x, (double) this.getHeight()/2d  + c2p(coordinate.getTile()).y);
        g2d.rotate(Math.PI/3d * (coordinate.getDirection().getOrdinal() - 1));
        g2d.setColor(c2c(color));
        g2d.fill(RECTANGLE);
        g2d.setTransform(originalTransform);
    }

    private void drawVillage(VertexCoordinate coordinate, me.cousinss.Color color, Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate((double) this.getWidth()/2d + v2p(coordinate).x, (double) this.getHeight()/2d  + v2p(coordinate).y);
        g2d.setColor(c2c(color));
        g2d.fill(VILLAGE);
        g2d.setColor(Color.BLACK);
        g2d.draw(VILLAGE);
        g2d.setTransform(originalTransform);
    }

    private void drawCity(VertexCoordinate coordinate, me.cousinss.Color color, Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate((double) this.getWidth()/2d + v2p(coordinate).x, (double) this.getHeight()/2d  + v2p(coordinate).y);
        g2d.setColor(c2c(color));
        g2d.fill(CITY);
        g2d.setColor(Color.BLACK);
        g2d.draw(CITY);
        g2d.setTransform(originalTransform);
    }

    private void drawDie(int face, Graphics2D g2d) {
        g2d.setColor(Color.decode("#c73c12"));
        g2d.fill(DIE);
        g2d.setColor(Color.BLACK);
        g2d.draw(DIE);
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate(DIE_PADDING, DIE_PADDING);
        g2d.translate(3 * DIE_SIDE/4D, 3 * DIE_SIDE/4D); //center
        g2d.setColor(Color.WHITE);
        // Choose which dots to draw for each face
        switch (face) {
            case 1:
                drawDot(g2d, DIE_DOT_POSITIONS[0]); // center
                break;
            case 2:
                drawDot(g2d, DIE_DOT_POSITIONS[1]); // top-left
                drawDot(g2d, DIE_DOT_POSITIONS[2]); // bottom-right
                break;
            case 3:
                drawDot(g2d, DIE_DOT_POSITIONS[0]); // center
                drawDot(g2d, DIE_DOT_POSITIONS[1]); // top-left
                drawDot(g2d, DIE_DOT_POSITIONS[2]); // bottom-right
                break;
            case 4:
                drawDot(g2d, DIE_DOT_POSITIONS[1]); // top-left
                drawDot(g2d, DIE_DOT_POSITIONS[2]); // bottom-right
                drawDot(g2d, DIE_DOT_POSITIONS[3]); // top-right
                drawDot(g2d, DIE_DOT_POSITIONS[4]); // bottom-left
                break;
            case 5:
                drawDot(g2d, DIE_DOT_POSITIONS[0]); // center
                drawDot(g2d, DIE_DOT_POSITIONS[1]); // top-left
                drawDot(g2d, DIE_DOT_POSITIONS[2]); // bottom-right
                drawDot(g2d, DIE_DOT_POSITIONS[3]); // top-right
                drawDot(g2d, DIE_DOT_POSITIONS[4]); // bottom-left
                break;
            case 6:
                drawDot(g2d, DIE_DOT_POSITIONS[1]); // top-left
                drawDot(g2d, DIE_DOT_POSITIONS[2]); // bottom-right
                drawDot(g2d, DIE_DOT_POSITIONS[3]); // top-right
                drawDot(g2d, DIE_DOT_POSITIONS[4]); // bottom-left
                drawDot(g2d, DIE_DOT_POSITIONS[5]); // middle-left
                drawDot(g2d, DIE_DOT_POSITIONS[6]); // middle-right
                break;
        }
        g2d.setTransform(originalTransform);
    }

    private void drawDot(Graphics2D g2d, Point p) {
        g2d.translate(p.x, p.y);
        g2d.fill(DIE_DOT);
        g2d.translate(-p.x, -p.y);
    }

    private void drawDice(Dice dice, Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        drawDie(dice.getFirst(), g2d);
        g2d.translate(DIE_SIDE * (1D + DIE_PADDING), 0);
        drawDie(dice.getSecond(), g2d);
        g2d.setTransform(originalTransform);
    }

    private void drawActivePlayerTurn(Player player, Graphics2D g2d) {
        g2d.setColor(c2c(player.getMeta().color()));
        String string = player.getMeta().name();
        int h = g2d.getFontMetrics().getAscent();
        int w = g2d.getFontMetrics().stringWidth(string);
        g2d.drawString(string, (float)(DIE_PADDING*1.5*DIE_SIDE + DIE_SIDE) - (float)w/2, (float)(1.5*DIE_PADDING*DIE_SIDE + DIE_SIDE) + h);
    }

    private void drawPlayers(List<Player> players, List<Integer> scores, Graphics2D g2d) {
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth("888 ");
        int h = g2d.getFontMetrics().getAscent();
        int nw = fm.stringWidth("(9) " + players.stream().map(p -> p.getMeta().name()).max(Comparator.comparingInt(String::length)).orElseGet(() -> "") + "  ");
        for (int j = 0; j < players.size(); j++) {
            float y = (float)(g2d.getClipBounds().getHeight() - DIE_PADDING*DIE_SIDE - h * j);
            Player p = players.get(j);
            Resource[] values = Resource.values();
            g2d.setColor(c2c(p.getMeta().color()));
            g2d.drawString("(" + scores.get(j)+ ") " + p.getMeta().name(), (float)(DIE_PADDING*DIE_SIDE), y);
            for (int i = 0; i < values.length; i++) {
                Resource r = values[i];
                g2d.setColor(RESOURCE_COLORS.get(r));
                g2d.drawString(""+p.getHand().count(r), (float)(DIE_PADDING*DIE_SIDE) + nw + w * i, y);
            }
        }
    }

    private void paintSimulator(Simulator simulator, Graphics2D g2d) {
        Game game = simulator.getGame();
        Board b = game.getBoard();
        for(Map.Entry<TileCoordinate, Tile> entry : b.getTileSet()) {
            drawTile(entry.getValue(), entry.getKey(), g2d);
        }
        for(Player player : game.getPlayers()) {
            for(EdgeCoordinate coordinate : game.getDevelopments().getRoads(player)) {
                drawRoad(coordinate, player.getMeta().color(), g2d);
            }
        }
        for(EdgeCoordinate road : game.getLongestRoad(game.getVictoryCards().assess(VictoryCards.Card
                .LONGEST_ROAD))) {
            drawRoad(road, me.cousinss.Color.WHITE, g2d);
        }
        for(Player player : game.getPlayers()) {
            for(VertexCoordinate coordinate : game.getDevelopments().getVillages(player)) {
                drawVillage(coordinate, player.getMeta().color(), g2d);
            }
        }
        for(Player player : game.getPlayers()) {
            for(VertexCoordinate coordinate : game.getDevelopments().getCities(player)) {
                drawCity(coordinate, player.getMeta().color(), g2d);
            }
        }

        game.rollDice();
        drawDice(game.getDice(), g2d);
        drawActivePlayerTurn(game.getTurnPlayer(), g2d);
        List<Player> players = new ArrayList<>(game.getPlayers());
        Collections.rotate(players, game.getPlayers().indexOf(game.getTurnPlayer()));
        drawPlayers(players, players.stream().map(game::getScore).toList(), g2d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        paintSimulator(simulator, g2d);

        g2d.dispose(); // Release system resources used by this Graphics2D context
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Catamaran");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new App());
            frame.pack(); // Adjust frame size to fit components
            frame.setLocationRelativeTo(null); // Center the frame on screen
            frame.setVisible(true);
        });
    }
}