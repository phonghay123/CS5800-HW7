import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextEditorTest {

    @Test
    public void testCharacterStyleFactory() {
        CharacterStyle style1 = CharacterStyleFactory.getStyle("Arial", 12, "Red");
        CharacterStyle style2 = CharacterStyleFactory.getStyle("Arial", 12, "Red");

        assertEquals(style1, style2);
    }

    @Test
    public void testDocumentAddCharacter() {
        Document document = new Document();
        CharacterStyle style = new ConcreteCharacterStyle("Arial", 12, "Red");

        document.addCharacter('A', style);

        assertEquals("A", document.getContent().toString());
    }

    @Test
    public void testDocumentSaveAndLoad() {
        Document document = new Document();
        CharacterStyle style = new ConcreteCharacterStyle("Arial", 12, "Red");

        document.addCharacter('A', style);
        document.saveToFile("test.txt");

        Document loadedDocument = new Document();
        loadedDocument.loadFromFile("test.txt");

        assertEquals(document.getContent().toString(), loadedDocument.getContent().toString());
    }

    @Test
    public void testDocumentGetStyleAt() {
        Document document = new Document();
        CharacterStyle style = new ConcreteCharacterStyle("Arial", 12, "Red");

        document.addCharacter('A', style);

        assertEquals(style, document.getStyleAt(0));
    }


}