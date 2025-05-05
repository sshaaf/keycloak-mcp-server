package dev.shaaf.experimental;

import dev.shaaf.experimental.formatter.KeycloakRepresentationFormatter;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeycloakRepresentationFormatterTest {

    @Test
    public void testFormat_withSingleObject() {
        DummyClass sampleObject = new DummyClass("John", 30);

        String result = KeycloakRepresentationFormatter.format(sampleObject);
        System.out.println(result);

        String expectedOutput = new String("");
        assertEquals(expectedOutput, result);
    }

    @Test
    public void testFormat_withObjectList() {
        DummyClass object1 = new DummyClass("John", 30);
        DummyClass object2 = new DummyClass("Jane", 25);
        List<DummyClass> objects = List.of(object1, object2);

        String result = KeycloakRepresentationFormatter.format(objects);

        System.out.println(result);
        String expectedOutput = KeycloakRepresentationFormatter.LIST_SEPARATOR;
        assertEquals(expectedOutput, result);
    }

    @Test
    public void testFormat_withNullInput() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> KeycloakRepresentationFormatter.format(null));
        assertEquals("Input is null.", exception.getMessage());
    }

    @Test
    public void testFormat_withEmptyObjectList() {
        List<DummyClass> emptyList = List.of();

        String result = KeycloakRepresentationFormatter.format(emptyList);

        assertEquals("", result);
    }

    @Test
    public void testFormat_withSingleUserRepresentation() {
        UserRepresentation user = createUserRepresentation("user1", "John", "Doe", "john@example.com", true);

        String result = KeycloakRepresentationFormatter.format(user);

        assertTrue(result.contains("username: user1"));
        assertTrue(result.contains("firstName: John"));
        assertTrue(result.contains("lastName: Doe"));
        assertTrue(result.contains("email: john@example.com"));
        assertTrue(result.contains("enabled: true"));
    }

    @Test
    public void testFormat_withUserRepresentationList() {
        UserRepresentation user1 = createUserRepresentation("user1", "John", "Doe", "john@example.com", true);
        UserRepresentation user2 = createUserRepresentation("user2", "Jane", "Smith", "jane@example.com", false);
        List<UserRepresentation> users = Arrays.asList(user1, user2);

        String result = KeycloakRepresentationFormatter.format(users);

        assertTrue(result.contains("username: user1"));
        assertTrue(result.contains("firstName: John"));
        assertTrue(result.contains("username: user2"));
        assertTrue(result.contains("firstName: Jane"));
        assertTrue(result.contains("---")); // Ensures the separator is present
    }

    @Test
    public void testFormat_withUserRepresentationWithAttributes() {
        UserRepresentation user = createUserRepresentation("user1", "John", "Doe", "john@example.com", true);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("department", List.of("IT"));
        attributes.put("roles", Arrays.asList("admin", "user"));
        user.setAttributes(attributes);

        String result = KeycloakRepresentationFormatter.format(user);

        assertTrue(result.contains("username: user1"));
        assertTrue(result.contains("attributes:"));
    }

    @Test
    public void testFormat_withEmptyUserRepresentationList() {
        // Arrange
        List<UserRepresentation> emptyUserList = List.of();

        // Act
        String result = KeycloakRepresentationFormatter.format(emptyUserList);

        // Assert
        assertEquals("", result);
    }

    private UserRepresentation createUserRepresentation(String username, String firstName,
                                                        String lastName, String email,
                                                        boolean enabled) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setEnabled(enabled);
        return user;
    }

    static class DummyClass {
        private String name;
        private int age;

        public DummyClass(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}