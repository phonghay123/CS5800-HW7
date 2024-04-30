import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
// Flyweight interface
interface CharacterStyle {
    void applyStyle(Graphics g);
    String getFont();
    String getColor();
    int getSize();
}

// Concrete Flyweight
class ConcreteCharacterStyle implements CharacterStyle {
    private final String font;
    private final String color;
    private final int size;

    public ConcreteCharacterStyle(String font, int size, String color) {
        this.font = font;
        this.size = size;
        this.color = color;
    }

    @Override
    public void applyStyle(Graphics g) {
        g.setFont(new Font(font, Font.PLAIN, size));
        g.setColor(getColor(color));
    }

    @Override
    public String getFont() {
        return font;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public int getSize() {
        return size;
    }

    private Color getColor(String colorName) {
        switch (colorName) {
            case "Red":
                return Color.RED;
            case "Blue":
                return Color.BLUE;
            case "Black":
                return Color.BLACK;
            default:
                return Color.BLACK;
        }
    }
}

// Flyweight Factory
class CharacterStyleFactory {
    private static final Map<String, CharacterStyle> styles = new HashMap<>();

    public static CharacterStyle getStyle(String fontName, int fontSize, String color) {
        String key = fontName + fontSize + color;
        return styles.computeIfAbsent(key, k -> new ConcreteCharacterStyle(fontName, fontSize, color));
    }
}

// Character class
class Character {
    private char value;
    private CharacterStyle style;

    public Character(char value, CharacterStyle style) {
        this.value = value;
        this.style = style;
    }

    public void draw(Graphics g, int x, int y) {
        style.applyStyle(g);
        g.drawString(String.valueOf(value), x, y);
    }

    public CharacterStyle getStyle() {
        return style;
    }
}

// Document class
class Document {
    private final StringBuilder content = new StringBuilder();
    private final Map<Integer, CharacterStyle> styleMap = new HashMap<>();

    public void addCharacter(char c, CharacterStyle style) {
        content.append(c);
        styleMap.put(content.length() - 1, style);
    }

    public Map<Integer, CharacterStyle> getStyleMap() {
        return styleMap;
    }

    public void draw(Graphics g, int x, int y) {
        for (int i = 0; i < content.length(); i++) {
            CharacterStyle style = styleMap.get(i);
            if (style != null) {
                style.applyStyle(g);
            }
            g.drawString(String.valueOf(content.charAt(i)), x + i * 10, y);
        }
    }

    public void saveToFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (int i = 0; i < content.length(); i++) {
                writer.write(content.charAt(i) + ",");

                CharacterStyle style = styleMap.get(i);
                if (style != null) {
                    writer.write(style.getFont() + "," + style.getSize() + "," + style.getColor() + ",");
                } else {
                    writer.write(",,,");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i += 4) {
                    char c = parts[i].charAt(0);
                    String font = parts[i + 1];
                    int size = Integer.parseInt(parts[i + 2]);
                    String color = parts[i + 3];

                    CharacterStyle style = CharacterStyleFactory.getStyle(font, size, color);
                    addCharacter(c, style);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CharacterStyle getStyleAt(int index) {
        return styleMap.get(index);
    }

    public StringBuilder getContent() {
        return content;
    }
}

// Main UI class
public class TextEditor extends JFrame {
    private final JTextArea textArea = new JTextArea();
    private final JComboBox<String> fontComboBox = new JComboBox<>(new String[]{"Arial", "Calibri", "Verdana"});
    private final JComboBox<String> colorComboBox = new JComboBox<>(new String[]{"Red", "Blue", "Black"});
    private final JComboBox<Integer> sizeComboBox = new JComboBox<>(new Integer[]{12, 14, 16});

    private final Document document = new Document();

    public TextEditor() {
        setTitle("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(new JLabel("Font:"));
        controlPanel.add(fontComboBox);
        controlPanel.add(new JLabel("Color:"));
        controlPanel.add(colorComboBox);
        controlPanel.add(new JLabel("Size:"));
        controlPanel.add(sizeComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                document.saveToFile("document.txt");
            }
        });

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                document.loadFromFile("document.txt");
                textArea.setText(document.getContent().toString());
            }
        });

        controlPanel.add(saveButton);
        controlPanel.add(loadButton);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                CharacterStyle style = CharacterStyleFactory.getStyle((String) fontComboBox.getSelectedItem(),
                        (int) sizeComboBox.getSelectedItem(), (String) colorComboBox.getSelectedItem());
                document.addCharacter(e.getKeyChar(), style);

            }
        });
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int caretPosition = textArea.getCaretPosition();
                CharacterStyle style = document.getStyleAt(caretPosition);
                if (style != null) {
                    fontComboBox.setSelectedItem(style.getFont());
                    colorComboBox.setSelectedItem(style.getColor());
                    sizeComboBox.setSelectedItem(style.getSize());
                }
            }
        });
        fontComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStyle();
            }
        });

        colorComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStyle();
            }
        });

        sizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStyle();
            }
        });



        setSize(800, 600);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        document.draw(g, 50, 50);
    }
    private void updateStyle() {
        int caretPosition = textArea.getCaretPosition();
        CharacterStyle style = CharacterStyleFactory.getStyle((String) fontComboBox.getSelectedItem(),
                (int) sizeComboBox.getSelectedItem(), (String) colorComboBox.getSelectedItem());
        document.getStyleMap().put(caretPosition, style);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TextEditor::new);
    }
}
