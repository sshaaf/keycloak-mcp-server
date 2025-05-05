package dev.shaaf.experimental;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class UserServiceTest {

    @InjectMock
    Keycloak keycloak;

    @InjectMock
    Jsonb jsonb;

    @Inject
    UserService userService;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private Response response;

    private static final String REALM = "test-realm";
    private static final String USERNAME = "testuser";
    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "User";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String USER_ID = "user-id-123";
    private static final String SUCCESS = "success";
    private static final String USER_JSON = "{\"username\":\"testuser\"}";

    @BeforeEach
    void setUp() {
        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(USER_ID)).thenReturn(userResource);
    }

    @Test
    void testGetUsers() {
        // Mock the behavior
        List<UserRepresentation> userList = Collections.singletonList(createUserRepresentation());
        when(usersResource.list()).thenReturn(userList);
        when(jsonb.toJson(userList)).thenReturn(USER_JSON);

        // Execute the method
        String result = userService.getUsers(REALM);

        // Verify the result
        assertEquals(USER_JSON, result);
        verify(keycloak).realm(REALM);
        verify(realmResource).users();
        verify(usersResource).list();
        verify(jsonb).toJson(userList);
    }

    @Test
    void testAddUser() {
        // Mock the behavior
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());
        when(response.getLocation()).thenReturn(URI.create("users/" + USER_ID));

        // Execute the method
        String result = userService.addUser(REALM, USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

        // Verify the result
        assertEquals(SUCCESS, result);
        verify(keycloak).realm(REALM);
        verify(realmResource).users();
        verify(usersResource).create(any(UserRepresentation.class));
        verify(response).getStatus();
        verify(response).getLocation();
    }

    @Test
    void testDeleteUser_UserExists() {
        // Mock the behavior - user exists
        List<UserRepresentation> userList = Collections.singletonList(createUserRepresentation());
        when(usersResource.search(USERNAME)).thenReturn(userList);

        // Execute the method
        String result = userService.deleteUser(REALM, USERNAME);

        // Verify the result
        assertEquals(SUCCESS, result);
        verify(keycloak).realm(REALM);
        verify(realmResource).users();
        verify(usersResource).search(USERNAME);
        verify(usersResource).delete(USER_ID);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Mock the behavior - user doesn't exist
        when(usersResource.search(USERNAME)).thenReturn(Collections.emptyList());

        // Execute the method
        String result = userService.deleteUser(REALM, USERNAME);

        // Verify the result
        assertEquals("User not found", result);
        verify(keycloak).realm(REALM);
        verify(realmResource).users();
        verify(usersResource).search(USERNAME);
        verify(usersResource, never()).delete(anyString());
    }

    @Test
    void testGetUserByUsername_Found() {
        // Mock the behavior - single user found
        UserRepresentation user = createUserRepresentation();
        List<UserRepresentation> userList = Collections.singletonList(user);
        when(usersResource.search(USERNAME)).thenReturn(userList);

        // Execute the method
        UserRepresentation result = userService.getUserByUsername(REALM, USERNAME);

        // Verify the result
        assertEquals(user, result);
        verify(keycloak).realm(REALM);
        verify(realmResource).users();
        verify(usersResource).search(USERNAME);
    }

    @Test
    void testGetUserByUsername_NotFound() {
        // Mock the behavior - no user found
        when(usersResource.search(USERNAME)).thenReturn(Collections.emptyList());

        // Execute the method
        UserRepresentation result = userService.getUserByUsername(REALM, USERNAME);

        // Verify the result
        assertNull(result);
        verify(keycloak).realm(REALM);
        verify(realmResource).users();
        verify(usersResource).search(USERNAME);
    }

    @Test
    void testGetUserByUsername_MultipleUsers() {
        // Mock the behavior - multiple users found
        UserRepresentation user1 = createUserRepresentation();
        UserRepresentation user2 = createUserRepresentation();
        user2.setId("user-id-456");

        List<UserRepresentation> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        when(usersResource.search(USERNAME)).thenReturn(userList);

        // Execute the method
        UserRepresentation result = userService.getUserByUsername(REALM, USERNAME);

        // Verify the result - should return first user when multiple found
        assertEquals(user1, result);
        verify(keycloak).realm(REALM);
        verify(realmResource).users();
        verify(usersResource).search(USERNAME);
    }

    private UserRepresentation createUserRepresentation() {
        UserRepresentation user = new UserRepresentation();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(PASSWORD);
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        return user;
    }
}