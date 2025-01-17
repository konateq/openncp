package eu.europa.ec.sante.openncp.webmanager.backend.web.rest;

import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.Role;
import eu.europa.ec.sante.openncp.webmanager.backend.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class RoleResource {

    private static final String STATUS_CODE_NOT_ACCEPTED_STATUS_CODE_VALUE_403 = "\"statusCode\": \"NOT_ACCEPTED\", \"statusCodeValue\": 403 }";
    private final Logger logger = LoggerFactory.getLogger(RoleResource.class);

    private final RoleService roleService;

    public RoleResource(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(path = "/roles")
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    @PostMapping("/roles/register")
    public ResponseEntity<String> registerRole(@Valid @RequestBody Role newRole) {
        List<Role> roles = roleService.getRoles();

        for (Role role : roles) {
            if (role.equals(newRole)) {
                if (logger.isInfoEnabled()) {
                    logger.info("[Gateway] Role Already exists! {}", sanitizeString(newRole.getName()));
                }
                return ResponseEntity.badRequest().body("{ \"body\": \"Role already exists!\", " +
                        STATUS_CODE_NOT_ACCEPTED_STATUS_CODE_VALUE_403);
            }
            if (role.getId().equals(newRole.getId())) {
                logger.info("[Gateway] ID Already exists! {}", newRole.getId());
                return ResponseEntity.badRequest().body("{ \"body\": \"Role ID already exists!\", " +
                        STATUS_CODE_NOT_ACCEPTED_STATUS_CODE_VALUE_403);
            }
            if (role.getName().equals(newRole.getName())) {
                logger.info("[Gateway] Role name Already exists! {}", newRole.getName());
                return ResponseEntity.badRequest().body("{ \"body\": \"Rolename Already exists!\", " +
                        STATUS_CODE_NOT_ACCEPTED_STATUS_CODE_VALUE_403);
            }
        }
        roleService.save(newRole);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/roles/delete")
    public ResponseEntity<String> deleteRole(@RequestParam() Role role) {
        roleService.deleteRole(role);
        return ResponseEntity.ok().build();
    }

    private static String sanitizeString(String stringToSanitize) {
        return stringToSanitize != null ? stringToSanitize.replaceAll("[\n\r]", "_") : "";
    }

}
